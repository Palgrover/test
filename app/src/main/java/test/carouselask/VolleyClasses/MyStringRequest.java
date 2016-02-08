package test.carouselask.VolleyClasses;

import android.support.v4.app.Fragment;

import java.util.Map;


/**
 * Created by navratansoni on 16/10/15.
 */
public class MyStringRequest extends BaseRequest<String> {

    public String convertStringResponseToRequiredFormat(String response) {
        return response;
    }

    public MyStringRequest(Fragment fragment, String url, Map<String, String> params, ResponseListener<String> listener) {
        super(fragment, url, params, listener);
    }

}