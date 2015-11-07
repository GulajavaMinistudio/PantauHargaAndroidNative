package pantauharga.gulajava.android.parsers;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okio.BufferedSource;
import okio.Okio;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.modelgson.KomoditasItem;
import pantauharga.gulajava.android.modelgsonkirim.HargaKomoditasCek;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class Parseran {


    private Context mContext;

    private Gson mGson;

    private List<String> arrStringNamaKomoditas;


    public Parseran(Context context) {
        mContext = context;

        mGson = new Gson();
    }


    public int acakGambar() {

        int[] alamatgambar = {R.drawable.hero1, R.drawable.hero2, R.drawable.hero3};
        int kodegambar = R.drawable.hero1;
        Random random = new Random();

        int posisigambar = 0;
        try {
            posisigambar = random.nextInt(alamatgambar.length + 1);
            kodegambar = alamatgambar[posisigambar];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kodegambar;
    }


    //AMBIL JSON DARI ASET
    public String ambilJsonAset(String namajson) {

        String jsonrespon = "";
        AssetManager asetmanager;
        InputStream inputstreamjson;

        try {

            asetmanager = mContext.getAssets();
            inputstreamjson = asetmanager.open(namajson);

            BufferedSource bufferedSource = Okio.buffer(Okio.source(inputstreamjson));
            jsonrespon = bufferedSource.readUtf8();
            bufferedSource.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            jsonrespon = "";
        }

        return jsonrespon;
    }


    //PARSE JSON DARI DB
    public List<KomoditasItem> parseJsonKomoditas(String json) {

        List<KomoditasItem> komoditasItemList = new ArrayList<>();
        arrStringNamaKomoditas = new ArrayList<>();

        try {

            Type typelist = new TypeToken<List<KomoditasItem>>() {
            }.getType();
            komoditasItemList = mGson.fromJson(json, typelist);

        } catch (Exception ex) {
            ex.printStackTrace();
            komoditasItemList = null;
        }

        if (komoditasItemList != null && komoditasItemList.size() > 0) {

            int panjangarray = komoditasItemList.size();

            for (int i = 0; i < panjangarray; i++) {

                KomoditasItem komoditasItem = komoditasItemList.get(i);
                String namakomoditas = komoditasItem.getName();

                arrStringNamaKomoditas.add(namakomoditas);
            }

            setArrStringNamaKomoditas(arrStringNamaKomoditas);

        }

        return komoditasItemList;
    }

    public List<String> getArrStringNamaKomoditas() {
        return arrStringNamaKomoditas;
    }

    public void setArrStringNamaKomoditas(List<String> arrStringNamaKomoditas) {
        this.arrStringNamaKomoditas = arrStringNamaKomoditas;
    }


    //CEK STATUS DATA WAKTUNYA
    public boolean isCekKadaluarsa(String longstrdatadb, long longwaktusekarang) {

        boolean isKadaluarsa;

        Log.w("WAKTU DB", "waktu db " + longstrdatadb);

        try {

            long longdatawaktu = Long.valueOf(longstrdatadb);
            long longwaktucek = longdatawaktu + Konstan.TAG_SATUJAM;

            isKadaluarsa = longwaktucek < longwaktusekarang;

        } catch (Exception ex) {
            ex.printStackTrace();
            isKadaluarsa = false;
        }

        Log.w("STATUS KADALUARSA DATA", "status kadaluarsa data " + isKadaluarsa);

        return isKadaluarsa;
    }


    //KONVERSI JSON KOMODITAS KE JSON
    public String konversiPojoJsonCekKomoditas(HargaKomoditasCek hargaKomoditasCek) {

        String json = "";

        try {

            json = mGson.toJson(hargaKomoditasCek);

        } catch (Exception ex) {
            ex.printStackTrace();
            json = "";
        }

        return json;
    }


}
