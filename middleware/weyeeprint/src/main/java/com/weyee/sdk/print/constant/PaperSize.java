package com.weyee.sdk.print.constant;

import com.weyee.sdk.util.sp.SpUtils;

/**
 * 打印机纸张的大小
 *
 * @author wuqi by 2019-06-14.
 */
public class PaperSize {
    /**
     * 纸张物理尺寸(mm)
     */
    public final static int PAPER_SIZE_80 = 78;
    public final static int PAPER_SIZE_58 = 56;
    public final static int PAPER_SIZE_110 = 112;
    public final static int PAPER_SIZE_150 = 150;
    public final static int PAPER_SIZE_210 = 210;


    /**
     * 机器页面逻辑尺寸（一行可容纳的字体长度：西文计算）
     */
    public final static int LOGIC_SIZE_MPOS_58 = 32;
    public final static int LOGIC_SIZE_GPRINT_80 = 48;
    public final static int LOGIC_SIZE_ZONERICH_112 = 68;
    public final static int LOGIC_SIZE_JOLIMARK_150 = 62;
    public final static int LOGIC_SIZE_EPSON_210 = 100;

    public static void savePaperSize(int paper) {
        SpUtils.getDefault().put(PaperSize.class.getSimpleName(), paper);
    }

    public static int getPaperSize() {
        return SpUtils.getDefault().getInt(PaperSize.class.getSimpleName(), PAPER_SIZE_80);
    }
}
