package com.wuqi.a_service.wan

import com.weyee.poscore.mvp.IModel
import com.weyee.poscore.mvp.IView
import io.reactivex.Observable

/**
 *
 * @author wuqi by 2019/4/17.
 */
interface LotteryContract {
    interface View : IView {
        fun onCompleted()
    }

    interface LotteryView : View {
        fun setHomeData(list: List<LotteryWapperCategoryAndInfo>?)
    }

    interface MachineView : View
    interface DetailView : View {
        fun setHeadData(lottery_no: String?, lottery_date: String?, data: List<BallBean>?)
        fun setInfoData(data: List<Any>?)
    }

    interface WechatView : View {
        fun setInfoData(data: WechatInfo?)
    }

    interface Model : IModel {
        fun lotterys(): Observable<List<LotteryCategory>>

        fun infos(lottery_id: String?, lottery_no: String?): Observable<LotteryInfo>

        fun historys(lottery_id: String, page: Int, page_size: Int): Observable<LotteryHistory>

        fun bonus(lottery_id: String, lottery_no: String, lottery_res: String): Observable<LotteryBonus>

        fun wechats(page: Int, page_size: Int): Observable<WechatInfo>
    }
}