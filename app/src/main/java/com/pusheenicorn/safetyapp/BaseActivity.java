package com.pusheenicorn.safetyapp;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
    public void setNavigationDestinations (final Activity activity, final BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_person:
                                Intent goHome = new Intent(activity, MainActivity.class);
                                startActivity(goHome);
                                return true;
                            case R.id.action_message:
                                Intent contactAction = new Intent(activity,
                                        ContactActivity.class);
                                startActivity(contactAction);
                                return true;
                            case R.id.action_emergency:
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                        "6304862146", null)));
                                return true;
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


    public void initializeNavItems(ArrayList<NavItem> mNavItems) {
        mNavItems.add(new NavItem("Home", "Main Screen", R.drawable.ic_vector_home));
        mNavItems.add(new NavItem("Messages", "Contact your friends", R.drawable.ic_vector_messages));
//        mNavItems.add(new NavItem("Chat", "Chat with your friends here", R.drawable.ic_vector_compose));
        mNavItems.add(new NavItem("Friends", "See your friends", R.drawable.ic_vector_person));
        mNavItems.add(new NavItem("Hotlines", "Easy access to a list of hotlines that you can call", R.drawable.phoneicon));
        mNavItems.add(new NavItem("Log out", "Log out", R.drawable.logout));
    }

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
//            case 2:
//                startActivity(new Intent(activity, ChatActivity.class));
//                break;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        return true;
    }

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
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
            ImageView iconView = (ImageView) view.findViewById(R.id.icon);

            titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

}
