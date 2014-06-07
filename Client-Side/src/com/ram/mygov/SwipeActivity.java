package com.ram.mygov;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SwipeActivity extends ActionBarActivity implements ActionBar.TabListener {

    // UI Elements
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ActionBar actionBar;
    private Menu mMenu;

    // Fragments
    public PollsFragment pollsFragment;
    public PostsFragment postsFragment;
    public EventsFragment eventsFragment;

    private int position;

    public String userID;
    public int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get User ID
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString(PHPScriptVariables.userIDString);
        userType = bundle.getInt(PHPScriptVariables.userTypeString);

        setContentView(R.layout.activity_swipe);

        // Create Fragments
        pollsFragment = new PollsFragment(userID, userType);
        eventsFragment = new EventsFragment(userID, userType);
        postsFragment = new PostsFragment(userID, userType);

        // Set up Action Bar
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create Adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            Tab tab = actionBar.newTab();
            tab.setTabListener(this);
            tab.setText(mSectionsPagerAdapter.getPageTitle(i));
            actionBar.addTab(tab);
            if (i == 1) {
                mViewPager.setCurrentItem(tab.getPosition());
                position = i;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        mMenu = menu;

        getMenuInflater().inflate(R.menu.main_activity_actions,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public Menu getMenu() {
        return mMenu;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        position = tab.getPosition();
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public void logout() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Logout");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                SwipeActivity.this.logout();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return pollsFragment;
            else if (position == 1)
                return postsFragment;
            else if (position == 2)
                return eventsFragment;
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Polls";
                case 1:
                    return "Posts";
                case 2:
                    return "Events";
            }
            return null;
        }
    }

}
