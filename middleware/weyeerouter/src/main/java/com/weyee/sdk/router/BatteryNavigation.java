package com.weyee.sdk.router;

import android.content.Context;

/**
 * @author wuqi by 2019/4/3.
 */
public class BatteryNavigation extends Navigation {

    private static final String MODULE_NAME = Path.Battery;

    public BatteryNavigation(Context context) {
        super(context);
    }

    /**
     * 配置Module
     */
    @Override
    protected String getModuleName() {
        return MODULE_NAME;
    }

    public void toBatteryActivity() {
        startActivity("Battery");
    }
}
