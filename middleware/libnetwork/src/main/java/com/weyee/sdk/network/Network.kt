package com.weyee.sdk.network

/**
 *
 * @author wuqi by 2019-10-09.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Network(val netType: Type = Type.AUTO)