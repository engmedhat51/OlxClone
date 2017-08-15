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
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
// TODO connect the app to firebase
    private GoogleApiClient mGoogleApiClient;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle toggle;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private boolean mIsResumed = false;
    private int selectedId= 0;
    public static final int REQUEST_CODE_AD = 0;
    private ProgressBar mProgressBar;
    ValueEventListener valueEventListener;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    TextView welcome;
    LinearLayout internetCheckLayout;
    private AppCompatButton reload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        welcome= (TextView) nvDrawer.getHeaderView(0).findViewById(R.id.welcome);
        internetCheckLayout = (LinearLayout)findViewById(R.id.internet_check);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        reload = (AppCompatButton) findViewById(R.id.reload);
         firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();
                if (user!=null){
                welcome.setText(getResources().getText(R.string.welcome)+ " " + user.getEmail());

                    if(! Utility.isNetworkAvailable(NavigationActivity.this)){

                        mProgressBar.setVisibility(View.INVISIBLE);
                        internetCheckLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(NavigationActivity.this,
                                "Please check internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fragment = new DisplayImagesFragment();

                    if( mIsResumed)
                            {
                                fm.beginTransaction()
                            .replace(R.id.flContent,  fragment)
                            .commit();

                            }
                }

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

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuItem menuIem = nvDrawer.getMenu().getItem(selectedId);
                selectDrawerItem(menuIem);
            }
        });


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



        switch(menuItem.getItemId()) {

            case R.id.nav_home_fragment:
                selectedId=0;
                if (getSupportFragmentManager().findFragmentById(R.id.flContent)!=null) {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.flContent)).commit();
                }
                if(! Utility.isNetworkAvailable(this)){

                    mProgressBar.setVisibility(View.INVISIBLE);
                    internetCheckLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(this,
                            "Please check internet connection", Toast.LENGTH_SHORT).show();
                    break;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                internetCheckLayout.setVisibility(View.INVISIBLE);

                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = new DisplayImagesFragment();


                fm.beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .commit();

                break;
            case R.id.nav_my_ads_fragment:
                selectedId=1;
                if (getSupportFragmentManager().findFragmentById(R.id.flContent)!=null) {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.flContent)).commit();
                }
                if(! Utility.isNetworkAvailable(this)){

                    mProgressBar.setVisibility(View.INVISIBLE);
                    internetCheckLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(this,
                            "Please check internet connection", Toast.LENGTH_SHORT).show();
                    break;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                internetCheckLayout.setVisibility(View.INVISIBLE);


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
                                mProgressBar.setVisibility(View.GONE);
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
        if(! Utility.isNetworkAvailable(this))
        {
        mProgressBar.setVisibility(View.VISIBLE);
        }
//            MenuItem menuIem = nvDrawer.getMenu().getItem(selectedposition);
//            selectDrawerItem(menuIem);
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
            MenuItem menuIem = nvDrawer.getMenu().getItem(selectedId);
            selectDrawerItem(menuIem);
            return;
        }
            MenuItem menuIem = nvDrawer.getMenu().getItem(1);
            selectDrawerItem(menuIem);
            Toast.makeText(getApplicationContext(), "Ad may be take while until being visible", Toast.LENGTH_LONG).show();
    }
}
