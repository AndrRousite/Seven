package com.weyee.sdk.nfc.chain

/**
 *
 * @author wuqi by 2019-08-23.
 */
open class BaseCardEntity {
    /**
     * 卡片类型，例如：羊城通，深圳通.
     */
    var type: Int = 0

    /**
     * 卡号.
     */
    var cardNumber: String? = null
        protected set
    /**
     * 卡片版本.
     */
    var version: Int = 0
        protected set
    /**
     * 卡片余额.
     */
    var balance: Long = 0
        protected set
    /**
     * 卡片生效日期.
     */
    var effectiveDate: String? = null
        protected set
    /**
     * 卡片失效日期.
     */
    var expiredDate: String? = null
        protected set
    /**
     * 卡片交易记录.
     */
    var records: MutableList<BaseRecordEntity> = ArrayList(16)
        protected set

    override fun toString(): String {
        return "BaseCardEntity(type=$type, cardNumber=$cardNumber, version=$version, balance=$balance, effectiveDate=$effectiveDate, expiredDate=$expiredDate, records=$records)"
    }


}