package test.carouselask.VolleyClasses;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Pair;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by subodh on 19/10/15.
 */

/**
 * Base class for all network requests.
 *
 * @param <T> The type of parsed response this request expects.
 */
public abstract class BaseRequest<T> extends Request<String> {
    private static final String CONTENT_TYPE = "Content-type";

    private WeakReference<Activity> activityWeakReference = null;
    private WeakReference<Fragment> fragmentWeakReference = null;
    Map<String, String> postParams;
    Response.Listener<T> baseListener;
    String postRequestBody;
    Response.Listener<String> stringListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            baseListener.onResponse(convertStringResponseToRequiredFormat(response));
        }
    };

    ArrayList<Pair<String, String>> custom_headers = new ArrayList<>();
    String content_type = null;

    boolean initiatedInBg = false;

    protected static final String PROTOCOL_CHARSET = "utf-8";

    /**
     *
     * @param response, actual String delivered by the network call
     * @return <T> convert response to The type of parsed response this request expects.
     */
    public abstract T convertStringResponseToRequiredFormat(String response);

    public BaseRequest(Activity activity, String url, String requestBody, ResponseListener<T> listener) {
        super(Method.POST, url, listener);
        baseListener = listener;
        postRequestBody = requestBody;
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public BaseRequest(Fragment fragment, String url, String requestBody, ResponseListener<T> listener) {
        this(fragment.getActivity(), url, requestBody, listener);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
    }

    public BaseRequest(Activity activity, String url, JsonObject requestBody, ResponseListener<T> listener) {
        this(activity, url, requestBody.toString(), listener);
    }

    public BaseRequest(Fragment fragment, String url, JsonObject requestBody, ResponseListener<T> listener) {
        this(fragment.getActivity(), url, requestBody, listener);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
    }

    public BaseRequest(Fragment fragment, String url, Map<String, String> params, ResponseListener<T> listener) {
        this(fragment.getActivity(), url, params, listener);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
    }

    public BaseRequest(Activity activity, String url,  Map<String, String> params, ResponseListener<T> listener) {
        super(Method.POST, url, listener);
        baseListener = listener;
        postParams = params;
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
    }

    public BaseRequest(Fragment fragment, String url, ResponseListener<T> listener) {
        this(fragment.getActivity(), url, listener);
        fragmentWeakReference = new WeakReference<Fragment>(fragment);
    }

    public BaseRequest(Activity activity, String url, ResponseListener<T> listener) {
        super(Method.GET, url, listener);
        baseListener = listener;
        activityWeakReference = new WeakReference<Activity>(activity);
        this.setRetryPolicy(VolleyHelper.generateDefaultRetryPolicy());
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
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        for (Pair<String, String> header : custom_headers) {
            headers.put(header.first, header.second);
        }
        return headers;
    }

    public void addCustom_header(String key, String value) {
        custom_headers.add(new Pair<String, String>(key, value));
        if (key.equals(CONTENT_TYPE)) {
            content_type = value;
        }
    }

    @Override
    protected Map<String, String> getParams() {
        return postParams;
    }

    public String getDefaultBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    public String getBodyContentType() {
        if (content_type == null)
            return getDefaultBodyContentType();
        else {
            return content_type;
        }
    }

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

    public void addAuthHeader(String username, String password) {
        String creds = String.format("%s:%s",username,password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        addCustom_header("Authorization", auth);
    }
}