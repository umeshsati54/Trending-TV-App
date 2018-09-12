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


public class Movies_All_Tab extends Fragment {

   private RecyclerView recyclerView;

    private static final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
    private static final DatabaseReference mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
    private static final DatabaseReference mDatabaseBookMark=FirebaseDatabase.getInstance().getReference().child("movies_bookmark");
    private ProgressBar progressBar;
    private Boolean mProcessLike=false;
    private Boolean mProcessBookmark=false;
    private static final FirebaseAuth mAuth=FirebaseAuth.getInstance();



    public Movies_All_Tab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_all__tab, container, false);
       Typeface custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");

        TextView toolbar_textview=(TextView)view.findViewById(R.id.tootlbar_text);



        toolbar_textview.setText("Search Here>>>>");
        toolbar_textview.setTypeface(custom_font);
        progressBar=(ProgressBar)view.findViewById(R.id.progress_id);
       // mDatabaseReference.keepSynced(true);
        //mDatabaseLike.keepSynced(true);
        //mDatabaseBookMark.keepSynced(true);


        recyclerView=(RecyclerView)view.findViewById(R.id.all_tab_recyclerview);
        MLinearLayoutManager mLayoutManager = new MLinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(mLayoutManager);

        final FirebaseRecyclerAdapter<Tvshow, Tvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, Tvshowholder>(
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

                    final String moviekey = getRef(position).getKey();
                YoYo.with(Techniques.FadeInRight).playOn(viewHolder.view);

                viewHolder.setTitle(model.getTitle());
                    viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                    viewHolder.setLikeButton(moviekey);
                    viewHolder.setBookmarkBtn(moviekey);
                    viewHolder.setIndicator(moviekey);
                   // mDatabaseReference.child(moviekey).keepSynced(true);
                   mDatabaseReference.keepSynced(true);

                    progressBar.setVisibility(View.GONE);
                    viewHolder.view.setOnClickListener(v -> {

                        //  Toast.makeText(getApplicationContext(),tvkey,Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                        i.putExtra("movie_id", moviekey);
                        startActivity(i);


                    });
                    viewHolder.mLikeButton.setOnClickListener(v -> {
                        YoYo.with(Techniques.ZoomInUp).playOn(viewHolder.mLikeButton);

                        mProcessLike = true;
                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {
                                    if (dataSnapshot.child(moviekey).hasChild(mAuth.getCurrentUser().getUid())) {


                                        mDatabaseLike.child(moviekey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;

                                    } else {
                                        Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
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

                        mProcessBookmark = true;
                        mDatabaseBookMark.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessBookmark) {
                                    if (dataSnapshot.child(moviekey).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseBookMark.child(moviekey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        Toast.makeText(getContext(), "Removed from favorites!!", Toast.LENGTH_SHORT).show();
                                        mProcessBookmark = false;
                                    } else {
                                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String title = dataSnapshot.child(moviekey).child("Title").getValue().toString();
                                                String desc = dataSnapshot.child(moviekey).child("Description").getValue().toString();
                                                String image = dataSnapshot.child(moviekey).child("Image").getValue().toString();
                                                mDatabaseBookMark.child(moviekey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
                                                mDatabaseBookMark.child(moviekey).child("info").child("Title").setValue(title);
                                                mDatabaseBookMark.child(moviekey).child("info").child("Description").setValue(desc);
                                                mDatabaseBookMark.child(moviekey).child("info").child("Image").setValue(image);
                                                Toast.makeText(getContext(), "Added to favorites!!", Toast.LENGTH_SHORT).show();

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        mProcessBookmark = false;
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
                        final String moviekey = getRef(position).getKey();
                        YoYo.with(Techniques.FadeInRight).playOn(viewHolder.view);
                        mDatabaseReference.keepSynced(true);

                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                        viewHolder.setLikeButton(moviekey);
                        viewHolder.setBookmarkBtn(moviekey);
                        viewHolder.setIndicator(moviekey);
                       // query.keepSynced(true);
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

                                            mDatabaseLike.child(moviekey).child(mAuth.getCurrentUser().getUid()).setValue("asd");
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

         ImageView tvimage;

        Typeface custom_font;

        public Tvshowholder(View itemView) {
            super(itemView);
            view = itemView;
            red=(ImageView)view.findViewById(R.id.red_indicator);
            blue=(ImageView)view.findViewById(R.id.blue_indicator);
            green=(ImageView)view.findViewById(R.id.green_indicator);
            mLikeButton=(ImageButton)view.findViewById(R.id.like_button);
            mBookmarkBtn=(ImageButton)view.findViewById(R.id.bookmark_btn);
            likes_text=(TextView)view.findViewById(R.id.no_of_like_text);
            custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");
            likes_text.setTypeface(custom_font);



            tvimage = (ImageView) view.findViewById(R.id.imageid);


            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseBookmark=FirebaseDatabase.getInstance().getReference().child("movies_bookmark");
            mIndicatorReference=FirebaseDatabase.getInstance().getReference().child("movies");
          //  mIndicatorReference.keepSynced(true);
            mAuth=FirebaseAuth.getInstance();
            //mDatabaseBookmark.keepSynced(true);
           // mDatabaseLike.keepSynced(true);

        }

        public  void setBookmarkBtn(final String moviekey){

            mDatabaseBookmark.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(moviekey).hasChild(mAuth.getCurrentUser().getUid())){
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
        public void setIndicator(final String moviekey){


            mIndicatorReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(moviekey).hasChild("red")){
                        red.setVisibility(View.VISIBLE);




                    }
                    if(dataSnapshot.child(moviekey).hasChild("green")){
                        green.setVisibility(View.VISIBLE);
                    }
                    if(dataSnapshot.child(moviekey).hasChild("blue")){
                        blue.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void setLikeButton(final String moviekey){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long like_count=dataSnapshot.child(moviekey).getChildrenCount();
                    if(dataSnapshot.child(moviekey)==null){
                        likes_text.setVisibility(View.GONE);
                    }

                    likes_text.setText(like_count+" Likes");




                    if(dataSnapshot.child(moviekey).hasChild( mAuth.getCurrentUser().getUid())){
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








            Picasso.with(imgctx).load(image).placeholder(R.drawable.placeholder).networkPolicy(NetworkPolicy.OFFLINE).into(tvimage, new Callback() {
                @Override
                public void onSuccess() {

                   // Toast.makeText(imgctx,"offline Image",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError() {
                  //  Toast.makeText(imgctx,"online Image",Toast.LENGTH_SHORT).show();

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
