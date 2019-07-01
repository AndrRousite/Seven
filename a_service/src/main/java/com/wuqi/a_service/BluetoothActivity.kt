package com.wuqi.a_service

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.app.AppOpsManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.BaseModel
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.poscore.mvp.IView
import com.weyee.possupport.arch.RxLiftUtils
import com.weyee.poswidget.layout.QMUIButton
import com.weyee.poswidget.stateview.state.ContentState
import com.weyee.sdk.api.rxutil.RxJavaUtils
import com.weyee.sdk.dialog.ChooseDialog
import com.weyee.sdk.multitype.BaseAdapter
import com.weyee.sdk.multitype.BaseHolder
import com.weyee.sdk.multitype.FlexibleDividerDecoration
import com.weyee.sdk.multitype.HorizontalDividerItemDecoration
import com.weyee.sdk.permission.PermissionIntents
import com.weyee.sdk.print.PrintManager
import com.weyee.sdk.print.constant.ConnectStatus
import com.weyee.sdk.print.constant.DeviceCode
import com.weyee.sdk.print.constant.PaperSize
import com.weyee.sdk.print.listener.PrintConnectListener
import com.weyee.sdk.print.scan.ble.BluetoothUtils
import com.weyee.sdk.print.scan.ble.IBluetoothListener
import com.weyee.sdk.router.Path
import com.weyee.sdk.toast.ToastUtils
import com.weyee.sdk.util.number.MNumberUtil
import com.wuqi.a_service.state.PrivilegesState
import com.wuqi.a_service.ticktdata.TestPrintDataMaker
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_bluetooth.*
import java.util.concurrent.TimeUnit

@Route(path = Path.Service + "Bluetooth")
class BluetoothActivity : BaseActivity<BasePresenter<BaseModel, IView>>() {

    private lateinit var adapter: BaseAdapter<Any>
    private var bluetoothUtils: BluetoothUtils? = null

    override fun setupActivityComponent(appComponent: AppComponent?) {
    }

    override fun getResourceId(): Int = R.layout.activity_bluetooth

    @SuppressLint("RtlHardcoded")
    override fun initView(savedInstanceState: Bundle?) {
        headerView.setTitle("蓝牙打印", Gravity.LEFT or Gravity.CENTER_VERTICAL)

        headerView.isShowMenuRightOneView(true)
        headerView.setMenuRightOneIcon(R.drawable.ic_settings_black_24dp)

        headerView.setOnClickRightMenuOneListener {
            val dialog = ChooseDialog(context)
            dialog.setTitle("选择打印尺寸")

            dialog.setNewData(arrayListOf(
                PaperSize.PAPER_SIZE_80,
                PaperSize.PAPER_SIZE_110,
                PaperSize.PAPER_SIZE_150,
                PaperSize.PAPER_SIZE_210
            ).map {
                ChooseDialog.ChooseItemModel(
                    PaperSize.getPaperSize() == it, String.format("%dmm", it)
                )
            })

            dialog.setOnItemClickListener { model ->
                run {
                    PaperSize.savePaperSize(MNumberUtil.convertToint(model.title.replace("mm", "")))
                }
            }

            dialog.show()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context)
                .visibilityProvider(object : FlexibleDividerDecoration.VisibilityProvider {
                    override fun shouldHideDivider(position: Int, parent: RecyclerView?): Boolean {
                        val item = adapter.getItem(position)
                        val nextItem = adapter.getItem(position + 1)
                        if (item is BluetoothDevice && nextItem is BluetoothDevice) {
                            return false
                        }
                        return true
                    }
                })
                .margin(resources.getDimensionPixelSize(R.dimen.default_activity_16dp))
                .color(Color.parseColor("#f1f1f1")).size(1).build()
        )
        adapter = object : BaseAdapter<Any>(null, { _, _, data, _ ->
            if (data is BluetoothDevice && data.bondState != BluetoothDevice.BOND_BONDED) {
                printLines(data.address)
            }
        }) {
            override fun getHolder(v: View, viewType: Int): BaseHolder<Any> {
                return object : BaseHolder<Any>(v) {
                    @SuppressLint("MissingPermission")
                    override fun setData(data: Any, position: Int) {
                        if (data is BluetoothDevice) {
                            setText(R.id.tvName, data.name)
                            setText(R.id.tvAddress, data.address)
                            setVisible(
                                R.id.tvPrint,
                                if (data.bondState == BluetoothDevice.BOND_BONDED) View.VISIBLE else View.GONE
                            )
                            setText(R.id.tvPrint, "打印")
                            setOnClickListener(R.id.tvPrint) {
                                printLines(data.address)
                            }
                        } else {
                            if ("未配对设备(点击名称进行配对)" == data) {
                                setVisible(R.id.tvSearch, View.VISIBLE)
                                setText(R.id.tvSearch, "搜索")
                                val qmuiButton = getView<QMUIButton>(R.id.tvSearch)
                                qmuiButton.setBorderColor(resources.getColor(if (bluetoothUtils?.isScanning == true) R.color.cl_666666 else R.color.cl_50a7ff))
                                qmuiButton.setTextColor(resources.getColor(if (bluetoothUtils?.isScanning == true) R.color.cl_666666 else R.color.cl_50a7ff))
                            } else {
                                setVisible(R.id.tvSearch, View.GONE)
                            }
                            setText(R.id.tvTitle, data as String?)
                            setOnClickListener(R.id.tvSearch) {
                                bluetoothUtils?.startDiscovery()
                            }
                        }
                    }

                }
            }

            override fun getItemViewType(position: Int): Int {
                return if (getItem(position) is BluetoothDevice) 1 else super.getItemViewType(position)
            }

            override fun getLayoutId(viewType: Int): Int =
                if (viewType == 0) R.layout.item_bluetooth_title else R.layout.item_bluetooth

        }

        recyclerView.adapter = adapter
        recyclerView.isFocusable = false
        recyclerView.isFocusableInTouchMode = false

        stateLayout.stateManager.addState(PrivilegesState())
    }

    override fun initData(savedInstanceState: Bundle?) {
        AndPermission.with(context).runtime()
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .onGranted {
                val result =
                    AppOpsManagerCompat.permissionToOp(Manifest.permission.ACCESS_COARSE_LOCATION)?.let { it1 ->
                        AppOpsManagerCompat.noteProxyOp(
                            context,
                            it1, context.packageName
                        )
                    }
                if (result == AppOpsManagerCompat.MODE_IGNORED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 6.0部分国产手机room的定制兼容问题
                    stateLayout.showState(PrivilegesState.STATE)
                } else {
                    stateLayout.showState(ContentState.STATE)
                    bindService()
                }
            }
            .onDenied {
                stateLayout.showState(PrivilegesState.STATE)
                AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("提示")
                    .setMessage("此功能可能需要位置信息权限，请前往打开该权限")
                    .setPositiveButton("前往") { dialog, _ ->
                        run {
                            dialog.dismiss()
                            PermissionIntents.toPermissionSetting(context)
                        }
                    }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
            .start()
    }

    /**
     * 开始蓝牙扫描的服务
     */
    private fun bindService() {
        bluetoothUtils = BluetoothUtils(DeviceCode.getDeviceCode(), object : IBluetoothListener {
            override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
                adapter.add(adapter.all.size - 1, device)
            }

            override fun onClassicScan(device: BluetoothDevice?) {
                adapter.add(adapter.all.size - 1, device)
            }

            override fun onScanStateChange(isScanning: Boolean) {
                if (isScanning) {
                    adapter.addAll(adapter.all.filter {
                        if (it is BluetoothDevice) {
                            it.bondState == BluetoothDevice.BOND_BONDED
                        } else {
                            true
                        }
                    }, true)
                }
                adapter.modify(adapter.itemCount - 1, if (isScanning) "正在搜索可配对设备..." else "搜索完毕")
                adapter.notifyDataSetChanged()
            }

            override fun onBondStateChange(device: BluetoothDevice?, newState: Int) {
                when (newState) {
                    BluetoothDevice.BOND_BONDED -> {
                        // 绑定设备
                        val list: MutableList<Any> = adapter.all.filter {
                            if (it is BluetoothDevice) {
                                it.bondState != BluetoothDevice.BOND_BONDED
                            } else {
                                true
                            }
                        }.toMutableList()
                        bluetoothUtils?.bondedDevices?.toList()?.let { list.addAll(1, it) }
                        adapter.addAll(list, true)
                    }
                    BluetoothDevice.BOND_NONE -> {
                        // 解绑设备
                        adapter.remove(device)
                    }
                    BluetoothDevice.BOND_BONDING -> {
                        // 正在绑定
                    }
                }
            }
        })
        adapter.add("已配对设备")
        RxJavaUtils.delay(200, TimeUnit.MILLISECONDS)
            .`as`(RxLiftUtils.bindLifecycle(this))
            .subscribe {
                adapter.addAll(1, bluetoothUtils?.bondedDevices?.toList())
            }
        adapter.add("未配对设备(点击名称进行配对)")
        adapter.add("正在搜索可配对设备...")
        bluetoothUtils?.bindService()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothUtils?.unBindService()
    }

    override fun useProgressAble(): Boolean {
        return !super.useProgressAble()
    }

    private fun printLines(address: String) {
        PrintManager.getInstance().connect(address, object : PrintConnectListener {
            override fun onStart() {
                //Ble正在连接
                showProgress("正在连接...")
            }

            override fun onProcess(state: Int) {
                when (state) {
                    ConnectStatus.STATE_WRITE_BEGIN -> {
                        showProgress("正在打印...")
                    }
                    ConnectStatus.STATE_DISCONNECTING -> {
                        //Ble正在断开连接
                        showProgress("正在断开连接...")
                    }
                    ConnectStatus.STATE_CONNECTED -> {
                        //Ble已连接
                        showProgress("已连接...")
                        TestPrintDataMaker().printData(PaperSize.getPaperSize())
                    }
                }
            }

            override fun onSuccess() {
                ToastUtils.show("打印完成")
            }

            override fun onError(code: Int, msg: String?) {
                ToastUtils.show("打印失败")
            }

            override fun onComplete() {
                hideProgress()
            }

        })
    }
}
