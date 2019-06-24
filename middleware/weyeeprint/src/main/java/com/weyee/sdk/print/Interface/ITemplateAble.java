package com.weyee.sdk.print.Interface;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * 小票模板构建接口
 *
 * @author wuqi by 2019-06-14.
 */
public interface ITemplateAble {

    void init();

    void write(byte[] data);

    /**
     * 设置居中
     */
    void setAlignCenter();

    /**
     * 设置左对齐
     */
    void setAlignLeft();

    /**
     * 设置右对齐
     */
    void setAlignRight();

    /**
     * 开启着重
     */
    void setEmphasizedOn();

    /**
     * 关闭着重
     */
    void setEmphasizedOff();

    /**
     * 设置文字大小
     *
     * @param size 文字大小 （0～7）（默认0）
     */
    void setFontSize(@IntRange(from = 0, to = 7) int size);

    /**
     * 设置行高度
     *
     * @param height 行高度
     */
    void setLineHeight(int height);

    /**
     * 写入字符串
     *
     * @param string 字符串
     */
    void printText(String string);

    /**
     * 写入字符串
     *
     * @param string 字符串
     */
    void printText(String string, String charsetName);

    /**
     * 根据比例去打印一行的文本数据
     *
     * @param strings
     * @param ratios
     */
    void printText(String[] strings, int[] ratios);

    /**
     * 根据比例去打印一行的文本数据
     *
     * @param strings
     * @param ratios
     */
    void printText(String[] strings, int[] ratios, String charsetName);


    /**
     * 根据gravity去打印一行的文本数据
     *
     * @param left   排在左边的数据
     * @param center
     * @param right
     */
    void printText(@Nullable String left, @Nullable String center, @Nullable String right);

    /**
     * 根据gravity去打印一行的文本数据
     *
     * @param left   排在左边的数据
     * @param center
     * @param right
     */
    void printText(@Nullable String left, @Nullable String center, @Nullable String right, String charsetName);

    /**
     * 打印图片
     *
     * @param drawable
     */
    void printImage(Drawable drawable);

    /**
     * 打印图片
     *
     * @param bitmap
     */
    void printImage(Bitmap bitmap);

    /**
     * 打印图片
     *
     * @param filepath
     */
    void printImage(String filepath);

    /**
     * 一行打印多张图片
     *
     * @param elements 二维码信息列表
     * @param remarks  备注信息列表
     */
    void printImage(List<String> elements, List<String> remarks);

    /**
     * 写入一条横线
     */
    void printLine();

    /**
     * 输出并换行
     */
    void printLineFeed();

    /**
     * 进纸切割
     */
    void feedPaperCut();

    /**
     * 进纸切割（留部分）
     */
    void feedPaperCutPartial();

    /**
     * 获取图片打印高度分割值
     * 最大允许255像素
     */
    int getParting();

    /**
     * 获取一行字符串长度
     *
     * @param textSize 文字大小
     * @return 一行字符串长度
     */
    int getLineWidth(@IntRange(from = 0, to = 1) int textSize);

    /**
     * 获取文本数据的字符长度
     *
     * @param str
     * @return
     */
    int getStringWidth(String str);

    /**
     * 一行单张图片的最大宽度
     *
     * @return
     */
    int getSingleDrawableMaxWidth();

    /**
     * 一行多张图片的最大宽度
     *
     * @return
     */
    int getMultipleDrawableMaxWidth();

    /**
     * 获取纸张的大小，以像素为单位
     *
     * @return
     */
    int getPaperMaxWidth();

    /**
     * 获取模板数据
     *
     * @return
     */
    byte[] getPrintData() throws IOException;
}
