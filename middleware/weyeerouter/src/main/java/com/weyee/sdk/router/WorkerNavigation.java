package com.weyee.sdk.router;

import android.content.Context;
import android.os.Bundle;

/**
 * @author wuqi by 2019/4/17.
 */
public class WorkerNavigation extends Navigation {
    public WorkerNavigation(Context context) {
        super(context);
    }

    @Override
    protected String getModuleName() {
        return Path.Service;
    }

    public void toWorkerActivity() {
        startActivity("Worker");
    }

    public void toWanActivity() {
        startActivity("Wan");
    }

    public void toLocationActivity() {
        startActivity("Location");
    }

    public void toBitmapActivity() {
        startActivity("Bitmap");
    }

    public void toDetailActivity(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        startActivity("Detail", bundle);
    }

    public void toUILayoutActivity() {
        startActivity("UILayout");
    }

    public void toTabLayoutActivity() {
        startActivity("TabLayout");
    }

    public void toTabHostActivity() {
        startActivity("TabHost");
    }

    public void toLotteryActivity() {
        startActivity("Lottery");
    }

    public void toMachineActivity(String lottery_id, String lottery_no) {
        Bundle bundle = new Bundle();
        bundle.putString("lottery_no", lottery_no);
        bundle.putString("lottery_id", lottery_id);
        startActivity("Machine", bundle);
    }

    public void toLotteryDetailActivity(String lottery_id, String lottery_no) {
        Bundle bundle = new Bundle();
        bundle.putString("lottery_no", lottery_no);
        bundle.putString("lottery_id", lottery_id);
        startActivity("LotteryDetail", bundle);
    }

    public void toWeChatSelectionActivity() {
        startActivity("WeChatSelection");
    }

    public void toBluetoothActivity() {
        startActivity("Bluetooth");
    }

    public void toNetworkActivity() {
        startActivity("Network");
    }
}
