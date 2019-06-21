package com.wuqi.a_service.ticktdata

import com.blankj.utilcode.util.AppUtils
import com.weyee.poscore.base.ThreadPool
import com.weyee.sdk.print.Interface.ITemplateAble
import com.weyee.sdk.print.constant.PaperSize
import com.weyee.sdk.print.manager.ble.BluetoothUtils
import com.weyee.sdk.print.template.*
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * @author wuqi by 2019-06-20.
 */
class TestPrintDataMaker {
    fun printData(paper: Int, utils: BluetoothUtils) {
        ThreadPool.run {
            val printer: ITemplateAble? = when (paper) {
                PaperSize.PAPER_SIZE_58 -> PrintWriter58mm()
                PaperSize.PAPER_SIZE_80 -> PrintWriter80mm()
                PaperSize.PAPER_SIZE_110 -> PrintWriter110mm()
                PaperSize.PAPER_SIZE_150 -> PrintWriter150mm()
                PaperSize.PAPER_SIZE_210 -> PrintWriter210mm()
                else -> null
            }

            printer?.setAlignCenter()
            printer?.printImage(AppUtils.getAppIcon())

            printer?.setAlignLeft()
            printer?.printLineFeed()

            printer?.printLineFeed()
            printer?.setAlignCenter()
            printer?.setEmphasizedOn()
            printer?.setFontSize(1)
            printer?.printText("刘枫9987")
            printer?.printLineFeed()
            printer?.setFontSize(0)
            printer?.setEmphasizedOff()
            printer?.printLineFeed()

            printer?.printText("最时尚的明星餐厅")
            printer?.printLineFeed()
            printer?.printText("客服电话：400-8008800")
            printer?.printLineFeed()

            printer?.setAlignLeft()
            printer?.printLineFeed()

            printer?.printText("订单号：88888888888888888")
            printer?.printLineFeed()

            printer?.printText("预计送达：" + SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date()))
            printer?.printLineFeed()

            printer?.setEmphasizedOn()
            printer?.printText("#8（已付款）")
            printer?.printLineFeed()
            printer?.printText("××区××路×××大厦××楼×××室")
            printer?.printLineFeed()
            printer?.setEmphasizedOff()
            printer?.printText("13843211234")
            printer?.printText("（张某某）")
            printer?.printLineFeed()
            printer?.printText("备注：多加点辣椒，多加点香菜，多加点酸萝卜，多送点一次性手套")
            printer?.printLineFeed()

            printer?.printLine()
            printer?.printLineFeed()

            printer?.printText(arrayOf("星级美食（豪华套餐）×1", "￥88.88"), intArrayOf(2, 1))
            printer?.printLineFeed()
            printer?.printText(arrayOf("星级美食（限量套餐）×1", "￥888.88"), intArrayOf(2, 1))
            printer?.printLineFeed()
            printer?.printText(arrayOf("餐具×1", "￥0.00"), intArrayOf(2, 1))
            printer?.printLineFeed()
            printer?.printText(arrayOf("配送费", "免费"), intArrayOf(2, 1))
            printer?.printLineFeed()

            printer?.printLine()
            printer?.printLineFeed()

            printer?.setAlignRight()
            printer?.printText("合计：977.76")
            printer?.printLineFeed()
            printer?.printLineFeed()

            printer?.setAlignLeft()
            printer?.printImage(mutableListOf("刘枫9987", null, "这个🐶"), mutableListOf("微信"))
            printer?.printImage(mutableListOf("刘枫9987", "Hello 啊", "https://www.baidu.com"), mutableListOf("微信"))


            printer?.printLineFeed()
            printer?.setAlignCenter()
            printer?.printText("扫一扫，查看详情")
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()

            printer?.feedPaperCutPartial()


            utils.write(printer?.printData)
        }
    }
}