package net.medhatblog.olxclone;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavigationActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle toggle;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private boolean mIsResumed = false;
    private int selectedposition= 0;
    private static final int REQUEST_CODE_AD = 0;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();

            }
        };
        firebaseAuth.addAuthStateListener(mAuthListener);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // connect to google api
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        mGoogleApiClient.connect();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        toggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
//        MenuItem menuIem= nvDrawer.getMenu().getItem(0);
//        selectDrawerItem(menuIem);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        FragmentManager fm;
        Fragment fragment;
        switch(menuItem.getItemId()) {

            case R.id.nav_home_fragment:

                selectedposition= 0;

                fragment = new DisplayImagesFragment();

                fm = getSupportFragmentManager();

                fm.beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .commit();

                break;
            case R.id.nav_my_ads_fragment:
                selectedposition= 1;



                if (user!=null) {
                     valueEventListener=new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if ((dataSnapshot.hasChild(user.getUid())) && (mIsResumed)) {
                                Fragment fragment2 = new MyAdsFragment();

                                FragmentManager fm2 = getSupportFragmentManager();


                                fm2.beginTransaction()
                                        .replace(R.id.flContent, fragment2)
                                        .commit();

                            } else if (mIsResumed) {

                                Fragment fragment3 = new NoAdFragment();

                                FragmentManager fm3 = getSupportFragmentManager();

                                fm3.beginTransaction()
                                        .replace(R.id.flContent, fragment3)
                                        .commit();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    databaseReference.addValueEventListener(valueEventListener);

                }

                break;


            case R.id.nav_favorites_fragment:
                selectedposition= 2;
                break;
            case R.id.nav_sell_your_item_fragment:

                startActivityForResult(new Intent(NavigationActivity.this, SellYourItemActivity.class),REQUEST_CODE_AD);
                break;
            case R.id.signout:

                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                FirebaseAuth.getInstance().signOut();
                finish();



                break;
            default:
        }
    // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
    // Set action bar title
    setTitle(menuItem.getTitle());
    // Close the navigation drawer
        mDrawer.closeDrawers();
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
        }else {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


        finish();
    }
    }
    @Override
    public void onResume() {
        super.onResume();
        mIsResumed = true;
            MenuItem menuIem = nvDrawer.getMenu().getItem(selectedposition);
            selectDrawerItem(menuIem);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
        if (valueEventListener!=null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Toast.makeText(getApplicationContext(),"Ad may be take while until being visible",Toast.LENGTH_LONG).show();
    }
}
