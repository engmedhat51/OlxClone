package net.medhatblog.olxclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ViewAdDetails extends AppCompatActivity {


    private TextView title;
    private TextView price;
    private TextView description;
    private TextView name;
    private AppCompatButton delete;
    private LinearLayout layout;



    private DatabaseReference databaseReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ad_details);
        if(! Utility.isNetworkAvailable(this)){


            Toast.makeText(this,
                    "Please check internet connection", Toast.LENGTH_SHORT).show();

        }
        Intent intent = getIntent();

        final AdUploadInfo adUploadInfo = (AdUploadInfo) intent.getSerializableExtra("AdDetails");

        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference(adUploadInfo.getUserId()).child(adUploadInfo.getAdId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        title = (TextView) findViewById(R.id.title);
        price = (TextView) findViewById(R.id.price);
        description = (TextView) findViewById(R.id.description);
        name = (TextView) findViewById(R.id.name);
        layout = (LinearLayout)findViewById(R.id.layout);

        delete = (AppCompatButton) findViewById(R.id.delete);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        title.setText(adUploadInfo.adTitle);
        price.setText(adUploadInfo.adPrice);
        description.setText(adUploadInfo.adDescription);
        name.setText(adUploadInfo.adName);

        if(user.getUid().equals(adUploadInfo.getUserId())){
          delete.setVisibility(View.VISIBLE);
        }
            else{

            fab.setVisibility(View.VISIBLE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:"+adUploadInfo.adPhone));
                startActivity(dial);
            }
        });
        databaseReference.child("images").addListenerForSingleValueEvent(new ValueEventListener() {
            int i =0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                View v = layout.getChildAt(i);
                    i=i+1;
                if (v instanceof ImageView) {
                    Picasso.with(getApplicationContext())
                            .load(postSnapshot.getValue().toString())
                            .placeholder(R.drawable.image_processing)
                            .error(R.drawable.no_image)
                            .fit()
                            .centerInside()
                            .into((ImageView) v);
                    v.setVisibility(View.VISIBLE);
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! Utility.isNetworkAvailable(ViewAdDetails.this)){


                    Toast.makeText(ViewAdDetails.this,
                            "Please check internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(ViewAdDetails.this)
                        .setTitle(getResources().getString(R.string.delete))
                        .setMessage(getResources().getString(R.string.delete_confirmation))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                databaseReference.child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(postSnapshot.getValue().toString());
                            photoRef.delete();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                databaseReference.removeValue();
                                Toast.makeText(getApplicationContext(), "Ad may be take while until being deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        })      .setNegativeButton(android.R.string.no, null).show();
            }});

    }
    public void imageClick(View v){

        String IdAsString = v.getResources().getResourceName(v.getId());
        final String idNumber=IdAsString.substring(IdAsString.length()-1);

        databaseReference.child("images").addListenerForSingleValueEvent(new ValueEventListener() {
            int i =0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    i=i+1;
                    if (i== Integer.valueOf(idNumber)){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(postSnapshot.getValue().toString()), "image/*");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                        }
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
