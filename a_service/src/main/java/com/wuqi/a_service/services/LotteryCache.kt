package com.wuqi.a_service.services

import com.wuqi.a_service.wan.cache.*
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictProvider
import io.rx_cache2.LifeCache
import java.util.concurrent.TimeUnit

/**
 *
 * @author wuqi by 2019/4/17.
 */
interface LotteryCache {

    /**
     *  种类
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    fun lotterys(
        category: Observable<CategoryCache>,
        dynamicKey: DynamicKey,
        evictProvider: EvictProvider
    ): Observable<CategoryCache>

    /**
     * 当期开奖结果查询
     */
    @LifeCache(duration = 5, timeUnit = TimeUnit.HOURS)
    fun infos(
        info: Observable<InfoCache>,
        dynamicKey: DynamicKey,
        evictProvider: EvictProvider
    ): Observable<InfoCache>

    /**
     * 历史开奖结果列表
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    fun historys(
        history: Observable<HistoryCache>,
        dynamicKey: DynamicKey,
        evictProvider: EvictProvider
    ): Observable<HistoryCache>

    /**
     * 是否中
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    fun bonus(
        history: Observable<BounsCache>,
        dynamicKey: DynamicKey,
        evictProvider: EvictProvider
    ): Observable<BounsCache>

    /**
     * 微信精选
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    fun wechats(
        wecahts: Observable<WechatCache>,
        dynamicKey: DynamicKey,
        evictProvider: EvictProvider
    ): Observable<WechatCache>

}