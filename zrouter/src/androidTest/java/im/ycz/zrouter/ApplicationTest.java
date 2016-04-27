package im.ycz.zrouter;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import java.util.HashSet;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

//        ZRouter.from(ApplicationTest..)
//                .assets("routes.json")
//                .domain("beta.hello.com")
//                .accept(new HashSet<String>())
//                .debug(true)
//                .defaultRoute(new ZRoute("default", H5ContainerFragment.class))
//                .initialize();

    }
}