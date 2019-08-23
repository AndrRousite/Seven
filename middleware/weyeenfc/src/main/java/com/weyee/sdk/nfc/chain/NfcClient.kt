package com.weyee.sdk.nfc.chain

import com.weyee.sdk.nfc.NfcCardReaderManager
import java.io.IOException
import java.util.*

/**
 * Created by ZP on 2018/1/5.
 */
class NfcClient private constructor(builder: Builder) {

    private val readers: List<IReader>
    private val chain: IReader.Chain
    private var mNfcCardReaderManager: NfcCardReaderManager


    init {
        this.readers = builder.readers
        this.mNfcCardReaderManager = builder.mNfcCardReaderManager
        chain = NfcChain(this.readers)
    }

    @Throws(IOException::class)
    fun execute(): BaseCardEntity? {
        return chain.proceed(mNfcCardReaderManager)
    }


    class Builder {
        internal val readers = ArrayList<IReader>()
        internal lateinit var mNfcCardReaderManager: NfcCardReaderManager

        constructor() {}

        constructor(copy: NfcClient) {
            this.readers.addAll(copy.readers)
            this.mNfcCardReaderManager = copy.mNfcCardReaderManager
        }

        fun addReader(reader: IReader): Builder {
            readers.add(reader)
            return this
        }

        fun nfcManager(manager: NfcCardReaderManager): Builder {
            mNfcCardReaderManager = manager
            return this
        }

        fun build(): NfcClient {
            return NfcClient(this)
        }
    }
}
