package pantauharga.gulajava.android.internets;

import com.android.volley.DefaultRetryPolicy;

import pantauharga.gulajava.android.Konstan;

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

    //LAPORKAN HARGA KOMODITAS

    //AMBIL DAFTAR KOMODITAS

    //AMBIL DAFTAR KOMODITAS

    //REGISTER PENGGUNA

    //LOGIN PENGGUNA









}
