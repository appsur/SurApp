package com.pusheenicorn.safetyapp.activities;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.adapters.friends.FriendsAdapter;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends BaseActivity {
    Context mContext;
    User mUser;
    User mCurrentUser;
    //declared the navigation view
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    //declare adapter and associated variables for both safe and alert friends
    FriendsAdapter safeFriendsAdapter;
    ArrayList<Friend> safeFriends;
    RecyclerView rvSafeFriendList;
    FriendsAdapter alertFriendsAdapter;
    ArrayList<Friend> alertFriends;
    RecyclerView rvAlertFriendList;

    //declare views on the page
    EditText etUsername;
    ImageButton ibAddFriend;
    ImageButton ibSearch;

    /**
     * This function is executed upon creation. It calls various methods to
     * set up the UI and populate adapters.
     * @param- savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        // Set the context.
        mContext = this;
        setNavigationViews();
        initializeViews();
        populateList();
    }

    /**
     * This function sets the bottom navigation view.
     */
    public void setNavigationViews(){
        //initialize the bottom navigation view and the destinations
        bottomNavigationView = findViewById(R.id.bottom_navigation_friends);
        bottomNavigationView.setSelectedItemId(R.id.action_friends);
        setNavigationDestinations(FriendsActivity.this, bottomNavigationView);
        initializeNavItems(mNavItems);
        //initialize the drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position, mDrawerList, mDrawerPane, mDrawerLayout,
                        FriendsActivity.this, mNavItems);
            }
        });
    }

    /**
     * This function initializes the views needed in this activity.
     */
    public void initializeViews() {
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
        // initialize the views that were preset
        etUsername = (EditText) findViewById(R.id.etUsername);
        ibAddFriend = (ImageButton) findViewById(R.id.ibAddFriend);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        // set current user
        mCurrentUser = (User) ParseUser.getCurrentUser();
    }

    /**
     * This function populates the safe and alert recycler views depending on the user's settings
     */
    public void populateList() {
        //check to make sure that the current user isn't null
        if (mCurrentUser != null && mCurrentUser.getFriends() != null) {
            for (int i = 0; i < mCurrentUser.getFriendUsers().size(); i++) {
                Friend newFriend = null;
                try {
                    newFriend = (Friend) mCurrentUser.fetch().getList("friends").get(i);
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
                //if the user is safe, add them to the safe friends recycler view
                if (isSafe) {
                    safeFriends.add(newFriend);
                    //else add the user to the alert friends recycler view
                } else {
                    alertFriends.add(newFriend);
                }
            }
            safeFriendsAdapter.notifyDataSetChanged();
            alertFriendsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This function links to the settings page when the button is clicked.
     * @param view- the settings button.
     */
    public void onSettings(View view) {
        //create intent to access the map activity and then toast the information
        Intent intent = new Intent(FriendsActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * This function makes visible the functionality to add a friend;
     * @param view- the add button
     */
    public void onAddFriend(View view) {
        ibAddFriend.setVisibility(View.INVISIBLE);
        ibSearch.setVisibility(View.VISIBLE);
        etUsername.setVisibility(View.VISIBLE);
    }

    /**
     * This function searches for a user and adds them if he/she exists.
     * @param view- the search button
     */
    public void onSearch(View view) {
        String username = etUsername.getText().toString();
        // Do not allow the user to add themselves as a friend!
        if (username == mCurrentUser.getUsername()) {
            Toast.makeText(this, "Sorry, you cannot add yourself as a friend!",
                    Toast.LENGTH_LONG).show();
            ibAddFriend.setVisibility(View.VISIBLE);
            ibSearch.setVisibility(View.INVISIBLE);
            etUsername.setVisibility(View.INVISIBLE);
            return;
        }
        if (mCurrentUser.getFriendUserNames() != null && mCurrentUser.getFriendUserNames()
                .contains(username)) {
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
                        mUser = objects.get(0);
                    }
                    // Otherwise, tell the userf to try again.
                    else {
                        Toast.makeText(mContext, "Sorry, that user doesn't exist",
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
                                newFriend.setUser(mUser);
                                newFriend.setName(mUser.getName());
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

}