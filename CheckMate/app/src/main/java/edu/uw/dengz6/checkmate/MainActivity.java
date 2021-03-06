package edu.uw.dengz6.checkmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAINACTIVITY";
    // Fragment
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SessionManager session;

    private String groupName;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shopping:

                    // Change to "Shopping Fragment"
                    ShoppingFragment shoppingFragment = ShoppingFragment.newInstance();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.container, shoppingFragment, "Shopping_Fragment");
                    ft.commit();
                    return true;

                case R.id.navigation_tasks:

                    // Change to "Tasks Fragment"
                    TasksFragment tasksFragment = TasksFragment.newInstance();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.container, tasksFragment, "Tasks_Fragment");
                    ft.commit();
                    return true;

                case R.id.navigation_announcement:

                    // Change to "Announcement Fragment"
                    AnnouncementFragment announcementFragment = AnnouncementFragment.newInstance();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.container, announcementFragment, "Shopping_Fragment");
                    ft.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Get current group name
        final SessionManager sessionManager = new SessionManager(this);
        groupName = sessionManager.getUserDetails().get(SessionManager.KEY_GROUP_NAME);

        // Firebase cloud message
        // Subscribe to this group's notification
        FirebaseMessaging.getInstance().subscribeToTopic("group_" + groupName);

        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());

        session.checkLogin();
        if(!session.isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            String name = user.get(SessionManager.KEY_NAME);

            // email
            String email = user.get(SessionManager.KEY_EMAIL);
            Log.v(TAG, name);
            Log.v(TAG, email);
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initialize FragmentManager
        fm = getSupportFragmentManager();

        // Retrieve data from intent sent by notification
        String menuFragment = getIntent().getStringExtra(MyFirebaseMessagingService.MENU_FRAGMENT_KEY);

        // If menuFragment is defined, then this activity should be launched with a selected fragment
        if(menuFragment != null) {

            if(menuFragment.equalsIgnoreCase("shopping")) {

                ShoppingFragment shoppingFragment = ShoppingFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, shoppingFragment, "Shopping_Fragment");
                ft.commit();
                navigation.getMenu().getItem(0).setChecked(true);

            } else if(menuFragment.equalsIgnoreCase("announcement")) {

                AnnouncementFragment announcementFragment = AnnouncementFragment.newInstance();
                ft = fm.beginTransaction();
                ft.replace(R.id.container, announcementFragment, "Announcement_Fragment");
                ft.commit();
                navigation.getMenu().getItem(2).setChecked(true);
            }

        } else {

            // If not, set "Tasks Fragment" as default
            TasksFragment tasksFragment = TasksFragment.newInstance();
            ft = fm.beginTransaction();
            ft.replace(R.id.container, tasksFragment, "Tasks_Fragment");
            ft.commit();
            navigation.getMenu().getItem(1).setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        return true; //we've provided a menu!
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:

                // Unsubscribe to this group's notification
                FirebaseMessaging.getInstance().unsubscribeFromTopic("group_" + groupName);

                session.logoutUser();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);

                return true; //handled

            case R.id.action_add_member:
                String groupName = session.getUserDetails().get(session.KEY_GROUP_NAME);
                Intent addNewMemberIntent = new Intent(getApplicationContext(), AddMembersActivity.class);
                addNewMemberIntent.putExtra("group_id", groupName);
                startActivityForResult(addNewMemberIntent, 0);
                return true;

            case R.id.action_settings:

                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
