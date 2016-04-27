package im.ycz.zrouter;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuanqi on 4/26/16.
 */
public class ZRequest {

    private Uri uri;
    private int mFlags;

    public ZRequest(String url) {
        this.uri = Uri.parse(url);
    }

    public String getFragment() {
        return uri.getFragment();
    }

    public String getURL() {
        return uri.toString();
    }

    public String getCleanURL() {
        return uri.buildUpon().clearQuery().fragment("").toString();
    }

    public String getParam(String key) {
        return uri.getQueryParameter(key);
    }

    public Map<String, String> getParams() {
        Set<String> keys = uri.getQueryParameterNames();
        Map<String, String> params = new HashMap<>();
        for (String key : keys) {
            params.put(key, uri.getQueryParameter(key));
        }
        return params;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    public int getFlags() {
        return mFlags;
    }

}
