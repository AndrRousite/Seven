package com.weyee.sdk.print.utils;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

/**
 * @author wuqi by 2019-06-15.
 */
public class Utils {
    private static final String special = "~!#%^&*=+\\|{};:'\\\",<>/?○●★☆☉♀♂※¤╬の〆×";

    /**
     * 判断是否中文
     * GENERAL_PUNCTUATION 判断中文的“号
     * CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
     * HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
     *
     * @param c 字符
     * @return 是否中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 判断是否为西文
     *
     * @param c
     * @return
     */
    public static boolean isCyrillic(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CYRILLIC;
    }

    /**
     * 是否是特殊字符
     *
     * @param c
     * @return
     */
    public static boolean isSpecial(char c) {
        for (int j = 0; j < special.length(); j++) {
            if (c == special.charAt(j)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理文件目录下的图片
     *
     * @param filepath
     * @return
     */
    public static Bitmap decodeFile(String filepath) {
        Bitmap bitmap;
        try {
            int width;
            int height;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filepath, options);
            width = options.outWidth;
            height = options.outHeight;
            if (width <= 0 || height <= 0)
                return null;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(filepath, options);
        } catch (OutOfMemoryError | Exception e) {
            return null;
        }
        return bitmap;
    }

    /**
     * 缩放图片
     *
     * @param image    图片
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    public static Bitmap scalingBitmap(Bitmap image, int maxWidth) {
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0)
            return null;
        try {
            final int width = image.getWidth();
            final int height = image.getHeight();
            // 精确缩放
            float scale = 1;
            if (maxWidth <= 0 || width <= maxWidth) {
                scale = maxWidth / (float) width;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 缩放图片
     *
     * @param drawable 图片
     * @param maxWidth 最大宽
     * @return 缩放后的图片
     */
    public static Bitmap scalingDrawable(Drawable drawable, int maxWidth) {
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0)
            return null;
        final int width = drawable.getIntrinsicWidth();
        final int height = drawable.getIntrinsicHeight();
        try {
            Bitmap image = Bitmap.createBitmap(width, height,
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(image);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            // 精确缩放
            if (maxWidth <= 0 || width <= maxWidth) {
                return image;
            }
            final float scale = maxWidth / (float) width;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizeImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
            image.recycle();
            return resizeImage;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 横向拼接二维码
     * 注意：拼接后bitmap的长度有可能超出纸张长度，若显示不出或有异常请调整单个bitmap的大小
     *
     * @param bitmapMap 单个元素bitmap的集合，Key为{@link android.view.Gravity#LEFT,android.view.Gravity#CENTER,android.view.Gravity#RIGHT}
     * @param pageWidth 纸张长度
     * @return
     */
    public static Bitmap transSpliceQrcode(SparseArray<Bitmap> bitmapMap, int pageWidth) {
        if (bitmapMap == null || bitmapMap.size() <= 0) {
            LogUtils.e("二维码元素为空");
            return null;
        }

        int resultHeight = 0;
        int resultWidth = 0;
        for (int i = 0; i < bitmapMap.size(); i++) {
            Bitmap bitmap = bitmapMap.valueAt(i);
            if (bitmap != null) {
                resultHeight = Math.max(resultHeight, bitmap.getHeight());
                resultWidth = Math.max(resultWidth, bitmap.getWidth());
            }
        }
        Bitmap resultBitmap = Bitmap.createBitmap(pageWidth, resultHeight, Bitmap.Config.ARGB_8888);
        resultBitmap.eraseColor(0);  // 将图片背景置为透明

        // 单个元素最大宽度
        int elementMaxWidth = pageWidth / bitmapMap.size();
        Canvas canvas = new Canvas(resultBitmap);

        for (int i = 0; i < bitmapMap.size(); i++) {
            Bitmap bitmap = bitmapMap.get(i);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                matrix.setTranslate((elementMaxWidth * i) + (elementMaxWidth - bitmap.getWidth()) / 2f, 0);
                canvas.drawBitmap(bitmap, matrix, null);
            }
        }

        return resultBitmap;
    }

    /**
     * 生成二维码图片，底部带备注信息
     *
     * @param qrcode         二维码的信息
     * @param widthAndHeight 只支持正方形的二维码，宽高一致
     * @param remark         备注信息
     * @return
     */
    public static Bitmap createQrcode(String qrcode, int widthAndHeight, String remark) {
        return createBarcode(qrcode, widthAndHeight, widthAndHeight, BarcodeFormat.QR_CODE, remark, 24, 8);
    }


    /**
     * 生成条码码图片，底部带备注信息
     *
     * @param barcode  条码的信息
     * @param width    宽
     * @param height   高
     * @param format   条码的编码方式
     * @param remark   备注信息
     * @param textSize 备注的字体大小
     * @param margin   备注信息距离条码的高度
     * @return
     */
    public static Bitmap createBarcode(String barcode, int width, int height, BarcodeFormat format, String remark,
                                       int textSize, int margin) {
        if (TextUtils.isEmpty(barcode)) {
            return null;
        }
        //配置参数
        HashMap<EncodeHintType, Object> hints = new HashMap<>();

        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        //错误纠正，Aztec格式，pdf417格式和其它条码配置不同，不能通用，否则会产生错误。
        if (format == BarcodeFormat.AZTEC) {
            //默认，可以不设
            hints.put(EncodeHintType.ERROR_CORRECTION, Encoder.DEFAULT_EC_PERCENT);
        } else if (format == BarcodeFormat.PDF_417) {
            //纠错级别，允许为0到8。默认2，可以不设
            hints.put(EncodeHintType.ERROR_CORRECTION, 2);
        } else {
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//
        }

        //设置空白边距的宽度,默认值为4
        hints.put(EncodeHintType.MARGIN, 0);


        // 矩阵转换
        BitMatrix bitMatrix = null;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            bitMatrix = multiFormatWriter.encode(barcode, format, width, height, hints);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bitMatrix == null) {
            return null;
        }

        /**
         * 去除白色的像素
         */
        int[] rec = bitMatrix.getEnclosingRectangle();
        int resWidth = rec[2];
        int resHeight = rec[3];
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (bitMatrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        bitMatrix = resMatrix;


        int bitWidth = bitMatrix.getWidth();
        int bitHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitWidth * bitHeight];

        for (int y = 0; y < bitHeight; y++) {
            for (int x = 0; x < bitWidth; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * bitWidth + x] = 0xff000000;
                } else {
                    pixels[y * bitWidth + x] = 0xffffffff;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(bitWidth, bitHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, bitWidth, 0, 0, bitWidth, bitHeight);

        Log.d("TAG", "预期宽高：" + width + " * " + height + "  矩阵宽高：" + bitWidth + " * " + bitHeight);


        /**
         * bitmap有可能不等于预先设置的{@link width}和{@link height}，需要进行等比缩放，
         * 尤其{@link BarcodeFormat#DATA_MATRIX}小的不可想象。
         */

        float wMultiple = ((float) bitWidth) / (float) width;
        float hMultiple = ((float) bitHeight) / (float) height;
        //符合预期不需要缩放
        if (wMultiple == 1f || hMultiple == 1f) {
            return bitmap;
        }

        if (wMultiple > hMultiple) {
            //以宽的比例为标准进行缩放
            int dstHeight = (int) (bitHeight / wMultiple);
            bitmap = BitmapFlex.flex(bitmap, width, dstHeight);
        } else {
            //以高的比例为标准进行缩放。
            int dstWidth = (int) (bitWidth / hMultiple);
            bitmap = BitmapFlex.flex(bitmap, dstWidth, height);
        }


        if (TextUtils.isEmpty(remark)) {
            return bitmap;
        }

        TextView tv = new TextView(ActivityUtils.getTopActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(remark);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(bitmap.getWidth());
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap textBitmap = tv.getDrawingCache();

        Bitmap resultBitmap = Bitmap.createBitmap(Math.max(bitmap.getWidth(), textBitmap.getWidth()), bitmap.getHeight() + textBitmap.getHeight() + margin, Bitmap.Config.ARGB_8888);
        // 把背景设置为透明的
        resultBitmap.eraseColor(0);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(textBitmap, 0, bitmap.getHeight() + margin, null);

        return resultBitmap;
    }
}
