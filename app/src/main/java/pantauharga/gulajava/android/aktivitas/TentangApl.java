package pantauharga.gulajava.android.aktivitas;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;

/**
 * Created by Gulajava Ministudio on 11/12/15.
 */
public class TentangApl extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    private ActionBar mActionBar;

    @Bind(R.id.pembuat1)
    TextView tekspembuat1;
    @Bind(R.id.pembuat1s)
    TextView tekspembuat1s;
    @Bind(R.id.pembuat2)
    TextView tekspembuat2;
    @Bind(R.id.pembuat2s)
    TextView tekspembuat2s;
    @Bind(R.id.pembuat3)
    TextView tekspembuat3;
    @Bind(R.id.pembuat3s)
    TextView tekspembuat3s;
    @Bind(R.id.pembuat4)
    TextView tekspembuat4;
    @Bind(R.id.pembuat4s)
    TextView tekspembuat4s;
    @Bind(R.id.pembuat5)
    TextView tekspembuat5;
    @Bind(R.id.pembuat5a)
    TextView tekspembuat5a;
    @Bind(R.id.pembuat5b)
    TextView tekspembuat1b;

    @Bind(R.id.gambarlogo)
    ImageView gambarlogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tentangapl);
        ButterKnife.bind(TentangApl.this);

        if (mToolbar != null) {

            TentangApl.this.setSupportActionBar(mToolbar);
        }

        mActionBar = TentangApl.this.getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(R.string.tentangapl_judul);
        mActionBar.setDisplayHomeAsUpEnabled(true);


        tekspembuat1.setText(Konstan.CREDIT_PEMBUAT_1);
        tekspembuat1s.setText(Konstan.CREDIT_PEMBUAT_1s);
        tekspembuat2.setText(Konstan.CREDIT_PEMBUAT_2);
        tekspembuat2s.setText(Konstan.CREDIT_PEMBUAT_2s);
        tekspembuat3.setText(Konstan.CREDIT_PEMBUAT_3);
        tekspembuat3s.setText(Konstan.CREDIT_PEMBUAT_3s);
        tekspembuat4.setText(Konstan.CREDIT_PEMBUAT_4);
        tekspembuat4s.setText(Konstan.CREDIT_PEMBUAT_4s);
        tekspembuat5.setText(Konstan.CREDIT_PEMBUAT_5);
        tekspembuat5a.setText(Konstan.CREDIT_PEMBUAT_5a);
        tekspembuat1b.setText(Konstan.CREDIT_PEMBUAT_5b);

        Glide.with(TentangApl.this).load(R.drawable.web_hi_res_512).into(gambarlogo);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemid = item.getItemId();
        if (itemid == android.R.id.home) {
            TentangApl.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
