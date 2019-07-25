/*
 *
 *  Copyright 2017 liu-feng
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  imitations under the License.
 *
 */

package com.weyee.sdk.dialog;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

/**
 * 很友好的加载弹窗
 *
 * @author wuqi by 2019/2/22.
 */
public class LoadingDialog extends BaseDialog {
    private TextView tvTips;
    private String tips;

    public LoadingDialog(@NonNull Context context, String tips) {
        super(context, R.style.QMUI_LoadingDialog);
        this.tips = tips;
        //setCancelable(false);
        //setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.mrmo_loading_dialog, null);
        setContentView(inflate);
        setViewLocation();
        tvTips = inflate.findViewById(R.id.tvTips);
        tvTips.setText(tips);
        Palette.from(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mrmo_icon_loading))
                .generate(palette -> {
                    if (palette != null) {
                        Palette.Swatch swatch = palette.getMutedSwatch();
                        if (swatch == null){
                            swatch = palette.getVibrantSwatch();
                        }
                        if (tvTips != null && swatch != null) {
                            tvTips.setTextColor(swatch.getRgb());
                        }
                    }
                });
    }

    /**
     * 设置该dialog总是显示在最顶层(需要悬浮窗权限，且国产ROM需要适配)
     */
    protected void setViewLocation() {
        //Window window = this.getWindow();
        //Objects.requireNonNull(window).setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getResources().getString(titleId));
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        if (tvTips != null) {
            tvTips.setText(title);
        }
    }
}
