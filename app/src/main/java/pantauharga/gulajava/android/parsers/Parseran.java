package pantauharga.gulajava.android.parsers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pantauharga.gulajava.android.internets.GsonRekuest;
import pantauharga.gulajava.android.modelgson.KomoditasItem;

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



    public List<KomoditasItem> parseJsonKomoditas(String json) {

        List<KomoditasItem> komoditasItemList = new ArrayList<>();
        arrStringNamaKomoditas = new ArrayList<>();

        try {

            Type typelist = new TypeToken<List<KomoditasItem>>(){}.getType();
            komoditasItemList = mGson.fromJson(json, typelist);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            komoditasItemList = null;
        }

        if (komoditasItemList != null && komoditasItemList.size() > 0) {

            int panjangarray = komoditasItemList.size();

            for (int i=0; i < panjangarray; i++) {

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









}
