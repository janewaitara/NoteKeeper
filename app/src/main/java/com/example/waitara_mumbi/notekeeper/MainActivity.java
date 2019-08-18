package com.example.waitara_mumbi.notekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mNotesLayoutManager;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private GridLayoutManager mCoursesGridLayoutManager;

    private NoteKeeperOpenHelper mDbOpenHelper;//member field of the openHelper //reference to the DBHELPER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instance of the DBHELPER
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });


        Log.d("Preference","it does not reach");
        //Initializing the preference system with the default values
        PreferenceManager.setDefaultValues(this, R.xml.general_preferences, false);//the preference already has a value dont force
        PreferenceManager.setDefaultValues(this, R.xml.messages_preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.sync_preferences, false);
        Log.d("Preferences do","it reaches");



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();//helps the actionBarDrawer to know when the Navigation Drawer is open or closed
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNoteRecyclerAdapter.notifyDataSetChanged();

        //updating the values in the nav header
        updateNavHeader();
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();//gets destroyed when activity is destroyed
        super.onDestroy();
    }

    private void updateNavHeader() {

        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);//reference tyo the navigation view
        View headerNav = navigationView.getHeaderView(0);//getting the reference to the header

        TextView textUserName = (TextView) headerNav.findViewById(R.id.text_user_name);
        TextView textEmailAddress = (TextView) headerNav.findViewById(R.id.text_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name","");
        String emailAddress = pref.getString("user_email_address","");

        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);

    }

    private void initializeDisplayContent() {
        //getting the reference to the Recycler View
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_items);
        //instance of layout Manager needed by recycler View
        mNotesLayoutManager = new LinearLayoutManager(this);
      //mRecyclerItems.setLayoutManager(mNotesLayoutManager); //associating both
        mCoursesGridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        //instance of our noteRecyclerAdapter
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(this, courses);

        displayNotes();
    }

    private void displayNotes() {
        mRecyclerItems.setLayoutManager(mNotesLayoutManager); //associating both
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);

        //connecting to the database to create it
        SQLiteDatabase db =  mDbOpenHelper.getReadableDatabase(); //connecting to our database

        selectNavigationMenuItem(R.id.nav_notes);
    }

    //helps to show the selected menu item on the navigation
    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//reference to the navigation
        Menu menu = navigationView.getMenu();//getting the menu in the navigation
        menu.findItem(id).setChecked(true); //shows that the menu item notes is selected when the app opens
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) { //GravityCompat.START identifies the navigation Drawer on the start edge in case there is more than one ND
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent intent =  new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNotes();
            // Handle the camera action
        } else if (id == R.id.nav_courses) {
            displayCourses();
        }  else if (id == R.id.nav_share) {
            //handleSelection(R.string.nav_share_message);
            handleShare();
        } else if (id == R.id.nav_send) {
            handleSelection(R.string.nav_send_message);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void  displayCourses(){
        mRecyclerItems.setLayoutManager(mCoursesGridLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_courses);
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "share to - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social",""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message_id,Snackbar.LENGTH_LONG).show();
    }
}
