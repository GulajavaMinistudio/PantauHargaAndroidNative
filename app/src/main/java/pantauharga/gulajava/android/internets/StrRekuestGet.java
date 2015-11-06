package pantauharga.gulajava.android.internets;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;


/**
 * Created by Kucing Imut on 8/25/15.
 */
public class StrRekuestGet extends Request<String> {


    private final Response.Listener<String> listener;
    private final Map<String, String> headers;

    public StrRekuestGet(int method,

                         String url,
                         Map<String, String> headers,
                         Response.Listener<String> listener, Response.ErrorListener errorlistener) {

        super(method, url, errorlistener);

        this.listener = listener;
        this.headers = headers;
    }


    public StrRekuestGet addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        try {

            InputStream inputStream = new ByteArrayInputStream(response.data);
            BufferedSource bufferedSource = Okio.buffer(Okio.source(inputStream));

            String jsons = bufferedSource.readUtf8();
            bufferedSource.close();

            return Response.success(jsons,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e) {

            return Response.error(new ParseError(e));

        }
    }


    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

}
