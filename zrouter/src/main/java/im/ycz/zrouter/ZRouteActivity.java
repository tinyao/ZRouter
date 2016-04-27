package im.ycz.zrouter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by xuanqi on 4/26/16.
 */
public class ZRouteActivity extends Activity {

    private static final String TAG = ZRouteActivity.class.getSimpleName();

    private static boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        Log.d(TAG, "url: " + uri.toString());
        ZRequest request = new ZRequest(uri.toString());



        if (ZRouter.getInstance() == null) {
            throw new RuntimeException("Router have not been initialized");
        } else {

            if (firstLoad) {
                addInterceptors(ZRouter.getInstance());
                firstLoad = false;
            }

            ZRoute route = ZRouter.getInstance().route(request);
            Log.d(TAG, "" + route);
            if (route != null) {
                route.fire(this, request);
            }
        }

        finish();
    }

    public void addInterceptors(ZRouter router) {

    }
//        ZRouter.getInstance().addInterceptor(new ZRouter.Interceptor() {
//            @Override
//            public ZRoute intercept(ZRequest request, ZRoute route) {
//                if (TextUtils.isEmpty(request.getParam("redirect"))
//                        && "needLogin".equals(request.getFragment())) {
//                    // needLogin, check Login State
//
//                }
//                return null;
//            }
//        });
//    }

}
