package com.weyee.sdk.api.observer;

import android.content.Context;
import com.weyee.sdk.api.bean.HttpResponse;
import com.weyee.sdk.api.exception.ApiException;
import com.weyee.sdk.api.observer.listener.ProgressAble;
import io.reactivex.disposables.Disposable;

/**
 * <p>
 *
 * @author wuqi
 * @describe ...
 * @date 2018/12/7 0007
 */
public abstract class ProgressSubscriber<T> extends RxSubscriber<T> {

    private ProgressAble progressAble;

    public ProgressSubscriber() {
    }

    public ProgressSubscriber(ProgressAble progressAble) {
        this.progressAble = progressAble;
    }

    public ProgressSubscriber(Context progressAble) {
        if (progressAble instanceof ProgressAble) {
            this.progressAble = (ProgressAble) progressAble;
        }
    }

    @Override
    public void doOnSubscribe(Disposable d) {
        super.doOnSubscribe(d);
        if (progressAble != null) {
            progressAble.showProgress();
        }
    }

    @Override
    public void doOnCompleted() {
        super.doOnCompleted();
        if (progressAble != null) {
            progressAble.hideProgress();
        }
    }

    @Override
    public void doOnNext(T tHttpResponse) {
        if (tHttpResponse instanceof HttpResponse) {
            if (((HttpResponse) tHttpResponse).getStatus() == 1) {
                onSuccess(tHttpResponse);
            } else {
                onError(ApiException.handleException(((HttpResponse) tHttpResponse).getStatus(), ((HttpResponse) tHttpResponse).getError()));
            }
        } else {
            super.doOnNext(tHttpResponse);
        }
    }
}
