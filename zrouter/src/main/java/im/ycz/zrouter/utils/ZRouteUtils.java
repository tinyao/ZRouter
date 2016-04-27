package im.ycz.zrouter.utils;

import android.net.Uri;
import android.support.v4.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuanqi on 4/26/16.
 */
public class ZRouteUtils {

    private static Map<String, Class> clazzes = new HashMap<>();

    /**
     * 根据 class name 获取类的 class 实例，会缓存已查找过的类
     * @param className
     * @return
     */
    public static Class getClazz(String className) {
        Class<?> clazz = null;
        if (clazzes.containsKey(className)) {
            clazz = clazzes.get(className);
        } else {
            try {
                clazz = Class.forName(className);
                clazzes.put(className, clazz);   // 缓存 class 的解析
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return clazz;
    }

    private static boolean isRelativeURL(Uri uri, String domain) {
        // 相对地址，同时不以默认域名开头
        return uri.isRelative() && !uri.toString().startsWith(domain);
    }

    /**
     * 包装链接成标准形式
     * 相对地址 -> 默认域名下绝对地址
     * 不包含scheme -> 加上默认 scheme
     *
     * @param url
     * @return
     */
    public static String wrapURL(String url, String scheme, String domain) {
        Uri uri = Uri.parse(url);
        if (isRelativeURL(uri, domain)) {
            // 相对地址，或者以不以默认域名开, /static/m/、 static/m
            if (url.startsWith("/")) {
                return scheme + "://" + domain + url;
            } else {
                return scheme + "://" + domain + "/" + url;
            }
        } else {
            // 绝对地址，或者带默认域名的地址, http://example.com, local://index.js, example.com
            if (!uri.isAbsolute()) {
                // 如果是 example.com 的地址
                url = uri.buildUpon().scheme(scheme).toString();
            }
        }
        return url;
    }

    public static Fragment create(String fragmentName, String url, boolean isStandalone) {
        try {
            Class clazz = getClazz(fragmentName);
            Method method = clazz.getDeclaredMethod("newInstance", String.class, boolean.class);
            Fragment fragment = (Fragment) method.invoke(null, url, isStandalone);
            return fragment;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Fragment create(Class fragmentClazz, String url, boolean isStandalone) {
        try {
            Method method = fragmentClazz.getDeclaredMethod("newInstance", String.class, boolean.class);
            Fragment fragment = (Fragment) method.invoke(null, url, isStandalone);
            return fragment;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
