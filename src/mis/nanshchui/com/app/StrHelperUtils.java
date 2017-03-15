package mis.nanshchui.com.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baoxing on 2016/10/21.
 */
public class StrHelperUtils {

    public static byte[] getBytes (byte[] oriBytes, int startPos, int len)  throws IOException{
        int oriLen = oriBytes.length;
        byte[] rBytes;

        if (oriLen < len || len <=0) {
            throw new IOException("[StrHelperUtils#getBytes]解析包错误，请重试");
        }

        rBytes = new byte[len];

        try {
            System.arraycopy(oriBytes, startPos, rBytes, 0, len);

        }catch (Exception e) {
//            e.printStackTrace();
//            LogUtils.logE(e.getLocalizedMessage());
            throw new IOException("[StrHelperUtils#getBytes]解析包错误，请重试");
        }

        return rBytes;
    }



    public static String convertToStrUtf8(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String convertToStrUtf8(String strHex) throws UnsupportedEncodingException {
        return new String(hexStr2Bytes(strHex), "utf-8");
    }

    public static byte[] getStrBytesUtf8(String str) throws UnsupportedEncodingException {
        byte[] bytes = str.getBytes("utf-8");

        return bytes;
    }

    public static String getQQHex(String strQQ) {
        int cnt;
        int diffCnt;
        String qqHex;
        StringBuilder sb;

        qqHex = Long.toHexString(Long.parseLong(strQQ));

        if ((qqHex.length() % 2) != 0) {
            qqHex = "0" + qqHex;
        }

        sb = new StringBuilder();
        cnt = 0;
        for (int i = 0, len = qqHex.length(); i < len; i += 2) {
            cnt++;
            sb.append(qqHex.charAt(i));
            sb.append(qqHex.charAt(i + 1));
            sb.append(" ");
        }

        diffCnt = 4 - cnt;
        for (int i = 0; i < diffCnt; i++) {
            sb.insert(0, "00 ");

        }

        qqHex = sb.toString();

        return qqHex;
    }

    public static String getCkHex(String strCk) {
        String ckHex;
        StringBuilder sb = new StringBuilder();

        ckHex = StrHelperUtils.byte2HexStr(strCk.getBytes());

        for (int i = 0, len = ckHex.length(); i < len; i += 2) {
            sb.append(ckHex.charAt(i));
            sb.append(ckHex.charAt(i + 1));
            sb.append(" ");
        }

        ckHex = sb.toString();

        return ckHex;
    }


    public static long byteArray2Long(byte[] byteData) {
        String s = byte2HexStr(byteData);
        long num = Long.parseLong(s, 16);

        return num;
    }

    public static String byte2HexStr(byte[] b) {
        String sTmp = "";
        StringBuilder sb = new StringBuilder("");

        for (int n = 0; n < b.length; n++) {
            sTmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((sTmp.length() == 1) ? "0" + sTmp : sTmp);
//			sb.append(" ");
        }

        return sb.toString().toUpperCase().trim();
    }
    
    
    
    
    public static String longValueToHexStr(long value) {
        String hexStr;
        StringBuilder sb;

        sb = new StringBuilder();
        hexStr = String.format("%08x", value);

        int i = 0;
        for (char item : hexStr.toCharArray()) {
            if ( (i++ % 2) == 0) {
                sb.append(" ");
            }

            sb.append(item);
        }

        hexStr = sb.toString().trim();

        return hexStr;
    }


    public static byte[] hexStr2Bytes(String cmd) {
        byte[] cmdByte;
        List<Byte> xx = new ArrayList<>();
        String[] split = cmd.split(" ");

        byte byteItem;
        for (String item : split) {
            item = item.trim();
            if (!item.equals("")) {
                byteItem = (byte) Integer.parseInt(item, 16);
                xx.add(byteItem);
            }
        }

        cmdByte = new byte[xx.size()];

        int i = 0;
        for (Byte item : xx) {
            cmdByte[i++] = item;
        }

        return cmdByte;
    }

    public static void debugBytes(String tag, byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int value;
        String itemStr;

        if (bytes != null) {
            for (byte item : bytes) {
                value = (int) item;
                value &= 0xff;
                itemStr = Integer.toHexString(value);
                if (itemStr.trim().length() == 1) {
                    itemStr = "0" + itemStr;
                }
                sb.append(itemStr);
                sb.append(" ");
            }
        } else {
            sb.append("null");
        }

        log("tag=%s, bytesHex=%s", tag, sb.toString());

        String cmd = sb.toString().replace(" ", "");
        log("tag=%s, bytesHex2=%s", tag, cmd);

    }

    public static long getLongValue(byte[] resultBytes, int curPos, int valueLen) {
        int resultLen;
        byte[] valueBytes = new byte[valueLen];

        resultLen = resultBytes.length;

        if (curPos + valueLen > resultLen) {
            return -1;
        }

        int tempJ = 0;
        String strValue;
        long longValue;
        for (int i = curPos, len = curPos + valueLen; i < len; i++) {
            valueBytes[tempJ++] = resultBytes[i];
        }
        strValue = StrHelperUtils.byte2HexStr(valueBytes);
        longValue = Long.parseLong(strValue, 16);

        return longValue;
    }

    public static int getIntegerValue(byte[] resultBytes, int curPos,
                                      int valueLen) {
        int resultLen;
        byte[] valueBytes = new byte[valueLen];

        resultLen = resultBytes.length;

        if (curPos + valueLen > resultLen) {
            return -1;
        }

        int tempJ = 0;
        String strValue;
        int intValue;
        for (int i = curPos, len = curPos + valueLen; i < len; i++) {
            valueBytes[tempJ++] = resultBytes[i];
        }
        strValue = StrHelperUtils.byte2HexStr(valueBytes);
        intValue = Integer.parseInt(strValue, 16);

        return intValue;
    }


    public static void log(String format, Object... msg) {
        System.out.println(String.format(format, msg));
    }

    public static void logDebug(String username, String qq, String tag, String format, Object... msg) {
        System.out.println(String.format("username=%s, qq=%s, tag=%s", username, qq, tag) + "," +  String.format(format, msg));
        
    }
    
    
    public static void logInfo(String username, String qq, String tag, String format, Object... msg) {
        System.out.println(String.format("username=%s, qq=%s, tag=%s", username, qq, tag) + "," +  String.format(format, msg));
    }
    
    public static void logError(String username, String qq, String tag, String format, Object... msg) {
        System.out.println(String.format("username=%s, qq=%s, tag=%s", username, qq, tag) + "," +  String.format(format, msg));
        
    }
    
    
    public static void logVerbose(String username, String qq, String tag, String format, Object... msg) {
        System.out.println(String.format("username=%s, qq=%s, tag=%s", username, qq, tag) + "," +  String.format(format, msg));
        
    }
    
    
    public static void log(String msg) {
        System.out.println(msg);
    }

}
