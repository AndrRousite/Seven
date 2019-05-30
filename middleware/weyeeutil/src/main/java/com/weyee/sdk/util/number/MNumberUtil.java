package com.weyee.sdk.util.number;

import android.text.TextUtils;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author wuqi by 2019/5/29.
 */
public class MNumberUtil {
    //类型转换
    public static int convertToint(String string) {
        return convertToint(string, 0);
    }

    public static int convertToint(String string, int defValue) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ignored) {
        }
        return defValue;
    }

    public static long convertTolong(String string) {
        return convertTolong(string, 0);
    }

    public static long convertTolong(String string, long defValue) {
        try {
            return Long.parseLong(string);
        } catch (Exception ignored) {
        }
        return defValue;
    }

    public static float convertTofloat(String string) {
        return convertTofloat(string, 0f);
    }

    public static float convertTofloat(String string, float defValue) {
        try {
            return Float.parseFloat(string);
        } catch (Exception ignored) {
        }
        return defValue;
    }

    public static double convertTodouble(String string) {
        return convertTodouble(string, 0);
    }

    public static double convertTodouble(String dStr, double defValue) {
        try {
            return Double.parseDouble(dStr);
        } catch (Exception ignored) {
        }
        return defValue;
    }


    public static Integer convertToInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ignored) {
        }
        return 0;
    }

    public static Long convertToLong(String string) {
        try {
            return Long.parseLong(string);
        } catch (Exception ignored) {
        }
        return 0L;
    }

    public static Float convertToFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (Exception ignored) {
        }
        return (float) 0;
    }

    public static Double convertToDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (Exception ignored) {
        }
        return (double) 0;
    }

    // 加减乘除
    private static String getDefaultDoubleValues(String str) {
        if (TextUtils.isEmpty(str)) {
            str = "0";
        }

        return str;
    }

    /**
     * double 相加
     *
     * @param number1
     * @param number2
     * @return
     */
    public static double sum(String number1, String number2) {
        BigDecimal bigDecimal1 = new BigDecimal(getDefaultDoubleValues(number1));
        BigDecimal bigDecimal2 = new BigDecimal(getDefaultDoubleValues(number2));
        return bigDecimal1.add(bigDecimal2).doubleValue();
    }

    /**
     * double 相加
     *
     * @param number1
     * @param number2
     * @return
     */
    public static String sumReturnStr(String number1, String number2) {
        BigDecimal bigDecimal1 = new BigDecimal(getDefaultDoubleValues(number1));
        BigDecimal bigDecimal2 = new BigDecimal(getDefaultDoubleValues(number2));
        return bigDecimal1.add(bigDecimal2).toString();
    }

    public static double sum(double number1, double number2) {
        return sum(Double.toString(number1), Double.toString(number2));
    }


    /**
     * double 相减
     *
     * @param number1
     * @param number2
     * @return
     */
    public static double sub(String number1, String number2) {
        BigDecimal bigDecimal1 = new BigDecimal(getDefaultDoubleValues(number1));
        BigDecimal bigDecimal2 = new BigDecimal(getDefaultDoubleValues(number2));
        return bigDecimal1.subtract(bigDecimal2).doubleValue();
    }

    public static double sub(double number1, double number2) {
        return sub(Double.toString(number1), Double.toString(number2));
    }

    /**
     * double 乘法
     *
     * @param number1
     * @param number2
     * @return
     */
    public static double mul(String number1, String number2) {
        BigDecimal bigDecimal1 = new BigDecimal(getDefaultDoubleValues(number1));
        BigDecimal bigDecimal2 = new BigDecimal(getDefaultDoubleValues(number2));
        return bigDecimal1.multiply(bigDecimal2).doubleValue();
    }

    public static double mul(double number1, double number2) {
        return mul(Double.toString(number1), Double.toString(number2));
    }


    /**
     * double 除法
     *
     * @param number1
     * @param number2
     * @param scale   四舍五入 小数点位数
     * @return
     */
    public static double div(String number1, String number2, int scale) {
        if (convertTodouble(number2) == 0) {
            number2 = "1";
        }
        BigDecimal bigDecimal1 = new BigDecimal(getDefaultDoubleValues(number1));
        BigDecimal bigDecimal2 = new BigDecimal(getDefaultDoubleValues(number2));
        return bigDecimal1.divide(bigDecimal2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double div(String number1, String number2) {
        return div(number1, number2, 2);
    }

    public static double div(double number1, double number2) {
        return div(number1, number2, 2);
    }

    public static double div(double number1, double number2, int scale) {
        return div(Double.toString(number1), Double.toString(number2));
    }

    /**
     * 对double数据进行取精度.
     *
     * @param value        double数据.
     * @param scale        精度位数(保留的小数位数).
     * @param roundingMode 精度取值方式.
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }


    // 数据格式化
    public static String format1Decimal(double d) {
        DecimalFormat df = new DecimalFormat("######0.0");
        return df.format(d);
    }

    public static String format1Decimal(String str) {
        return format1Decimal(convertTodouble(str));
    }

    public static String format2Decimal(double d) {
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(d);
    }

    public static String format2Decimal(String str) {
        return format2Decimal(convertTodouble(str));
    }

    public static String format2DecimalLong(double d) {
        DecimalFormat df = new DecimalFormat("###############0.00");
        return df.format(d);
    }

    public static String getPercent(double number, double number2) {
        if (number2 <= 0) {
            return "0%";
        }

        double percent = number / number2 * 100;
        if (percent == 0) {
            return "0%";
        } else {
            return format2Decimal(percent) + "%";
        }

    }

    public static String getPercent(String number, String number2) {
        return getPercent(convertTodouble(number), convertTodouble(number2));
    }


    /**
     * 格式化EditText 只能输入2位小数
     *
     * @param editText
     * @param maxNumber 最大输入数
     * @return true：已经格式化成两们小数
     */
    public static boolean formatInputNumber2Decimal(EditText editText, double maxNumber) {
        String number = editText.getText().toString().trim();
        double price = convertTodouble(number);
        if (price > maxNumber) {
            editText.setText(String.valueOf(format2Decimal(maxNumber)));
            editText.setSelection(editText.length());
            return true;
        }

        int index = number.indexOf(".");
        if (index != -1) {
            String tem = number.substring(index, number.length());
            if (tem.length() > 3) {
                editText.setText(number.substring(0, index + 3));
                editText.setSelection(editText.length());
                return true;
            }
        }
        return false;
    }

    public static String getInputNumber2Decimal(String number, double maxNumber) {
        double price = convertTodouble(number);
        if (price > maxNumber) {
            return String.valueOf(format2Decimal(maxNumber));
        }

        int index = number.indexOf(".");
        if (index != -1) {
            String tem = number.substring(index, number.length());
            if (tem.length() > 3) {
                return number.substring(0, index + 3);
            }
        }
        return number;
    }

}
