package com.wuqi.a_service

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poswidget.textview.ball.BallTextView
import com.weyee.sdk.multitype.listview.BaseHolder
import com.weyee.sdk.router.Path
import com.weyee.sdk.toast.ToastUtils
import com.wuqi.a_service.di.DaggerMachineComponent
import com.wuqi.a_service.di.LotteryModule
import com.wuqi.a_service.wan.BallBean
import com.wuqi.a_service.wan.LotteryContract
import com.wuqi.a_service.wan.LotteryPresenter
import kotlinx.android.synthetic.main.activity_machine.*

/**
 * 机选页面
 */
@Route(path = Path.Service + "Machine")
class MachineActivity : BaseActivity<LotteryPresenter>(), LotteryContract.MachineView {
    private var lotteryNo: String? = null
    private var lotteryId: String? = null
    private var redAdapter: com.weyee.sdk.multitype.listview.BaseAdapter<BallBean>? = null
    private var blueAdapter: com.weyee.sdk.multitype.listview.BaseAdapter<BallBean>? = null

    override fun setupActivityComponent(appComponent: AppComponent?) {
        DaggerMachineComponent
            .builder()
            .appComponent(appComponent)
            .lotteryModule(LotteryModule(this))
            .build()
            .inject(this@MachineActivity)
    }

    override fun getResourceId(): Int = R.layout.activity_machine

    override fun initView(savedInstanceState: Bundle?) {
        lotteryNo = intent.getStringExtra("lottery_no")
        lotteryId = intent.getStringExtra("lottery_id")

        redGridView.setOnItemClickListener { _, _, position, _ ->
            val item = redAdapter?.getItem(position)
            item?.selected = item?.selected != true
            redAdapter?.notifyDataSetChanged()
        }
        redAdapter =
            object : com.weyee.sdk.multitype.listview.BaseAdapter<BallBean>(mPresenter.reds(lotteryId)) {
                override fun getLayoutId(position: Int): Int = R.layout.item_lottery_ball

                override fun convert(viewHolder: BaseHolder, item: BallBean?, position: Int) {
                    (viewHolder.itemView as BallTextView).textColor =
                        Color.parseColor(if (item?.selected == true) "#FFFFFF" else "#e34c4c")
                    (viewHolder.itemView as BallTextView).strokeColor =
                        Color.parseColor(if (item?.selected == true) "#00000000" else "#e34c4c")
                    (viewHolder.itemView as BallTextView).backgroundColor =
                        Color.parseColor(if (item?.selected == true) "#e34c4c" else "#00000000")
                    (viewHolder.itemView as BallTextView).text = item?.data
                }
            }
        redGridView.adapter = redAdapter
        blueGridView.setOnItemClickListener { _, _, position, _ ->
            val item = blueAdapter?.getItem(position)
            item?.selected = item?.selected != true
            blueAdapter?.notifyDataSetChanged()
        }
        blueAdapter = object : com.weyee.sdk.multitype.listview.BaseAdapter<BallBean>(mPresenter.blues(lotteryId)) {
            override fun getLayoutId(position: Int): Int = R.layout.item_lottery_ball

            override fun convert(viewHolder: BaseHolder, item: BallBean?, position: Int) {
                (viewHolder.itemView as BallTextView).textColor =
                    Color.parseColor(if (item?.selected == true) "#FFFFFF" else "#158ad2")
                (viewHolder.itemView as BallTextView).strokeColor =
                    Color.parseColor(if (item?.selected == true) "#00000000" else "#158ad2")
                (viewHolder.itemView as BallTextView).backgroundColor =
                    Color.parseColor(if (item?.selected == true) "#158ad2" else "#00000000")
                (viewHolder.itemView as BallTextView).text = item?.data
            }
        }
        blueGridView.adapter = blueAdapter
        btnQuery.setOnClickListener {
            if ("查询" == btnQuery.text) {
                val result = mPresenter.lotteryRes(
                    lotteryId,
                    redAdapter?.all,
                    blueAdapter?.all
                )
                if (!TextUtils.isEmpty(result)) {
                    mPresenter.bonus(lotteryId!!, spinnerView.getSelectedItem(), result!!)
                } else {
                    ToastUtils.show("选中的号码不符合规范")
                }
            } else {
                //随机
                mPresenter.lotteryRandom(lotteryId, redAdapter?.all, blueAdapter?.all)
                redAdapter?.notifyDataSetChanged()
                blueAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        if (lotteryNo != null) {
            fl_filter.visibility = View.VISIBLE
            btnQuery.text = "查询"
            spinnerView.setItems(mPresenter.periods(lotteryNo!!))
            spinnerView.setOnItemSelectedListener { _, _, _, item ->
                lotteryNo = item as String?
            }
            // 模拟点击
            btnQuery.performClick()
        } else {
            fl_filter.visibility = View.GONE
            btnQuery.text = "生成"
        }
    }

    override fun onCompleted() {
    }
}
