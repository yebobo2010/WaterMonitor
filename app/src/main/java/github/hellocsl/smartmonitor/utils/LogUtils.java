package github.hellocsl.smartmonitor.utils;

import android.text.TextUtils;
import android.util.Log;

import github.hellocsl.smartmonitor.BuildConfig;

/**
 * Created by yebo on 2016/11/1.
 */

public class LogUtils {

    private LogUtils() {
    }

    // 容许打印日志的类型，默认是true，设置为false则不打印
    public static boolean allowD = BuildConfig.DEBUG;
    public static boolean allowE = BuildConfig.DEBUG;
    public static boolean allowI = BuildConfig.DEBUG;
    public static boolean allowV = BuildConfig.DEBUG;
    public static boolean allowW = BuildConfig.DEBUG;
    public static boolean allowWtf = BuildConfig.DEBUG;

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(),
                caller.getLineNumber()); // 替换
        return tag;
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 自定义的logger
     */
    public static CustomLogger customLogger;

    public interface CustomLogger {
        void d(String tag, String content);

        void d(String tag, String content, Throwable tr);

        void e(String tag, String content);

        void e(String tag, String content, Throwable tr);

        void i(String tag, String content);

        void i(String tag, String content, Throwable tr);

        void v(String tag, String content);

        void v(String tag, String content, Throwable tr);

        void w(String tag, String content);

        void w(String tag, String content, Throwable tr);

        void w(String tag, Throwable tr);

        void wtf(String tag, String content);

        void wtf(String tag, String content, Throwable tr);

        void wtf(String tag, Throwable tr);
    }

    public static void d(String tag, String content) {
        if (!allowD)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.d(tagName, content);
        } else {
            Log.d(tagName, content);
        }
    }

    public static void d(String content, Throwable tr) {
        if (!allowD)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.d(tag, content, tr);
        } else {
            Log.d(tag, content, tr);
        }
    }

    public static void e(String tag, String content) {
        if (!allowE)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.e(tagName, content);
        } else {
            Log.e(tagName, content);
        }
    }

    public static void e(String content, Throwable tr) {
        if (!allowE)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.e(tag, content, tr);
        } else {
            Log.e(tag, content, tr);
        }
    }

    public static void i(String tag, String content) {
        if (!allowI)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.i(tagName, content);
        } else {
            Log.i(tagName, content);
        }

    }

    public static void i(String content, Throwable tr) {
        if (!allowI)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.i(tag, content, tr);
        } else {
            Log.i(tag, content, tr);
        }

    }

    public static void v(String tag, String content) {
        if (!allowV)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.v(tagName, content);
        } else {
            Log.v(tagName, content);
        }
    }

    public static void v(String content, Throwable tr) {
        if (!allowV)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        if (customLogger != null) {
            customLogger.v(tag, content, tr);
        } else {
            Log.v(tag, content, tr);
        }
    }

    public static void w(String tag, String content) {
        if (!allowW)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.w(tagName, content);
        } else {
            Log.w(tagName, content);
        }
    }

    public static void w(String tag, String content, Throwable tr) {
        if (!allowW)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.w(tagName, content, tr);
        } else {
            Log.w(tagName, content, tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (!allowW)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.w(tagName, tr);
        } else {
            Log.w(tagName, tr);
        }
    }

    public static void wtf(String tag, String content) {
        if (!allowWtf)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.wtf(tagName, content);
        } else {
            Log.wtf(tagName, content);
        }
    }

    public static void wtf(String tag, String content, Throwable tr) {
        if (!allowWtf)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.wtf(tagName, content, tr);
        } else {
            Log.wtf(tagName, content, tr);
        }
    }

    public static void wtf(String tag, Throwable tr) {
        if (!allowWtf)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tagName = tag;
        if (TextUtils.isEmpty(tagName)) {
            tagName = generateTag(caller);
        }

        if (customLogger != null) {
            customLogger.wtf(tagName, tr);
        } else {
            Log.wtf(tagName, tr);
        }
    }

}
