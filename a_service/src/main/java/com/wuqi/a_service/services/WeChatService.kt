package com.wuqi.a_service.services

import com.wuqi.a_service.wan.LotteryResponse
import com.wuqi.a_service.wan.WechatInfo
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @author wuqi by 2019/4/17.
 */
interface WeChatService {

    companion object {
        const val baseUrl: String = "http://v.juhe.cn/"

        const val ApiKey: String = "3135a3313074d4a4ded8d8a164f16634"
    }

    /**
     * 获取微信精选
     */
    @GET("weixin/query?key=$ApiKey")
    fun wechats(
        @Query(value = "pno") page: Int, @Query(value = "ps") pageSize: Int
    ): Observable<LotteryResponse<WechatInfo>>

}