package com.asun.trendingtv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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


public class TV_animation extends Fragment {


    RecyclerView recyclerView;
    TV_Action_Tab.MLinearLayoutManager mLayoutManager;
    public DatabaseReference mDatabaseReference;
    public DatabaseReference mDatabaseLike;
    public DatabaseReference mDatabaseBookMark;
    ProgressBar progressBar;
    private Boolean mProcessLike=false;
    private Boolean mProcessBookmark=false;
    FirebaseAuth mAuth;




    public TV_animation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_all__tab, container, false);



        recyclerView=(RecyclerView)view.findViewById(R.id.all_tab_recyclerview);
        mLayoutManager=new TV_Action_Tab.MLinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);

        recyclerView.setLayoutManager(mLayoutManager);
        AppBarLayout appBarLayout=(AppBarLayout)view.findViewById(R.id.appbarall);
        appBarLayout.setVisibility(View.GONE);
        progressBar=(ProgressBar)view.findViewById(R.id.progress_id);
        mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("tvshow");
        mDatabaseBookMark=FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark");
        mDatabaseReference.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mAuth=FirebaseAuth.getInstance();

        Query query=mDatabaseReference.orderByChild("category11").equalTo("animation");

        FirebaseRecyclerAdapter<Tvshow, All_Tab.Tvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, All_Tab.Tvshowholder>(
                Tvshow.class,
                R.layout.tvshowcard,
                All_Tab.Tvshowholder.class,
                query


        ) {
            @Override
            public void populateViewHolder(final All_Tab.Tvshowholder viewHolder, Tvshow model, int position) {
                final String tvkey = getRef(position).getKey();
                YoYo.with(Techniques.FadeInRight).playOn(viewHolder.view);

                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setLikeButton(tvkey);
                viewHolder.setBookmarkBtn(tvkey);
                viewHolder.setIndicator(tvkey);
                mDatabaseReference.keepSynced(true);
                progressBar.setVisibility(View.GONE);
                viewHolder.view.setOnClickListener(v -> {

                    //  Toast.makeText(getApplicationContext(),tvkey,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                    i.putExtra("tv_id", tvkey);
                    startActivity(i);





                });
                viewHolder.mLikeButton.setOnClickListener(v -> {
                    YoYo.with(Techniques.ZoomInUp).playOn(viewHolder.mLikeButton);

                    mProcessLike=true;
                    mDatabaseLike.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(mProcessLike) {
                                if (dataSnapshot.child(tvkey).hasChild(mAuth.getCurrentUser().getUid())) {

                                    mDatabaseLike.child(tvkey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    mProcessLike = false;

                                } else {
                                    mDatabaseLike.child(tvkey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
                                    Toast.makeText(getContext(),"Liked!!",Toast.LENGTH_SHORT).show();
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
                                    if(dataSnapshot.child(tvkey).hasChild(mAuth.getCurrentUser().getUid())){
                                        mDatabaseBookMark.child(tvkey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        Toast.makeText(getContext(),"Removed from favorites!!",Toast.LENGTH_SHORT).show();
                                        mProcessBookmark=false;
                                    }
                                    else {
                                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String title=  dataSnapshot.child(tvkey).child("Title").getValue().toString();
                                                String desc=dataSnapshot.child(tvkey).child("Description").getValue().toString();
                                                String image=dataSnapshot.child(tvkey).child("Image").getValue().toString();
                                                mDatabaseBookMark.child(tvkey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
                                                mDatabaseBookMark.child(tvkey).child("info").child("Title").setValue(title);
                                                mDatabaseBookMark.child(tvkey).child("info").child("Description").setValue(desc);
                                                mDatabaseBookMark.child(tvkey).child("info").child("Image").setValue(image);
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


    public static class MLinearLayoutManager extends LinearLayoutManager {
        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public MLinearLayoutManager(Context context) {
            super(context);
        }

        public MLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public MLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
        {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }



}
