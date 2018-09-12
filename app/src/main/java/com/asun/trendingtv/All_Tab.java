package com.asun.trendingtv;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class All_Tab extends Fragment
{
    private RecyclerView recyclerView;
    MLinearLayoutManager mLayoutManager;
    private static final DatabaseReference mDatabaseReference  = FirebaseDatabase.getInstance().getReference().child("tvshow");
    private static final DatabaseReference mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
    private static final DatabaseReference mDatabaseBookMark=FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark");


    private ProgressBar progressBar;
    private Boolean mProcessLike=false;
    private Boolean mProcessBookmark=false;
    private static final FirebaseAuth mAuth=FirebaseAuth.getInstance();


    public All_Tab() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all__tab, container, false);
        Typeface custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");
        TextView toolbar_textview=(TextView)view.findViewById(R.id.tootlbar_text);
        toolbar_textview.setText(R.string.search_hear);
        toolbar_textview.setTypeface(custom_font);

        progressBar=(ProgressBar)view.findViewById(R.id.progress_id);


        recyclerView=(RecyclerView)view.findViewById(R.id.all_tab_recyclerview);
        mLayoutManager=new MLinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<Tvshow, Tvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, Tvshowholder>(
                Tvshow.class,
                R.layout.tvshowcard,
                Tvshowholder.class,
                mDatabaseReference


        ) {
            @Override
            public void onBindViewHolder(Tvshowholder viewHolder, int position) {
                super.onBindViewHolder(viewHolder, position);
            }

            @Override
            public void populateViewHolder(final Tvshowholder viewHolder, Tvshow model, int position) {

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

                    Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                    i.putExtra("tv_id", tvkey);
                    startActivity(i);





                });
                viewHolder.mLikeButton.setOnClickListener(v -> {
                    YoYo.with(Techniques.ZoomInUp).playOn(viewHolder.mLikeButton);

                    mProcessLike=true;
                    mDatabaseLike.
                            addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(mProcessLike) {
                                if (dataSnapshot.child(tvkey).hasChild(mAuth.getCurrentUser().getUid())) {


                                    mDatabaseLike.child(tvkey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    mProcessLike = false;

                                } else {
                                    Toast.makeText(getContext(),"Liked",Toast.LENGTH_SHORT).show();
                                    mDatabaseLike.child(tvkey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
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
                                            mDatabaseBookMark.child(tvkey).child("info").child("Image").setValue(image);


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


        SearchView searchView =(SearchView)view.findViewById(R.id.search_id);
        searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.END));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final Query query=mDatabaseReference.orderByChild("searchname").startAt(newText).endAt("~");
                FirebaseRecyclerAdapter<Tvshow, Tvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, Tvshowholder>(
                        Tvshow.class,
                        R.layout.tvshowcard,
                        Tvshowholder.class,
                        query


                ) {
                    @Override
                    public void populateViewHolder(final Tvshowholder viewHolder, Tvshow model, int position) {
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

                return true;
            }
        });


        return view;
    }


    public static class Tvshowholder extends RecyclerView.ViewHolder {
        View view;
        ImageButton mLikeButton;
        ImageButton mBookmarkBtn;
        DatabaseReference mDatabaseLike;
        DatabaseReference mDatabaseBookmark;
        DatabaseReference mIndicatorReference;
        FirebaseAuth mAuth;
        TextView likes_text;
        ImageView red,blue,green;
        Typeface custom_font;



        public Tvshowholder(View itemView) {
            super(itemView);
            view = itemView;
            custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");

            red=(ImageView)view.findViewById(R.id.red_indicator);
            blue=(ImageView)view.findViewById(R.id.blue_indicator);
            green=(ImageView)view.findViewById(R.id.green_indicator);
            mLikeButton=(ImageButton)view.findViewById(R.id.like_button);
            mBookmarkBtn=(ImageButton)view.findViewById(R.id.bookmark_btn);
            likes_text=(TextView)view.findViewById(R.id.no_of_like_text);
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseBookmark=FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark");
            mIndicatorReference=FirebaseDatabase.getInstance().getReference().child("tvshow");
           mAuth=FirebaseAuth.getInstance();
            likes_text.setTypeface(custom_font);

        }

        public  void setBookmarkBtn(final String tvkey){

            mDatabaseBookmark.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(tvkey).hasChild(mAuth.getCurrentUser().getUid())){
                        mBookmarkBtn.setImageResource(R.drawable.star);
                    }
                    else {
                        mBookmarkBtn.setImageResource(R.drawable.star_outline);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setIndicator(final String tvkey){


            mIndicatorReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(tvkey).hasChild("red")){
                        red.setVisibility(View.VISIBLE);




                    }
                    if(dataSnapshot.child(tvkey).hasChild("green")){
                        green.setVisibility(View.VISIBLE);
                    }
                    if(dataSnapshot.child(tvkey).hasChild("blue")){
                        blue.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setLikeButton(final String tvkey){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long like_count=dataSnapshot.child(tvkey).getChildrenCount();
                    if(dataSnapshot.child(tvkey)==null){
                        likes_text.setVisibility(View.GONE);
                    }

                    likes_text.setText(like_count+" Likes");

                    if(dataSnapshot.child(tvkey).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikeButton.setImageResource(R.drawable.thumb_uped);
                    }
                    else {


                        likes_text.setText(like_count+" Likes");

                        mLikeButton.setImageResource(R.drawable.thumb_up);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title) {
            TextView titletextView = (TextView) view.findViewById(R.id.titleid);
            titletextView.setTypeface(custom_font);
            titletextView.setText(title);
        }


        public void setImage(final Context imgctx, final String image) {
            final ImageView tvimage = (ImageView) view.findViewById(R.id.imageid);




            Picasso.with(imgctx).load(image).placeholder(R.drawable.placeholder).networkPolicy(NetworkPolicy.OFFLINE).into(tvimage, new Callback() {
                @Override
                public void onSuccess() {


                }

                @Override
                public void onError() {

                    Picasso.with(imgctx).load(image).placeholder(R.drawable.placeholder).into(tvimage);
                }
            });

        }

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
