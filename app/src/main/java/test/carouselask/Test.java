package test.carouselask;

import android.app.Application;

/**
 * Created by pallavgrover on 2/7/16.
 */
public class Test extends Application {
    private ApplicationComponent applicationComponent;

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public void setApplicationComponent(ApplicationComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpGraph();
    }
    public void setUpGraph(){
        Test that = this;
        applicationComponent = DaggerApplicationComponent.builder()
                .volleyModule(new VolleyModule(that))
                .build();
        applicationComponent.inject(this);
    }
}
