package pantauharga.gulajava.android.aktivitas;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.dialogs.DialogGagalGpsLokasi;
import pantauharga.gulajava.android.dialogs.DialogMintaAksesLoks;
import pantauharga.gulajava.android.messagebus.MessageBusAktAkt;
import pantauharga.gulajava.android.parsers.CekGPSNet;

/**
 * Created by Gulajava Ministudio on 10/13/15.
 */
public abstract class BaseActivityLocation extends AppCompatActivity {


    //GPS DAN PENCARI LOKASI
    //ambil lokasi pengguna
    private boolean statusGPS = false;
    private boolean statusInternet = false;
    private boolean isInternet = false;
    private boolean isNetworkNyala = false;
    private boolean isGPSNyala = false;
    private CekGPSNet cekGpsNet = null;


    private double latitudesaya = 0;
    private double longitudesaya = 0;
    private Location lokasisaya;
    private LocationManager locmanager = null;
    private double latitude = 0;
    private double longitude = 0;

    //location listener
    private ListenerLokasi lokasilistenerNet = null;
    private ListenerLokasi lokasilistenerGps = null;

    private Location lokasiNet;
    private Location lokasiGPS;

    //UNTUK AMBIL LOKASI
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 60000;//1000 * 60 * 1; // 1 minute

    private boolean isPemissionLokasiOK = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


    //CEK IJIN PERMISSION UNTUK ANDROID MARSHMALLOW
    public void cekPermissionLokasi() {

        Log.w("CEK PERMISI", "CEK PERMISI DULU SEBELUM JALAN");

        int intKodeFineLokasiOK = ActivityCompat.checkSelfPermission(BaseActivityLocation.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int intkodeCoarseLokOK = ActivityCompat.checkSelfPermission(BaseActivityLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION);


        if (intKodeFineLokasiOK != PackageManager.PERMISSION_GRANTED
                && intkodeCoarseLokOK != PackageManager.PERMISSION_GRANTED) {


            if (!ActivityCompat.shouldShowRequestPermissionRationale(BaseActivityLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(BaseActivityLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                isPemissionLokasiOK = false;

                //tampilkan dialog
                tampilDialogLokasiRasional();
                Log.w("CEK RASIONAL", "CEK PERMISI RASIONAL false");

            } else {
                Log.w("CEK RASIONAL", "CEK PERMISI RASIONAL true");
                tampilDialogLokasiRasional();
            }

            // Request missing location permission.
//            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//            ActivityCompat.requestPermissions(BaseLocationAktivitas.this, permission, Konstan.REQUEST_CODE_LOCATION);

        } else {
            // Location permission has been granted, continue as usual.
            isPemissionLokasiOK = true;

            //ambil lokasi
            cekGpsInternet();
        }
    }


    //MINTA DATA LOKASI
    public void mintaLokasi() {

        // Request missing location permission.
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(BaseActivityLocation.this, permission, Konstan.REQUEST_CODE_LOCATION);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Konstan.REQUEST_CODE_LOCATION) {

            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // success!
                isPemissionLokasiOK = true;

            } else {
                // Permission was denied or request was cancelled
                isPemissionLokasiOK = false;
            }
        }

        Log.w("LOG REQUEST", "LOG REQUEST KODE LOKASI " + isPemissionLokasiOK);

        if (isPemissionLokasiOK) {
            //ambil lokasi
            cekGpsInternet();
        } else {
            Toast.makeText(BaseActivityLocation.this, "Aplikasi memerlukan akses lokasi", Toast.LENGTH_SHORT).show();
        }
    }


    //HENTIKAN LOKASI LISTENER
    public void hentikanListenerLokasi() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isPemissionLokasiOK) {
            try {
                if (lokasilistenerNet != null) {
                    locmanager.removeUpdates(lokasilistenerNet);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            try {
                if (lokasilistenerGps != null) {
                    locmanager.removeUpdates(lokasilistenerGps);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    //CEK KONEKSI INTERNET DAN GPS AKTIF ATAU TIDAK
    public void cekGpsInternet() {

        lokasilistenerNet = new ListenerLokasi(BaseActivityLocation.this, Konstan.TIPENETWORK);
        lokasilistenerGps = new ListenerLokasi(BaseActivityLocation.this, Konstan.TIPEGPS);

        cekGpsNet = new CekGPSNet(BaseActivityLocation.this);

        isInternet = cekGpsNet.cekStatsInternet();
        isNetworkNyala = cekGpsNet.cekStatsNetwork();
        isGPSNyala = cekGpsNet.cekStatsGPS();

        statusInternet = cekGpsNet.getKondisiNetwork(isInternet, isNetworkNyala);
        statusGPS = cekGpsNet.getKondisiGPS(isGPSNyala);

        Log.w("STATUS GPS  HASIL", "GPS " + statusGPS + " INTERNET " + statusInternet);

        locmanager = (LocationManager) BaseActivityLocation.this.getSystemService(Context.LOCATION_SERVICE);

        //ambil lokasi dari sinyal gsm
        if (statusInternet || isNetworkNyala) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                try {

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.w("NETWORK", "GPS DIAMBIL DARI NETWORK");
            locmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, lokasilistenerNet);

            if (locmanager != null) {

                lokasiNet = locmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lokasiNet != null) {

                    latitude = lokasiNet.getLatitude();
                    longitude = lokasiNet.getLongitude();

                    Log.w("NETWORK", "GPS DIAMBIL DARI NETWORK " + latitude + " " + longitude);
                }
            }
        }

        //ambil lokasi dari sinyal gps
        if (statusGPS) {

            Log.w("GPS", "GPS DIAMBIL DARI SATELIT GPS");
            locmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, lokasilistenerGps);

            if (locmanager != null) {

                lokasiGPS = locmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lokasiGPS != null) {

                    latitude = lokasiGPS.getLatitude();
                    longitude = lokasiGPS.getLongitude();
                    Log.w("GPS", "GPS DIAMBIL DARI SATELIT GPS " + latitude + " " + longitude);
                }
            }
        }


        if (lokasiNet != null) {
            lokasisaya = lokasiNet;
        }

        if (lokasiGPS != null) {
            lokasisaya = lokasiGPS;
        }


        if (!statusInternet || !statusGPS) {

            //tampilkan dialog gps ga nyala
            if (lokasiNet == null && lokasiGPS == null) {
                Log.w("LOKASI TIDAK AKTIF", "LOKASI TIDAK AKTIF");
                tampilDialogGagalLokasi();
            }
        }

        //ambil lokasi sekarang dan set ke getter setter
        ambilKordinatLokasiAwal(lokasisaya);

    }


    //DIALOG GAGAL LOKASI
    private void tampilDialogGagalLokasi() {

        DialogGagalGpsLokasi dialogGagalGpsLokasi = new DialogGagalGpsLokasi();
        FragmentTransaction fts = BaseActivityLocation.this.getSupportFragmentManager().beginTransaction();
        dialogGagalGpsLokasi.show(fts, "dialog gagal lokasi");

    }


    //TAMPIL DIALOG MINTA AKSES RASIONAL LOKASI
    private void tampilDialogLokasiRasional() {

        DialogMintaAksesLoks dialogMintaAksesLoks = new DialogMintaAksesLoks();
        FragmentTransaction fts = BaseActivityLocation.this.getSupportFragmentManager().beginTransaction();
        dialogMintaAksesLoks.setCancelable(false);
        dialogMintaAksesLoks.show(fts, "dialog minta lokasi rasional");

    }


    //LISTENER AMBIL LOKASI JARINGAN DAN GPS
    //LISTENER PERGANTIAN LOKASI
    protected class ListenerLokasi implements LocationListener {


        public Location location;
        public double latitudes = 0;
        public double longitudes = 0;

        public Context ctxs;
        public String tipeListener = "";

        public ListenerLokasi(Context ctx, String STRTIPE) {
            this.tipeListener = STRTIPE;
            this.ctxs = ctx;
        }


        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {

                latitudes = location.getLatitude();
                longitudes = location.getLongitude();

                setLocation(location);
                setLatitudes(latitudes);
                setLongitudes(longitudes);

                Log.w("LOKASI GANTI", "lokasi ganti tipe " + tipeListener);

                //kirim dan jalankan fungsi void data
                updateDataLokasi(tipeListener);

            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public double getLatitudes() {
            return latitudes;
        }

        public void setLatitudes(double latitudes) {
            this.latitudes = latitudes;
        }

        public double getLongitudes() {
            return longitudes;
        }

        public void setLongitudes(double longitudes) {
            this.longitudes = longitudes;
        }
    }


    //UPDATE JSON LOKASI
    //FUNGSI JALANKAN DATA CEK UPDATE LOKASI
    private void updateDataLokasi(String kodeListener) {


        if (kodeListener.contentEquals(Konstan.TIPENETWORK)) {

            Log.w("LOKASI NET", "lokasi jaringan");
            BaseActivityLocation.this.lokasisaya = lokasilistenerNet.getLocation();

        } else if (kodeListener.contentEquals(Konstan.TIPEGPS)) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                try {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (isPemissionLokasiOK) {
                Log.w("LOKASI GPS", "lokasi gps, hilangkan listener jaringan");
                locmanager.removeUpdates(lokasilistenerNet);
                BaseActivityLocation.this.lokasisaya = lokasilistenerGps.getLocation();
            }
        }

        if (lokasisaya != null) {

            ambilKordinatLokasiSekarang(lokasisaya);

        }
    }


    //AMBIL KOORDINAT LOKASI SEKARANG
    private void ambilKordinatLokasiAwal(Location location) {

        if (location != null) {
            BaseActivityLocation.this.latitudesaya = location.getLatitude();
            BaseActivityLocation.this.longitudesaya = location.getLongitude();

            Log.w("LOKASI SEKARANG", "lokasi sekarang " + latitudesaya + " , " + longitudesaya);

            kirimPesanLokasiJalanOK(Konstan.KODE_LOKASIAWAL);
        }
    }


    //AMBIL KOORDINAT LOKASI SEKARANG
    private void ambilKordinatLokasiSekarang(Location location) {

        if (location != null) {
            BaseActivityLocation.this.latitudesaya = location.getLatitude();
            BaseActivityLocation.this.longitudesaya = location.getLongitude();

            Log.w("LOKASI SEKARANG", "lokasi sekarang " + latitudesaya + " , " + longitudesaya);

            kirimPesanLokasiJalanOK(Konstan.KODE_LOKASIBARU);
        }
    }


    //AMBIL NILAI LOKASI DAN KORDINAT
    public double getLatitudesaya() {
        return latitudesaya;
    }

    public double getLongitudesaya() {
        return longitudesaya;
    }

    public Location getLokasisaya() {
        return lokasisaya;
    }

    public void setIsInternet(boolean isInternet) {
        this.isInternet = isInternet;
    }

    public boolean isInternet() {
        return isInternet;
    }

    private void kirimPesanLokasiJalanOK(int kodepesans) {

        MessageBusAktAkt messageBusAktAkt = new MessageBusAktAkt();
        messageBusAktAkt.setKode(kodepesans);

        EventBus.getDefault().post(messageBusAktAkt);
    }


    //CEK KONEKSI INTERNET
    public void cekInternet() {

        CekGPSNet cekinternet = new CekGPSNet(BaseActivityLocation.this);
        isInternet = cekinternet.cekStatsInternet();

        setIsInternet(isInternet);
    }



}
