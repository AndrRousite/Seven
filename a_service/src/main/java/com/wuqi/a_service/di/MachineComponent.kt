package com.wuqi.a_service.di

import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.di.scope.ActivityScope
import com.wuqi.a_service.MachineActivity
import dagger.Component

/**
 *
 * @author wuqi by 2019/4/12.
 */
@ActivityScope
@Component(modules = [LotteryModule::class], dependencies = [AppComponent::class])
interface MachineComponent {
    fun inject(activity: MachineActivity)
}