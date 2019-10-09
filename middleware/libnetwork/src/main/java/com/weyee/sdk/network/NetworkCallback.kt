package com.weyee.sdk.network

import android.content.Context

/**
 * @author wuqi by 2019-10-09.
 */
interface NetworkCallback {
    fun post(type: Type)

    /**
     * 这个方法可以删除
     */
    fun register(observer: Any)

    /**
     * 这个方法可以删除
     */
    fun unregister(observer: Any)

    /**
     * 这个方法可以删除，通过RxBus代替事件分发
     */
    fun unregisterAll()

    fun netType(context: Context?): Type = Type.NONE
}