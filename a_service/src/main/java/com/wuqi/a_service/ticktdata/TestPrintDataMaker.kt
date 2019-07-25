package com.wuqi.a_service.ticktdata

import com.weyee.poscore.base.ThreadPool
import com.weyee.sdk.print.Interface.ITemplateAble
import com.weyee.sdk.print.PrintManager
import com.weyee.sdk.print.constant.PaperSize
import com.weyee.sdk.print.template.*


/**
 *
 * @author wuqi by 2019-06-20.
 */
class TestPrintDataMaker {
    fun printData(paper: Int) {
        ThreadPool.run {
            val printer: ITemplateAble? = when (paper) {
                PaperSize.PAPER_SIZE_58 -> PrintWriter58mm()
                PaperSize.PAPER_SIZE_80 -> PrintWriter80mm()
                PaperSize.PAPER_SIZE_110 -> PrintWriter110mm()
                PaperSize.PAPER_SIZE_150 -> PrintWriter150mm()
                PaperSize.PAPER_SIZE_210 -> PrintWriter210mm()
                else -> null
            }

            printer?.initPrinter()
            printer?.setAlignRight()
            printer?.printText("销售单")
            printer?.printLineFeed()

            printer?.setAlignCenter()
            printer?.setEmphasizedOn()
            printer?.setFontSize(1)
            printer?.printText("工厂仓")
            printer?.printLineFeed()
            printer?.printLineFeed()


            printer?.setAlignLeft()
            printer?.setFontSize(0)
            printer?.setEmphasizedOff()

            printer?.printText(arrayOf("单号:A6621", "客户:A12"), intArrayOf(1, 1))
            printer?.printLineFeed()
            printer?.printLine()

            printer?.printText(arrayOf("", "商品", "数量", "单价", "小计"), intArrayOf(1, 8, 3, 4, 5))
            printer?.printLineFeed()
            printer?.printLine()

            printer?.printText(arrayOf("1", "855", "1", "￥26", "￥26"), intArrayOf(1, 8, 3, 4, 5))
            printer?.printLineFeed()
            printer?.printText(arrayOf("", "均码"), intArrayOf(1, 4))
            printer?.printLineFeed()
            printer?.printText(arrayOf("", "樱桃白1", "", "", ""), intArrayOf(1, 8, 3, 4, 5))
            printer?.printLineFeed()
            printer?.printLine()
            printer?.printText("合计", "数量:1", "金额:￥26")
            printer?.printLineFeed()
            printer?.printLine()

            printer?.setAlignCenter()
            printer?.printImage(mutableListOf("刘枫9987", null, "这个🐶"), mutableListOf("微信"))
            printer?.printImage(mutableListOf("刘枫9987", "Hello 啊", "https://www.baidu.com"), mutableListOf("微信"))
            printer?.printLineFeed()
            printer?.printText("扫一扫，查看详情")
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()
            printer?.printLineFeed()

            printer?.feedPaperCutPartial()

            PrintManager.getInstance().printLines(printer?.printData)
        }

    }
}