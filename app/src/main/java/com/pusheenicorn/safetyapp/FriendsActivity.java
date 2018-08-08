package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.adapters.friends.FriendsAdapter;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends BaseActivity {
    //declared variables
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();

    ProgressBar progressBar;

    FriendsAdapter safeFriendsAdapter;
    ArrayList<Friend> safeFriends;
    //    ArrayList<Friend> friends;
    RecyclerView rvSafeFriendList;

    FriendsAdapter alertFriendsAdapter;
    ArrayList<Friend> alertFriends;
    RecyclerView rvAlertFriendList;

    EditText etUsername;
    ImageButton ibAddFriend;
    ImageButton ibSearch;
    Context context;
    User user;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Set the context.
        context = this;
        //setting the bottom navigation view
        //set and populated the bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation_friends);
        bottomNavigationView.setSelectedItemId(R.id.action_friends);
        setNavigationDestinations(FriendsActivity.this, bottomNavigationView);
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
                        FriendsActivity.this, mNavItems);
            }
        });

        rvSafeFriendList = (RecyclerView) findViewById(R.id.rvSafe);
        safeFriends = new ArrayList<Friend>();
        // construct the adapter from this data source
        safeFriendsAdapter = new FriendsAdapter(safeFriends);
        // recycler view setup
        rvSafeFriendList.setLayoutManager(new LinearLayoutManager(this));
        rvSafeFriendList.setAdapter(safeFriendsAdapter);


        rvAlertFriendList = (RecyclerView) findViewById(R.id.rvFriendList);
        alertFriends = new ArrayList<>();
        alertFriendsAdapter = new FriendsAdapter(alertFriends);
        // recycler view setup
        rvAlertFriendList.setLayoutManager(new LinearLayoutManager(this));
        rvAlertFriendList.setAdapter(alertFriendsAdapter);

        // declare username
        etUsername = (EditText) findViewById(R.id.etUsername);
        ibAddFriend = (ImageButton) findViewById(R.id.ibAddFriend);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);

        currentUser = (User) ParseUser.getCurrentUser();
        populateList();
    }

    public void populateList() {

        if (currentUser != null && currentUser.getFriends() != null)
        {
            for (int i = 0; i < currentUser.getFriendUsers().size(); i++) {
                Friend newFriend = null;
                try {
                    newFriend = (Friend) currentUser.fetch().getList("friends").get(i);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                User userFriend;
                Boolean isSafe = true;
                try {
                    userFriend = (User) newFriend.fetchIfNeeded().getParseUser("user");
                    isSafe = userFriend.fetchIfNeeded().getBoolean("safe");
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                if (isSafe) {
                    safeFriends.add(newFriend);
                    safeFriendsAdapter.notifyDataSetChanged();
                } else {
                    alertFriends.add(newFriend);
                    alertFriendsAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    public void onSettings(View view) {
        //create intent to access the map activity and then toast the information
        Intent intent = new Intent(FriendsActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onAddFriend(View view) {
        ibAddFriend.setVisibility(View.INVISIBLE);
        ibSearch.setVisibility(View.VISIBLE);
        etUsername.setVisibility(View.VISIBLE);
    }

    public void onSearch(View view) {
        String username = etUsername.getText().toString();

        // Do not allow the user to add themselves as a friend!
        if (username == currentUser.getUsername()) {
            Toast.makeText(this, "Sorry, you cannot add yourself as a friend!",
                    Toast.LENGTH_LONG).show();
            ibAddFriend.setVisibility(View.VISIBLE);
            ibSearch.setVisibility(View.INVISIBLE);
            etUsername.setVisibility(View.INVISIBLE);
            return;
        }

        if (currentUser.getFriendUserNames() != null && currentUser.getFriendUserNames().contains(username)) {
            Toast.makeText(this, "Sorry, this user is already your friend!",
                    Toast.LENGTH_LONG).show();
            ibAddFriend.setVisibility(View.VISIBLE);
            ibSearch.setVisibility(View.INVISIBLE);
            etUsername.setVisibility(View.INVISIBLE);
            return;
        }

        // Get the actual checkin object by making a query.
        final User.Query userQuery = new User.Query();
        userQuery.getTop().whereEqualTo("username", username);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, com.parse.ParseException e) {
                if (e == null) {
                    // Get the user that matches this name, if one exists.
                    if (objects != null && objects.size() > 0) {
                        user = objects.get(0);
                    }

                    // Otherwise, tell the userf to try again.
                    else {
                        Toast.makeText(context, "Sorry, that user doesn't exist",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Create a new friend
                    final Friend newFriend = new Friend();
                    newFriend.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                // Set the name and user according to the User's input and
                                // save in background.
                                newFriend.setUser(user);
                                newFriend.setName(user.getName());
                                newFriend.saveInBackground();
                                // Repopulate the friends list.
                                safeFriendsAdapter.clear();
                                alertFriendsAdapter.clear();
                                populateList();
                                // Hide/show views
                                ibAddFriend.setVisibility(View.VISIBLE);
                                ibSearch.setVisibility(View.INVISIBLE);
                                etUsername.setVisibility(View.INVISIBLE);
                                etUsername.setText("");
                                // Update the current user's friends list
                                final User currentUser = (User) ParseUser.getCurrentUser();
                                currentUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        // Add the new friend to the user's list of friends on Parse
                                        currentUser.addFriend(newFriend);
                                        currentUser.saveInBackground();
                                        alertFriendsAdapter.clear();
                                        safeFriendsAdapter.clear();
                                        populateList();
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        bottomNavigationView.setSelectedItemId(R.id.action_friends);
//    }

}