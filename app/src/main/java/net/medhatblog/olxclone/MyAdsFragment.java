package net.medhatblog.olxclone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAdsFragment extends Fragment {


    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog


    // Creating List of ImageUploadInfo class.
    List<AdUploadInfo> list = new ArrayList<>();
    ProgressDialog progressDialog;
    private FirebaseUser user;


    public MyAdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.activity_display_images, container, false);


        user = FirebaseAuth.getInstance().getCurrentUser();

        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Assign activity this to progress dialog.

        progressDialog = new ProgressDialog(getActivity());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();


        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                    for(DataSnapshot child2:snapshot.getChildren()){

                        AdUploadInfo adUploadInfo= child2.getValue(AdUploadInfo.class);


                        for(DataSnapshot child3:child2.child("images").getChildren()) {

                            adUploadInfo.setImageUrl(child3.getValue().toString());


                            list.add(adUploadInfo);

                            break;
                        }



                }

                adapter = new RecyclerViewAdapter(getActivity(), list);

                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.

                progressDialog.dismiss();

            }
        });



        return view;    }

}
