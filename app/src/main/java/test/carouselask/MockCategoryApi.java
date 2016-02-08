package test.carouselask;

import com.android.volley.RequestQueue;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import test.carouselask.VolleyClasses.ResponseListener;
import test.carouselask.utils.JsonFileHelper;

/**
 * Created by subodh on 24/3/15.
 */
public class MockCategoryApi {
    Test application;
    @Inject
    RequestQueue requestQ;

    public MockCategoryApi(Test app){
        this.application = app;
        application.getApplicationComponent().inject(this);
    }

    public void loadCategories(ResponseListener<JsonObject> responseListener){
        JsonObject json = JsonFileHelper.loadFromFileAsJsonObject(application, "f_one.txt");
        responseListener.onResponse(json);
    }
}
