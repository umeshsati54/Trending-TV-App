package com.asun.trendingtv;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class Fav_Tab extends All_Tab {

private ProgressBar mProgressBar;
    private TextView textView;
    private static final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("tvshow");
   private static final FirebaseAuth mAuth= FirebaseAuth.getInstance();


    public Fav_Tab() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fav_tab, container, false);


        mProgressBar=(ProgressBar)view.findViewById(R.id.book_progress_id);
       RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.bookmark_tab_recyclerview);
        textView=(TextView)view.findViewById(R.id.bookmarktext);
        DatabaseReference mDatabaseBookMark = FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark");
        mLayoutManager=new MLinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        mDatabaseBookMark.keepSynced(true);




        mDatabaseBookMark.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final FirebaseRecyclerAdapter<Tvshow, Tvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, Tvshowholder>(
                        Tvshow.class,
                        R.layout.bookmark_card,
                        Tvshowholder.class,
                        mDatabaseReference)
                {
                    @Override
                    public void populateViewHolder(Tvshowholder viewHolder, Tvshow model, int position) {
                         final String tvkey = getRef(position).getKey();

                        if(dataSnapshot.child(tvkey).hasChild(mAuth.getCurrentUser().getUid())) {
                           // DatabaseReference mDatabaseBookMark = FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark").child("info");

                            viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                            //mDatabaseBookMark.keepSynced(true);
                            mProgressBar.setVisibility(View.GONE);
                            viewHolder.view.setOnClickListener(v -> {
                                Intent i = new Intent(getContext(), DetailTVshow.class);
                                i.putExtra("tv_id", tvkey);
                                startActivity(i);


                            });
                        }

                else {
                            textView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);


                        }

                    }

                };

                recyclerView.setAdapter(firebaseRecyclerAdapter);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        FirebaseAuth mAuth;
        TextView likes_text;


        public Tvshowholder(View itemView) {
            super(itemView);
            view = itemView;
            mAuth=FirebaseAuth.getInstance();
            mLikeButton=(ImageButton)view.findViewById(R.id.like_button);
            mBookmarkBtn=(ImageButton)view.findViewById(R.id.bookmark_btn);
            likes_text=(TextView)view.findViewById(R.id.no_of_like_text);
            mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
            mDatabaseBookmark=FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark").child(mAuth.getCurrentUser().getUid());
            mDatabaseBookmark.keepSynced(true);
            mDatabaseLike.keepSynced(true);
        }



        public void setTitle(String title) {

        }

        public void setDescription(String description) {

        }

        public void setImage(final Context imgctx, final String image) {
            final ImageView tvimage = (ImageView) view.findViewById(R.id.imageid);
            if (image==null){
                tvimage.setVisibility(View.GONE);
            }
            Picasso.with(imgctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(tvimage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(imgctx).load(image).into(tvimage);

                }
            });

        }

    }

}
