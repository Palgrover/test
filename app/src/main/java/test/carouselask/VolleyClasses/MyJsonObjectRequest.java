package test.carouselask.VolleyClasses;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import test.carouselask.JsonHelper;

/**
 * Created by samvedana on 16/6/15.
 */
public class MyJsonObjectRequest extends Request<String> {
    private String mApiVersion;
    private WeakReference<Activity> activityWeakReference = null;
    private WeakReference<Fragment> fragmentWeakReference = null;
    Map<String, String> postParams;
    ResponseListener<JsonObject> jsonObjListener;
    String postRequestBody;
    Response.Listener<String> stringListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            JsonObject result = JsonHelper.StringToJsonObject(response);
            if(jsonObjListener!=null) {
                jsonObjListener.onResponse(result);
            }
        }
    };

    boolean initiatedInBg = false;

    protected static final String PROTOCOL_CHARSET = "utf-8";

    public MyJsonObjectRequest(Activity activity, int method, String url,  Map<String, String> params, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        postParams = params;
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }


    public MyJsonObjectRequest(Activity activity, int method, String url, JsonObject requestBody, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        postRequestBody = requestBody.toString();
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonObjectRequest(int method, String url, JsonObject requestBody, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        postRequestBody = requestBody.toString();
        initiatedInBg = true;
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonObjectRequest(Activity activity, int method, String url, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonObjectRequest(Fragment fragment, int method, String url, Map<String, String> params, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        postParams = params;
        Activity activity = fragment.getActivity();
        activityWeakReference = new WeakReference<Activity>(activity);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonObjectRequest(Fragment fragment, int method, String url, JsonObject requestBody, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        postRequestBody = requestBody.toString();
        Activity activity = fragment.getActivity();
        activityWeakReference = new WeakReference<Activity>(activity);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonObjectRequest(Fragment fragment, int method, String url, ResponseListener<JsonObject> listener) {
        super(method, url, listener);
        jsonObjListener = listener;
        Activity activity = fragment.getActivity();
        activityWeakReference = new WeakReference<Activity>(activity);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public MyJsonObjectRequest(Fragment fragment, int method, String url, String api_version, ResponseListener<JsonObject> listener) {
       // MyJsonObjectRequest(fragment, method, url, api_version);
        super(method, url, listener);
        jsonObjListener = listener;
        Activity activity = fragment.getActivity();
        activityWeakReference = new WeakReference<Activity>(activity);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
        mApiVersion=api_version;
    }

    public MyJsonObjectRequest(Activity activity, String url,  Map<String, String> params,
                               ResponseListener<JsonObject> responseListener) {
        this(activity, Method.GET, url, params, responseListener);
    }

    public MyJsonObjectRequest(Fragment fragment, String url, Map<String, String> params,
                               ResponseListener<JsonObject> responseListener) {
        this(fragment, Method.GET, url, params, responseListener);
    }

    @Override
    protected void deliverResponse(String response) {
        if (!initiatedInBg) {
            if (activityWeakReference.get() != null && !activityWeakReference.get().isFinishing()) {
                if (fragmentWeakReference != null && fragmentWeakReference.get() != null && fragmentWeakReference.get().isAdded()) {
                    stringListener.onResponse(response);
                    //complete the callback - was from fragment
                } else if (fragmentWeakReference == null) {
                    stringListener.onResponse(response);
                    //complete the callback - was from activity
                }
            }
        }
        else {
            stringListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (!initiatedInBg) {
            if (activityWeakReference.get() != null && !activityWeakReference.get().isFinishing()) {
                if (fragmentWeakReference != null && fragmentWeakReference.get() != null && fragmentWeakReference.get().isAdded()) {
                    super.deliverError(error);
                    //complete the callback - was from fragment
                } else if (fragmentWeakReference == null) {
                    super.deliverError(error);
                    //complete the callback - was from activity
                }
            }
        }else {
            super.deliverError(error);
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        if (jsonObjListener != null) {
            jsonObjListener.setStatusCode(response.statusCode);
        }
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {

        if (jsonObjListener != null && volleyError.networkResponse != null) {
            jsonObjListener.setStatusCode(volleyError.networkResponse.statusCode);
        }

        return super.parseNetworkError(volleyError);

    }

    @Override
    protected Map<String, String> getParams() {
        return postParams;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(mApiVersion)) {
            params.put("X-Version", mApiVersion);
        }
        return params;
    };

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (postRequestBody != null) {
            try {
                return postRequestBody.getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        postRequestBody, PROTOCOL_CHARSET);
                return null;
            }
        }
        else {
            return super.getBody();
        }
    }
}
