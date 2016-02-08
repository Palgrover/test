package test.carouselask;

import com.android.volley.RequestQueue;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by subodh on 4/7/15.
 */
@Singleton
@Component(
        modules = {
                VolleyModule.class
        }
)
public interface ApplicationComponent {

    /* the app */
    void inject(Test app);
    void inject(MockCategoryApi api);

    RequestQueue requestQueue();
    MockCategoryApi mockCategoryApi();
}
