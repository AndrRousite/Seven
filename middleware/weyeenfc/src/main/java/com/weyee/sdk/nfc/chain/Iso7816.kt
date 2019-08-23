package com.weyee.sdk.nfc.chain

/**
 *
 * @author wuqi by 2019-08-23.
 */
class Iso7816 {
    var cmd: ByteArray
    lateinit var resp: ByteArray
    var isContinue: Boolean = false
        private set

    constructor(cmd: ByteArray, isContinue: Boolean) {
        this.cmd = cmd
        this.isContinue = isContinue
    }

    constructor(cmd: ByteArray) {
        this.cmd = cmd
        this.isContinue = false
    }
}