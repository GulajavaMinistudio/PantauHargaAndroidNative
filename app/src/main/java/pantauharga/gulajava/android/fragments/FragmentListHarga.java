package pantauharga.gulajava.android.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.adapters.RecyclerDaftarHarga;
import pantauharga.gulajava.android.adapters.RecyclerDividers;
import pantauharga.gulajava.android.aktivitas.MenuUtama;
import pantauharga.gulajava.android.messagebus.MessageAktFrag;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItem;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItemKomparator;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class FragmentListHarga extends Fragment {


    @Bind(R.id.swipelayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.tombol_menucariharga)
    FloatingActionButton mFloatingActionButton;


    private List<HargaKomoditasItem> mListKomoditasHarga;
    private List<HargaKomoditasItemKomparator> mListKomoHargaKomparator;

    private Parseran mParseran;
    private RecyclerDaftarHarga mRecyclerDaftarHarga;
    private int modeUrutan = Konstan.MODE_TERDEKAT;

    private boolean isDisegarkanUrutan = false;

    private Location mLocationSaya;
    private LinearLayoutManager linearLayoutManager;

    private MenuUtama mMenuUtamaAkt;

    private int posisiklikharga = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_daftarharga, container, false);
        ButterKnife.bind(FragmentListHarga.this, view);

        mMenuUtamaAkt = (MenuUtama) FragmentListHarga.this.getActivity();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeRefreshLayout.setEnabled(false);

        mParseran = new Parseran(FragmentListHarga.this.getActivity());
        linearLayoutManager = new LinearLayoutManager(FragmentListHarga.this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecyclerDividers(FragmentListHarga.this.getActivity(), R.drawable.dividerlistmenu));

        mFloatingActionButton.setOnClickListener(listenerfloatingbutton);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(FragmentListHarga.this)) {
            EventBus.getDefault().register(FragmentListHarga.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(FragmentListHarga.this)) {
            EventBus.getDefault().unregister(FragmentListHarga.this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentListHarga.this);
    }


    /**
     * EVENT DARI AKTIVITAS KE FRAGMENT
     **/
    public void onEvent(MessageAktFrag messageAktFrag) {

        int kodepesan = messageAktFrag.getKode();

        switch (kodepesan) {

            case Konstan.KODE_LISTBARU:

                mListKomoditasHarga = messageAktFrag.getListHargaKomoditas();
                modeUrutan = messageAktFrag.getModelist();
                mLocationSaya = messageAktFrag.getLocation();

                //segarkan daftars
                cekDaftarHasil();

                break;
        }
    }


    //CEK DAFTAR
    private void cekDaftarHasil() {

        if (mListKomoditasHarga != null && mListKomoditasHarga.size() > 0) {

            parseKomparator();

        } else {
            tampilToast(R.string.toastolahdata);
        }
    }


    //PARSE KE DALAM BENTUK KOMPARATOR
    private void parseKomparator() {

        mSwipeRefreshLayout.setRefreshing(true);

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                mListKomoHargaKomparator = mParseran.parseListKomparator(mListKomoditasHarga, mLocationSaya,
                        modeUrutan);

                return null;
            }
        })

                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(Task<Object> task) throws Exception {

                        if (mListKomoHargaKomparator != null && mListKomoHargaKomparator.size() > 0) {

                            mRecyclerDaftarHarga = new RecyclerDaftarHarga(mListKomoHargaKomparator, FragmentListHarga.this.getActivity());
                            mRecyclerDaftarHarga.setOnItemClickListener(listenerkliklist);
                            mRecyclerView.setAdapter(mRecyclerDaftarHarga);
                            //
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            tampilToast(R.string.toastolahdata);
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);

    }


    View.OnClickListener listenerfloatingbutton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mMenuUtamaAkt.tampilDialogPilihKomoditas();
        }
    };


    //JIKA RECYCLER VIEW DIKLIK
    RecyclerDaftarHarga.OnItemClickListener listenerkliklist = new RecyclerDaftarHarga.OnItemClickListener() {
        @Override
        public void onItemClick(View view, HargaKomoditasItemKomparator hargaItem, int posisi) {

            posisiklikharga = posisi;
            mMenuUtamaAkt.setelPilihanKePeta(posisiklikharga);

        }
    };


    private void tampilToast(int resStr) {
        Toast.makeText(FragmentListHarga.this.getActivity(), resStr, Toast.LENGTH_SHORT).show();
    }


}
