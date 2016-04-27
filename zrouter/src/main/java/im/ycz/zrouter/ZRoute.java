package im.ycz.zrouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import im.ycz.zrouter.utils.ZRouteUtils;

/**
 * Created by xuanqi on 4/26/16.
 */
public class ZRoute {

    private String url;
    private Class clazz;
    private String tag;

    private static final String DEFAULT_ROUTE_TAG = "";

    public ZRoute(String urlRegex, String className, String tag) {
        this.clazz = ZRouteUtils.getClazz(className);
        this.url = urlRegex;
        this.tag = tag;
    }

    public ZRoute(String urlRegex, String className) {
        this(urlRegex, className, DEFAULT_ROUTE_TAG);
    }

    public ZRoute(String urlRegex, Class clazz) {
        this(urlRegex, clazz, DEFAULT_ROUTE_TAG);
    }

    public ZRoute(String urlRegex, Class clazz, String tag) {
        this.url = urlRegex;
        this.clazz = clazz;
        this.tag = tag;
    }

    public Uri getURI() {
        return Uri.parse(url);
    }

    public String getCleanURL() {
        String clean = getURI().buildUpon().clearQuery().fragment("").build().toString();
        return clean;
    }

    public boolean match(ZRequest request) {
        return getCleanURL().matches(request.getCleanURL());
    }

    public void fire(Context context, ZRequest request) {
        if (Activity.class.isAssignableFrom(clazz)) {
            launchActivity(context, request, clazz);
        } else if (Fragment.class.isAssignableFrom(clazz)) {
            // 跳转到 NativeFragmentContainer 中，路由中指定了接收 Fragment
            Class containerClazz = ZRouter.getInstance().getFragmentContainerActivityClass();
            if (containerClazz != null) {
                launchFragment(context, request, containerClazz, clazz);
            } else {
                Log.v("ZRoute", "No FragmentContainer is available");
            }
        } else if (ZRouteHandler.class.isAssignableFrom(clazz)) {
            // 执行 ZRouteHandler
            launchHandler(request, clazz);
        }
    }

    private void launchActivity(Context context, ZRequest request, Class<? extends Activity> aclazz) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(request.getURL()));

        // 将请求参数分拆，放入 intent
        if (request.getParams() != null) {
            for (Map.Entry<String, String> param : request.getParams().entrySet()) {
                intent.putExtra(param.getKey(), param.getValue());
            }
        }

        if (request.getFlags() != -1) {
            intent.setFlags(request.getFlags());
        }

        // 启动 Activity
        intent.setClass(context, clazz);
        context.startActivity(intent);
    }

    private void launchFragment(Context context, ZRequest request, Class<? extends Activity> activityClazz, Class<? extends Fragment> fragClass) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(request.getURL()));

        // 将请求参数分拆，放入 intent
        if (request.getParams() != null) {
            for (Map.Entry<String, String> param : request.getParams().entrySet()) {
                intent.putExtra(param.getKey(), param.getValue());
            }
        }

        if (request.getFlags() != -1) {
            intent.setFlags(request.getFlags());
        }

        intent.setClass(context, activityClazz);
        intent.putExtra("fragment", clazz);
        context.startActivity(intent);
    }

    public void launchHandler(ZRequest request, Class<? extends ZRouteHandler> handleClass) {
        try {
            ZRouteHandler handler = handleClass.newInstance();
            if (handler != null) {
                handler.handle(request);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getURL() {
        return url;
    }

    public String getTag() {
        return tag;
    }

    public Class getClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        return "ZRoute{" +
                "url='" + url + '\'' +
                ", clazz=" + clazz.getName() +
                ", tag='" + tag + '\'' +
                '}';
    }
}
