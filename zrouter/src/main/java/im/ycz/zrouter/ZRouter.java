package im.ycz.zrouter;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import im.ycz.zrouter.utils.ZRouteUtils;

/**
 * Created by xuanqi on 4/26/16.
 */
public class ZRouter {

    private static ZRouter instance;

    private Set<ZRoute> routes;
    private Set<Interceptor> interceptors;

    private String assets;
    private String domain;
    private Set<String> acceptDomains;
    private boolean debug;
    private ZRoute defaultRoute;

    private Class<? extends Activity> fragmentContainerActivityClass;

    public ZRouter(Builder builder) {
        this.domain = builder.domain;
        this.acceptDomains = builder.acceptDomains;
        this.debug = builder.debug;
        this.defaultRoute = builder.defaultRoute;

        if (builder.activityClass == null) {
            this.fragmentContainerActivityClass = ZFragmentContainerActivity.class;
        } else {
            this.fragmentContainerActivityClass = builder.activityClass;
        }

        if (routes == null) {
            routes = new HashSet<>();
            interceptors = new HashSet<>();
        }
    }

    public static ZRouter getInstance() {
        if (instance == null) {
            Log.d("DEBUG", "ZRouter have not been initialized");
        }
        return instance;
    }

    public static Builder from(Context context) {
        return new Builder(context);
    }

    public void add(ZRoute route) {
        routes.add(route);
    }

    public void add(Set<ZRoute> nroutes) {
        this.routes.addAll(nroutes);
    }

    public Class<? extends Activity> getFragmentContainerActivityClass() {
        return fragmentContainerActivityClass;
    }

    public void remove(ZRoute route) {
        ZRoute route2Remove = null;
        for (ZRoute item : routes) {
            if (item.getURL().equals(route.getURL())) {
                route2Remove = item;
            }
        }
        if (route2Remove != null) {
            routes.remove(route2Remove);
        }
    }

    public ZRoute get(String tag) {
        for (ZRoute item : routes) {
            if (tag != null && tag.equals(item.getTag())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Retreive the correspond class name by url
     *
     * @param url
     * @return
     */
    public String getClassName(String url) {
        for (ZRoute route : routes) {
            if (route.getURL().equals(url)) {
                return route.getClazz().getName();
            }
        }
        return null;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public ZRoute route(ZRequest request) {

        ZRoute matchedRoute = null;
        for (ZRoute item : routes) {
            if (item.match(request)) {
                matchedRoute = item;
            }
        }

        for (Interceptor interceptor : interceptors) {
            matchedRoute = interceptor.intercept(request, matchedRoute);
        }

        if (matchedRoute != null) {
            return matchedRoute;
        } else {
            return defaultRoute;
        }
    }

    public boolean isMatch(Uri uri, String role) {
        ZRequest request = new ZRequest(uri.toString());
        ZRoute route = get(role);
        return route.match(request);
    }

    public static class Builder {

        private static final String TAG = "Router.Builder";
        private Context context;
        private String assets;
        private String domain;
        private Set<String> acceptDomains;
        private boolean debug;
        private String scheme = "http";

        private Class<? extends Activity> activityClass;

        private ZRoute defaultRoute;

        public Builder(Context context) {
            this.context = context;
            acceptDomains = new HashSet<>();
        }

        public Builder assets(String assets) {
            this.assets = assets;
            return this;
        }

        public Builder domain(String domain) {
            if (acceptDomains != null && acceptDomains.contains(domain)) {
                this.acceptDomains.remove(domain);
            }
            this.domain = domain;
            this.acceptDomains.add(domain);
            return this;
        }

        public Builder accept(Set<String> domains) {
            acceptDomains.addAll(domains);
            return this;
        }

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }

        public Builder defaultRoute(ZRoute route) {
            this.defaultRoute = route;
            return this;
        }

        public Builder debug(boolean isDebug) {
            this.debug = isDebug;
            return this;
        }

        public Builder fragmentContainer(Class<? extends Activity> clazz) {
            this.activityClass = clazz;
            return this;
        }

        public ZRouter initialize() {
            instance = new ZRouter(this);
            Set<ZRoute> routes = load(context, assets);
            instance.add(routes);
            return instance;
        }

        private Set<ZRoute> load(Context context, String mRoutesFile) {
            Set<ZRoute> routeLoaed = new HashSet<>();
            try {
                String jstring = readConfigFile(context, mRoutesFile);
                JSONObject json = new JSONObject(jstring);
                JSONArray routesInJson = json.getJSONArray("routes");

                for (int i = 0; i < routesInJson.length(); i++) {
                    JSONObject item = routesInJson.getJSONObject(i);
                    String url = "";
                    if (item.has("url")) {
                        url = item.getString("url");
                    }
                    String className = "";
                    if (item.has("class")) {
                        className = item.getString("class");
                    }
                    String tag = null;
                    if (item.has("tag")) {
                        tag = item.getString("tag");
                    }

                    url = ZRouteUtils.wrapURL(url, scheme, domain);
                    ZRoute route = new ZRoute(url, className, tag);
                    routeLoaed.add(route);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to load routes configuration, confirm " + "route_config.json exists in assets");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to load routes configuration, confirm " + "route_config.json is in correct format");
            }
            return routeLoaed;
        }

        /**
         * Read routes from assete file
         *
         * @param context
         * @param routesFile
         * @return
         * @throws IOException
         */
        private static String readConfigFile(Context context, String routesFile) throws IOException {
            AssetManager assetManager = context.getResources().getAssets();
            InputStream inputStream = assetManager.open(routesFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        }

    }

    public static interface Interceptor {
        ZRoute intercept(ZRequest request, ZRoute route);
    }

}
