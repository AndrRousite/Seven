package com.weyee.sdk.print.scan.ble;

import android.os.ParcelUuid;
import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * @author wuqi by 2019-07-12.
 */
public class UuidUtils {
    private static final String UUID_LONG_STYLE_PREFIX = "0000";
    private static final String UUID_LONG_STYLE_POSTFIX = "-0000-1000-8000-00805F9B34FB";

    /**
     * Parses a UUID string with the format defined by toString().
     *
     * @param uuidString the UUID string to parse.
     * @return an UUID instance.
     * @throws @NullPointerException     if uuid is null.
     * @throws @IllegalArgumentException if uuid is not formatted correctly.
     */
    @NonNull
    public static UUID fromString(@NonNull final String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            // may be a short style
            return UUID.fromString(UUID_LONG_STYLE_PREFIX + uuidString + UUID_LONG_STYLE_POSTFIX);
        }
    }

    /**
     * Obtains a UUID from Short style value.
     *
     * @param uuidShortValue the Short style UUID value.
     * @return an UUID instance.
     */
    @NonNull
    public static UUID fromShortValue(final int uuidShortValue) {
        return UUID.fromString(UUID_LONG_STYLE_PREFIX + String.format("%04X", uuidShortValue & 0xffff) + UUID_LONG_STYLE_POSTFIX);
    }

    /**
     * Obtains a ParcelUuid from Short style value.
     *
     * @param uuidShortValue the Short style UUID value.
     * @return an UUID instance.
     */
    @NonNull
    public static ParcelUuid parcelShortValue(final int uuidShortValue) {
        return ParcelUuid.fromString(UUID_LONG_STYLE_PREFIX + String.format("%04X", uuidShortValue & 0xffff) + UUID_LONG_STYLE_POSTFIX);
    }

}
