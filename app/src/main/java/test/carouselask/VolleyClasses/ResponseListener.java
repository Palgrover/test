package test.carouselask.VolleyClasses;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by subodh on 24/6/15.
 */
public abstract class ResponseListener<T> implements Response.Listener<T>, Response.ErrorListener {

    private int statusCode = 0;

    public abstract void onResponse(T response);

    public abstract void onErrorResponse(VolleyError error);

    //access only allowed from same package classes
    void setStatusCode(int code) {
        statusCode = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
