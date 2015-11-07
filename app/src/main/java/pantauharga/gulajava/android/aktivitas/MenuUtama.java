package pantauharga.gulajava.android.aktivitas;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.adapters.AdapterTabPager;
import pantauharga.gulajava.android.fragments.FragmentListHarga;
import pantauharga.gulajava.android.fragments.FragmentPetaHarga;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class MenuUtama extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    private ActionBar mActionBar;

    @Bind(R.id.layoutdrawer) DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) NavigationView mNavigationView;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.tabsbars) TabLayout mTabLayout;

    private ImageView gambarheaders;
    private View view_navigasi;

    private AdapterTabPager mAdapterTabPager;

    private boolean isDisegarkanList = false;
    private boolean isAktJalan = false;
    private boolean isDrawerBuka = false;

    private FragmentListHarga mFragmentListHarga;
    private FragmentPetaHarga mFragmentPetaHarga;


    //realm db








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menumain);
        ButterKnife.bind(MenuUtama.this);
        munculMenuAction(MenuUtama.this);

        if (mToolbar != null) {
            MenuUtama.this.setSupportActionBar(mToolbar);
        }

        mActionBar = MenuUtama.this.getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mFragmentListHarga = new FragmentListHarga();
        mFragmentPetaHarga = new FragmentPetaHarga();

        //ambil acak gambar headers

        view_navigasi = mNavigationView.inflateHeaderView(R.layout.nav_header);
        gambarheaders = (ImageView) view_navigasi.findViewById(R.id.gambarheader);
        Glide.with(MenuUtama.this).load(R.drawable.gbrhero2).into(gambarheaders);

        setelDrawerView(mViewPager);
        mNavigationView.setNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigationView.setItemTextColor(MenuUtama.this.getResources().getColorStateList(R.color.menu_teks_color_selector));

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(MenuUtama.this.getResources().getColorStateList(R.color.tab_teks_color_selector));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mDrawerLayout.setDrawerListener(mDrawerListener);

        //ambil data realms logins

        //cek permisission




    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuUtama.this.getMenuInflater().inflate(R.menu.menu_utama, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home :

                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_segarkan :


                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //SETEL NAVIGATION DRAWER
    private void setelDrawerView(ViewPager pagers) {

        mAdapterTabPager = new AdapterTabPager(MenuUtama.this.getSupportFragmentManager());
        mAdapterTabPager.addFragment(mFragmentListHarga, "CEK HARGA");
        mAdapterTabPager.addFragment(mFragmentPetaHarga, "PETA HARGA");

        pagers.setAdapter(mAdapterTabPager);
        pagers.addOnPageChangeListener(mOnPageChangeListener);

    }


    ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            //indikator tab dipilih di atas
            TabLayout.Tab tab = mTabLayout.getTabAt(position);
            if (tab != null) {
                tab.select();
            }

            switch (position) {

                case 0 :


                    break;

                case 1 :


                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



    //SETEL DRAWER VIEW
    NavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            return false;
        }
    };


    DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

            isDrawerBuka = true;
        }

        @Override
        public void onDrawerClosed(View drawerView) {

            isDrawerBuka = false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {

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
