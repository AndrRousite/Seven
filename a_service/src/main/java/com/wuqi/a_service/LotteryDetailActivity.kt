package com.wuqi.a_service

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.appbar.AppBarLayout
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poswidget.textview.ball.BallTextView
import com.weyee.sdk.multitype.BaseAdapter
import com.weyee.sdk.multitype.BaseHolder
import com.weyee.sdk.multitype.HorizontalDividerItemDecoration
import com.weyee.sdk.router.Path
import com.weyee.sdk.router.WorkerNavigation
import com.wuqi.a_service.di.DaggerLotteryDetailComponent
import com.wuqi.a_service.di.LotteryModule
import com.wuqi.a_service.wan.*
import kotlinx.android.synthetic.main.activity_lottery_detail.*

@Route(path = Path.Service + "LotteryDetail")
class LotteryDetailActivity : BaseActivity<LotteryPresenter>(), LotteryContract.DetailView,
    AppBarLayout.OnOffsetChangedListener {

    private var lottery_no: String? = null
    private var lotteryId: String? = null
    private var state: State? = null
    private val titles = arrayOf("开奖详情", "下期预测", "热议")
    private var mAdapter: BaseAdapter<Any>? = null

    override fun setupActivityComponent(appComponent: AppComponent?) {
        DaggerLotteryDetailComponent
            .builder()
            .appComponent(appComponent)
            .lotteryModule(LotteryModule(this))
            .build()
            .inject(this@LotteryDetailActivity)
    }

    override fun getResourceId(): Int = R.layout.activity_lottery_detail

    override fun initView(savedInstanceState: Bundle?) {
        lotteryId = intent.getStringExtra("lottery_id")
        lottery_no = intent.getStringExtra("lottery_no")

        appbarLayout.addOnOffsetChangedListener(this)
        tvBetting.setOnClickListener {
            WorkerNavigation(context).toMachineActivity(
                lotteryId,
                null
            )
        }
        for ((i, item) in titles.withIndex()) {
            tabLayout.addTab(tabLayout.newTab())
            tabLayout.getTabAt(i)?.text = item
        }
        refreshView.setEnableLoadMore(false)
        refreshView.setOnRefreshListener {
            mPresenter.infos(lotteryId, lottery_no)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(context).margin(SizeUtils.dp2px(16f)).build())
        mAdapter = object : BaseAdapter<Any>(null) {
            override fun getHolder(v: View, viewType: Int): BaseHolder<Any> {
                return object : BaseHolder<Any>(v) {
                    override fun setData(data: Any, position: Int) {
                        val viewType = getItemViewType(position)
                        if (viewType == 0) {
                            getView<TextView>(R.id.tvName).text = (data as LotteryPrize).prize_name
                            getView<TextView>(R.id.tvRequire).text = data.prize_require
                            getView<TextView>(R.id.tvNum).text = data.prize_num
                            getView<TextView>(R.id.tvAmount).text = data.prize_money
                        } else {
                            val sale = mPresenter.calculatedAmount((data as PrizeBean).lottery_sale_amount)
                            val prize = mPresenter.calculatedAmount(data.lottery_prize_amount)
                            val pool = mPresenter.calculatedAmount(data.lottery_pool_amount)
                            getView<TextView>(R.id.tvSaleAmount).text = SpanUtils()
                                .append(sale[0])
                                .setFontSize(20, true).setForegroundColor(resources.getColor(R.color.cl_333333))
                                .appendLine(sale[1])
                                .setFontSize(14, true).setForegroundColor(resources.getColor(R.color.cl_999999))
                                .appendLine("本期销量")
                                .setFontSize(12, true).setForegroundColor(resources.getColor(R.color.cl_999999))
                                .create()
                            getView<TextView>(R.id.tvPrizeAmount).text = SpanUtils()
                                .append(prize[0])
                                .setFontSize(20, true).setForegroundColor(resources.getColor(R.color.cl_333333))
                                .appendLine(prize[1])
                                .setFontSize(14, true).setForegroundColor(resources.getColor(R.color.cl_999999))
                                .appendLine("中奖合计")
                                .setFontSize(12, true).setForegroundColor(resources.getColor(R.color.cl_999999))
                                .create()
                            getView<TextView>(R.id.tvPoolAmount).text = SpanUtils()
                                .append(pool[0])
                                .setFontSize(20, true).setForegroundColor(resources.getColor(R.color.cl_333333))
                                .appendLine(pool[1])
                                .setFontSize(14, true).setForegroundColor(resources.getColor(R.color.cl_999999))
                                .appendLine("奖池滚存")
                                .setFontSize(12, true).setForegroundColor(resources.getColor(R.color.cl_999999))
                                .create()
                        }
                    }
                }
            }

            override fun getItemViewType(position: Int): Int {
                return if (getItem(position) is PrizeBean) 1 else super.getItemViewType(position)
            }

            override fun getLayoutId(viewType: Int): Int =
                if (viewType == 1) R.layout.item_parize_header else R.layout.item_parize

        }
        recyclerView.adapter = mAdapter

        gridView.adapter =
            object : com.weyee.sdk.multitype.listview.BaseAdapter<BallBean>(null) {
                override fun getLayoutId(position: Int): Int = R.layout.item_lottery_ball

                override fun convert(
                    viewHolder: com.weyee.sdk.multitype.listview.BaseHolder,
                    item: BallBean?,
                    position: Int
                ) {
                    (viewHolder.itemView as BallTextView).text = item?.data
                    (viewHolder.itemView as BallTextView).textColor = Color.parseColor(item?.textColor)
                    (viewHolder.itemView as BallTextView).backgroundColor = Color.parseColor(item?.backgroundColor)
                }

            }
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPresenter.infos(lotteryId, lottery_no)
    }

    override fun useProgressAble(): Boolean {
        return !super.useProgressAble()
    }

    override fun setHeadData(lottery_no: String?, lottery_date: String?, data: List<BallBean>?) {
        tvHead.text = String.format("%s期 %s", lottery_no, lottery_date)
        (gridView.adapter as com.weyee.sdk.multitype.listview.BaseAdapter<BallBean>).addAll(data, true)
    }

    override fun setInfoData(data: List<Any>?) {
        mAdapter?.addAll(data, true)
    }

    override fun onCompleted() {
        refreshView.finishRefresh()
    }

    override fun onOffsetChanged(p0: AppBarLayout?, verticalOffset: Int) {
        if (verticalOffset == 0) {
            if (state != State.EXPANDED) {
                state = State.EXPANDED//修改状态标记为展开
            }
        } else if (Math.abs(verticalOffset) >= appbarLayout.totalScrollRange) {
            if (state != State.COLLAPSED) {
                // TODO
                state = State.COLLAPSED//修改状态标记为折叠
            }
        } else {
            if (state != State.INTERNEDIATE) {
                if (state == State.COLLAPSED) {
                    //TODO
                }
                state = State.INTERNEDIATE//修改状态标记为中间
            }
        }

    }

    /**
     * 展开、折叠、中间三种状态
     */
    private enum class State {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }
}
