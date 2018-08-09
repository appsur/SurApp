package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * This class holds the activity for hotlines.
 */
public class HotlineActivity extends BaseActivity {

    // Declare constants for the phone numbers for hotlines
    private static final String SUICIDE_NUMBER = "18008277571";
    private static final String DOMESTIC_NUMBER = "18007997233";
    private static final String SPANISH_NUMBER = "18009426908";
    private static final String BWC_NUMBER = "18006034357";
    private static final String ELDER_NUMBER = "18002528966";
    private static final String DRUG_NUMBER = "18006624357";
    private static final String ALCOHOL_NUMBER = "18002526465";
    private static final String SOCIAL_SERVICES_NUMBER = "18003423720";
    private static final String SEXUAL_ABUSE_NUMBER = "18006564673";
    private static final String TELEPHONE_KEY = "tel";
    // Declare bottom navigation bar
    private BottomNavigationView bottomNavigationView;
    // Variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    /**
     * On creation, the buttons are found, context is set, and listeners are set.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline);
        //set and populated the bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation_hotline);
        setNavigationDestinations(HotlineActivity.this, bottomNavigationView);

        initializeNavItems(mNavItems);
        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position, mDrawerList, mDrawerPane, mDrawerLayout,
                        HotlineActivity.this, mNavItems);
            }
        });

    }

    /*
     * The following functions set hardcoded string phone numbers to dial when the appropriate
     * hotline is clicked.
     */


    public void onSettings(View view) {
        Intent i = new Intent(HotlineActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void onSuicidePrevention(View view) {
        // Call: 1-800-827-7571
        dialContactPhone(SUICIDE_NUMBER);
    }

    public void onDomesticAbuse(View view) {
        // Call: 1-800-799-SAFE
        dialContactPhone(DOMESTIC_NUMBER);
    }

    public void onDomesticAbuseSpanish(View view) {
        // Call: 1-800-942-6908
        dialContactPhone(SPANISH_NUMBER);
    }

    public void onBatteredWomenAndChildren(View view) {
        // Call: 1-800-603-HELP
        dialContactPhone(BWC_NUMBER);
    }

    public void onElderAbuse(View view) {
        // Call: 1-800-252-8966
        dialContactPhone(ELDER_NUMBER);
    }

    public void onDrugs(View view) {
        // Call 1-800-662-4357
        dialContactPhone(DRUG_NUMBER);
    }

    public void onSS(View view) {
        // Call: 1-800-342-3720
        dialContactPhone(SOCIAL_SERVICES_NUMBER);
    }

    public void onAlcoholics(View view) {
        // Call: 1-800-252-6465
        dialContactPhone(ALCOHOL_NUMBER);
    }

    public void onSexualAssault (View view) {
        // Call: 1-800-252-6465
        dialContactPhone(SEXUAL_ABUSE_NUMBER);
    }

    //method for dialing the contact number provided
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts(TELEPHONE_KEY, phoneNumber,
                null)));
    }
}
