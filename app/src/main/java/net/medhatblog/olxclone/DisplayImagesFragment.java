package net.medhatblog.olxclone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesFragment extends Fragment {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;




    // Creating List of ImageUploadInfo class.
    List<AdUploadInfo> list = new ArrayList<>();

    ValueEventListener listener;
    ValueEventListener listener2;
    private FirebaseUser user;
    public DisplayImagesFragment() {
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
        final LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);



            databaseReference = FirebaseDatabase.getInstance().getReference();





         listener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (user != null) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            if (postSnapshot.getKey().equals(user.getUid())) {
                                continue;
                            }
                            for (DataSnapshot child2 : postSnapshot.getChildren()) {

                                AdUploadInfo adUploadInfo = child2.getValue(AdUploadInfo.class);


                                for (DataSnapshot child3 : child2.child("images").getChildren()) {

                                    adUploadInfo.setImageUrl(child3.getValue().toString());
                                    adUploadInfo.setUserId(postSnapshot.getKey());
                                    adUploadInfo.setAdId(child2.getKey());
                                    if (list.size()==5){
                                        break;
                                    }

                                        list.add(adUploadInfo);

                                    break;
                                }

                            }

                        }

                        adapter = new RecyclerViewAdapter(getActivity(), list);

                        recyclerView.setAdapter(adapter);

                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {





                }
         };
        databaseReference.addListenerForSingleValueEvent(listener);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int queryOffset=5;
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount        = linearLayoutManager.getChildCount();
                int totalItemCount          = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition= linearLayoutManager.findFirstVisibleItemPosition();

                if (((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0))/*&&(firstVisibleItemPosition+1 % 5 == 0))*/ {

                    queryOffset = queryOffset + 5;
                    final int alreadyLoaded =queryOffset-5;


                    listener2=new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            int x=0;
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                                    if (postSnapshot.getKey().equals(user.getUid())) {
                                        continue;
                                    }
                                    for (DataSnapshot child2 : postSnapshot.getChildren()) {

                                        x=x+1;
                                        if (x<=alreadyLoaded){
                                            continue;
                                        }
                                        AdUploadInfo adUploadInfo = child2.getValue(AdUploadInfo.class);


                                        for (DataSnapshot child3 : child2.child("images").getChildren()) {

                                            adUploadInfo.setImageUrl(child3.getValue().toString());
                                            adUploadInfo.setUserId(postSnapshot.getKey());
                                            adUploadInfo.setAdId(child2.getKey());
                                            if (list.size()==queryOffset){
                                                break;
                                            }

                                            list.add(adUploadInfo);

                                            break;
                                        }

                                    }

                                }

                            recyclerView.getAdapter().notifyItemRangeInserted(list.size(),5);



                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {





                        }
                    };
                    databaseReference.addListenerForSingleValueEvent(listener2);
                }


            }

        });
           return view;    }

    @Override
    public void onPause() {
        super.onPause();
        if (listener!=null){
            databaseReference.removeEventListener(listener);
        }
        if (listener2!=null){
            databaseReference.removeEventListener(listener2);
        }
    }
}
