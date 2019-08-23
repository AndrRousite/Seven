package com.wuqi.a_gpuimage

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Route
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.dialog.ChooseDialog
import com.weyee.sdk.dialog.MenuDialog
import com.weyee.sdk.imageloader.glide.GlideApp
import com.weyee.sdk.multitype.BaseAdapter
import com.weyee.sdk.multitype.BaseHolder
import com.weyee.sdk.router.Path
import com.weyee.sdk.toast.ToastUtils
import com.wuqi.a_gpuimage.di.DaggerImageHandleComponent
import com.wuqi.a_gpuimage.di.ImageHandleModule
import com.wuqi.a_gpuimage.di.ImageHandlePresenter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.android.synthetic.main.activity_image_handle.*
import java.io.File
import java.util.*
import kotlin.concurrent.thread

/**
 * 图片处理
 */
@Route(path = Path.GPU + "ImageHandle")
class ImageHandleActivity : BaseActivity<ImageHandlePresenter>(), IView {

    private lateinit var rightDialog: MenuDialog
    private val array = arrayOf(
        "http://image.stage.yiminct.com//weyee_test_2018_20190619155446819952",
        "http://image.stage.yiminct.com//weyee_test_2018_20190621113214905859"
    )

    override fun setupActivityComponent(appComponent: AppComponent?) {
        DaggerImageHandleComponent
            .builder()
            .appComponent(appComponent)
            .imageHandleModule(ImageHandleModule(this))
            .build()
            .inject(this@ImageHandleActivity)
    }

    override fun getResourceId(): Int = R.layout.activity_image_handle

    override fun initView(savedInstanceState: Bundle?) {
        headerView.setTitle("图片变换")
        headerView.isShowMenuRightOneView(true)
        headerView.isShowMenuRightTwoView(true)
        headerView.isShowMenuRightThreeView(true)
        headerView.setMenuRightOneIcon(R.drawable.ic_more_vert_black_24dp)
        headerView.setMenuRightTwoIcon(R.drawable.ic_undo_black_24dp)
        headerView.setMenuRightThreeIcon(R.drawable.ic_add_a_photo_black_24dp)

        headerView.setOnClickRightMenuOneListener {
            rightDialog.show()
        }
        headerView.setOnClickRightMenuTwoListener {
            val dialog = ChooseDialog(context)
            dialog.setTitle("选择图片")

            val files = mPresenter.getFiles()

            dialog.setNewData(

                files.map {
                    ChooseDialog.ChooseItemModel(
                        false, it.absolutePath
                    )
                })

            dialog.setOnItemClickListener { model ->
                run {
                    loadImage(File(model.title))
                }
            }

            dialog.show()
        }

        headerView.setOnClickRightMenuThreeListener {
            imageView.saveToPictures("GPUImage", "${UUID.randomUUID()}.jpg", null)
        }

        loadImage(array[1])

    }

    override fun initData(savedInstanceState: Bundle?) {
        rightDialog =
            MenuDialog(context).setAdapter(object : BaseAdapter<String>(mPresenter.getFunction(), { _, _, data, _ ->
                try {
                    imageView.filter =
                        Class.forName("jp.co.cyberagent.android.gpuimage.filter.$data").newInstance() as GPUImageFilter?
                } catch (e: InstantiationException) {
                    ToastUtils.show("构造函数错误")
                } catch (e: IllegalAccessException) {
                    ToastUtils.show("私有函数禁止访问")
                }
            }) {
                override fun getHolder(v: View, viewType: Int): BaseHolder<String> {
                    return object : BaseHolder<String>(v) {
                        override fun setData(data: String, position: Int) {
                            getView<TextView>(android.R.id.text1).text = data
                        }
                    }
                }

                override fun getLayoutId(viewType: Int): Int = android.R.layout.simple_list_item_1

            })
    }

    private fun loadImage(url: String) {
        thread {
            val bmp = GlideApp.with(context).asBitmap()
                .load(url).submit().get()
            runOnUiThread {
                imageView.setImage(bmp)
            }
        }
    }

    private fun loadImage(file: File) {
        thread {
            runOnUiThread {
                imageView.setImage(file)
            }
        }
    }
}
