package com.weyee.sdk.network

import java.lang.reflect.InvocationTargetException


/**
 *
 * @author wuqi by 2019-10-09.
 */
object Invoke {
    /**
     * 遍历注册类中的所有方法，收集被注解方法的信息
     * @param observer
     * @return
     */
    fun getAnnotationMethod(observer: Any): MutableList<MethodManager> {
        val methodList = mutableListOf<MethodManager>()
        val methods = observer.javaClass.methods
        for (method in methods) {
            val network = method.getAnnotation(Network::class.java) ?: continue
            //校验返回值
            val returnType = method.genericReturnType
            if ("void" != returnType.toString()) {
                throw RuntimeException(method.name + "return type should be null")
            }
            //校验参数
            val parameterTypes = method.parameterTypes
            if (parameterTypes.size != 1) {
                throw RuntimeException(method.name + "arguments should be one")
            }

            val methodManager = MethodManager(
                parameterTypes[0],
                network.netType, method
            )
            methodList.add(methodManager)
        }
        return methodList
    }

    fun invoke(methodManager: MethodManager, observer: Any, netType: Type) {
        try {
            val execute = methodManager.method
            execute.invoke(observer, netType)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }
}