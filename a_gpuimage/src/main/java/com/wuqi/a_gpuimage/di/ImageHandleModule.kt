package com.wuqi.a_gpuimage.di

import com.weyee.poscore.di.scope.ActivityScope
import com.weyee.poscore.mvp.IView
import dagger.Module
import dagger.Provides

/**
 *
 * @author wuqi by 2019/4/12.
 */
@Module
class ImageHandleModule(val view: IView) {
    @ActivityScope
    @Provides
    fun provideView(): IView = view

}