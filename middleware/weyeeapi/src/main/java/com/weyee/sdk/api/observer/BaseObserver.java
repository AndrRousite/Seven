package com.weyee.sdk.api.observer;

import androidx.annotation.NonNull;
import com.weyee.sdk.api.dispose.DisposeManager;
import com.weyee.sdk.api.exception.ApiException;
import com.weyee.sdk.api.exception.HttpException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <p>定义请求结果处理接口
 *
 * @author wuqi
 * @describe ...
 * @date 2018/12/7 0007
 */
abstract class BaseObserver<T> implements Observer<T>, ISubscriber<T> {
    /**
     * 是否显示toast
     *
     * @return
     */
    protected boolean showToast() {
        return true;
    }

    /**
     * 标记网络请求的tag
     * tag下的一组或一个请求，用来处理一个页面的所以请求或者某个请求
     * 设置一个tag就行就可以取消当前页面所有请求或者某个请求了
     *
     * @return string
     */
    protected String setTag() {
        return null;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        DisposeManager.get().add(setTag(), d);
        doOnSubscribe(d);
    }

    @Override
    public void onNext(@NonNull T t) {
        doOnNext(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();

        HttpException httpException = HttpException.handleException(e);
        doOnError(httpException.getCode(), httpException.getMessage());

        /**
         * onError和onComplete是互斥的
         */
        onComplete();
    }

    /**
     * 处理后台接口返回的错误信息，属于正常流程
     *
     * @param e
     */
    void onError(@NonNull ApiException e) {
        e.printStackTrace();
        doOnError(e.getCode(), e.getMessage());
    }


    @Override
    public void onComplete() {
        doOnCompleted();
    }
}
