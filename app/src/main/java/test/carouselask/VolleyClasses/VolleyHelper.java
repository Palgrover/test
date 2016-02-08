package test.carouselask.VolleyClasses;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import test.carouselask.utils.TimeoutValues;


public class VolleyHelper {
    //http://stackoverflow.com/questions/17094718/android-volley-timeout has an answer explaining the calculations

    /*
    if we want two retry attempts to take no more than 30 seconds, and the 1st attempt to say take 12 secs max
    eqns are
    t(b +1) = 12
    t(b+1)^2 + t(b+1) = 30

    Solved to give t=8 and b = 0.5
     */
    public static RetryPolicy generateDefaultRetryPolicy() {
        return new DefaultRetryPolicy(TimeoutValues.BASE_TIMEOUT, TimeoutValues.NUM_RETRIES, TimeoutValues.BACK_OFF_MULTIPLIER);

    }
}
