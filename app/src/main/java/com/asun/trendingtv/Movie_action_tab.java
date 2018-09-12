package com.asun.trendingtv;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class Movie_action_tab extends Fragment {

    private static final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
    private static final DatabaseReference mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
    private static final DatabaseReference mDatabaseBookMark=FirebaseDatabase.getInstance().getReference().child("movies_bookmark");

    private ProgressBar progressBar;
    private Boolean mProcessLike=false;
    private Boolean mProcessBookmark=false;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public Movie_action_tab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all__tab, container, false);
        AppBarLayout appBarLayout=(AppBarLayout)view.findViewById(R.id.appbarall);
        appBarLayout.setVisibility(View.GONE);





        progressBar=(ProgressBar)view.findViewById(R.id.progress_id);
        mDatabaseReference.keepSynced(true);
        mDatabaseLike.keepSynced(true);


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.all_tab_recyclerview);
        Movies_All_Tab.MLinearLayoutManager mLayoutManager = new Movies_All_Tab.MLinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(null);

        Query query=mDatabaseReference.orderByChild("category1").equalTo("action");

        FirebaseRecyclerAdapter<Tvshow, Movies_All_Tab.Tvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, Movies_All_Tab.Tvshowholder>(
                Tvshow.class,
                R.layout.tvshowcard,
                Movies_All_Tab.Tvshowholder.class,
                query


        ) {
            @Override
            public void populateViewHolder(final Movies_All_Tab.Tvshowholder viewHolder, Tvshow model, int position) {

                final String moviekey = getRef(position).getKey();
                YoYo.with(Techniques.FadeInRight).playOn(viewHolder.view);
                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setLikeButton(moviekey);
                viewHolder.setBookmarkBtn(moviekey);
                viewHolder.setIndicator(moviekey);
                mDatabaseReference.child(moviekey).keepSynced(true);
                mDatabaseReference.keepSynced(true);

                progressBar.setVisibility(View.GONE);
                viewHolder.view.setOnClickListener(v -> {

                    Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                    i.putExtra("movie_id", moviekey);
                    startActivity(i);





                });
                viewHolder.mLikeButton.setOnClickListener(v -> {
                    YoYo.with(Techniques.ZoomInUp).playOn(viewHolder.mLikeButton);

                    mProcessLike=true;
                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(mProcessLike) {
                                    if (dataSnapshot.child(moviekey).hasChild(mAuth.getCurrentUser().getUid())) {


                                        mDatabaseLike.child(moviekey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;

                                    } else {
                                        Toast.makeText(getContext(),"Liked",Toast.LENGTH_SHORT).show();
                                        mDatabaseLike.child(moviekey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
                                        mProcessLike = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                });
                viewHolder.mBookmarkBtn.setOnClickListener(v -> {
                    YoYo.with(Techniques.ZoomInUp).playOn(viewHolder.mBookmarkBtn);

                    mProcessBookmark=true;
                        mDatabaseBookMark.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProcessBookmark){
                                    if(dataSnapshot.child(moviekey).hasChild(mAuth.getCurrentUser().getUid())){
                                        mDatabaseBookMark.child(moviekey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        Toast.makeText(getContext(),"Removed from favorites!!",Toast.LENGTH_SHORT).show();
                                        mProcessBookmark=false;
                                    }
                                    else {
                                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String title=  dataSnapshot.child(moviekey).child("Title").getValue().toString();
                                                String desc=dataSnapshot.child(moviekey).child("Description").getValue().toString();
                                                String image=dataSnapshot.child(moviekey).child("Image").getValue().toString();
                                                mDatabaseBookMark.child(moviekey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
                                                mDatabaseBookMark.child(moviekey).child("info").child("Title").setValue(title);
                                                mDatabaseBookMark.child(moviekey).child("info").child("Description").setValue(desc);
                                                mDatabaseBookMark.child(moviekey).child("info").child("Image").setValue(image);
                                                Toast.makeText(getContext(),"Added to favorites!!",Toast.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        mProcessBookmark=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                });

            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        return view;
    }






}
