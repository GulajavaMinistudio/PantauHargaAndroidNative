package pantauharga.gulajava.android.internets;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Gulajava Ministudio on 8/25/15.
 */
public class Volleys {


    private static final String TAG = Volleys.class.getSimpleName();

    private static Volleys volleys;
    private RequestQueue requestQueue;
    private static Context context;
    private OkHttpStacks okHttpStacks;


    public Volleys(Context ctx) {

        context = ctx;
        okHttpStacks = new OkHttpStacks();
    }

    public static synchronized Volleys getInstance(Context ctx) {

        if (volleys == null) {
            volleys = new Volleys(ctx);
        }

        return volleys;
    }


    private RequestQueue getRequestQueue() {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext(), okHttpStacks);
        }

        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {

        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public void cancelPendingRequestsNoTag() {
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }


    public void clearVolleyCache() {

        if (requestQueue != null) {
            requestQueue.getCache().clear();
        }
    }


}
