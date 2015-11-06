package pantauharga.gulajava.android.internets;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

/**
 * Created by Kucing Imut on 8/25/15.
 */
public class GsonRekuestArray<T> extends Request<List<T>> {

    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Response.Listener<List<T>> listener;
    private final String mJsonRequestBody;

    /**
     * Content type for request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);


    public GsonRekuestArray(int method,

                            String url,
                            Class<T> clazz,
                            Map<String, String> headers,
                            Map<String, String> parameterbody,
                            String jsonRequestBody,

                            Response.Listener<List<T>> listener, Response.ErrorListener errorlistener) {

        super(method, url, errorlistener);

        gson = new Gson();
        this.clazz = clazz;
        this.headers = headers;
        this.params = parameterbody;
        this.mJsonRequestBody = jsonRequestBody;
        this.listener = listener;
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

            return mJsonRequestBody == null ? null : mJsonRequestBody.getBytes(PROTOCOL_CHARSET);

        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mJsonRequestBody,
                    PROTOCOL_CHARSET);
            return null;
        }
    }


    @Override
    protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
        try {

            InputStream inputStream = new ByteArrayInputStream(response.data);
            BufferedSource bufferedSource = Okio.buffer(Okio.source(inputStream));

            String json = bufferedSource.readUtf8();
            bufferedSource.close();

            //ambil type java reflect dari array list untuk parse dengan gson
            Type typearray = new TypeToken<List<T>>(){}.getType();
            List<T> listTs = gson.fromJson(json, typearray);

            return Response.success(listTs,
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
