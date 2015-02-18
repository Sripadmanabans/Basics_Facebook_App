package com.example.sripadmanaban.basics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallback{

    private int LOCATION = 0;

    private static final String FRAGMENT_TO_OPEN = "FragmentPosition";

    private Fragment fragment;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigationDrawer();

        if(savedInstanceState == null) {
            // Adding the Login details fragment on starting
            openFragmentByPosition(LOCATION);
        } else {
            // Restoring the fragment that we need
            LOCATION = savedInstanceState.getInt(FRAGMENT_TO_OPEN);
            openFragmentByPosition(LOCATION);
        }
    }

    private void setUpNavigationDrawer()
    {
        NavigationDrawerFragment mNavigationDrawerFragment =
                (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer_fragment, drawerLayout, toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void openFragmentByPosition(int position)
    {
        LOCATION = position;
        Fragment fragment;
        switch (position)
        {
            case 0:
                toolbar.setTitle(R.string.loginDetail_title);
                fragment = new LoginDetailsFragment();
                openFragment(fragment, "HomeFrag");
                break;

            case 1:
                toolbar.setTitle(R.string.loginBatchRequest_title);
                fragment = new LoginBatchRequestFragment();
                openFragment(fragment, "DisplayFrag");
                break;

        }
    }

    private void openFragment(Fragment frag, String tag)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment == null)
        {
            transaction.replace(R.id.container, frag, tag);
        }
        transaction.commit();
    }

    @Override
    public void NavigationDrawerSelection(int position) {
        LOCATION = position;
        openFragmentByPosition(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FRAGMENT_TO_OPEN, LOCATION);
    }
}
