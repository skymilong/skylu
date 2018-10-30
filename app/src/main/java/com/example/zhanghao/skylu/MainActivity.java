package com.example.zhanghao.skylu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhanghao.skylu.activities.ProxyActivity;
import com.example.zhanghao.skylu.activities.VerificationActivity;
import com.example.zhanghao.skylu.fragment.LogFragment;
import com.example.zhanghao.skylu.fragment.OptionsFragment;
import com.example.zhanghao.skylu.fragment.RunListsFragment;
import com.example.zhanghao.skylu.httpTool.APICommonJM;
import com.example.zhanghao.skylu.httpTool.InstanceYZ;
import com.example.zhanghao.skylu.httpTool.SimpleHttp;
import com.example.zhanghao.skylu.httpTool.dz.GetMobilenumResp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener  {


    private TabLayout mTabTl;
    private ViewPager mContentVp;
    private EditText editText;

    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;
   // private List<GetMobilenumResp> source = new ArrayList<>();
   // Handler handler = RunListsFragment.handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editText = (EditText) findViewById(R.id.editText_Log);
        mTabTl = (TabLayout) findViewById(R.id.tab_content);
        mContentVp = (ViewPager) findViewById(R.id.view_page_content);

        initTab();
        initContent();




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    private void initTab(){
        ViewCompat.setElevation(mTabTl, 10);
        mTabTl.setupWithViewPager(mContentVp);
    }

    private void initContent(){
        tabIndicators = new ArrayList<>();
        tabIndicators.add("运行");
        tabIndicators.add("配置");
        tabIndicators.add("日志");
        tabFragments = new ArrayList<>();
        tabFragments.add(new RunListsFragment());
        tabFragments.add(new OptionsFragment());
        tabFragments.add(new LogFragment());
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setOffscreenPageLimit(2);
        mContentVp.setAdapter(contentAdapter);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(this,VerificationActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(this,ProxyActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }
    }

    private Map<String,String> getJMProp(String ps){
        Map<String,String> map = new HashMap<>();
        SharedPreferences jm = getSharedPreferences(ps, MODE_PRIVATE);
        String token = jm.getString(ps + ":token", "");
        String uname = jm.getString(ps + ":uname", "");
        String pwd = jm.getString(ps + ":pwd", "");
        Log.i("获取接码配置：","获取到信息："+token);
        map.put("token",token);
        map.put("uid",uname);
        map.put("pwd",pwd);

        return map;
    }





    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }
    }

}
