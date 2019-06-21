package com.weyee.sdk.print.template;

import androidx.annotation.IntRange;
import com.weyee.sdk.print.constant.PaperSize;

/**
 * @author wuqi by 2019-06-15.
 */
public class PrintWriter110mm extends PrintWriter {

    public PrintWriter110mm() {
        super(PaperSize.PAPER_SIZE_110, 0);
    }

    /**
     * 初始化模板数据
     *
     * @param paper    纸张大小{@link PaperSize}
     * @param textsize 获取默认的字体大小，用于计算一行可以放下多少个字
     */
    PrintWriter110mm(int textsize) {
        super(PaperSize.PAPER_SIZE_110, textsize);
    }

    /**
     * 当textsize 越大时，每行可容纳字符减半
     *
     * @param textSize 文字大小
     * @return
     */
    @Override
    public int getLineWidth(@IntRange(from = 0, to = 1) int textSize) {
        return PaperSize.LOGIC_SIZE_ZONERICH_112 / (textSize + 1);
    }

    @Override
    public int getSingleDrawableMaxWidth() {
        return 200;
    }

    @Override
    public int getMultipleDrawableMaxWidth() {
        return 150;
    }

    @Override
    public int getPaperMaxWidth() {
        return 680;
    }

}
