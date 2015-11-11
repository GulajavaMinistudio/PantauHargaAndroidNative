package pantauharga.gulajava.android.aktivitas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.dialogs.DialogBantuanLokasi;
import pantauharga.gulajava.android.messagebus.MessageBusAktAkt;


/**
 * Created by Gulajava Ministudio on 9/27/15.
 */

public class PetaAmbilLokasi extends BaseActivityLocation {


    @Bind(R.id.toolbar)
    Toolbar tolbar;
    private ActionBar aksibar;

    @Bind(R.id.teks_lokasikordinat)
    TextView tekskoordinat;
    @Bind(R.id.tombolsimpan)
    FloatingActionButton tombolsimpan;

    //GPS DAN PENCARI LOKASI
    //ambil lokasi pengguna
    private boolean isInternet = false;


    private double latitudesaya = 0;
    private double longitudesaya = 0;
    private Location lokasisaya;


    //GOOGLE MAPS
    private SupportMapFragment supportMapFragment;
    private GoogleMap petagoogle;
    private static final int paddingTop_dp = 0;
    private static final int paddingBottom_dp = 120;
    private static int paddingTop_px = 0;
    private static int paddingBottom_px = 0;

    //untuk menampilkan marker posisi pengguna
    private LatLng kordinatsaya = null;
    private CameraPosition posisikamerasaya = null;
    private Marker markersaya = null;
    private double latitudesaya_fixes = 0;
    private double longitudesaya_fixes = 0;


    //JIKA PETA MARKERNYA DIKLIK DAN DIGESER GESER
    private LatLng kordinatklikgeser = null;
    private Marker markerklik = null;

    private String strposisipengguna = "";


    //GEOCODER AMBIL LOKASI DAN POSISI PENGGUNA ALAMATNYA JIKA ADA
    private Geocoder geocoderPengguna;
    private List<Address> addressListPengguna = null;
    private String gecoder_alamat = "";
    private String gecoder_namakota = "";
    private String alamatgabungan = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_laporhargapeta);
        ButterKnife.bind(PetaAmbilLokasi.this);
        munculMenuAction(PetaAmbilLokasi.this);

        if (tolbar != null) {
            PetaAmbilLokasi.this.setSupportActionBar(tolbar);
        }

        aksibar = PetaAmbilLokasi.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setTitle(R.string.petaambil_judulhalaman);
        aksibar.setDisplayHomeAsUpEnabled(true);

        supportMapFragment = (SupportMapFragment) PetaAmbilLokasi.this.getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(onMapReadyCallback);

        tombolsimpan.setOnClickListener(listenertombolsimpan);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(PetaAmbilLokasi.this)) {
            EventBus.getDefault().register(PetaAmbilLokasi.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(PetaAmbilLokasi.this)) {
            EventBus.getDefault().unregister(PetaAmbilLokasi.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        hentikanListenerLokasi();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        PetaAmbilLokasi.this.getMenuInflater().inflate(R.menu.menu_ambilkordinat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                PetaAmbilLokasi.this.setResult(AppCompatActivity.RESULT_CANCELED);
                PetaAmbilLokasi.this.finish();

                return true;

            case R.id.action_bantuan:

                tampilDialogGeserLokasi();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //LISTENER JIKA MAP SIAP
    OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            petagoogle = googleMap;

            hitungSkalaAtasBawah();

            petagoogle.setPadding(0, paddingTop_px, 0, paddingBottom_px);
            petagoogle.getUiSettings().setCompassEnabled(true);
            petagoogle.getUiSettings().setZoomControlsEnabled(true);
//            petagoogle.getUiSettings().setMapToolbarEnabled(true);

            petagoogle.setTrafficEnabled(true);
            petagoogle.setIndoorEnabled(true);
            petagoogle.setBuildingsEnabled(true);

            //listener marker
            petagoogle.setOnMapClickListener(listenermapklik);
            petagoogle.setOnMarkerDragListener(listenermarkergeser);

            //ambil lokasi
            cekPermissionLokasi();
        }
    };


    //HITUNG SKALA PETA ATAS DAN BAWAH
    private void hitungSkalaAtasBawah() {

        final float scale = getResources().getDisplayMetrics().density;

        //padding atas
        paddingTop_px = (int) (paddingTop_dp * scale + 0.5f);
        //padding bawah
        paddingBottom_px = (int) (paddingBottom_dp * scale + 0.5f);

    }


    //SETEL MARKER POSISI SAYA DI PETA
    private void setelPosisiSayaMarker() {

        if (lokasisaya != null) {

            petagoogle.clear();

            kordinatsaya = new LatLng(latitudesaya, longitudesaya);

            posisikamerasaya = new CameraPosition.Builder().target(kordinatsaya)
                    .zoom(16)
                    .bearing(0)
                    .tilt(0)
                    .build();

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(kordinatsaya)
                    .title("Posisi saya")
                    .draggable(true);

            markersaya = petagoogle.addMarker(markerOptions);
            petagoogle.moveCamera(CameraUpdateFactory.newCameraPosition(posisikamerasaya));
            markersaya.showInfoWindow();

            latitudesaya_fixes = latitudesaya;
            longitudesaya_fixes = longitudesaya;

            setelKeEditTextLokasi();

            //ambil geocoder
            taskAmbilGeocoder();
        }
    }


    //LISTENER JIKA PETA GOOGLE DI KLIK
    GoogleMap.OnMapClickListener listenermapklik = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

            petagoogle.clear();

            latitudesaya_fixes = latLng.latitude;
            longitudesaya_fixes = latLng.longitude;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title("Posisi saya");

            markerklik = petagoogle.addMarker(markerOptions);
            markerklik.showInfoWindow();

            setelKeEditTextLokasiDragKlik();

            //ambil geocoder
            taskAmbilGeocoder();
        }
    };


    //LISTENER JIKA MARKER PIN DIGESER LOKASINYA
    GoogleMap.OnMarkerDragListener listenermarkergeser = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {

        }

        @Override
        public void onMarkerDrag(Marker marker) {

            marker.hideInfoWindow();
            kordinatklikgeser = marker.getPosition();

            latitudesaya_fixes = kordinatklikgeser.latitude;
            longitudesaya_fixes = kordinatklikgeser.longitude;

            setelKeEditTextLokasiDragKlik();

        }

        @Override
        public void onMarkerDragEnd(Marker marker) {

            marker.hideInfoWindow();

            kordinatklikgeser = marker.getPosition();

            latitudesaya_fixes = kordinatklikgeser.latitude;
            longitudesaya_fixes = kordinatklikgeser.longitude;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(kordinatklikgeser)
                    .title("Posisi saya")
//                    .snippet(latitudesaya_fixes + " , " + longitudesaya_fixes)
                    .draggable(true);

            petagoogle.clear();
            markersaya = petagoogle.addMarker(markerOptions);

            setelKeEditTextLokasiDragKlik();

            //ambil geocoder
            taskAmbilGeocoder();
        }
    };


    //TAMPIL DIALOG PETUNJUK GESER LOKASI
    private void tampilDialogGeserLokasi() {

        FragmentTransaction fts = PetaAmbilLokasi.this.getSupportFragmentManager().beginTransaction();
        DialogBantuanLokasi dialogBantuanLokasi = new DialogBantuanLokasi();
        dialogBantuanLokasi.show(fts, "dialog bantuan lokasi");

    }


    /**
     * TERIMA PESAN DARI LISTENER LOKASI
     **/
    public void onEvent(MessageBusAktAkt message) {

        int kodeperintah = message.getKode();
        switch (kodeperintah) {

            case Konstan.KODE_LOKASIBARU:

                ambilKordinatLokasiSekarang();

                break;

            case Konstan.KODE_LOKASIAWAL:

                ambilKordinatLokasiSekarangAwal();
                break;

        }

    }


    //AMBIL KOORDINAT LOKASI SEKARANG
    private void ambilKordinatLokasiSekarang() {

        PetaAmbilLokasi.this.latitudesaya = getLatitudesaya();
        PetaAmbilLokasi.this.longitudesaya = getLongitudesaya();
        isInternet = isInternet();
        lokasisaya = getLokasisaya();

        Log.w("LOKASI SEKARANG", "lokasi sekarang " + latitudesaya + " , " + longitudesaya);

    }


    private void ambilKordinatLokasiSekarangAwal() {

        PetaAmbilLokasi.this.latitudesaya = getLatitudesaya();
        PetaAmbilLokasi.this.longitudesaya = getLongitudesaya();
        isInternet = isInternet();
        lokasisaya = getLokasisaya();

        Log.w("LOKASI SEKARANG", "lokasi sekarang " + latitudesaya + " , " + longitudesaya);

        setelPosisiSayaMarker();
    }


    //SETEL KE EDIT TEXT
    private void setelKeEditTextLokasi() {

        strposisipengguna = latitudesaya + " , " + longitudesaya;

        tekskoordinat.setText(strposisipengguna);
    }

    private void setelKeEditTextLokasiDragKlik() {

        strposisipengguna = latitudesaya_fixes + " , " + longitudesaya_fixes;

        tekskoordinat.setText(strposisipengguna);
    }


    View.OnClickListener listenertombolsimpan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            cekLokasiKordinat();
        }
    };


    //CEK LOKASI
    private void cekLokasiKordinat() {

        if (latitudesaya_fixes != 0 && longitudesaya_fixes != 0) {

            String strlatitudefixes = latitudesaya_fixes + "";
            String strlongitudefixes = longitudesaya_fixes + "";

            if (strlatitudefixes.length() > 3 && strlongitudefixes.length() > 3) {

                Intent intenthasil = new Intent();
                intenthasil.putExtra(Konstan.TAG_INTENT_LAT, strlatitudefixes);
                intenthasil.putExtra(Konstan.TAG_INTENT_LONG, strlongitudefixes);
                intenthasil.putExtra(Konstan.TAG_INTENT_ALAMATGABUNGANS, alamatgabungan);

                Toast.makeText(PetaAmbilLokasi.this, R.string.toast_oksimpan, Toast.LENGTH_SHORT).show();

                PetaAmbilLokasi.this.setResult(Activity.RESULT_OK, intenthasil);
                PetaAmbilLokasi.this.finish();

            } else {
                Toast.makeText(PetaAmbilLokasi.this, R.string.toastgagal_ambilkordinat, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(PetaAmbilLokasi.this, R.string.toastgagal_ambilkordinat, Toast.LENGTH_LONG).show();
        }
    }


    //AMBIL GEOCODER PENGGUNA
    private void ambilGeocoderPengguna() {

        geocoderPengguna = new Geocoder(PetaAmbilLokasi.this, Locale.getDefault());

        if (isInternet) {

            try {

                addressListPengguna = geocoderPengguna.getFromLocation(latitudesaya_fixes, longitudesaya_fixes, 1);
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
    }


    private void taskAmbilGeocoder() {

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                ambilGeocoderPengguna();

                return null;
            }
        })

                .continueWith(new Continuation<Object, Object>() {
                    @Override
                    public Object then(Task<Object> task) throws Exception {

                        if (alamatgabungan.length() > 1) {

                            Log.w("ALAMAT GABUNGAN TASK", "ALAMAT " + alamatgabungan);
                            tekskoordinat.setText(alamatgabungan);

                        }

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
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
