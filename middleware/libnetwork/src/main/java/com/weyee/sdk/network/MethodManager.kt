package com.weyee.sdk.network

import java.lang.reflect.Method

/**
 *
 * @author wuqi by 2019-10-09.
 */
data class MethodManager(val type: Class<*>, val netType: Type, val method: Method)