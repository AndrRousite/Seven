package com.weyee.sdk.print.template;

import androidx.annotation.IntRange;
import com.weyee.sdk.print.constant.PaperSize;

/**
 * @author wuqi by 2019-06-15.
 */
public class PrintWriter58mm extends PrintWriter {

    public PrintWriter58mm() {
        super(PaperSize.PAPER_SIZE_58, 0);
    }

    /**
     * 初始化模板数据
     *
     * @param textsize 获取默认的字体大小，用于计算一行可以放下多少个字
     */
    PrintWriter58mm(int textsize) {
        super(PaperSize.PAPER_SIZE_58, textsize);
    }

    /**
     * 当textsize 越大时，每行可容纳字符减半
     *
     * @param textSize 文字大小
     * @return
     */
    @Override
    public int getLineWidth(@IntRange(from = 0, to = 1) int textSize) {
        return PaperSize.LOGIC_SIZE_MPOS_58 / (textSize + 1);
    }

    @Override
    public int getSingleDrawableMaxWidth() {
        return 150;
    }


    @Override
    public int getMultipleDrawableMaxWidth() {
        return 120;
    }

    @Override
    public int getPaperMaxWidth() {
        return 380;
    }

}
