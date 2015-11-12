package pantauharga.gulajava.android.aktivitas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.databases.RMDataRiwayat;
import pantauharga.gulajava.android.databases.RMJsonData;
import pantauharga.gulajava.android.databases.RMLogin;
import pantauharga.gulajava.android.dialogs.DialogHapusDataIni;
import pantauharga.gulajava.android.dialogs.DialogOkKirimEdit;
import pantauharga.gulajava.android.internets.Apis;
import pantauharga.gulajava.android.internets.JacksonRequest;
import pantauharga.gulajava.android.internets.Volleys;
import pantauharga.gulajava.android.messagebus.MessageBusAktAkt;
import pantauharga.gulajava.android.modelgson.HargaKomoditasLapor;
import pantauharga.gulajava.android.modelgson.KomoditasItem;
import pantauharga.gulajava.android.modelgsonkirim.HargaKomoditasKirim;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/10/15.
 */
public class LaporHargaEdit extends BaseActivityLocation {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private ActionBar aksibar;

    @Bind(R.id.spin_namakomoditas)
    Spinner spinkomoditas;
    @Bind(R.id.edit_harga)
    EditText edit_harga;
    @Bind(R.id.edit_jumlahkomoditas)
    EditText edit_jumlahkomoditas;
    @Bind(R.id.edit_koordinatlokasi)
    EditText edit_kordinatlokasi;


    @Bind(R.id.tombol_setelpeta)
    ImageButton tombolsetelpeta;
    @Bind(R.id.tombol_kirim)
    Button tombolkirim;

    @Bind(R.id.tombol_simpandraft)
    Button tomboldraft;

    @Bind(R.id.layout_jumlahkomoditas)
    LinearLayout layout_jumlahkomoditas;

    @Bind(R.id.layout_tombolkirimdraft)
    LinearLayout layout_tombolkirimdraft;


    private String idkomoditas = "";
    private String namakomoditas = "";
    private String hargakomoditas = "";
    private String jumlahkomoditas = "0";
    private String namalokasi = "";
    private String latitude = "0";
    private String longitude = "0";


    //database
    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    private RealmQuery<RMJsonData> mRealmQueryJsonData;
    private RealmResults<RMJsonData> mRealmResultsJsonData;

    private RealmQuery<RMDataRiwayat> mRealmQueryRiwayat;
    private RealmResults<RMDataRiwayat> mRealmResultsRiwayat;

    private String datakirim_nohp = "0";
    private String jsondata_komoditas = "";

    private List<KomoditasItem> mListKomoditasItem;
    private List<String> mStringListNamaKomoditas;
    private ArrayAdapter<String> mAdapterSpin;

    private Parseran mParseran;

    private boolean isAktJalan = false;
    private boolean isProsesKirim = false;
    private boolean isInternet = false;
    private boolean isLokasiSetels = false;


    //lokasi pengguna
    private double longitudepengguna = 0;
    private double latitudepengguna = 0;


    //GEOCODER AMBIL LOKASI DAN POSISI PENGGUNA ALAMATNYA JIKA ADA
    private Geocoder geocoderPengguna;
    private List<Address> addressListPengguna = null;
    private String gecoder_alamat = "";
    private String gecoder_namakota = "";
    private String alamatgabungan = "";
    private String alamatgabungansetel = "";


    private ProgressDialog mProgressDialog;


    Runnable jedamuatawal = new Runnable() {
        @Override
        public void run() {

            ambilDataDatabase();
        }
    };


    private int kode_kirimKomoditas = Konstan.KODE_KIRIMHARGAJUALKOMO_AKT;


    //simpan database
    private String datasimpan_id = "";
    private String datasimpan_harga = "";
    private String datasimpan_lat = "";
    private String datasimpan_lng = "";
    private String datasimpan_nohp = "";
    private String datasimpan_quantity = "";


    //BUNDEL DATA EDIT
    private Bundle bundels;
    private boolean isKirim = false;
    private boolean isDraft = false;
    private int posisiklik = 0;
    private int posisispin = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_laporharga);
        ButterKnife.bind(LaporHargaEdit.this);
        munculMenuAction(LaporHargaEdit.this);


        if (toolbar != null) {
            LaporHargaEdit.this.setSupportActionBar(toolbar);
        }

        bundels = LaporHargaEdit.this.getIntent().getExtras();
        posisiklik = bundels.getInt(Konstan.TAG_INTENT_EDIT_POSISIDB);


        aksibar = LaporHargaEdit.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.lapor_hargajuduledit);

        mRealm = Realm.getInstance(LaporHargaEdit.this);
        mParseran = new Parseran(LaporHargaEdit.this);

        isAktJalan = true;

        if (kode_kirimKomoditas == Konstan.KODE_KIRIMHARGA_AKT) {
            layout_jumlahkomoditas.setVisibility(View.GONE);
        } else {
            layout_jumlahkomoditas.setVisibility(View.VISIBLE);
        }


        edit_harga.addTextChangedListener(new Parseran.NumberTextWatcher(edit_harga));
        edit_kordinatlokasi.setEnabled(false);

        //cek permisi lokasi tapi dengan jeda dulu
        Handler handler = new Handler();
        handler.postDelayed(jedamuatawal, 500);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(LaporHargaEdit.this)) {
            EventBus.getDefault().register(LaporHargaEdit.this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(LaporHargaEdit.this)) {
            EventBus.getDefault().unregister(LaporHargaEdit.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        isAktJalan = false;

        hentikanListenerLokasi();

        Volleys.getInstance(LaporHargaEdit.this).cancelPendingRequestsNoTag();
        Volleys.getInstance(LaporHargaEdit.this).clearVolleyCache();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LaporHargaEdit.this.getMenuInflater().inflate(R.menu.menu_hapusdataini, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                LaporHargaEdit.this.finish();
                return true;

            case R.id.action_riwayathapus:

                tampilDialogHapusDataIni();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * ====== AMBIL DATA DARI DATABASE DAN PARSE JSON KOMODITAS =========
     ***/


    //AMBIL DATA DATABASE
    private void ambilDataDatabase() {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            RMLogin rmLogin = mRealmResultsLogin.first();
            datakirim_nohp = rmLogin.getNohp();

        } else {
            datakirim_nohp = "0";
        }

        mRealmQueryJsonData = mRealm.where(RMJsonData.class);
        mRealmResultsJsonData = mRealmQueryJsonData.findAll();

        if (mRealmResultsJsonData.size() > 0) {

            RMJsonData rmJsonData = mRealmQueryJsonData.findFirst();
            jsondata_komoditas = rmJsonData.getJsonkomoditas();

        } else {
            jsondata_komoditas = "";
        }

        if (jsondata_komoditas.length() > 5) {

            //parse data
            parseJSONDataKomoditas();

        } else {
            //muncul snackbar peringatan gagal data
            munculSnackbar(R.string.toastgagaldata);
        }

    }


    //AMBIL DATA JSON RIWAYAT
    private void ambilDataRiwayat() {

        mRealmQueryRiwayat = mRealm.where(RMDataRiwayat.class);
        mRealmResultsRiwayat = mRealmQueryRiwayat.findAll();

        if (mRealmResultsRiwayat.size() > 0) {

            RMDataRiwayat rmDataRiwayat = mRealmResultsRiwayat.get(posisiklik);
            idkomoditas = rmDataRiwayat.getId();
            namakomoditas = rmDataRiwayat.getNamakomoditas();
            hargakomoditas = rmDataRiwayat.getHarga();
            jumlahkomoditas = rmDataRiwayat.getQuantity();
            namalokasi = rmDataRiwayat.getAlamatkomoditas();
            latitude = rmDataRiwayat.getLat();
            longitude = rmDataRiwayat.getLng();
            isKirim = rmDataRiwayat.isKirim();
            isDraft = rmDataRiwayat.isDraft();

            setelTampilan();
        }
    }


    //SETEL TAMPILAN
    private void setelTampilan() {

        int panjangarraykomods = mListKomoditasItem.size();

        for (int i = 0; i < panjangarraykomods; i++) {

            KomoditasItem komoditasItem = mListKomoditasItem.get(i);
            String idkomoditasarr = komoditasItem.getId();

            if (idkomoditas.contentEquals(idkomoditasarr)) {
                posisispin = i;
                break;
            }
        }

        spinkomoditas.setSelection(posisispin);

        edit_harga.setText(hargakomoditas);

        edit_jumlahkomoditas.setText(jumlahkomoditas);

        setelEditTextLokasi(latitude + " , " + longitude);
        taskAmbilGeocoder(latitude, longitude);

        if (isKirim && !isDraft) {
            spinkomoditas.setEnabled(false);
            edit_harga.setEnabled(false);
            edit_jumlahkomoditas.setEnabled(false);
            edit_kordinatlokasi.setEnabled(false);

            layout_tombolkirimdraft.setVisibility(View.GONE);
        } else {
            spinkomoditas.setEnabled(true);
            edit_harga.setEnabled(true);
            edit_jumlahkomoditas.setEnabled(true);
            edit_kordinatlokasi.setEnabled(true);

            layout_tombolkirimdraft.setVisibility(View.VISIBLE);
        }

        try {
            latitudepengguna = Double.valueOf(latitude);
            longitudepengguna = Double.valueOf(longitude);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    //PARSE DATA JSON KOMODITAS
    private void parseJSONDataKomoditas() {

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                mListKomoditasItem = mParseran.parseJsonKomoditas(jsondata_komoditas);

                return null;
            }
        })

                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(Task<Object> task) throws Exception {

                        if (mListKomoditasItem != null && mListKomoditasItem.size() > 0) {

                            mStringListNamaKomoditas = mParseran.getArrStringNamaKomoditas();

                            tampilSpinnerList();

                            //cek permisi lokasi
                            cekPermissionLokasi();

                        } else {
                            //gagal parse dan snackbar
                            munculSnackbar(R.string.toastgagaldata);
                        }

                        return null;

                    }
                }, Task.UI_THREAD_EXECUTOR);

    }


    //TAMPILKAN KE SPINNER
    private void tampilSpinnerList() {

        mAdapterSpin = new ArrayAdapter<>(LaporHargaEdit.this, android.R.layout.simple_spinner_item, mStringListNamaKomoditas);
        mAdapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinkomoditas.setAdapter(mAdapterSpin);
        spinkomoditas.setOnItemSelectedListener(listenerspinner);
    }


    /**
     * TERIMA PESAN DARI LISTENER LOKASI PENGGUNA
     **/
    public void onEvent(MessageBusAktAkt messageBusAktAkt) {

        int kodeperintah = messageBusAktAkt.getKode();

        //setel listener tombol
        tombolkirim.setOnClickListener(listenertombol);
        tombolsetelpeta.setOnClickListener(listenertombol);
        tomboldraft.setOnClickListener(listenertombol);
        tombolsetelpeta.setOnClickListener(listenertombol);

        isInternet = isInternet();

        if (kodeperintah == Konstan.KODE_LOKASIAWAL) {

            ambilDataRiwayat();
        }
    }


    //SUNTING LOKASI DENGAN PETA
    private void setelLokasiPeta() {

        Intent intentkordinat = new Intent(LaporHargaEdit.this, PetaAmbilLokasi.class);
        intentkordinat.putExtra(Konstan.TAG_INTENT_EDIT_KIRIM, isKirim);
        intentkordinat.putExtra(Konstan.TAG_INTENT_EDIT_DRAFTKIRIM, isDraft);
        intentkordinat.putExtra(Konstan.TAG_INTENT_LATEDIT, latitudepengguna + "");
        intentkordinat.putExtra(Konstan.TAG_INTENT_LONGEDIT, longitudepengguna + "");

        LaporHargaEdit.this.startActivityForResult(intentkordinat, Konstan.REQUEST_CODE_LOCATION);
    }


    //HASIL SUNTING LOKASI DENGAN PETA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Konstan.REQUEST_CODE_LOCATION) {

                Bundle bundels = data.getExtras();
                String str_latitudesetel = bundels.getString(Konstan.TAG_INTENT_LAT);
                String str_longitudesetel = bundels.getString(Konstan.TAG_INTENT_LONG);
                alamatgabungansetel = bundels.getString(Konstan.TAG_INTENT_ALAMATGABUNGANS);

                Log.w("LOKASI SETEL", "lokasi setel " + str_latitudesetel + " , " + str_longitudesetel);

                double dolatsetel = 0;
                double dolongisetel = 0;

                try {
                    if (str_latitudesetel != null && str_longitudesetel != null) {
                        dolatsetel = Double.valueOf(str_latitudesetel);
                        dolongisetel = Double.valueOf(str_longitudesetel);
                    } else {
                        dolatsetel = 0;
                        dolongisetel = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dolatsetel = 0;
                    dolongisetel = 0;
                }

                if (dolatsetel != 0 && dolongisetel != 0) {

                    isLokasiSetels = true;

                    LaporHargaEdit.this.latitudepengguna = dolatsetel;
                    LaporHargaEdit.this.longitudepengguna = dolongisetel;

                    setelEditTextLokasi(latitudepengguna + " , " + longitudepengguna);

                    //ambil geocoder
                    taskAmbilGeocoder("" + latitudepengguna, "" + longitudepengguna);
                }

            }
        }
    }


    //AMBIL DATA DARI MASUKAN PENGGUNA
    private void ambilDataPengguna() {

        hargakomoditas = edit_harga.getText().toString().replace(",", "").replace(".", "");

        if (kode_kirimKomoditas == Konstan.KODE_KIRIMHARGAJUALKOMO_AKT) {
            jumlahkomoditas = edit_jumlahkomoditas.getText().toString();
        } else {
            jumlahkomoditas = "0";
        }

        namalokasi = alamatgabungan;
        latitude = "" + latitudepengguna;
        longitude = "" + longitudepengguna;

        boolean isValids = false;

        if (hargakomoditas.length() < 4) {
            tampilPeringatanGagalIsi(edit_harga, R.string.lapor_toast_gagalharga);
        } else if (kode_kirimKomoditas == Konstan.KODE_KIRIMHARGAJUALKOMO_AKT && jumlahkomoditas.length() < 1) {
            tampilPeringatanGagalIsi(edit_jumlahkomoditas, R.string.lapor_toast_jumlahkomod);
        } else if (namalokasi.length() < 4) {
            tampilPeringatanGagalIsi(edit_kordinatlokasi, R.string.lapor_toast_gagalokasi);
        } else if (latitude.length() < 3 || longitude.length() < 3) {
            tampilPeringatanGagalIsi(edit_kordinatlokasi, R.string.lapor_toast_gagalokasi);
        } else {
            isValids = true;
        }

        if (isValids && !isProsesKirim) {

            //kirim ke server
            cekKoneksiInternet();

        }
    }


    //AMBIL DATA DARI MASUKAN PENGGUNA
    private void ambilDataPenggunaDraft() {

        hargakomoditas = edit_harga.getText().toString().replace(",", "").replace(".", "");

        if (kode_kirimKomoditas == Konstan.KODE_KIRIMHARGAJUALKOMO_AKT) {
            jumlahkomoditas = edit_jumlahkomoditas.getText().toString();
        } else {
            jumlahkomoditas = "0";
        }

        namalokasi = alamatgabungan;
        latitude = "" + latitudepengguna;
        longitude = "" + longitudepengguna;

        simpanDatabase(true, false, idkomoditas, namakomoditas, latitude, longitude,
                namalokasi, datakirim_nohp, hargakomoditas, jumlahkomoditas);
    }


    //PERINGATAN JIKA SALAH ISI
    private void tampilPeringatanGagalIsi(EditText edit, int resPesan) {
        edit.requestFocus();
        munculSnackbar(resPesan);
    }


    //CEK KONEKSI INTERNET
    private void cekKoneksiInternet() {

        if (isInternet) {
            isProsesKirim = true;
            susunJsonKirimHarga();
        } else {
            isProsesKirim = false;
            munculSnackbar(R.string.toastnointernet);
        }
    }


    //SUSUN JSON DATA KIRIM HARGA
    private void susunJsonKirimHarga() {

        //progress dialog
        tampilProgressDialog("Mengirim data laporan...");

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                //{"id": "1034","lat":"-6.217","lng":"106.9","nohp":08123123,"harga":"20000","quantity":"30"}
                HargaKomoditasKirim hargaKomoditasKirim = new HargaKomoditasKirim();
                hargaKomoditasKirim.setId(idkomoditas);
                hargaKomoditasKirim.setLat(latitude);
                hargaKomoditasKirim.setLng(longitude);
                hargaKomoditasKirim.setNohp(datakirim_nohp);
                hargaKomoditasKirim.setHarga(hargakomoditas);
                hargaKomoditasKirim.setQuantity(jumlahkomoditas);

                return mParseran.konversiPojoKirimHarga(hargaKomoditasKirim);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String hasiljsons = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + hasiljsons);

                        //kirim ke server
                        kirimDataServer(hasiljsons);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    //KIRIM DATA KE SERVER
    private void kirimDataServer(String jsonbody) {

        String urls = Apis.getLinkLaporHargaKomoditas();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<HargaKomoditasLapor> jacksonRequest = Apis.postRequestHargaLapor(
                urls,
                headers,
                parameters,
                jsonbody,
                new Response.Listener<HargaKomoditasLapor>() {
                    @Override
                    public void onResponse(HargaKomoditasLapor response) {

                        Log.w("SUKSES", "SUKSES");
                        if (isAktJalan) {
                            cekHasilRespon(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Log.w("GAGAL", "GAGAL");
                        if (isAktJalan) {
                            cekHasilRespon(null);
                        }
                    }
                }
        );

        Volleys.getInstance(LaporHargaEdit.this).addToRequestQueue(jacksonRequest);
    }


    //CEK HASIL RESPON DARI VOLLEY
    private void cekHasilRespon(HargaKomoditasLapor hargaKomoditasLapor) {

        if (hargaKomoditasLapor != null) {

            datasimpan_id = hargaKomoditasLapor.getId();
            datasimpan_harga = hargaKomoditasLapor.getHarga() + "";
            datasimpan_lat = hargaKomoditasLapor.getLat() + "";
            datasimpan_lng = hargaKomoditasLapor.getLng() + "";
            datasimpan_nohp = hargaKomoditasLapor.getNohp();
            datasimpan_quantity = hargaKomoditasLapor.getQuantity() + "";

            Log.w("HASIL HARGA", datasimpan_id + " harga " + datasimpan_harga + " jumlah "
                    + datasimpan_quantity + " lat " + datasimpan_lat + " lng " + datasimpan_lng);

            if (datakirim_nohp.length() > 1 && datakirim_nohp.length() > 3) {

                //simpan ke database riwayat
                simpanDatabase(false, true, datasimpan_id, namakomoditas, datasimpan_lat,
                        datasimpan_lng, namalokasi, datasimpan_nohp, datasimpan_harga, datasimpan_quantity);

            } else {
                //gagal kirim data laporan
                munculSnackbar(R.string.lapor_gagalkirimdata);
                isProsesKirim = false;
                mProgressDialog.dismiss();
            }
        } else {
            //gagal kirim data laporan
            munculSnackbar(R.string.lapor_gagalkirimdata);
            isProsesKirim = false;
            mProgressDialog.dismiss();
        }
    }


    //SIMPAN KE DALAM DATABASE
    private void simpanDatabase(boolean isDraft, boolean isKirim, String id, String namakomoditas, String lats,
                                String lngs, String alamatkomods, String nohps,
                                String hargas, String quantitis) {


        mRealmQueryRiwayat = mRealm.where(RMDataRiwayat.class);
        mRealmResultsRiwayat = mRealmQueryRiwayat.findAll();

        //update data riwayat
        if (mRealmResultsRiwayat.size() > 0) {

            RMDataRiwayat rmDataRiwayat = mRealmResultsRiwayat.get(posisiklik);

            mRealm.beginTransaction();

            rmDataRiwayat.setId(id);
            rmDataRiwayat.setNamakomoditas(namakomoditas);
            rmDataRiwayat.setLat(lats);
            rmDataRiwayat.setLng(lngs);
            rmDataRiwayat.setAlamatkomoditas(alamatkomods);
            rmDataRiwayat.setNohp(nohps);
            rmDataRiwayat.setHarga(hargas);
            rmDataRiwayat.setQuantity(quantitis);
            rmDataRiwayat.setIsKirim(isKirim);
            rmDataRiwayat.setIsDraft(isDraft);


            mRealm.commitTransaction();

        }

        isProsesKirim = false;

        if (isKirim) {

            mProgressDialog.dismiss();

            //tampil dialog data telah dikirim
            tampilDialogBerhasil();
        } else {
            Toast.makeText(LaporHargaEdit.this, R.string.lapor_okkirimdraft, Toast.LENGTH_SHORT).show();
            LaporHargaEdit.this.finish();
        }

    }


    //AMBIL GEOCODER LOKASI
    private void ambilGeocoderPengguna(String latitude, String longitude) {

        geocoderPengguna = new Geocoder(LaporHargaEdit.this, Locale.getDefault());
        double dolatitu = 0;
        double dolongi = 0;

        try {
            dolatitu = Double.valueOf(latitude);
            dolongi = Double.valueOf(longitude);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {

            addressListPengguna = geocoderPengguna.getFromLocation(dolatitu, dolongi, 1);
            if (addressListPengguna.size() > 0) {

                int panjangalamat = addressListPengguna.get(0).getMaxAddressLineIndex();

                if (panjangalamat > 0) {

                    gecoder_alamat = addressListPengguna.get(0).getAddressLine(0);
                    gecoder_namakota = addressListPengguna.get(0).getLocality();

                } else {
                    gecoder_alamat = "";
                    gecoder_namakota = "";
                    alamatgabungan = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            gecoder_alamat = "";
            gecoder_namakota = "";
            alamatgabungan = "";
        }

        Log.w("NAMA KOTA", "NAMA KOTA " + gecoder_alamat + " " + gecoder_namakota);

        if (gecoder_namakota != null && gecoder_namakota.length() > 0) {

            alamatgabungan = gecoder_namakota;

            if (gecoder_alamat != null && gecoder_alamat.length() > 0) {

                alamatgabungan = gecoder_alamat + ", " + gecoder_namakota;

            }
        } else {
            alamatgabungan = "";
        }
    }


    //TASK AMBIL GEOCODER PENGGUNA
    private void taskAmbilGeocoder(final String strlatitude, final String strlongitude) {

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                ambilGeocoderPengguna(strlatitude, strlongitude);
                return null;
            }
        })

                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(Task<Object> task) throws Exception {

                        if (alamatgabungan.length() > 3) {

                            Log.w("ALAMAT GABUNGAN TASK", "ALAMAT " + alamatgabungan);

                            //setel ke edit text
                            setelEditTextLokasi(alamatgabungan);
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    //SETEL LOKASI TAMPILAN KE EDITTEXT
    private void setelEditTextLokasi(String lokasikordinat) {

        edit_kordinatlokasi.setText(lokasikordinat);
    }


    /** ======== TAMPIL DIALOG ======== **/
    /**
     * ======== TAMPIL DIALOG ========
     **/


    //TAMPILKAN PROGRESS DIALOG
    private void tampilProgressDialog(String pesan) {

        mProgressDialog = new ProgressDialog(LaporHargaEdit.this);
        mProgressDialog.setMessage(pesan);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(listenerprogresbatal);
        mProgressDialog.show();

    }


    ProgressDialog.OnCancelListener listenerprogresbatal = new ProgressDialog.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialogInterface) {

            isProsesKirim = false;
            Volleys.getInstance(LaporHargaEdit.this).cancelPendingRequestsNoTag();
        }
    };


    //TAMPIL DIALOG BERHASIL
    private void tampilDialogBerhasil() {

        DialogOkKirimEdit dialogOkKirim = new DialogOkKirimEdit();
        dialogOkKirim.setCancelable(false);

        FragmentTransaction fts = LaporHargaEdit.this.getSupportFragmentManager().beginTransaction();
        dialogOkKirim.show(fts, "dialog ok kirim");

    }


    //SET DIALOG OK TERKIRIM
    public void setOkTerkirim() {
        LaporHargaEdit.this.finish();
    }


    //TAMPIL DIALOG HAPUS DATA
    private void tampilDialogHapusDataIni() {

        DialogHapusDataIni dialogHapusDataIni = new DialogHapusDataIni();
        dialogHapusDataIni.setCancelable(false);

        FragmentTransaction fts = LaporHargaEdit.this.getSupportFragmentManager().beginTransaction();
        dialogHapusDataIni.show(fts, "hapus data ini");

    }


    //SETEL DARI DIALOG UNTUK HAPUS DATA
    public void setOkHapusDataIni() {

        mRealmQueryRiwayat = mRealm.where(RMDataRiwayat.class);
        mRealmResultsRiwayat = mRealmQueryRiwayat.findAll();

        //update data riwayat
        if (mRealmResultsRiwayat.size() > 0) {

            RMDataRiwayat rmDataRiwayat = mRealmResultsRiwayat.get(posisiklik);

            mRealm.beginTransaction();
            rmDataRiwayat.removeFromRealm();
            mRealm.commitTransaction();

            Toast.makeText(LaporHargaEdit.this, R.string.riwayat_hapusdataselesai, Toast.LENGTH_SHORT).show();
            LaporHargaEdit.this.finish();
        }

    }


    //LISTENER SPINNER
    AdapterView.OnItemSelectedListener listenerspinner = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            KomoditasItem komoditasItem = mListKomoditasItem.get(i);
            idkomoditas = komoditasItem.getId();
            namakomoditas = komoditasItem.getName();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };


    //LISTENER TOMBOL
    View.OnClickListener listenertombol = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            sembunyikeyboard(LaporHargaEdit.this, view);
            switch (view.getId()) {

                case R.id.tombol_setelpeta:

                    setelLokasiPeta();
                    break;

                case R.id.tombol_kirim:

                    ambilDataPengguna();
                    break;

                case R.id.tombol_simpandraft:

                    ambilDataPenggunaDraft();
                    break;
            }
        }
    };


    //MUNCUL SNACKBAR
    private void munculSnackbar(int resPesan) {

        Snackbar.make(toolbar, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(LaporHargaEdit.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    View.OnClickListener listenersnackbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };


    //SEMBUNYIKAN KEYBOARD
    private static void sembunyikeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


    //MENAMPILKAN MENU ACTION BAR
    private void munculMenuAction(Context context) {

        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKey != null) {
                menuKey.setAccessible(true);
                menuKey.setBoolean(config, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
