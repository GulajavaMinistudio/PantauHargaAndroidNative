package pantauharga.gulajava.android.internets;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.List;
import java.util.Map;

import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItem;
import pantauharga.gulajava.android.modelgson.KomoditasItem;

/**
 * Created by Gulajava Ministudio on 11/6/15.
 */
public class Apis {

    public static int JUMLAH_TIMEOUT = 2500;
    public static int JUMLAH_COBA = 2;
    public static float PENGALI_TIMEOUT = 1;

    public Apis() {
    }


    /** AMBIL LINK **/

    //AMBIL HARGA KOMODITAS TERDEKAT
    ///Api/hargaall.json
    public static String getLinkHargaKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/hargaall.json";
    }


    //LAPORKAN HARGA KOMODITAS
    ///Api/input.json
    public static String getLinkLaporHargaKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/input.json";
    }

    //AMBIL DAFTAR KOMODITAS
    ///Api/comodityall.json
    public static String getLinkDaftarKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/comodityall.json";
    }

    //REGISTER PENGGUNA
    ///Api/register.json
    public static String getLinkRegisterPengguna() {
        return Konstan.ALAMATSERVER + "/Api/register.json";
    }

    //LOGIN PENGGUNA
    ///Api/login.json
    public static String getLinkLoginPengguna() {
        return Konstan.ALAMATSERVER + "/Api/login.json";
    }



    private static DefaultRetryPolicy getRetryPolicy() {

        return new DefaultRetryPolicy(JUMLAH_TIMEOUT, JUMLAH_COBA, PENGALI_TIMEOUT);
    }



    /** AMBIL REQUEST VOLLEY UNTUK KIRIM KE SERVERS  **/

    //AMBIL HARGA KOMODITAS TERDEKAT
    public static GsonRekuestArray<HargaKomoditasItem> postRequestHargaKomoditasSekitar(
            String urls,
            Map<String,String> headers,
            Map<String, String> params,
            String jsonbodystr,
            Response.Listener<List<HargaKomoditasItem>> listenerok,
            Response.ErrorListener listenergagal
    ) {

        GsonRekuestArray<HargaKomoditasItem> gsonRekuestArray = new GsonRekuestArray<>(
                Request.Method.POST,
                urls,
                HargaKomoditasItem.class,
                headers,
                params,
                jsonbodystr,
                listenerok,
                listenergagal
        );

        gsonRekuestArray.setRetryPolicy(getRetryPolicy());

        return gsonRekuestArray;
    }






    //LAPORKAN HARGA KOMODITAS

    //AMBIL DAFTAR KOMODITAS
    public static StrRekuestGet getRequestDaftarKomoditas(String urls, Response.Listener<String> listenerok,
                                                   Response.ErrorListener listenergagal) {

        StrRekuestGet strRekuestGet = new StrRekuestGet(
                Request.Method.GET,
                urls,
                null,
                listenerok,
                listenergagal
        );

        strRekuestGet.setRetryPolicy(getRetryPolicy());

        return strRekuestGet;
    }


    //REGISTER PENGGUNA

    //LOGIN PENGGUNA









}
