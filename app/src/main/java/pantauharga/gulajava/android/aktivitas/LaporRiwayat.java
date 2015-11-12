package pantauharga.gulajava.android.aktivitas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.adapters.ViewHolderan;
import pantauharga.gulajava.android.databases.RMDataRiwayat;
import pantauharga.gulajava.android.dialogs.DialogHapusAllData;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/11/15.
 */
public class LaporRiwayat extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar tolbar;
    private ActionBar aksibar;

    @Bind(R.id.listview)
    ListView mListView;

    @Bind(R.id.layout_riwayatkosong)
    LinearLayout layout_riwayatkosong;

    private Realm mRealm;
    private RealmQuery<RMDataRiwayat> mRealmQueryRiwayat;
    private RealmResults<RMDataRiwayat> mRealmResultsRiwayat;

    private Parseran mParseran;

    private boolean isDataAda = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_riwayatlaporan);
        ButterKnife.bind(LaporRiwayat.this);
        munculMenuAction(LaporRiwayat.this);

        if (tolbar != null) {
            LaporRiwayat.this.setSupportActionBar(tolbar);
        }

        aksibar = LaporRiwayat.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.riwayat_judul);

        mParseran = new Parseran(LaporRiwayat.this);

        //ambil data realm
        mRealm = Realm.getInstance(LaporRiwayat.this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mRealm.refresh();
        ambilDataRealm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuhapusriwayat = menu.findItem(R.id.action_riwayathapus);

        if (isDataAda) {
            menuhapusriwayat.setVisible(true).setEnabled(true);
        } else {
            menuhapusriwayat.setVisible(false).setEnabled(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LaporRiwayat.this.getMenuInflater().inflate(R.menu.menu_riwayat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                LaporRiwayat.this.finish();
                return true;

            case R.id.action_riwayathapus:

                tampilDialogHapusSemua();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //AMBIL DATA REALM
    private void ambilDataRealm() {

        mRealmQueryRiwayat = mRealm.where(RMDataRiwayat.class);
        mRealmResultsRiwayat = mRealmQueryRiwayat.findAll();

        if (mRealmResultsRiwayat.size() > 0) {

            AdapterRealms adapterRealms = new AdapterRealms(LaporRiwayat.this, mRealmResultsRiwayat, true);
            mListView.setAdapter(adapterRealms);

            mListView.setVisibility(View.VISIBLE);
            layout_riwayatkosong.setVisibility(View.GONE);
            isDataAda = true;

            mListView.setOnItemClickListener(listenerlist);

        } else {
            layout_riwayatkosong.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            isDataAda = false;
        }

        LaporRiwayat.this.supportInvalidateOptionsMenu();
    }


    //ADAPTER REALM
    protected class AdapterRealms extends RealmBaseAdapter<RMDataRiwayat> implements ListAdapter {

        private LayoutInflater mInflater;
        private Context cts;
        private RealmResults<RMDataRiwayat> mRealmResultsRiwa;


        public AdapterRealms(Context context, RealmResults<RMDataRiwayat> realmResults, boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
            this.cts = context;
            this.mRealmResultsRiwa = realmResults;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolderan viewHolderan;

            if (view == null) {

                mInflater = LayoutInflater.from(cts);
                view = mInflater.inflate(R.layout.desainbaris_riwayat, viewGroup, false);
                viewHolderan = new ViewHolderan(view);
                view.setTag(viewHolderan);
            } else {
                viewHolderan = (ViewHolderan) view.getTag();
            }

            try {

                RMDataRiwayat rmDataRiwayat = mRealmResultsRiwa.get(i);
                String namakomoditas = rmDataRiwayat.getNamakomoditas();
                String namalokasi = rmDataRiwayat.getAlamatkomoditas();
                boolean isKiriman = rmDataRiwayat.isKirim();

                String bulatanharga = mParseran.pembulatanBilangan(rmDataRiwayat.getHarga(), 0);
                int bulatanhargaint = Integer.valueOf(bulatanharga);
                String hargapisah = "Rp " + mParseran.formatAngkaPisah(bulatanhargaint) + ",-";

                viewHolderan.getTeks_namakomoditas().setText(namakomoditas);
                viewHolderan.getTeks_alamatkomoditas().setText(namalokasi);
                viewHolderan.getTeks_hargakomoditas().setText(hargapisah);

                if (namalokasi.length() < 4) {
                    viewHolderan.getTeks_alamatkomoditas().setVisibility(View.GONE);
                } else {
                    viewHolderan.getTeks_alamatkomoditas().setVisibility(View.VISIBLE);
                }

                if (isKiriman) {
                    Glide.with(LaporRiwayat.this).load(R.drawable.ic_action_terkirim).into(viewHolderan.getGambar_statuskirim());
                } else {
                    Glide.with(LaporRiwayat.this).load(R.drawable.ic_action_draft).into(viewHolderan.getGambar_statuskirim());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            return view;
        }
    }


    //TAMPIL DIALOG HAPUS SEMUA
    private void tampilDialogHapusSemua() {

        DialogHapusAllData dialogHapusAllData = new DialogHapusAllData();
        dialogHapusAllData.setCancelable(false);

        FragmentTransaction ft = LaporRiwayat.this.getSupportFragmentManager().beginTransaction();
        dialogHapusAllData.show(ft, "dialog hapus data");
    }


    //HAPUS SEMUA DATA
    public void hapusDataSemua() {

        try {

            mRealm.refresh();

            if (mRealmResultsRiwayat.size() > 0) {

                mRealm.beginTransaction();
                mRealmResultsRiwayat.clear();
                mRealm.commitTransaction();

                Toast.makeText(LaporRiwayat.this, R.string.riwayat_hapusdataselesai, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ambilDataRealm();
    }


    AdapterView.OnItemClickListener listenerlist = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intendetil = new Intent(LaporRiwayat.this, LaporHargaEdit.class);
            intendetil.putExtra(Konstan.TAG_INTENT_EDIT_POSISIDB, i);
            LaporRiwayat.this.startActivity(intendetil);
        }
    };


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
