package com.weyee.sdk.util.number;

import android.text.TextUtils;
import com.blankj.utilcode.util.LogUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author wuqi by 2019/6/4.
 */
public class MPriceUtil {
    public static String getPrice(double price) {
        return getPrice(String.valueOf(price));
    }

    public static String getPrice(String priceStr) {
        return getUnit(getFormatDecimal(priceStr));
    }

    /**
     * 获取有负号的价格
     *
     * @param price
     * @return
     */
    public static String getMinusPrice(double price) {
        return getMinusPrice(String.valueOf(price));
    }

    public static String getMinusPrice(String priceStr) {
        return getMinusUnit(getFormatDecimal(priceStr));
    }

    /**
     * 不过滤符号
     *
     * @param priceStr
     * @return
     */
    public static String getPriceNoMinus(String priceStr) {
        return getUnit(getFormatDecimal(priceStr, "##,###,##0.00", true));
    }

    public static String getPriceNoMinus(double price) {
        return getPriceNoMinus(String.valueOf(price));
    }


    /**
     * @param priceStr
     * @param isMinuSign 是否过滤指定符号
     * @return
     */
    public static String filterPriceUnit(String priceStr, boolean isMinuSign) {
        if (TextUtils.isEmpty(priceStr)) {
            return "";
        }

        String price = priceStr.replace("¥", "").trim();
        price = price.replace("￥", "").trim();
        price = price.replace("$", "").trim();
        price = price.replace(",", "").trim();
        price = price.replaceAll(" ", "").trim();
        price = price.replaceAll("\\u0000", "").trim();
        if (isMinuSign)
            price = priceStr.replace("-", "").trim();

        return price.trim();
    }

    public static String filterPriceUnit(String priceStr) {
        return filterPriceUnit(priceStr, false);
    }


    public static String getUnit(String priceStr) {
        return "¥" + priceStr;
    }

    public static String getMinusUnit(String priceStr) {
        return "¥-" + priceStr;
    }

    public static String parseMoney(String pattern, BigDecimal bd) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(bd);
    }

    public static String getFormatDecimal(String priceStr, String pattern) {
        if (TextUtils.isEmpty(priceStr)) {
            priceStr = "0";
        }
        BigDecimal bigDecimal = new BigDecimal(filterPriceUnit(priceStr));
        return parseMoney(pattern, bigDecimal);
    }


    public static String getFormatDecimal(double price, String pattern) {
        return getFormatDecimal(String.valueOf(price), pattern);
    }

    public static String getFormatDecimal(String priceStr, String pattern, boolean isMinuSign) {
        if (TextUtils.isEmpty(priceStr)) {
            priceStr = "0";
        }
        BigDecimal bigDecimal = new BigDecimal(filterPriceUnit(priceStr, isMinuSign));
        return parseMoney(pattern, bigDecimal);
    }

    /**
     * @param priceStr
     * @param isDelimiter 是否需要分隔符
     * @return
     */
    public static String getFormatDecimal(String priceStr, boolean isDelimiter) {
        return getFormatDecimal(priceStr, isDelimiter ? "##,###,##0.00" : "#######0.00");
    }

    /**
     * @param priceStr
     * @return
     */
    public static Double getFormatDecimalPrice(String priceStr) {
        return MNumberUtil.convertToDouble(getFormatDecimal(priceStr, false ? "##,###,##0.00" : "#######0.00"));
    }

    public static String getFormatDecimal(double price, boolean isDelimiter) {
        return getFormatDecimal(String.valueOf(price), isDelimiter);
    }

    /**
     * 默认使用千分位
     *
     * @param priceStr
     * @return
     */
    public static String getFormatDecimal(String priceStr) {
        return getFormatDecimal(priceStr, true);
    }

    public static String getFormatDecimal(int priceStr) {
        return getFormatDecimal(String.valueOf(priceStr));
    }

    public static String getFormatDecimal(double priceStr) {
        return getFormatDecimal(String.valueOf(priceStr));
    }

    public static String formatWan(int number) {
        int wan = 10000;
        if (number < wan) {
            return number + "";
        }

        int tem = number / wan;
        String temStr = tem + "万";
        if (number % wan != 0) {
            temStr = temStr + "+";
        }
        return temStr;
    }

    /**
     * 按指定区域格式化百分数
     *
     * @param d
     * @param pattern :"##,##.000%"-->不要忘记“%”
     * @param l
     * @return
     */
    public static String formatPercent(double d, String pattern, Locale l) {
        String s = "";
        try {
            DecimalFormat df = (DecimalFormat) NumberFormat.getPercentInstance(l);
            df.applyPattern(pattern);
            s = df.format(d);
        } catch (Exception e) {
            LogUtils.e("formatPercent is error!");
        }
        return s;
    }

    /**
     * 使用默认区域格式化百分数
     *
     * @param d
     * @param pattern
     * @return
     */
    public static String formatPercent(double d, String pattern) {
        return formatPercent(d, pattern, Locale.getDefault());
    }

    /**
     * 格式化百分数
     *
     * @param d
     * @return
     */
    public static String formatPercent(double d) {
        String s = "";
        try {
            DecimalFormat df = (DecimalFormat) NumberFormat.getPercentInstance();
            s = df.format(d);
        } catch (Exception e) {
            LogUtils.e("formatPercent is error!");
        }
        return s;
    }

    /**
     * 在最后添加至两位小数.00
     *
     * @param dou
     * @return
     */
    public static String addZero(String dou) {
        if (dou.contains(".")) {
            int indexOf = dou.indexOf(".");
            int length = dou.length();
            if (indexOf + 2 == length) {
                return dou + "0";
            }
            if (indexOf + 1 == length) {
                return dou + "00";
            }
            return dou;
        } else {
            return dou + ".00";
        }
    }
}
