package pantauharga.gulajava.android.internets;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;


/**
 * Created by Kucing Imut on 8/24/15.
 */


public class OkHttpStacks extends HurlStack {


    private OkUrlFactory okFactory;


    public OkHttpStacks() {

        OkHttpClient okHttpKlien = new OkHttpClient();
        okHttpKlien.setConnectTimeout(5, TimeUnit.SECONDS);
        okHttpKlien.setReadTimeout(10, TimeUnit.SECONDS);

        okFactory = new OkUrlFactory(okHttpKlien);
    }


    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        super.createConnection(url);

        HttpURLConnection httpconnect = okFactory.open(url);
        httpconnect.setRequestProperty("Accept-Encoding", "");

        return httpconnect;
    }
}
