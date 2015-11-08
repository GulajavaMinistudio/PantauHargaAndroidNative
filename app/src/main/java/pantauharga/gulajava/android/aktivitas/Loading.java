package pantauharga.gulajava.android.aktivitas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.databases.RMJsonData;
import pantauharga.gulajava.android.databases.RMLogin;
import pantauharga.gulajava.android.internets.Apis;
import pantauharga.gulajava.android.internets.StrRekuestGet;
import pantauharga.gulajava.android.internets.Volleys;
import pantauharga.gulajava.android.parsers.CekGPSNet;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */


public class Loading extends AppCompatActivity {


    @Bind(R.id.gambarbesar)
    ImageView gambarbesar;

    private Handler mHandler;

    Runnable splash = new Runnable() {
        @Override
        public void run() {

            Intent intentloading = new Intent(Loading.this, MenuUtama.class);
            Loading.this.startActivity(intentloading);
            Loading.this.finish();
        }
    };


    private Parseran mParseran;
    private CekGPSNet mCekGPSNet;

    private String jsonkomoditas = "";

    private Realm mRealm;
    private RealmQuery<RMJsonData> mRealmQueryJson;
    private RealmResults<RMJsonData> mRealmResultsJson;

    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    private boolean isAktivitasJalan = false;
    private boolean isRealmUpdate = false;
    private boolean isInternetAda = false;

    private long mLongWaktuSekarang = 0;
    private String strwaktudb = "0";
    private Calendar mCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadings);
        ButterKnife.bind(Loading.this);

        Glide.with(Loading.this).load(R.drawable.web_hi_res_512).into(gambarbesar);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(Loading.this).build();

        try {
            mRealm = Realm.getInstance(realmConfiguration);
        } catch (Exception e) {
            e.printStackTrace();
            isRealmUpdate = Realm.deleteRealm(realmConfiguration);
        }

        if (isRealmUpdate) {
            mRealm = Realm.getInstance(realmConfiguration);
        }

        isAktivitasJalan = true;

        mCalendar = Calendar.getInstance();
        mLongWaktuSekarang = mCalendar.getTimeInMillis();

        mParseran = new Parseran(Loading.this);
        mCekGPSNet = new CekGPSNet(Loading.this);

        ambilDataDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
        isAktivitasJalan = false;

        Volleys.getInstance(Loading.this).cancelPendingRequestsNoTag();
        Volleys.getInstance(Loading.this).clearVolleyCache();
    }


    //INISIALISASI DATA DATABASE
    private void ambilDataDatabase() {

        //ambil data realm db
        mRealmQueryJson = mRealm.where(RMJsonData.class);
        mRealmResultsJson = mRealmQueryJson.findAll();

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        //inisialisasi db login
        if (mRealmResultsLogin.size() == 0) {

            //db belum ada, diisi dulu
            RMLogin rmLogin = new RMLogin();
            rmLogin.setUsername("");
            rmLogin.setNama("");
            rmLogin.setEmail("");
            rmLogin.setKtp("");
            rmLogin.setNohp("");
            rmLogin.setAlamat("");
            rmLogin.setKodepos("");

            mRealm.beginTransaction();
            mRealm.copyToRealm(rmLogin);
            mRealm.commitTransaction();
        }


        if (mRealmResultsJson.size() > 0) {

            Log.w("PINDAH HALAMAN", "DATA ADA DAN PINDAH KE HALAMAN BARU");

            RMJsonData RMJsonData = mRealmResultsJson.first();
            strwaktudb = RMJsonData.getWaktukadaluarsa();

            boolean isKadaluarsa = mParseran.isCekKadaluarsa(strwaktudb, mLongWaktuSekarang);
            if (isKadaluarsa) {

                //cek koneksi internet dan ambil data dari server
                isInternetAda = mCekGPSNet.cekStatsInternet();

                Log.w("AMBIL DATA", "AMBIL DATA UNTUK GANTIKAN DATA KADALUARSA DARI INTERNET DAN CEK KONEKSI " + isInternetAda);
                if (isInternetAda) {

                    ambilDataServerKomoditas();

                } else {
                    //ga ada internet
                    munculSnackbar(R.string.toast_nointernet);
                    //pindah halaman
                    pindahHalaman();
                }

            } else {

                //pindah halaman
                pindahHalaman();

            }
        } else {

            //ambil data aset json
            ambilAsetJson();
        }
    }


    //AMBIL DATA DARI ASET
    private void ambilAsetJson() {


        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                jsonkomoditas = mParseran.ambilJsonAset(Konstan.NAMAJSONASET_KOMODITAS);
                Log.w("AMBIL JSON", "HASIL JSON " + jsonkomoditas);

                return null;
            }
        })
                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(Task<Object> task) throws Exception {

                        Log.w("AMBIL JSON", "SELESAI AMBIL JSON, SIMPAN DATA KE REALM");

                        try {

                            mCalendar = Calendar.getInstance();
                            mLongWaktuSekarang = mCalendar.getTimeInMillis();

                            //buat data awal untuk database json komoditas
                            RMJsonData rmJsonData = new RMJsonData();
                            rmJsonData.setWaktukadaluarsa("" + mLongWaktuSekarang);
                            rmJsonData.setJsonkomoditas("" + jsonkomoditas);

                            mRealm.beginTransaction();
                            mRealm.copyToRealm(rmJsonData);
                            mRealm.commitTransaction();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR)

                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(Task<Object> task) throws Exception {

                        //cek koneksi internet untuk ambil data baru selain data bawaan
                        isInternetAda = mCekGPSNet.cekStatsInternet();

                        Log.w("AMBIL DATA", "AMBIL DATA DARI INTERNET DAN CEK KONEKSI " + isInternetAda);
                        if (isInternetAda) {

                            ambilDataServerKomoditas();

                        } else {

                            munculSnackbar(R.string.toast_nointernet);
                            pindahHalaman();
                        }

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    //AMBIL DATA DARI SERVER UNTUK KOMODITAS
    private void ambilDataServerKomoditas() {

        String alamatserver = Apis.getLinkDaftarKomoditas();

        StrRekuestGet strRekuestGet = Apis.getRequestDaftarKomoditas(alamatserver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.w("SUKSES", "SUKSES");

                        if (isAktivitasJalan) {
                            cekSimpanData(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Log.w("GAGAL", "GAGAL");

                        if (isAktivitasJalan) {
                            cekSimpanData("");
                        }
                    }
                }
        );

        Volleys.getInstance(Loading.this).addToRequestQueue(strRekuestGet);
    }


    //CEK DATA DAN SIMPAN KE DATABASE
    //SIMPAN KE DATABASE
    private void cekSimpanData(String strjsonserver) {

        Log.w("HASIL DATA", "HASIL DATA DARI SERVER " + strjsonserver);

        if (strjsonserver.length() > 5) {

            //ambil data realm db
            mRealmQueryJson = mRealm.where(RMJsonData.class);
            mRealmResultsJson = mRealmQueryJson.findAll();

            if (mRealmResultsJson.size() > 0) {

                mCalendar = Calendar.getInstance();
                mLongWaktuSekarang = mCalendar.getTimeInMillis();

                //update data awal untuk database json komoditas
                RMJsonData rmJsonDataupdate = mRealmResultsJson.first();

                mRealm.beginTransaction();
                rmJsonDataupdate.setWaktukadaluarsa("" + mLongWaktuSekarang);
                rmJsonDataupdate.setJsonkomoditas("" + strjsonserver);
                mRealm.commitTransaction();
            } else {

                mCalendar = Calendar.getInstance();
                mLongWaktuSekarang = mCalendar.getTimeInMillis();

                //buat data awal untuk database json komoditas
                RMJsonData rmJsonData = new RMJsonData();
                rmJsonData.setWaktukadaluarsa("" + mLongWaktuSekarang);
                rmJsonData.setJsonkomoditas("" + strjsonserver);

                mRealm.beginTransaction();
                mRealm.copyToRealm(rmJsonData);
                mRealm.commitTransaction();
            }

        }

        //pindah halaman
        pindahHalaman();


    }


    //PINDAH HALAMAN
    private void pindahHalaman() {

        mHandler = new Handler();
        mHandler.postDelayed(splash, 1000);

    }


    //MUNCUL SNACKBAR
    private void munculSnackbar(int resPesan) {

        Snackbar.make(gambarbesar, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(Loading.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    View.OnClickListener listenersnackbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };


}
