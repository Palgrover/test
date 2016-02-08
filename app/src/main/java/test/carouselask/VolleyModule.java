package test.carouselask;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class VolleyModule {
    private Test application;

    public VolleyModule(Test app){
        this.application = app;
    }

    @Provides
    @Singleton
    public RequestQueue provideRequestQueue() {
        return Volley.newRequestQueue(application);
    }

    @Provides
    @Singleton
    public ImageLoader provideImageLoader(RequestQueue reqQueue) {
        return new ImageLoader(reqQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);


            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }
}
