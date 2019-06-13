package com.wuqi.a_service.util

import java.util.*

/**
 *
 * @author wuqi by 2019-06-06.
 */
class ColorGenerator private constructor(private val colorList: List<Int>) {


    private var mRandom: Random = Random(System.currentTimeMillis())

    companion object {
        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }

        var DEFAULT: ColorGenerator = create(
            arrayListOf(
                0xf16364,
                0xf58559,
                0xf9a43e,
                0xe4c62e,
                0x67bf74,
                0x59a2be,
                0x2093cd,
                0xad62a7,
                0x805781
            )
        )

        var MATERIAL: ColorGenerator = create(
            arrayListOf(
                0xe57373,
                0xf06292,
                0xba68c8,
                0x9575cd,
                0x7986cb,
                0x64b5f6,
                0x4fc3f7,
                0x4dd0e1,
                0x4db6ac,
                0x81c784,
                0xaed581,
                0xff8a65,
                0xd4e157,
                0xffd54f,
                0xffb74d,
                0xa1887f,
                0x90a4ae
            )
        )
    }

    fun getRandomColor(): Int {
        return colorList[mRandom.nextInt(colorList.size)]
    }

    fun getColor(key: Any): Int {
        return colorList[Math.abs(key.hashCode()) % colorList.size]
    }
}