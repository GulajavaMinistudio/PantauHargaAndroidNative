package pantauharga.gulajava.android.aktivitas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import pantauharga.gulajava.android.dialogs.DialogOkKirim;
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
public class LaporHarga extends BaseActivityLocation {


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


    private String idkomoditas = "";
    private String namakomoditas = "";
    private String hargakomoditas = "";
    private String jumlahkomoditas = "0";
    private String namalokasi = "";
    private String latitude = "";
    private String longitude = "";


    //database
    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    private RealmQuery<RMJsonData> mRealmQueryJsonData;
    private RealmResults<RMJsonData> mRealmResultsJsonData;

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
    private Location mLocationPengguna;
    private double longitudepengguna = 0;
    private double latitudepengguna = 0;


    //GEOCODER AMBIL LOKASI DAN POSISI PENGGUNA ALAMATNYA JIKA ADA
    private Geocoder geocoderPengguna;
    private List<Address> addressListPengguna = null;
    private String gecoder_alamat = "";
    private String gecoder_namakota = "";
    private String alamatgabungan = "";


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_laporharga);
        ButterKnife.bind(LaporHarga.this);
        munculMenuAction(LaporHarga.this);

        Bundle bundle = LaporHarga.this.getIntent().getExtras();
        kode_kirimKomoditas = bundle.getInt(Konstan.TAG_INTENT_STATKIRIMHARGA);

        if (toolbar != null) {
            LaporHarga.this.setSupportActionBar(toolbar);
        }

        aksibar = LaporHarga.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.lapor_hargajudul);

        mRealm = Realm.getInstance(LaporHarga.this);
        mParseran = new Parseran(LaporHarga.this);

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

        if (!EventBus.getDefault().isRegistered(LaporHarga.this)) {
            EventBus.getDefault().register(LaporHarga.this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(LaporHarga.this)) {
            EventBus.getDefault().unregister(LaporHarga.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        isAktJalan = false;

        hentikanListenerLokasi();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                LaporHarga.this.finish();
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

        mAdapterSpin = new ArrayAdapter<>(LaporHarga.this, android.R.layout.simple_spinner_item, mStringListNamaKomoditas);
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

        switch (kodeperintah) {

            case Konstan.KODE_LOKASIAWAL:

                ambilLokasiPenggunaAwal();
                break;

            case Konstan.KODE_LOKASIBARU:

                ambilLokasiPenggunaUpdate();
                break;
        }
    }


    //AMBIL LOKASI AWAL PENGGUNA
    private void ambilLokasiPenggunaAwal() {

        LaporHarga.this.latitudepengguna = getLatitudesaya();
        LaporHarga.this.longitudepengguna = getLongitudesaya();

        isInternet = isInternet();
        mLocationPengguna = getLokasisaya();

        Log.w("LOKASI AWAL", "lokasi awal pengguna " + latitudepengguna + " , " + longitudepengguna);


        //setel ke edit text lokasi
        String strkoordinatlokasi = latitudepengguna + " , " + longitudepengguna;
        setelEditTextLokasi(strkoordinatlokasi);

        //ambil data awal alamat lokasi pengguna
        taskAmbilGeocoder(latitudepengguna + "", longitudepengguna + "");

    }


    //AMBIL LOKASI UPDATE DARI PENGGUNA
    private void ambilLokasiPenggunaUpdate() {

        //jika lokasi tidak disetel dengan peta, update lokasi pengguna
        if (!isLokasiSetels) {
            LaporHarga.this.latitudepengguna = getLatitudesaya();
            LaporHarga.this.longitudepengguna = getLongitudesaya();
            mLocationPengguna = getLokasisaya();

            //setel ke edit text lokasi
            String strkoordinatlokasi = latitudepengguna + " , " + longitudepengguna;
            setelEditTextLokasi(strkoordinatlokasi);

            //ambil data awal alamat lokasi pengguna
            taskAmbilGeocoder(latitudepengguna + "", longitudepengguna + "");
        }


        cekInternet();
        isInternet = isInternet();

        Log.w("LOKASI UPDATE", "lokasi update pengguna " + latitudepengguna + " , " + longitudepengguna);
    }


    //SUNTING LOKASI DENGAN PETA
    private void setelLokasiPeta() {


    }


    //HASIL SUNTING LOKASI DENGAN PETA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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

        simpanDatabase(false, idkomoditas, namakomoditas, latitude, longitude,
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

        Volleys.getInstance(LaporHarga.this).addToRequestQueue(jacksonRequest);
    }


    //CEK HASIL RESPON DARI VOLLEY
    private void cekHasilRespon(HargaKomoditasLapor hargaKomoditasLapor) {

        if (hargaKomoditasLapor != null) {

            datasimpan_id = hargaKomoditasLapor.getId();
            datasimpan_harga = hargaKomoditasLapor.getHarga();
            datasimpan_lat = hargaKomoditasLapor.getLat();
            datasimpan_lng = hargaKomoditasLapor.getLng();
            datasimpan_nohp = hargaKomoditasLapor.getNohp();
            datasimpan_quantity = hargaKomoditasLapor.getQuantity();

            if (datakirim_nohp.length() > 1 && datakirim_nohp.length() > 3) {

                //simpan ke database riwayat
                simpanDatabase(true, datasimpan_id, namakomoditas, datasimpan_lat,
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
    private void simpanDatabase(boolean isKirim, String id, String namakomoditas, String lats,
                                String lngs, String alamatkomods, String nohps,
                                String hargas, String quantitis) {


        RMDataRiwayat rmDataRiwayat = new RMDataRiwayat();
        rmDataRiwayat.setId(id);
        rmDataRiwayat.setNamakomoditas(namakomoditas);
        rmDataRiwayat.setLat(lats);
        rmDataRiwayat.setLng(lngs);
        rmDataRiwayat.setAlamatkomoditas(alamatkomods);
        rmDataRiwayat.setNohp(nohps);
        rmDataRiwayat.setHarga(hargas);
        rmDataRiwayat.setQuantity(quantitis);
        rmDataRiwayat.setIsKirim(isKirim);

        mRealm.beginTransaction();
        mRealm.copyToRealm(rmDataRiwayat);
        mRealm.commitTransaction();

        isProsesKirim = false;

        if (isKirim) {

            mProgressDialog.dismiss();

            //tampil dialog data telah dikirim
            tampilDialogBerhasil();
        } else {
            Toast.makeText(LaporHarga.this, R.string.lapor_okkirimdraft, Toast.LENGTH_SHORT).show();
            LaporHarga.this.finish();
        }

    }


    //AMBIL GEOCODER LOKASI
    private void ambilGeocoderPengguna(String latitude, String longitude) {

        geocoderPengguna = new Geocoder(LaporHarga.this, Locale.getDefault());
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

        mProgressDialog = new ProgressDialog(LaporHarga.this);
        mProgressDialog.setMessage(pesan);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(listenerprogresbatal);
        mProgressDialog.show();

    }


    ProgressDialog.OnCancelListener listenerprogresbatal = new ProgressDialog.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialogInterface) {

            isProsesKirim = false;
            Volleys.getInstance(LaporHarga.this).cancelPendingRequestsNoTag();
        }
    };


    //TAMPIL DIALOG BERHASIL
    private void tampilDialogBerhasil() {

        DialogOkKirim dialogOkKirim = new DialogOkKirim();
        dialogOkKirim.setCancelable(false);

        FragmentTransaction fts = LaporHarga.this.getSupportFragmentManager().beginTransaction();
        dialogOkKirim.show(fts, "dialog ok kirim");

    }


    //SET DIALOG OK TERKIRIM
    public void setOkTerkirim() {
        LaporHarga.this.finish();
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

            sembunyikeyboard(LaporHarga.this, view);
            switch (view.getId()) {

                case R.id.tombol_setelpeta:


                    break;

                case R.id.tombol_kirim:

                    ambilDataPengguna();
                    break;

                case R.id.tombol_simpandraft :

                    ambilDataPenggunaDraft();
                    break;
            }
        }
    };


    //MUNCUL SNACKBAR
    private void munculSnackbar(int resPesan) {

        Snackbar.make(toolbar, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(LaporHarga.this.getResources().getColor(R.color.kuning_indikator)).show();
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
