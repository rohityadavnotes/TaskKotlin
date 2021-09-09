package com.task.utilities;

import java.math.BigDecimal;

public class MemoryUnitUtils {

    public static final String TAG = MemoryUnitUtils.class.getSimpleName();

    /**
     * Unit of memory start from byte, 1024 bytes = 1KB
     */
    public static final int BYTE = 1;

    /**
     * Multiple of KB
     *
     * e.g.,
     *          1024 * 1 = 1KB
     *          1024 * 10 = 10KB
     *          1024 * 9 = 9KB
     */
    public static final int KB = 1024;

    /**
     * Multiple of MB
     *
     * e.g.,
     *          1048576 * 1 = 1GB
     *          1048576 * 10 = 10GB
     *          1048576 * 9 = 9GB
     */
    public static final int MB = 1048576;

    /**
     * Multiple of GB
     *
     * e.g.,
     *          1073741824 * 1 = 1GB
     *          1073741824 * 10 = 10GB
     *          1073741824 * 9 = 9GB
     */
    public static final int GB = 1073741824;

    public enum MemoryUnit {
        BYTE,
        KB,
        MB,
        GB
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size the size passed in
     * @return formatting unit returns the value after formatting
     */
    public static String getReadableFileSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + " Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + " TB";
    }
}
