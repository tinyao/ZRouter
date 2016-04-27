package im.ycz.zrouter;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by xuanqi on 4/26/16.
 */
public class RoutableFragment extends Fragment {

    private static final String ARG_URL = "url_to_load";
    private static final String ARG_STANDALONE = "is_standalone";

    public static RoutableFragment newInstance(String url, boolean standalone) {
        RoutableFragment fragment = new RoutableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putBoolean(ARG_STANDALONE, standalone);
        fragment.setArguments(args);
        return fragment;
    }

    public RoutableFragment() {}

}
