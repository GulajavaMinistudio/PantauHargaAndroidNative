package pantauharga.gulajava.android.aktivitas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.databases.RMLogin;
import pantauharga.gulajava.android.dialogs.DialogOkLogOut;
import pantauharga.gulajava.android.dialogs.DialogOkLogin;
import pantauharga.gulajava.android.fragments.FragmentDataPengguna;
import pantauharga.gulajava.android.fragments.FragmentLogin;
import pantauharga.gulajava.android.fragments.FragmentRegistrasi;
import pantauharga.gulajava.android.internets.Apis;
import pantauharga.gulajava.android.internets.JacksonRequest;
import pantauharga.gulajava.android.internets.Volleys;
import pantauharga.gulajava.android.modelgson.Logins;
import pantauharga.gulajava.android.modelgson.Registrasis;
import pantauharga.gulajava.android.modelgsonkirim.LoginKirim;
import pantauharga.gulajava.android.modelgsonkirim.RegisterKirim;
import pantauharga.gulajava.android.parsers.CekGPSNet;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class LoginRegistersPengguna extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private ActionBar aksibar;


    private FragmentTransaction mFragmentTransaction;

    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    //login
    private String str_loginnamapanggilan = "";
    private String str_loginpassword = "";

    //registrasi
    private String str_regis_namalengkap = "";
    private String str_regis_namapanggilan = "";
    private String str_regis_password = "";
    private String str_regis_email = "";
    private String str_regis_nomorktp = "";
    private String str_regis_nomorhandphone = "";
    private String str_regis_alamatlengkap = "";
    private String str_regis_kodepos = "";

    //hasil login atau registrasi
    private String str_namalengkap = "";
    private String str_namapanggilan = "";
    private String str_email = "";
    private String str_nomorktp = "";
    private String str_nomorhandphone = "";
    private String str_alamatlengkap = "";
    private String str_kodepos = "";

    private boolean isLogin = false;

    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialogFragment;

    private String strcek_username = "";
    private String strcek_nomorhp = "";

    private int kodepindah = Konstan.KODE_FRAGMENT_LOGINREGISTER;

    private Parseran mParseran;
    private CekGPSNet mCekGPSNet;
    private boolean isInternet = false;
    private boolean isProgresKirim = false;
    private boolean isAktJalan = false;

    private FragmentLogin mFragmentLogin;
    private FragmentRegistrasi mFragmentRegistrasi;
    private FragmentDataPengguna mFragmentDataPengguna;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loginregister);
        ButterKnife.bind(LoginRegistersPengguna.this);

        if (toolbar != null) {
            LoginRegistersPengguna.this.setSupportActionBar(toolbar);
        }

        isAktJalan = true;

        aksibar = LoginRegistersPengguna.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.login_register_datapengguna);

        mRealm = Realm.getInstance(LoginRegistersPengguna.this);
        mParseran = new Parseran(LoginRegistersPengguna.this);
        mCekGPSNet = new CekGPSNet(LoginRegistersPengguna.this);

        ambilDataRealmLogins();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isAktJalan = false;
        mRealm.close();

        Volleys.getInstance(LoginRegistersPengguna.this).cancelPendingRequestsNoTag();
        Volleys.getInstance(LoginRegistersPengguna.this).clearVolleyCache();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (kodepindah == Konstan.KODE_FRAGMENT_REGISTER) {
                    pindahFragments(Konstan.KODE_FRAGMENT_LOGINREGISTER);
                } else {
                    LoginRegistersPengguna.this.finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //AMBIL DATA REALM LOGIN
    private void ambilDataRealmLogins() {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            //cek tampilkan data pengguna
            RMLogin rmLogin = mRealmResultsLogin.first();
            strcek_username = rmLogin.getUsername();
            strcek_nomorhp = rmLogin.getNohp();

            isLogin = strcek_username.length() > 2 && strcek_nomorhp.length() > 3;

        } else {

            isLogin = false;

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

        if (isLogin) {

            //tampilkan fragment data pengguna
            pindahFragments(Konstan.KODE_FRAGMENT_DATAPENGGUNA);

        } else {

            //tampilkan halaman login/register
            pindahFragments(Konstan.KODE_FRAGMENT_LOGINREGISTER);

        }
    }


    //PINDAH HALAMAN FRAGMENT
    public void pindahFragments(int kodepindahfragment) {

        kodepindah = kodepindahfragment;

        //muncul progress
        tampilProgressDialogFrag("Membuka halaman...");

        switch (kodepindah) {

            case Konstan.KODE_FRAGMENT_LOGINREGISTER:

                mFragmentLogin = new FragmentLogin();
                mFragmentTransaction = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frag_containers, mFragmentLogin);
                mFragmentTransaction.commit();

                break;

            case Konstan.KODE_FRAGMENT_REGISTER:

                mFragmentRegistrasi = new FragmentRegistrasi();
                mFragmentTransaction = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frag_containers, mFragmentRegistrasi);
                mFragmentTransaction.commit();

                break;

            case Konstan.KODE_FRAGMENT_DATAPENGGUNA:

                mFragmentDataPengguna = new FragmentDataPengguna();
                mFragmentTransaction = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frag_containers, mFragmentDataPengguna);
                mFragmentTransaction.commit();

                break;
        }

        Handler handler = new Handler();
        handler.postDelayed(jedadismisfrag, 900);

        //invalidasi menu


    }


    Runnable jedadismisfrag = new Runnable() {
        @Override
        public void run() {

            mProgressDialogFragment.dismiss();
        }
    };


    /**
     * ==============  LOGIN ======================
     **/

    //MENERIMA HASIL DARI FRAGMENT LOGIN
    public void terimaDataLogin(Bundle bundels) {

        isInternet = mCekGPSNet.cekStatsInternet();

        if (isInternet) {

            //ambil data
            str_loginnamapanggilan = bundels.getString(Konstan.TAG_INTENT_USERNAME);
            str_loginpassword = bundels.getString(Konstan.TAG_INTENT_PASSWORD);

            //kirim data ke volley
            parseDataLogin();

        } else {
            munculSnackbar(R.string.toastnointernet);
        }
    }


    //KIRIM DATA LOGIN PARSE JSON
    private void parseDataLogin() {

        //progress dialog
        isProgresKirim = true;
        tampilProgressDialog("Mengirim data...");


        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                LoginKirim loginKirim = new LoginKirim();
                loginKirim.setUsername(str_loginnamapanggilan);
                loginKirim.setPassword(str_loginpassword);

                return mParseran.konversiPojoJsonLogin(loginKirim);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String hasiljson = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + hasiljson);

                        //kirim ke server
                        kirimJsonServerLogin(hasiljson);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    //KIRIM DATA LOGIN VOLLEY
    private void kirimJsonServerLogin(String responbody) {

        String urls = Apis.getLinkLoginPengguna();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<Logins> jacksonRequest = Apis.postRequestLogin(
                urls,
                headers,
                parameters,
                responbody,
                new Response.Listener<Logins>() {
                    @Override
                    public void onResponse(Logins response) {

                        Log.w("SUKSES", "SUKSES");
                        if (isAktJalan) {
                            cekHasilLogin(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Log.w("GAGAL", "GAGAL");
                        if (isAktJalan) {
                            cekHasilLogin(null);
                        }
                    }
                }
        );

        Volleys.getInstance(LoginRegistersPengguna.this).addToRequestQueue(jacksonRequest);
    }


    //CEK HASIL LOGIN
    private void cekHasilLogin(Logins logins) {

        if (logins != null) {

            str_namalengkap = logins.getNama();
            str_namapanggilan = logins.getUsername();
            str_email = logins.getEmail();
            str_nomorktp = logins.getKtp();
            str_nomorhandphone = logins.getNohp();
            str_alamatlengkap = logins.getAlamat();
            str_kodepos = logins.getKodepos();

            if (str_namalengkap.length() > 0 && str_nomorhandphone.length() > 4) {

                //simpan ke database
                simpanDatabase(str_namapanggilan, str_namalengkap, str_email,
                        str_nomorktp, str_nomorhandphone, str_alamatlengkap,
                        str_kodepos);

            } else {
                munculSnackbar(R.string.gagalogin_masuk);
                isProgresKirim = false;
                mProgressDialog.dismiss();
            }
        } else {
            //gagal login
            munculSnackbar(R.string.gagalogin_masuk);
            isProgresKirim = false;
            mProgressDialog.dismiss();
        }
    }


    /** =====================================================**/


    /**
     * ===========  REGISTRASI ============
     **/

    //MENERIMA DATA REGISTRASI
    public void terimaDataRegistrasi(Bundle bundel) {

        isInternet = mCekGPSNet.cekStatsInternet();

        if (isInternet) {

            str_regis_namalengkap = bundel.getString(Konstan.TAG_INTENT_NAMALENGKAP);
            str_regis_namapanggilan = bundel.getString(Konstan.TAG_INTENT_USERNAME);
            str_regis_password = bundel.getString(Konstan.TAG_INTENT_PASSWORD);
            str_regis_email = bundel.getString(Konstan.TAG_INTENT_EMAIL);
            str_regis_nomorktp = bundel.getString(Konstan.TAG_INTENT_NOMORKTP);
            str_regis_nomorhandphone = bundel.getString(Konstan.TAG_INTENT_NOMORHP);
            str_regis_alamatlengkap = bundel.getString(Konstan.TAG_INTENT_ALAMATLENGKAP);
            str_regis_kodepos = bundel.getString(Konstan.TAG_INTENT_KODEPOS);

            //parse data registrasi
            parseDataRegistrasi();

        } else {
            munculSnackbar(R.string.toastnointernet);
        }

    }


    //PARSE DATA REGISTRASI JADI KE JSON
    private void parseDataRegistrasi() {

        //progress dialog
        isProgresKirim = true;
        tampilProgressDialog("Mengirim data...");

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                RegisterKirim registerKirim = new RegisterKirim();
                registerKirim.setUsername(str_regis_namapanggilan);
                registerKirim.setNama(str_regis_namalengkap);
                registerKirim.setPassword(str_regis_password);
                registerKirim.setEmail(str_regis_email);
                registerKirim.setKtp(str_regis_nomorktp);
                registerKirim.setNohp(str_regis_nomorhandphone);
                registerKirim.setAlamat(str_regis_alamatlengkap);
                registerKirim.setKodepos(str_regis_kodepos);

                return mParseran.konversiPojoRegistrasi(registerKirim);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String hasiljson = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + hasiljson);

                        kirimJsonServerRegistrasi(hasiljson);
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    //KIRIM DATA REGISTRASI VOLLEY
    private void kirimJsonServerRegistrasi(String responbody) {

        String urls = Apis.getLinkRegisterPengguna();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<Registrasis> jacksonRequest = Apis.postRequestRegistrasi(
                urls,
                headers,
                parameters,
                responbody,

                new Response.Listener<Registrasis>() {
                    @Override
                    public void onResponse(Registrasis response) {

                        Log.w("SUKSES", "SUKSES");
                        if (isAktJalan) {
                            cekHasilRegistrasi(response);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Log.w("GAGAL", "GAGAL");
                        if (isAktJalan) {

                            cekHasilRegistrasi(null);
                        }
                    }
                }
        );

        Volleys.getInstance(LoginRegistersPengguna.this).addToRequestQueue(jacksonRequest);
    }


    //CEK HASIL REGISTRASI
    private void cekHasilRegistrasi(Registrasis registrasis) {

        if (registrasis != null) {

            str_namalengkap = registrasis.getNama();
            str_namapanggilan = registrasis.getUsername();
            str_email = registrasis.getEmail();
            str_nomorktp = registrasis.getKtp();
            str_nomorhandphone = registrasis.getNohp();
            str_alamatlengkap = registrasis.getAlamat();
            str_kodepos = registrasis.getKodepos();

            if (str_namalengkap.length() > 0 && str_nomorhandphone.length() > 4) {

                //simpan ke database registrasi
                simpanDatabase(str_namapanggilan, str_namalengkap, str_email,
                        str_nomorktp, str_nomorhandphone, str_alamatlengkap,
                        str_kodepos);

            } else {
                munculSnackbar(R.string.gagalogin_buatakun);
                isProgresKirim = false;
                mProgressDialog.dismiss();
            }
        } else {
            //gagal login
            munculSnackbar(R.string.gagalogin_buatakun);
            isProgresKirim = false;
            mProgressDialog.dismiss();
        }
    }


    /**
     * ========= SIMPAN DATABASE ===========
     **/

    private void simpanDatabase(String username, String namalengkap, String email,
                                String nomorktp, String nomorhp, String alamat,
                                String kodepos) {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            Log.w("HASIL DAFTAR LOG", "HASIL REGISTRASI ATAU LOGIN " + username + " " + nomorhp);

            //cek tampilkan data pengguna
            RMLogin rmLogin = mRealmResultsLogin.first();

            mRealm.beginTransaction();

            rmLogin.setUsername(username);
            rmLogin.setNama(namalengkap);
            rmLogin.setEmail(email);
            rmLogin.setKtp(nomorktp);
            rmLogin.setNohp(nomorhp);
            rmLogin.setAlamat(alamat);
            rmLogin.setKodepos(kodepos);

            mRealm.commitTransaction();


            isProgresKirim = false;
            mProgressDialog.dismiss();

            //beri peringatan dialog berhasil login
            tampilDialogOkLogin();

        }
    }


    /**
     * ============ TAMPIL DIALOG ===========
     **/


    //TAMPIL DIALOG OK LOGIN
    public void tampilDialogOkLogin() {

        FragmentTransaction fts = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
        DialogOkLogin dialogOkLogin = new DialogOkLogin();
        dialogOkLogin.setCancelable(false);
        dialogOkLogin.show(fts, "dialog ok login");
    }


    //AKSI DIALOG OK LOGIN
    public void setOkLogin() {

        LoginRegistersPengguna.this.finish();

    }


    private void tampilProgressDialog(String pesan) {

        //progress dialogs
        mProgressDialog = new ProgressDialog(LoginRegistersPengguna.this);
        mProgressDialog.setMessage(pesan);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(listenerbatals);
        mProgressDialog.show();

    }

    private void tampilProgressDialogFrag(String pesan) {

        //progress dialogs
        mProgressDialogFragment = new ProgressDialog(LoginRegistersPengguna.this);
        mProgressDialogFragment.setMessage(pesan);
        mProgressDialogFragment.setCancelable(false);
        mProgressDialogFragment.show();

    }

    ProgressDialog.OnCancelListener listenerbatals = new ProgressDialog.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {

            isProgresKirim = false;
            Volleys.getInstance(LoginRegistersPengguna.this).cancelPendingRequestsNoTag();
        }
    };


    //TAMPIL DIALOG LOG OUT
    public void tampilDialogLogOut() {

        FragmentTransaction fts = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
        DialogOkLogOut dialogOkLogOut = new DialogOkLogOut();
        dialogOkLogOut.setCancelable(false);
        dialogOkLogOut.show(fts, "keluar aplikasi ok");

    }


    public void setOkLogOut() {

        Handler handler = new Handler();
        handler.postDelayed(jedaLogouts, 500);

    }

    Runnable jedaLogouts = new Runnable() {
        @Override
        public void run() {

            pindahFragments(Konstan.KODE_FRAGMENT_LOGINREGISTER);
        }
    };


    //MUNCUL SNACKBAR
    private void munculSnackbar(int resPesan) {

        Snackbar.make(toolbar, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(LoginRegistersPengguna.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    View.OnClickListener listenersnackbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (kodepindah == Konstan.KODE_FRAGMENT_REGISTER) {

            switch (keyCode) {

                case KeyEvent.KEYCODE_BACK:

                    //jika di halaman registrasi batal, balikan kembali ke halaman login
                    pindahFragments(Konstan.KODE_FRAGMENT_LOGINREGISTER);
                    return true;
            }

        }


        return super.onKeyDown(keyCode, event);
    }


}
