package com.weyee.sdk.nfc.chain

import com.weyee.sdk.nfc.NfcCardReaderManager
import java.io.IOException

/**
 *
 * @author wuqi by 2019-08-23.
 */
interface IReader {
    @Throws(IOException::class)
    fun readCard(nfcCardReaderManager: NfcCardReaderManager): BaseCardEntity?

    interface Chain {
        @Throws(IOException::class)
        fun proceed(nfcCardReaderManager: NfcCardReaderManager): BaseCardEntity?
    }

    val type: Int
}