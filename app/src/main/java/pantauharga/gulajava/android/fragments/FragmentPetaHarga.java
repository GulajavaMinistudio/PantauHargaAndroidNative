package pantauharga.gulajava.android.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.messagebus.MessageAktFrag;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItem;
import pantauharga.gulajava.android.modelgson.HargaKomoditasItemKomparator;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class FragmentPetaHarga extends Fragment {


    //GOOGLE MAPS
    private SupportMapFragment mapfragment;
    private GoogleMap map;
    private static final int paddingTop_dp = 0;
    private static final int paddingBottom_dp = 125;
    private static int paddingTop_px = 0;
    private static int paddingBottom_px = 0;

    @Bind(R.id.teks_namakomoditas) TextView teks_namakomoditas;
    @Bind(R.id.teks_hargakomoditas) TextView teks_hargakomoditas;
    @Bind(R.id.teks_alamatlokasi) TextView teks_alamatkomoditas;
    @Bind(R.id.teks_nomortelpon) TextView teks_telponkomoditas;
    @Bind(R.id.tombol_navigasi) FloatingActionButton tombolnavigasikan;

    //untuk menampilkan marker posisi pengguna
    private LatLng kordinatsaya = null;
    private CameraPosition posisikamerasaya = null;
    private Marker markersaya = null;
    private double latitudesaya = 0;
    private double longitudesaya = 0;
    private Location lokasisaya = null;
    private boolean isMapSiap = false;


    //DAFTAR HARGA KOMODITAS

    private List<HargaKomoditasItem> mListKomoditasHarga;
    private List<HargaKomoditasItemKomparator> mListKomoHargaKomparator;

    private Map<Marker, HargaKomoditasItemKomparator> hashmapListHarga;

    //JIKA PETA DIPILIH DARI HALAMAN SEBELAH
    private LatLng kordinatklik = null;
    private CameraPosition posisikameraklik = null;
    private Marker markerklik = null;


    //NAVIGASI KE GOOGLE MAPS
    private String latpeta = "0";
    private String longipeta = "0";

    private Parseran mParseran;
    private int modeUrutan = Konstan.MODE_TERDEKAT;









    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_petaharga, container, false);
        ButterKnife.bind(FragmentPetaHarga.this, view);

        mParseran = new Parseran(FragmentPetaHarga.this.getActivity());

        mapfragment = (SupportMapFragment) FragmentPetaHarga.this.getChildFragmentManager().findFragmentById(R.id.map);

        tombolnavigasikan.setOnClickListener(listenertombolfloat);
        teks_alamatkomoditas.setVisibility(View.GONE);
        teks_telponkomoditas.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentPetaHarga.this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(FragmentPetaHarga.this)) {
            EventBus.getDefault().register(FragmentPetaHarga.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(FragmentPetaHarga.this)) {
            EventBus.getDefault().unregister(FragmentPetaHarga.this);
        }
    }



    /** LISTENER EVENTBUS PESAN DARI AKTIVITAS UTAMA ***/
    public void onEvent(MessageAktFrag messageAktFrag) {

        int kodepesan = messageAktFrag.getKode();

        switch (kodepesan) {

            case Konstan.KODE_LISTBARU:

                mListKomoditasHarga = messageAktFrag.getListHargaKomoditas();
                lokasisaya = messageAktFrag.getLocation();
                modeUrutan = messageAktFrag.getModelist();

                //segarkan daftars
                cekDaftarHasil();

                break;
        }
    }


    //CEK DAFTAR
    private void cekDaftarHasil() {

        if (mListKomoditasHarga != null && mListKomoditasHarga.size() > 0) {

            //ambil list dan tampilkan ke peta

        }
    }


    //UBAH MENJADI BENTUK JARAK TERDEKAT DENGAN KOMPARATOR DKK
    private void parseKomparatorPeta() {

        //setel indikator memuat peta

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                mListKomoHargaKomparator = mParseran.parseListKomparator(mListKomoditasHarga, lokasisaya,
                        modeUrutan);

                return null;
            }
        }).continueWith(new Continuation<Object, Object>() {
            @Override
            public Object then(Task<Object> task) throws Exception {

                if (mListKomoHargaKomparator != null && mListKomoHargaKomparator.size() > 0) {


                }
                else {

                    //peta gagal dimuat

                }

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }



    //MUAT PETA GOOGLE MAPS
    private void muatPetaGoogleMaps() {

        mapfragment.getMapAsync(mOnMapReadyCallback);

    }

    OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            isMapSiap  = true;

            hitungSkalaAtasBawah();
            map.setPadding(0, paddingTop_px, 0, paddingBottom_px);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);

            //setel posisi saya awal

            //setel peta marker

        }
    };

    private void hitungSkalaAtasBawah() {

        final float scale = getResources().getDisplayMetrics().density;

        //padding atas
        paddingTop_px = (int) (paddingTop_dp * scale + 0.5f);
        //padding bawah
        paddingBottom_px = (int) (paddingBottom_dp * scale + 0.5f);

    }



    //SETEL POSISI SAYA
    public void setelPosisiSayaAwal() {

        try {

            if (markersaya != null) {
                markersaya.remove();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.w("LOKASI", "lokasi saya peta " + latitudesaya + " , " + longitudesaya);


        kordinatsaya = new LatLng(latitudesaya, longitudesaya);
        posisikamerasaya = new CameraPosition.Builder().target(kordinatsaya)
                .zoom(16)
                .bearing(0).tilt(0).build();

        markersaya = map.addMarker(new MarkerOptions()
                .position(kordinatsaya)
                .title("Posisi Anda")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_lokasi_saya)));

        map.moveCamera(CameraUpdateFactory.newCameraPosition(posisikamerasaya));
        markersaya.showInfoWindow();
    }


    public void setelPetaMarker() {

        if (lokasisaya != null) {

            latitudesaya = lokasisaya.getLatitude();
            longitudesaya = lokasisaya.getLongitude();

            Log.w("LOKASI", "lokasi saya peta " + latitudesaya + " , " + longitudesaya);

        }

        if (isMapSiap) {

            if (mListKomoHargaKomparator != null && mListKomoHargaKomparator.size() > 0) {

                //setel ke marker
                setelMarkerSemua();

            } else {
                map.clear();
                setelPosisiSayaAwal();
                teks_namakomoditas.setText("Nama tidak tersedia");
                teks_hargakomoditas.setText("Harga tidak tersedia");
                teks_alamatkomoditas.setText("Alamat tidak tersedia");
                teks_telponkomoditas.setText("Telepon tidak tersedia");
            }
        }

    }


    //TAMPILKAN KE DALAM MARKER
    /**
     * SETEL DAN PASANG MARKER SEMUA KE PETA *
     */
    private void setelMarkerSemua() {

        try {

            int panjangarray = mListKomoHargaKomparator.size();
            HargaKomoditasItemKomparator lokitem;
            double dolatitude = 0;
            double dolongitude = 0;
            String strnamalokasi = "";


            map.clear();

            hashmapListHarga = new HashMap<>();

            setelPosisiSayaAwal();

            HargaKomoditasItemKomparator itemloks = mListKomoHargaKomparator.get(0);

            String namakomoditas = itemloks.getBarang();
            int hargakomoditas = itemloks.getPrice();
            String alamatkomoditas = "";
            String telponkomoditas = itemloks.getNohp() + "";

            latpeta = itemloks.getLatitude();
            longipeta = itemloks.getLongitude();

            teks_namakomoditas.setText(namakomoditas);
            teks_telponkomoditas.setText("Telp : " + telponkomoditas);


            //setel ke peta
            for (int i = 0; i < panjangarray; i++) {

                lokitem = mListKomoHargaKomparator.get(i);
                int nojudul = i + 1;

                dolatitude = Double.valueOf(lokitem.getLatitude());
                dolongitude = Double.valueOf(lokitem.getLongitude());
                strnamalokasi = lokitem.getBarang();

                LatLng latlnglokasi = new LatLng(dolatitude, dolongitude);

                MarkerOptions markeropsi = new MarkerOptions();
                markeropsi.position(latlnglokasi);
                markeropsi.title(nojudul + ". " + strnamalokasi);
                markeropsi.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pinpetaharga));

                Marker markeradd = map.addMarker(markeropsi);

                hashmapListHarga.put(markeradd, lokitem);
            }

            tombolnavigasikan.setVisibility(View.VISIBLE);


        } catch (Exception ex) {
            ex.printStackTrace();
            tombolnavigasikan.setVisibility(View.GONE);
        }
    }



    //LISTENER KLO MARKER DI KLIK
    GoogleMap.OnMarkerClickListener listenermarker = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            marker.showInfoWindow();

            //tampilkan keterangan marker


            return true;
        }
    };




    View.OnClickListener listenertombolfloat = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };





}
