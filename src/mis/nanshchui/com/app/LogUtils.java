package mis.nanshchui.com.app;

import java.util.Objects;

/**
 * Created by baoxing on 2017/3/14.
 */
public class LogUtils {

    public static void logI(String msg) {
        System.out.println(msg);
    }


    public static void logI(String format, Object... msg) {
        System.out.println(String.format(format, msg));
    }


    public static void logE(String msg) {
        System.out.println(msg);
    }


    public static void logE(String format, Object... msg) {
        System.out.println(String.format(format, msg));
    }



}
