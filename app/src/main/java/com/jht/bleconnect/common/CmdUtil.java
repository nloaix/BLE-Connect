package com.jht.bleconnect.common;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by CHARWIN.
 */

public class CmdUtil {
    private final static int CRC_CCITT_Table[] = {
            0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
            0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
            0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
            0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
            0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC,
            0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823,
            0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B,
            0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12,
            0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A,
            0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41,
            0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49,
            0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70,
            0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78,
            0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F,
            0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
            0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D,
            0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C,
            0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x08E1, 0x3882, 0x28A3,
            0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A,
            0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92,
            0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9,
            0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1,
            0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8,
            0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0
    };

    public static int generateCRC_CCITT(int[] PUPtr8, int PU16_Count) {
        int crc = 0xFFFF;
        int crc2;
        for (int i = 0; i < PU16_Count; i++) {
            crc2 = CRC_CCITT_Table[((crc & 0xff00) >> 8) ^ (PUPtr8[i] & 0x00ff)];
            crc = ((crc << 8) & 0xff00) ^ crc2;
        }
        return crc;
    }

    public static byte[] checkSum(byte[] data) {
        int crc = 0;
        if (data != null) {
            crc = CrcUtil.crc_16_CCITT_False(data, data.length);
        }
        return short2bytesLH((short) crc);
//		return short2bytes((short) crc); // viafit???????????????checksum??????
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }


//	/**
//	 * ????????????
//	 *
//	 * @param bytes
//	 * @param start
//	 * @param len
//	 * @return
//	 */
//	public static byte[] subBytes(byte[] bytes, int start, int len) {
//		byte[] result = null;
//		if (bytes != null) {
//			result = new byte[len];
//			for (int i = 0; i < len; i++) {
//				result[i] = bytes[i + start];
//			}
//		}
//		return result;
//	}

    /**
     * ????????????
     *
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] mergeByte(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public static byte[] mergeBytes(byte[]... srcByte) {
        int length = 0;
        for (byte[] b : srcByte) {
            if (b != null && b.length > 0) {
                length += b.length;
            }
        }

        byte[] result = new byte[length];

        int position = 0;
        for (byte[] b : srcByte) {
            if (b != null && b.length > 0) {
                System.arraycopy(b, 0, result, position, b.length);
                position += b.length;
            }
        }

        return result;
    }

    public static byte[] asciiToBytes(String src) {
        byte[] b = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            b = src.getBytes(StandardCharsets.US_ASCII);
        } else {
            try {
                b = src.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * ??????????????????src????????????????????????????????????16????????????
     * ??????"2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] hexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * ?????????ASCII???????????????????????????
     * ??????"EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static byte[] int2bytes(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// ?????????
        targets[1] = (byte) ((res >> 8) & 0xff);// ?????????
        targets[2] = (byte) ((res >> 16) & 0xff);// ?????????
        targets[3] = (byte) (res >>> 24);// ?????????,??????????????????
        return targets;
    }


    /**
     * ???int?????????????????????????????????byte???????????????????????????(???????????????????????????)???????????? ???bytesToInt??????????????????
     *
     * @param value ????????????int???
     * @return byte??????
     */
    public static byte[] int2bytesLH(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * ???int?????????????????????????????????byte???????????????????????????(???????????????????????????)????????????  ???bytesToInt2??????????????????
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * byte????????????int???????????????????????????(???????????????????????????)??????????????????intToBytes??????????????????
     *
     * @param src    byte??????
     * @param offset ???????????????offset?????????
     * @return int??????
     */
    public static int bytes2intLH(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * byte????????????int???????????????????????????(???????????????????????????)???????????????intToBytes2??????????????????
     */
    public static int bytes2int(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

//	public static byte[] int2TwoBytesHL(int value) {
//		byte high = (byte) ((value & 0xff00) >> 8);
//		byte low = (byte) (value & 0x00ff);
//		return new byte[]{high, low};
//	}
//
//	public static byte[] int2TwoBytesLH(int value) {
//		byte high = (byte) ((value & 0xff00) >> 8);
//		byte low = (byte) (value & 0x00ff);
//		return new byte[]{low, high};
//	}
//
//	public static int twoBytesHLToInt(byte[] src) {
//		int value = (int) (
//				((src[0] << 8) & 0xff)
//						| ((src[1]) & 0x00ff)
//		);
//		return value;
//	}
//
//	public static int twoBytesLHToInt(byte[] src) {
//		int value = (int) (
//				((src[1]) & 0xff)
//						| ((src[0] << 8) & 0xff00)
//
//		);
//		return value;
//	}

    /**
     * ???16??????short?????????byte??????
     *
     * @param s short
     * @return byte[] ?????????2
     */
    public static byte[] short2bytes(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * ???16??????short?????????byte??????
     *
     * @param s short
     * @return byte[] ?????????2
     */
    public static byte[] short2bytesLH(short s) {
        int length = 2;
        byte[] targets = new byte[length];
        for (int i = 0; i < length; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[length - i - 1] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * ????????????2???byte???????????????16???int
     *
     * @param res byte[]
     * @return int
     */
    public static int byte2int(byte[] res) {
        // res = InversionByte(res);
        // ??????byte????????????24?????????0x??000000????????????8?????????0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | ???????????????
        return targets;
    }

    public static short byte2short(byte[] b) {
        return (short) ((b[0] << 8) | b[1] & 0xff);
    }

    public static short byte2shortLH(byte[] b) {
        return (short) (((b[1] << 8) | b[0] & 0xff));
    }

    public static String bytes2Ascii(byte[] src) {
        int length = src.length;
        // ?????????????????????
        for (int i = 0; i < length; i++) {
            if (src[i] == 0x00) {
                length = i; // ?????????????????????
                break;
            }
        }
        return bytes2Ascii(src, 0, length);
    }

    public static String bytes2Ascii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    public static byte[] utf8String2Bytes(String src) {
        try {
            return src.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String bytes2Utf8String(byte[] bytes) {
        try {
            int p = 0;
            for (int i = 0; i < bytes.length; i++) {
                if (bytes[i] == 0x00) {
                    p = i;
                    break;
                }
            }

            if (p > 0) {
                // ?????????????????????
                byte[] result = Arrays.copyOfRange(bytes, 0, p);
                return new String(result, "utf-8");
            } else {
                return new String(bytes, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param src   ?????????
     * @param start ????????????
     * @param data  ??????????????????
     * @return
     */
    public static boolean fillBytes(byte[] src, int start, byte[] data) {
        int end = start + data.length; // ?????????
        if (end <= src.length) {
            for (int i = start; i < end; i++) {
                src[i] = data[i - start];
            }
            return true;
        }
        return false;
    }


    /**
     * ip?????????byte??????
     *
     * @param ip
     * @return
     */
    public static byte[] ip2bytes(String ip) {
        byte[] result = new byte[4];
        try {
            String[] ips = ip.split("\\.");
            for (int i = 0; i < 4; i++) {
                result[i] = (byte) Short.parseShort(ips[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

