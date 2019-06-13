package com.wuqi.a_service

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.SizeUtils
import com.weyee.poscore.base.App
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.sdk.imageloader.ImageLoader
import com.weyee.sdk.imageloader.glide.GlideImageConfig
import com.weyee.sdk.multitype.BaseAdapter
import com.weyee.sdk.multitype.BaseHolder
import com.weyee.sdk.multitype.HorizontalDividerItemDecoration
import com.weyee.sdk.router.BrowserNavigation
import com.weyee.sdk.router.Path
import com.wuqi.a_service.di.DaggerWeChatSelectionComponent
import com.wuqi.a_service.di.LotteryModule
import com.wuqi.a_service.util.ColorGenerator
import com.wuqi.a_service.util.TextDrawable
import com.wuqi.a_service.wan.LotteryContract
import com.wuqi.a_service.wan.LotteryPresenter
import com.wuqi.a_service.wan.WechatInfo
import com.wuqi.a_service.wan.WechatItem
import kotlinx.android.synthetic.main.activity_lottery.*


/**
 * 微信精选
 */
@Route(path = Path.Service + "WeChatSelection")
class WeChatActivity : BaseActivity<LotteryPresenter>(), LotteryContract.WechatView {
    private var pageIndex: Int = 0
    private var imageloader: ImageLoader? = null

    override fun setupActivityComponent(appComponent: AppComponent?) {
        DaggerWeChatSelectionComponent
            .builder()
            .appComponent(appComponent)
            .lotteryModule(LotteryModule(this))
            .build()
            .inject(this@WeChatActivity)
    }

    override fun getResourceId(): Int = R.layout.activity_wechat_selection

    override fun initView(savedInstanceState: Bundle?) {
        headerView.setTitle("微信精选")
        refreshView.autoRefresh()
        refreshView.setOnRefreshListener {
            pageIndex = 1
            mPresenter.wechats(pageIndex, 12)
        }
        refreshView.setOnLoadMoreListener {
            pageIndex++
            mPresenter.wechats(pageIndex, 12)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context).margin(SizeUtils.dp2px(16f)).size(
                1
            ).build()
        )
        recyclerView.adapter =
            object : BaseAdapter<WechatItem>(null, { _, _, data, _ ->
                BrowserNavigation(this).toBrowserActivity(data.url)
            }) {
                override fun getHolder(v: View, viewType: Int): BaseHolder<WechatItem> {
                    return object : BaseHolder<WechatItem>(v) {
                        override fun setData(data: WechatItem, position: Int) {
                            if (TextUtils.isEmpty(data.source) || !(data.source.contains("http://") || data.source.contains(
                                    "https://"
                                ))
                            ) {
                                val builder = TextDrawable.Builder()
                                    .buildRound(data.source, ColorGenerator.MATERIAL.getRandomColor())
                                getView<ImageView>(R.id.ivIcon).setImageDrawable(builder)
                            } else {
                                imageloader?.loadImage(
                                    context, GlideImageConfig.builder()
                                        .imageView(getView(R.id.ivIcon))
                                        .resource(data.firstImg)
                                        .isCircle(true)
                                        .build()
                                )
                            }
                            getView<TextView>(R.id.tvTitle).text = data.source
                            getView<TextView>(R.id.tvDesc).text = data.title
                        }

                    }
                }

                override fun getLayoutId(viewType: Int): Int = R.layout.item_wechat

            }
    }

    override fun initData(savedInstanceState: Bundle?) {
        imageloader = (context.applicationContext as App).appComponent.imageLoader()
    }

    override fun useProgressAble(): Boolean {
        return !super.useProgressAble()
    }

    override fun setInfoData(data: WechatInfo?) {
        headerView.setTitle(String.format("微信精选(%d)", data?.totalPage ?: 0))
        (recyclerView.adapter as BaseAdapter<WechatItem>).addAll(data?.list, pageIndex <= 1)
    }


    override fun onCompleted() {
        refreshView.finishRefresh()
        refreshView.finishLoadMore()
    }
}
