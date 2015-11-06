package pantauharga.gulajava.android.parsers;

/**
 * Created by Gulajava Ministudio on 6/28/14.
 */

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class CekGPSNet {

    Context konteks = null;
    LocationManager lokasimanager = null;
    ConnectivityManager conmanager = null;
    NetworkInfo netinfo = null;
    boolean isInternet = false;
    boolean isNetworkNyala = false;
    boolean isGPSNyala = false;

    public boolean statusNetwork = false;
    public boolean statusGPS = false;


    public CekGPSNet(Context conteks) {
        this.konteks = conteks;

        isInternet = false;
        isNetworkNyala = false;
        isGPSNyala = false;
        statusNetwork = false;
        statusGPS = false;


        lokasimanager = (LocationManager) konteks.getSystemService(Context.LOCATION_SERVICE);
        conmanager = (ConnectivityManager) konteks.getSystemService(Context.CONNECTIVITY_SERVICE);

    }


    //cek status internet dan balikkan nilainya
    public boolean cekStatsInternet() {

        netinfo = conmanager.getActiveNetworkInfo();

        isInternet = netinfo != null && netinfo.isConnected();

        Log.w("TAG INTERNET CLASS", isInternet + "");

        return isInternet;
    }


    //cek status jaringan, apakah hidup atau ga
    public boolean cekStatsNetwork() {

        try {
            isNetworkNyala = lokasimanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
            isNetworkNyala = false;
        }

        Log.w("TAG NETWORK CLASS", isNetworkNyala + "");

        return isNetworkNyala;
    }


    //cek status gps apakah hidup atau ga
    public boolean cekStatsGPS() {

        try {
            isGPSNyala = lokasimanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
            isGPSNyala = false;
        }

        return isGPSNyala;
    }


    //setel status jaringan berdasarkan kondisi internet dan jaringan
    public boolean getKondisiNetwork(boolean statInet, boolean statNetw) {

        if (statInet && statNetw) {
            statusNetwork = true;
        } else if (!statInet && !statNetw) {
            statusNetwork = false;
        } else {
            statusNetwork = false;
        }

        return statusNetwork;
    }


    //setel status gps berdasarkan kondisi gps
    public boolean getKondisiGPS(boolean statGPS) {

        statusGPS = statGPS;

        return statusGPS;
    }


}
