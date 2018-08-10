package com.pusheenicorn.safetyapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.R;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    /**
     * @param activity             the activity that the bottom navigation view is being added to
     * @param bottomNavigationView the bottom navigation view that has been initialized in the given activity
     */
    public void setNavigationDestinations(final Activity activity, final BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            //home activity
                            case R.id.action_person:
                                Intent goHome = new Intent(activity, MainActivity.class);
                                startActivity(goHome);
                                return true;
                            //contact activity
                            case R.id.action_message:
                                Intent contactAction = new Intent(activity,
                                        ContactActivity.class);
                                startActivity(contactAction);
                                return true;
                            //emergency call activity
                            case R.id.action_emergency:
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                        "811", null)));
                                return true;
                            //friends activity
                            case R.id.action_friends:
                                Intent friendsAction = new Intent(activity,
                                        FriendsActivity.class);
                                startActivity(friendsAction);
                                return true;
                        }
                        bottomNavigationView.setSelectedItemId(item.getItemId());
                        return true;
                    }
                });
    }

    /**
     * initialize the items that will be used in the side navigation menu
     *
     * @param mNavItems arraylist of navigation items that will be edited
     */
    public void initializeNavItems(ArrayList<NavItem> mNavItems) {
        mNavItems.add(new NavItem("Home", "Main Screen", R.drawable.ic_vector_home));
        mNavItems.add(new NavItem("Contact", "Contact your friends", R.drawable.ic_vector_messages));
        mNavItems.add(new NavItem("Friends", "See your friends", R.drawable.ic_vector_person));
        mNavItems.add(new NavItem("Hotlines", "Easy access to a list of hotlines that you can call", R.drawable.phoneicon));
        mNavItems.add(new NavItem("Log out", "Log out", R.drawable.logout));
    }

    /**
     * @param position      the position of the selected item
     * @param mDrawerList   list view of the current items
     * @param mDrawerPane   relative layout
     * @param mDrawerLayout drawer layout item
     * @param activity      the activity that the navigation menu is located in
     * @param mNavItems     navigation items
     */
    public void selectItemFromDrawer(int position, ListView mDrawerList, RelativeLayout mDrawerPane,
                                     DrawerLayout mDrawerLayout, Activity activity,
                                     ArrayList<NavItem> mNavItems) {
        // Locate Position
        switch (position) {
            case 0:
                startActivity(new Intent(activity, MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(activity, ContactActivity.class));
                break;
            case 2:
                startActivity(new Intent(activity, FriendsActivity.class));
                break;
            case 3:
                startActivity(new Intent(activity, HotlineActivity.class));
                break;
            case 4:
                Intent logOut = new Intent(activity, HomeActivity.class);
                ParseUser.logOut();
                startActivity(logOut);
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    /**
     * @param item item that has been selected
     * @return true after the item has been set checked
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        return true;
    }

    /**
     * create a NavItem object that has a title, subtitle, and icon
     */
    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    /**
     * adapter that inflates the drawer list
     */
    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<MainActivity.NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<MainActivity.NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

        @Override
        public int getCount() {
            return mNavItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.drawer_item, null);
            } else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText(mNavItems.get(position).mTitle);
            subtitleView.setText(mNavItems.get(position).mSubtitle);
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

}
