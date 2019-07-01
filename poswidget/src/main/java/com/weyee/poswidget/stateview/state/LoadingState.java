package com.weyee.poswidget.stateview.state;

import android.view.View;
import com.weyee.poswidget.R;

/**
 * @author wuqi by 2019-07-01.
 */
public class LoadingState extends BaseState<StateProperty> {
    public static final String STATE = "LoadingState";

    @Override
    protected int getLayoutId() {
        return R.layout.loading_state;
    }

    @Override
    protected void onViewCreated(View stateView) {

    }

    @Override
    public String getState() {
        return STATE;
    }
}
