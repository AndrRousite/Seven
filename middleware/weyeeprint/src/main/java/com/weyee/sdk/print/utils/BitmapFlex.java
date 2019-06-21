package com.weyee.sdk.print.utils;

import android.graphics.Bitmap;

/**
 * <p>
 * 等间采样缩放
 * 1. 放在单一色彩的图中表现较好，不会产生模糊
 * 2. 可用于放大 DATA_MATRIX 二维码
 * </p>
 *
 * @author mcgrady
 * @date 2018/5/11
 */
final class BitmapFlex {

    /**
     * 等间隔采样的图像缩放
     *
     * @param bitmap    要缩放的图像对象
     * @param dstWidth  缩放后图像的宽
     * @param dstHeight 缩放后图像的高
     * @return 返回处理后的图像对象
     */
    static Bitmap flex(Bitmap bitmap, int dstWidth, int dstHeight) {
        float wScale = (float) dstWidth / bitmap.getWidth();
        float hScale = (float) dstHeight / bitmap.getHeight();
        return flex(bitmap, wScale, hScale);
    }

    /**
     * 等间隔采样的图像缩放
     *
     * @param bitmap 要缩放的bitap对象
     * @param wScale 要缩放的横列（宽）比列
     * @param hScale 要缩放的纵行（高）比列
     * @return 返回处理后的图像对象
     */
    private static Bitmap flex(Bitmap bitmap, float wScale, float hScale) {
        if (wScale <= 0 || hScale <= 0) {
            return null;
        }
        float ii = 1 / wScale;    //采样的行间距
        float jj = 1 / hScale; //采样的列间距

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int dstWidth = (int) (wScale * width);
        int dstHeight = (int) (hScale * height);

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] dstPixels = new int[dstWidth * dstHeight];

        for (int j = 0; j < dstHeight; j++) {
            for (int i = 0; i < dstWidth; i++) {
                dstPixels[j * dstWidth + i] = pixels[(int) (jj * j) * width + (int) (ii * i)];
            }
        }

        Bitmap outBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        outBitmap.setPixels(dstPixels, 0, dstWidth, 0, 0, dstWidth, dstHeight);

        return outBitmap;
    }
}
