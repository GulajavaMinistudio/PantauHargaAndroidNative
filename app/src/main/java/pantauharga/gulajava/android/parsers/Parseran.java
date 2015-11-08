package pantauharga.gulajava.android.parsers;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okio.BufferedSource;
import okio.Okio;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItem;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItemKomparator;
import pantauharga.gulajava.android.modelgson.KomoditasItem;
import pantauharga.gulajava.android.modelgsonkirim.HargaKomoditasCek;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class Parseran {


    private Context mContext;


    private ArrayList<String> arrStringNamaKomoditas;


    public Parseran(Context context) {
        mContext = context;
    }


    public int acakGambar() {

        int[] alamatgambar = {R.drawable.hero1, R.drawable.hero2, R.drawable.hero3};
        int kodegambar = R.drawable.hero1;
        Random random = new Random();

        int posisigambar = 0;
        try {
            posisigambar = random.nextInt(alamatgambar.length);
            kodegambar = alamatgambar[posisigambar];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kodegambar;
    }

    public int acakGambarList() {

        int[] alamatgambar = {R.drawable.ic_action_keranjang1, R.drawable.ic_action_keranjang2};
        int kodegambar = R.drawable.ic_action_keranjang1;
        Random random = new Random();

        int posisigambar = 0;
        try {
            posisigambar = random.nextInt(alamatgambar.length);
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

            komoditasItemList = JSON.std.listOfFrom(KomoditasItem.class, json);

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


    public ArrayList<String> getArrStringNamaKomoditas() {
        return arrStringNamaKomoditas;
    }

    public void setArrStringNamaKomoditas(ArrayList<String> arrStringNamaKomoditas) {
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

            json = JSON.std.asString(hargaKomoditasCek);

        } catch (Exception ex) {
            ex.printStackTrace();
            json = "";
        }

        return json;
    }


    //PARSE LIST KOMODITAS KE MODE LIST KOMPARATOR
    public List<HargaKomoditasItemKomparator> parseListKomparator(List<HargaKomoditasItem> hargaKomoditasItemList,
                                                                  Location locationSaya, int modelist) {

        List<HargaKomoditasItemKomparator> listkomparator = new ArrayList<>();
        HargaKomoditasItemKomparator hargaKomoditasItemKomparator;

        String barang = "";
        String latitude = "";
        String longitude = "";
        String nohp = "";
        int price = 0;

        float jaraklokasi = 0;

        Location lokasicekkomoditas = new Location("");

        if (hargaKomoditasItemList != null) {

            int panjangarray = hargaKomoditasItemList.size();

            try {

                for (int i = 0; i < panjangarray; i++) {

                    HargaKomoditasItem hargaKomoditasItem = hargaKomoditasItemList.get(i);
                    barang = hargaKomoditasItem.getBarang();
                    latitude = hargaKomoditasItem.getLatitude();
                    longitude = hargaKomoditasItem.getLongitude();
                    nohp = hargaKomoditasItem.getNohp();
                    price = hargaKomoditasItem.getPrice();

                    double dolatitude = Double.valueOf(latitude);
                    double dolongitude = Double.valueOf(longitude);

                    lokasicekkomoditas.setLatitude(dolatitude);
                    lokasicekkomoditas.setLongitude(dolongitude);

                    jaraklokasi = locationSaya.distanceTo(lokasicekkomoditas);

                    hargaKomoditasItemKomparator = new HargaKomoditasItemKomparator();
                    hargaKomoditasItemKomparator.setBarang(barang);
                    hargaKomoditasItemKomparator.setLatitude(latitude);
                    hargaKomoditasItemKomparator.setLongitude(longitude);
                    hargaKomoditasItemKomparator.setNohp(nohp);
                    hargaKomoditasItemKomparator.setPrice(price);
                    hargaKomoditasItemKomparator.setJaraklokasi(jaraklokasi + "");

                    Log.w("HARGA BARANG SIMPAN ", "" + barang + " " + price);

                    listkomparator.add(hargaKomoditasItemKomparator);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listkomparator = null;
            }

            if (listkomparator != null) {

                try {
                    switch (modelist) {

                        case Konstan.MODE_TERDEKAT:

                            Collections.sort(listkomparator, HargaKomoditasItemKomparator.komparatorJarakTerdekat);
                            break;

                        case Konstan.MODE_TERMURAH:

                            Collections.sort(listkomparator, HargaKomoditasItemKomparator.komparatorHargaMurah);
                            break;

                        case Konstan.MODE_TERMAHAL:

                            Collections.sort(listkomparator, HargaKomoditasItemKomparator.komparatorHargaMahal);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listkomparator = null;
                }
            }
        } else {
            listkomparator = null;
        }

        return listkomparator;
    }


    //FORMAT TEKS
    public static class NumberTextWatcher implements TextWatcher {

        private DecimalFormat df;
        private DecimalFormat dfnd;
        private boolean hasFractionalPart;

        private EditText et;

        public NumberTextWatcher(EditText et) {
            df = new DecimalFormat("#,###.##");
            df.setDecimalSeparatorAlwaysShown(true);
            dfnd = new DecimalFormat("#,###");
            this.et = et;
            hasFractionalPart = false;
        }

        @SuppressWarnings("unused")
        private static final String TAG = "NumberTextWatcher";

        public void afterTextChanged(Editable s) {

            et.removeTextChangedListener(this);

            try {

                int inilen, endlen;
                inilen = et.getText().length();

                String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                Number n = df.parse(v);

                int cp = et.getSelectionStart();
                if (hasFractionalPart) {
                    et.setText(df.format(n));
                } else {
                    et.setText(dfnd.format(n));
                }

                endlen = et.getText().length();
                int sel = (cp + (endlen - inilen));

                if (sel > 0 && sel <= et.getText().length()) {
                    et.setSelection(sel);
                } else {
                    // place cursor at the end?
                    et.setSelection(et.getText().length() - 1);
                }

            } catch (Exception nfe) {
                // do nothing?
            }

            et.addTextChangedListener(this);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            hasFractionalPart = s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
        }
    }


    //TEXTVIEW SEPARATOR
    public String formatAngkaPisah(int angkastr) {

        String bilanganpisah = "";

        try {
            bilanganpisah = String.format(Locale.getDefault(), "%,d", angkastr).replace(",", ".");
        } catch (Exception e) {
            e.printStackTrace();
            bilanganpisah = "0";
        }

        return bilanganpisah;
    }


    //UNTUK TEXTVIEW YANG UKURAN KILO
//    public String formatAngkaKilo(int angka) {
//
//        String angkabulatan = "";
//
//        try {
//            Double angkadouble = Double.valueOf(angka + "");
//            double angkabaru = angkadouble / 1000;
////            angkabulatan = pembulatanJarak("" + angkabaru, 3).replace(".", ",");
//            angkabulatan = ("" + angkabaru).replace(".", ",");
//        } catch (Exception e) {
//            e.printStackTrace();
//            angkabulatan = "0";
//        }
//
//        return angkabulatan;
//    }


    //FUNGSI PEMBULAT ANGKA
//    public String pembulatanJarak(String stringnilaiawal, int jumlahpembulatan) {
//
//        double dovalbelumbulat = Double.valueOf(stringnilaiawal);
//
//        BigDecimal bigdesimal = new BigDecimal(dovalbelumbulat);
//        bigdesimal = bigdesimal.setScale(jumlahpembulatan, RoundingMode.UP);
//
//        return bigdesimal.intValue() + "";
//    }


}
