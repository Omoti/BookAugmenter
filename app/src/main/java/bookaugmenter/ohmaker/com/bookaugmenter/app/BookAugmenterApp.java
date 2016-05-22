package bookaugmenter.ohmaker.com.bookaugmenter.app;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Application
 */
public class BookAugmenterApp extends Application{
    private RequestQueue mRequestQueue;

    synchronized public RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(this);
        }

        return mRequestQueue;
    }
}
