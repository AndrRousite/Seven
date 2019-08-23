package com.weyee.sdk.nfc.chain

import com.weyee.sdk.nfc.NfcCardReaderManager
import java.io.IOException

/**
 * Created by ZP on 2018/1/4.
 */
class NfcChain(private val mReaders: List<IReader>) : IReader.Chain {

    @Throws(IOException::class)
    override fun proceed(nfcCardReaderManager: NfcCardReaderManager): BaseCardEntity? {
        var defaultCardInfo: BaseCardEntity? = null
        for (reader in mReaders) {
            defaultCardInfo = reader.readCard(nfcCardReaderManager)
            if (defaultCardInfo != null) {
                break
            }
        }
        return defaultCardInfo
    }
}
