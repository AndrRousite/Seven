package com.weyee.sdk.print.template;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.annotation.Nullable;
import com.weyee.sdk.print.Interface.ITemplateAble;
import com.weyee.sdk.print.constant.PaperSize;
import com.weyee.sdk.print.utils.EscUtils;
import com.weyee.sdk.print.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.weyee.sdk.print.utils.Utils.*;

/**
 * 组装模板数据
 *
 * @author wuqi by 2019-06-14.
 */
abstract class PrintWriter implements ITemplateAble {
    private static final String CHARSET = "gb2312";
    protected int paper;
    protected int normalTextSize;
    protected ByteArrayOutputStream bos;

    /**
     * 初始化模板数据
     *
     * @param paper    纸张大小{@link com.weyee.sdk.print.constant.PaperSize}
     * @param textsize 获取默认的字体大小，用于计算一行可以放下多少个字
     */
    PrintWriter(int paper, int textsize) {
        this.paper = paper;
        this.normalTextSize = textsize;
    }

    @Override
    public void init() {
        bos = new ByteArrayOutputStream();
        write(EscUtils.initPrinter());
    }

    @Override
    public void write(byte[] data) {
        if (bos == null) init();
        try {
            bos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAlignCenter() {
        write(EscUtils.alignCenter());
    }

    @Override
    public void setAlignLeft() {
        write(EscUtils.alignLeft());
    }

    @Override
    public void setAlignRight() {
        write(EscUtils.alignRight());
    }

    @Override
    public void setEmphasizedOn() {
        write(EscUtils.emphasizedOn());
    }

    @Override
    public void setEmphasizedOff() {
        write(EscUtils.emphasizedOff());
    }

    @Override
    public void setFontSize(int size) {
        write(EscUtils.fontSizeSetBig(size));
    }

    @Override
    public void setLineHeight(int height) {
        write(EscUtils.printLineHeight(height));
    }

    @Override
    public void printText(String string) {
        printText(string, CHARSET);
    }

    @Override
    public void printText(String string, String charsetName) {
        if (TextUtils.isEmpty(string))
            return;
        try {
            write(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printText(String[] strings, int[] ratios) {
        printText(strings, ratios, CHARSET);
    }

    @Override
    public void printText(String[] strings, int[] ratios, String charsetName) {
        if (strings == null || ratios == null || strings.length != ratios.length) return;

        int length = getLineWidth(normalTextSize);

        int countRatio = 0;
        for (int ratio : ratios) {
            countRatio += ratio;
        }

        for (int i = 0; i < strings.length; i++) {
            if (strings[i] == null) strings[i] = "";
            int strLength = getStringWidth(strings[i]);
            int hasLength = (int) Math.floor(ratios[i] * length / (float) countRatio);

            StringBuilder sb = new StringBuilder();

            if (strLength >= hasLength) sb.append(strings[i].substring(0, hasLength - 1)).append("*");
            else {
                sb.append(strings[i]);
                int needEmpty = hasLength - strLength;
                while (needEmpty > 0) {
                    sb.append(" ");
                    needEmpty--;
                }
            }
            try {
                write(sb.toString().getBytes(charsetName));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void printText(@Nullable String left, @Nullable String center, @Nullable String right) {
        printText(left, center, right, CHARSET);
    }

    @Override
    public void printText(@Nullable String left, @Nullable String center, @Nullable String right, String charsetName) {
        if (TextUtils.isEmpty(left) && TextUtils.isEmpty(center) && TextUtils.isEmpty(right)) return;
        int length = getLineWidth(normalTextSize);

        int ratio = 3;
        if (!TextUtils.isEmpty(left) && TextUtils.isEmpty(center)) {
            ratio = 2;
        }

        StringBuilder sb = new StringBuilder();

        int hasLength = (int) Math.floor(length / (float) ratio);

        // 左边
        int strLength = getStringWidth(left);
        if (strLength >= hasLength) sb.append(left != null ? left.substring(0, hasLength - 1) : "").append("*");
        else {
            sb.append(left == null ? "" : left);
            int needEmpty = hasLength - strLength;
            while (needEmpty > 0) {
                sb.append(" ");
                needEmpty--;
            }
        }

        // 中间
        if (!TextUtils.isEmpty(center)) {
            int str2Length = getStringWidth(center);
            if (str2Length >= hasLength)
                sb.append(center != null ? center.substring(0, hasLength - 1) : "").append("*");
            else {
                int needEmpty = hasLength - str2Length;
                int tempEmpty = needEmpty / 2;  // 取中间值
                while (needEmpty > tempEmpty) {
                    sb.append(" ");
                    needEmpty--;
                }
                sb.append(center);
                while (needEmpty > 0) {
                    sb.append(" ");
                    needEmpty--;
                }
            }
        }

        // 右边
        int str3Length = getStringWidth(right);
        if (str3Length >= hasLength) sb.append(right != null ? right.substring(0, hasLength - 1) : "").append("*");
        else {
            int needEmpty = hasLength - str3Length;
            while (needEmpty > 0) {
                sb.append(" ");
                needEmpty--;
            }
            sb.append(right == null ? "" : right);
        }

        try {
            write(sb.toString().getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printImage(Drawable drawable) {
        int maxWidth = getSingleDrawableMaxWidth();
        Bitmap image = Utils.scalingDrawable(drawable, maxWidth);
        if (image == null)
            return;
        byte[] data = EscUtils.decodeBitmap(image, getParting());
        image.recycle();
        // 输出打印中的缓存，并换行
        printLineFeed();
        write(data);
    }

    @Override
    public void printImage(Bitmap bitmap) {
        int maxWidth = getSingleDrawableMaxWidth();
        Bitmap scalingImage = Utils.scalingBitmap(bitmap, maxWidth);
        if (scalingImage == null)
            return;
        byte[] data = EscUtils.decodeBitmap(bitmap, getParting());
        bitmap.recycle();
        // 输出打印中的缓存，并换行
        printLineFeed();
        write(data);
    }

    @Override
    public void printImage(String filepath) {
        printImage(Utils.decodeFile(filepath));
    }

    @Override
    public void printImage(List<String> elements, List<String> remarks) {
        if (elements == null || elements.isEmpty()) return;
        SparseArray<Bitmap> map = new SparseArray<>();
        for (int i = 0; i < elements.size(); i++) {
            // 58mm和80mm的打印机一行只能打印两个二维码
            if ((paper == PaperSize.PAPER_SIZE_58 || paper == PaperSize.PAPER_SIZE_80) && i > 1) break;
            Bitmap bitmap = Utils.createQrcode(elements.get(i), getMultipleDrawableMaxWidth(), remarks != null && remarks.size() > i ? remarks.get(i) : null);
            map.put(i, bitmap);
        }
        Bitmap bitmap = Utils.transSpliceQrcode(map, getPaperMaxWidth());
        // 其实本身通过canvas画出来的图片是不需要缩减的，原图和需要缩减的比例是一样的
        Bitmap scalingImage = Utils.scalingBitmap(bitmap, getPaperMaxWidth());
        if (scalingImage == null)
            return;
        byte[] data = EscUtils.decodeBitmap(bitmap, getParting());
        bitmap.recycle();
        // 输出打印中的缓存，并换行
        printLineFeed();
        write(data);
    }

    @Override
    public void printLine() {
        int length = getLineWidth(normalTextSize);
        StringBuilder line = new StringBuilder();
        while (length > 0) {
            line.append("-");
            length--;
        }
        printText(line.toString());
        printLineFeed(); // 换行
    }

    @Override
    public void printLineFeed() {
        write(EscUtils.printLineFeed());
    }

    @Override
    public void feedPaperCut() {
        write(EscUtils.feedPaperCut());
    }

    @Override
    public void feedPaperCutPartial() {
        write(EscUtils.feedPaperCutPartial());
    }

    @Override
    public int getParting() {
        return 255;
    }

    @Override
    public int getStringWidth(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        int width = 0;
        for (char c : str.toCharArray()) {
            width += isChinese(c) || isCyrillic(c) || isSpecial(c) ? 2 : 1;
        }
        return width;
    }

    @Override
    public byte[] getPrintData() throws IOException {
        byte[] data;
        bos.flush();
        data = bos.toByteArray();
        bos.close();
        bos = null;
        return data;
    }
}
