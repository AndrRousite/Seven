package com.weyee.sdk.print.template;

import androidx.annotation.IntRange;
import com.weyee.sdk.print.constant.PaperSize;

/**
 * @author wuqi by 2019-06-15.
 */
public class PrintWriter80mm extends PrintWriter {

    public PrintWriter80mm() {
        super(PaperSize.PAPER_SIZE_80, 0);
    }

    /**
     * 初始化模板数据
     *
     * @param paper    纸张大小{@link PaperSize}
     * @param textsize 获取默认的字体大小，用于计算一行可以放下多少个字
     */
    PrintWriter80mm(int textsize) {
        super(PaperSize.PAPER_SIZE_80, textsize);
    }

    /**
     * 当textsize 越大时，每行可容纳字符减半
     *
     * @param textSize 文字大小
     * @return
     */
    @Override
    public int getLineWidth(@IntRange(from = 0, to = 1) int textSize) {
        return PaperSize.LOGIC_SIZE_GPRINT_80 / (textSize + 1);
    }

    @Override
    public int getSingleDrawableMaxWidth() {
        return 200;
    }

    @Override
    public int getMultipleDrawableMaxWidth() {
        return 150;
    }

    /**
     * 440 = 150 * 2(80最多一行两个) + 70 * 2（一行最多两个个）
     * @return
     */
    @Override
    public int getPaperMaxWidth() {
        return 440;
    }

}
