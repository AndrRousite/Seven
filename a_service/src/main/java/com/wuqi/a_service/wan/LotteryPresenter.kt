package com.wuqi.a_service.wan

import cn.hutool.core.util.RandomUtil
import com.weyee.poscore.di.scope.ActivityScope
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.possupport.arch.RxLiftUtils
import com.weyee.sdk.api.observer.ProgressSubscriber
import com.weyee.sdk.api.observer.transformer.Transformer
import com.weyee.sdk.toast.ToastUtils
import com.weyee.sdk.util.number.MNumberUtil
import com.weyee.sdk.util.number.MPriceUtil
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import javax.inject.Inject

/**
 *
 * @author wuqi by 2019/4/17.
 */
@ActivityScope
class LotteryPresenter @Inject constructor(model: LotteryModel?, rootView: LotteryContract.View?) :
    BasePresenter<LotteryContract.Model, LotteryContract.View>(model, rootView) {

    fun home() {
        mModel.lotterys()
            .flatMap(Function<List<LotteryCategory>, ObservableSource<List<LotteryWapperCategoryAndInfo>>> {
                return@Function Observable.zip(it.map { category -> mModel.infos(category.lottery_id, null) }
                ) { infos ->
                    infos.map { info ->
                        info as LotteryInfo
                        LotteryWapperCategoryAndInfo(
                            it.find { predicate -> info.lottery_id == predicate.lottery_id && info.lottery_name == predicate.lottery_name }!!,
                            info
                        )
                    }
                }
            })
            .compose(Transformer.switchSchedulers())
            .`as`(RxLiftUtils.bindLifecycle(lifecycleOwner))
            .subscribe(object : ProgressSubscriber<List<LotteryWapperCategoryAndInfo>>() {
                override fun onSuccess(t: List<LotteryWapperCategoryAndInfo>?) {
                    if (mView is LotteryContract.LotteryView) {
                        (mView as LotteryContract.LotteryView).setHomeData(t)
                    }
                }

                override fun onCompleted() {
                    super.onCompleted()
                    mView.onCompleted()
                }

            })

    }

    fun lotterys() {
        mModel.lotterys()
            .compose(Transformer.switchSchedulers(progressAble))
            .`as`(RxLiftUtils.bindLifecycle(lifecycleOwner))
            .subscribe(object : ProgressSubscriber<List<LotteryCategory>>() {
                override fun onSuccess(t: List<LotteryCategory>?) {
                }

            })
    }

    fun infos(lottery_id: String?, lottery_no: String?) {
        mModel.infos(lottery_id, lottery_no)
            .compose(Transformer.switchSchedulers(progressAble))
            .`as`(RxLiftUtils.bindLifecycle(lifecycleOwner))
            .subscribe(object : ProgressSubscriber<LotteryInfo>() {
                override fun onSuccess(t: LotteryInfo?) {
                    if (mView is LotteryContract.DetailView) {
                        val list = mutableListOf<BallBean>()
                        val balls = t?.lottery_res?.split(",")
                        when (lottery_id) {
                            "ssq" -> balls?.forEachIndexed { index, s ->
                                list.add(
                                    BallBean(
                                        s,
                                        false,
                                        "#FFFFFF",
                                        if (index < balls.size - 1) "#e34c4c" else "#158ad2"
                                    )
                                )
                            }
                            "dlt"
                            -> balls?.forEachIndexed { index, s ->
                                list.add(
                                    BallBean(
                                        s,
                                        false,
                                        "#FFFFFF",
                                        if (index < balls.size - 2) "#e34c4c" else "#158ad2"
                                    )
                                )
                            }
                            "qlc" -> balls?.forEachIndexed { index, s ->
                                list.add(
                                    BallBean(
                                        s,
                                        false,
                                        "#FFFFFF",
                                        if (index < balls.size - 1) "#e34c4c" else "#158ad2"
                                    )
                                )
                            }
                            else -> balls?.forEachIndexed { index, s ->
                                list.add(
                                    BallBean(
                                        s,
                                        false,
                                        "#FFFFFF",
                                        "#e34c4c"
                                    )
                                )
                            }
                        }
                        (mView as LotteryContract.DetailView).setHeadData(t?.lottery_no, t?.lottery_date, list)
                        val tempList = mutableListOf<Any>()
                        tempList.add(PrizeBean(t?.lottery_sale_amount, null, t?.lottery_pool_amount))
                        tempList.add(LotteryPrize("单注奖金(元)", "奖项", "中奖注数", "中奖条件"))
                        tempList.addAll(t?.lottery_prize ?: emptyList())
                        (mView as LotteryContract.DetailView).setInfoData(tempList)
                    }
                }

                override fun onCompleted() {
                    super.onCompleted()
                    mView.onCompleted()
                }

            })
    }

    fun historys(lottery_id: String, page: Int = 1, page_size: Int = 10) {
        mModel.historys(lottery_id, page, page_size)
            .compose(Transformer.switchSchedulers(progressAble))
            .`as`(RxLiftUtils.bindLifecycle(lifecycleOwner))
            .subscribe(object : ProgressSubscriber<LotteryHistory>() {
                override fun onSuccess(t: LotteryHistory?) {
                }

            })
    }

    fun bonus(lottery_id: String, lottery_no: String, lottery_res: String) {
        mModel.bonus(lottery_id, lottery_no, lottery_res)
            .compose(Transformer.switchSchedulers(progressAble))
            .`as`(RxLiftUtils.bindLifecycle(lifecycleOwner))
            .subscribe(object : ProgressSubscriber<LotteryBonus>() {
                override fun onSuccess(t: LotteryBonus?) {
                    ToastUtils.show(t?.prize_msg)
                }
            })
    }

    /**
     * 获取期数信息
     */
    fun periods(lastPeriod: String): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until 30) {
            val data = MNumberUtil.convertToint(lastPeriod) - i
            list.add("$data")
        }
        return list
    }

    /**
     * 根据lottery_id 生成红球
     */
    fun reds(lottery_id: String?): List<BallBean> {
        val list = mutableListOf<BallBean>()
        when (lottery_id) {
            "ssq" -> {
                for (i in 1..33) {
                    list.add(BallBean(String.format("%2d", i), false))
                }
            }
            "dlt" -> {
                for (i in 1..35) {
                    list.add(BallBean(String.format("%2d", i), false))
                }
            }
        }
        return list
    }

    /**
     * 根据lottery_id 生成篮球
     */
    fun blues(lottery_id: String?): List<BallBean> {
        val list = mutableListOf<BallBean>()
        when (lottery_id) {
            "ssq" -> {
                for (i in 1..16) {
                    list.add(BallBean(String.format("%2d", i), false))
                }
            }
            "dlt" -> {
                for (i in 1..12) {
                    list.add(BallBean(String.format("%2d", i), false))
                }
            }
        }
        return list
    }

    /**
     * 查询是否中奖
     */
    fun lotteryRes(lottery_id: String?, reds: List<BallBean>?, blues: List<BallBean>?): String? {
        val tempReds = reds?.filter { it.selected }
        val tempBlues = blues?.filter { it.selected }
        when (lottery_id) {
            "ssq" -> {

                if (tempReds?.size ?: 0 >= 6 && tempBlues?.isNotEmpty() == true) {
                    val sb = StringBuilder()
                    tempReds?.forEach { sb.append(it.data).append(",") }
                        .apply { if (sb.isNotEmpty()) sb.delete(sb.length - 1, sb.length).append("@") }

                    tempBlues.forEach { sb.append(it.data).append(",") }
                        .apply { if (sb.isNotEmpty()) sb.delete(sb.length - 1, sb.length) }
                    return sb.toString()
                }
            }
            "dlt" -> {
                if (tempReds?.size ?: 0 >= 5 && tempBlues?.size ?: 0 >= 2) {
                    val sb = StringBuilder()
                    tempReds?.forEach { sb.append(it.data).append(",") }
                        .apply { if (sb.isNotEmpty()) sb.delete(sb.length - 1, sb.length).append("@") }

                    tempBlues?.forEach { sb.append(it.data).append(",") }
                        .apply { if (sb.isNotEmpty()) sb.delete(sb.length - 1, sb.length) }
                    return sb.toString()
                }
            }
        }
        return null

    }

    fun lotteryRandom(lottery_id: String?, reds: List<BallBean>?, blues: List<BallBean>?) {
        reds?.forEach { it.selected = false }
        blues?.forEach { it.selected = false }
        when (lottery_id) {
            "ssq" -> {
                RandomUtil.randomEles(reds, 6).forEach { it.selected = true }
                RandomUtil.randomEles(blues, 1).forEach { it.selected = true }
            }
            "dlt" -> {
                RandomUtil.randomEles(reds, 5).forEach { it.selected = true }
                RandomUtil.randomEles(blues, 2).forEach { it.selected = true }
            }
        }
    }

    /**
     * 计算金钱，保留单位，并保留小数点后两位
     */
    fun calculatedAmount(data: String?): Array<String> {
        val yi = 10000000f  // 1千万
        val qian = 1000f  // 1千

        var amount = MNumberUtil.convertToDouble(MPriceUtil.filterPriceUnit(data ?: "0"))

        val unit: String

        when {
            amount > yi -> {
                unit = "亿"
                amount /= yi * 10
            }
            amount > qian -> {
                unit = "万"
                amount /= qian * 10
            }
            else -> unit = "元"
        }

        return arrayOf(String.format("%.2f", amount), unit)
    }
}