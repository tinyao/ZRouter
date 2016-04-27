package im.ycz.zrouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import im.ycz.zrouter.utils.ZRouteUtils;

/**
 * Created by xuanqi on 4/26/16.
 */
public class ZFragmentContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_container);
        getSupportActionBar().setTitle("");

        // get fragment name from intent
        Class fragmentClass = (Class<? extends Fragment>) getIntent().getSerializableExtra("fragment");
        if (fragmentClass == null) {
            finish();
            return;
        }

        // Construct a fragment instance
        Fragment fragment = ZRouteUtils.create(fragmentClass, getIntent().getData().toString(), true);

        if (savedInstanceState == null && fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_container, fragment)
                    .commit();
        }
    }

}
