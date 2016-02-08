package test.carouselask.VolleyClasses;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonArray;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import test.carouselask.utils.JsonHelper;

public class MyJsonArrayRequest extends Request<String> {
    private WeakReference<Activity> activityWeakReference = null;
    private WeakReference<Fragment> fragmentWeakReference = null;
    Map<String, String> postParams;
    Response.Listener<JsonArray> jsonArrayListener;
    Response.Listener<String> stringListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            JsonArray result = JsonHelper.StringToJsonArray(response);
            jsonArrayListener.onResponse(result);
        }
    };

    public MyJsonArrayRequest(Activity activity, int method, String url,  Map<String, String> params, ResponseListener<JsonArray> listener) {
        super(method, url, listener);
        jsonArrayListener = listener;
        postParams = params;
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonArrayRequest(Fragment fragment, int method, String url, Map<String, String> params, ResponseListener<JsonArray> listener) {
        super(method, url, listener);
        jsonArrayListener = listener;
        postParams = params;
        Activity activity = fragment.getActivity();
        activityWeakReference = new WeakReference<Activity>(activity);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonArrayRequest(Activity activity, String url,  Map<String, String> params,
                               ResponseListener<JsonArray> responseListener) {
        this(activity, Method.GET, url, params, responseListener);
    }

    public MyJsonArrayRequest(Fragment fragment, String url, Map<String, String> params,
                              ResponseListener<JsonArray> responseListener) {
        this(fragment, Method.GET, url, params, responseListener);
    }

    @Override
    protected void deliverResponse(String response) {
        if(activityWeakReference.get() != null && !activityWeakReference.get().isFinishing()){
            if (fragmentWeakReference != null && fragmentWeakReference.get() != null && fragmentWeakReference.get().isAdded()) {
                stringListener.onResponse(response);
                //complete the callback - was from fragment
            }
            else if (fragmentWeakReference == null){
                stringListener.onResponse(response);
                //complete the callback - was from activity
            }
        }else{
            /* context has died, do nothing */
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if(activityWeakReference.get() != null && !activityWeakReference.get().isFinishing()){
            if (fragmentWeakReference != null && fragmentWeakReference.get() != null && fragmentWeakReference.get().isAdded()) {
                super.deliverError(error);
                //complete the callback - was from fragment
            }
            else if (fragmentWeakReference == null){
                super.deliverError(error);
                //complete the callback - was from activity
            }
        }else{
            /* context has died, do nothing */
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected Map<String, String> getParams() {
        return postParams;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
    }
}
