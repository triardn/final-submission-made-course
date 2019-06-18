package com.buruhkoding.secondsubmission;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public String choosenMenu = "now-playing";
    Fragment fragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_nowplaying:
                    choosenMenu = "now-playing";
                    fragment = new NowPlayingFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_upcoming:
                    choosenMenu = "upcoming";
                    fragment = new UpcomingFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_favorite:
                    choosenMenu = "favorite";
                    fragment = new FavoriteFragment();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // default value
            fragment = new NowPlayingFragment();

            loadFragment(fragment);
        } else {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "fragment");

            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(fragment.getTag());
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_settings:
                Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(mIntent);
                break;

            case R.id.action_alarm_settings:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                break;

            case R.id.action_search_menu:
                fragment = new SearchFragment();
                loadFragment(fragment);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState,"fragment", fragment);
    }

    public void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        fragment = getSupportFragmentManager().getFragment(inState,"fragment");
    }
}
