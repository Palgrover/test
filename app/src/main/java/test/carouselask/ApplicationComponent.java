package test.carouselask;

import javax.inject.Singleton;

import dagger.Component;


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

    //RequestQueue requestQueue();
    //MockCategoryApi mockCategoryApi();
}
