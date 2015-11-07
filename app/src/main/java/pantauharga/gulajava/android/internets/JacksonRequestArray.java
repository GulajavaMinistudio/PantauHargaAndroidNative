package pantauharga.gulajava.android.internets;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.jr.ob.JSON;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by Kucing Imut on 8/25/15.
 */
public class JacksonRequestArray<T> extends Request<List<T>> {


    private final Class<T> clazz;
    private final Response.Listener<List<T>> listener;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final String mRequestBody;

    /**
     * Content type for request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);


    public JacksonRequestArray(int method, String url, Class<T> clazz,
                               Map<String, String> headers,
                               Map<String, String> params,
                               String jsonrequestBody,
                               Response.Listener<List<T>> listener,
                               Response.ErrorListener errorlistener) {
        super(method, url, errorlistener);

        this.clazz = clazz;
        this.listener = listener;

        this.headers = headers;
        this.params = params;
        this.mRequestBody = jsonrequestBody;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params != null ? params : super.getParams();
    }


    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }


    @Override
    protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
        try {

            return Response.success(
                    JSON.std.listOfFrom(clazz, response.data),
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e) {

            return Response.error(new ParseError(e));

        }
    }

    @Override
    protected void deliverResponse(List<T> response) {
        listener.onResponse(response);
    }
}
