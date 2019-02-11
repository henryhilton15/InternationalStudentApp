package com.henryhilton.internationalstudentapp;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.TabLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);

    }

    // sets up the view pager and adds the various fragments
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SuppliesFragment(), "Supplies");
        adapter.addFragment(new BuddiesFragment(), "Buddies");
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(adapter);
    }
}