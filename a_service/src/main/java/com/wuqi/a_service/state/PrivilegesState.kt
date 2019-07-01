package com.wuqi.a_service.state

import android.view.View
import android.widget.Button
import com.weyee.poswidget.stateview.state.BaseState
import com.weyee.poswidget.stateview.state.StateProperty
import com.weyee.sdk.permission.PermissionIntents
import com.wuqi.a_service.R

/**
 *
 * @author wuqi by 2019-07-01.
 */
class PrivilegesState : BaseState<StateProperty>() {
    companion object {
        const val STATE = "PrivilegesState"
    }

    override fun getState(): String = STATE

    override fun getLayoutId(): Int = R.layout.layout_orivileges

    override fun onViewCreated(stateView: View?) {
        stateView?.findViewById<Button>(R.id.btnOpen)?.setOnClickListener {
            PermissionIntents.toPermissionSetting(context)
        }
    }
}