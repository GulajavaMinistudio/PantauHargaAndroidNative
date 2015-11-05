package pantauharga.gulajava.android.aktivitas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import pantauharga.gulajava.android.R;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */


public class Loading extends AppCompatActivity {


    private Handler mHandler;

    Runnable splash = new Runnable() {
        @Override
        public void run() {

            Intent intentloading = new Intent(Loading.this, MenuUtama.class);
            Loading.this.startActivity(intentloading);
            Loading.this.finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadings);
        ButterKnife.bind(Loading.this);

        mHandler = new Handler();

        mHandler.postDelayed(splash, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    //AMBIL DATA DARI ASET


    //INISIALISASI DATA DATABASE


    //SIMPAN KE DATABASE


    //CEK STATUS KADALUARSA DATA


    //AMBIL DATA DARI SERVER UNTUK KOMODITAS


    //SIMPAN KE DATABASE


    //PINDAH HALAMAN


    //TASK AMBIL DAN MUAT DATA


}
