package com.asun.trendingtv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailTVshow extends AppCompatActivity {
    private String tvkey;
    private static final DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("tvshow");
    private ImageView imageView;
   private static String show_title;
    private ProgressBar progressBar;
    private NativeExpressAdView adView;
    private TextView title_text,rating;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tvshow);
        final Typeface custom_font= Typeface.createFromAsset(getApplicationContext().getAssets(),  "fonts/canaro_extra_bold.otf");
        ImageView share_image = (ImageView) findViewById(R.id.image_share);
        share_image.setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"App Name");
            intent.putExtra(Intent.EXTRA_TEXT,"Download and Watch TV Show '"+show_title+"'  and more TV shows and movies on your Android for free. App name- link");
            startActivity(Intent.createChooser(intent,"Share Via"));

        });
        title_text=(TextView)findViewById(R.id.tvshow_title_text);
        rating=(TextView)findViewById(R.id.rating_text);
        rating.setTypeface(custom_font);
        title_text.setTypeface(custom_font);


        View view=View.inflate(this, R.layout.seasoncard, new ViewGroup(this) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        });
        //final ImageView tvimage=(ImageView)view.findViewById(R.id.seasonimageid);

        progressBar=(ProgressBar)findViewById(R.id.progress_id);
        adView = (NativeExpressAdView)findViewById(R.id.detail_tv_native_id);
        final AdRequest adRequest=new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adView.setVisibility(View.GONE);

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.loadAd(adRequest);

            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);

            }
        });
        adView.loadAd(adRequest);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.seasons);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        imageView=(ImageView)findViewById(R.id.collepImage);



        tvkey=getIntent().getExtras().getString("tv_id");

        DatabaseReference seasonDatabaseReference = FirebaseDatabase.getInstance().getReference().child("tvshow").child(tvkey).child("season");
        //Toast.makeText(getApplicationContext(),tvkey,Toast.LENGTH_SHORT).show();

        mDatabaseReference.child(tvkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               show_title =dataSnapshot.child("Title").getValue().toString();
                String show_image=dataSnapshot.child("Image").getValue().toString();

                String movie_rating=dataSnapshot.child("rating").getValue().toString();
                title_text.setText(show_title);
                rating.setText(movie_rating);




                Picasso.with(getApplicationContext()).load(show_image).placeholder(R.drawable.placeholder).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<Seasongettesette,Seasonsholder> seasongettesetteSeasonsholderFirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Seasongettesette, Seasonsholder>(
                Seasongettesette.class,
                R.layout.seasoncard,
                Seasonsholder.class,
                seasonDatabaseReference



        ) {
            @Override
            protected void populateViewHolder(Seasonsholder viewHolder, Seasongettesette model, int position) {
                final String Season_key = getRef(position).getKey();
                viewHolder.setSeason_title(model.getSeason_title());
                viewHolder.setSeason_Image(getApplicationContext(),model.getSeason_image());
                viewHolder.setNo_of_episodes(model.getNo_of_episods());
                final String season_no= String.valueOf(viewHolder.getAdapterPosition()+1);
                progressBar.setVisibility(View.GONE);
                viewHolder.view.setOnClickListener(v -> {
                    Intent i = new Intent(DetailTVshow.this, DetailSeasonepisodes.class);

                    i.putExtra("season_id", Season_key);
                    i.putExtra("tvshow_id", tvkey);
                    i.putExtra("show_title",show_title);
                    i.putExtra("season_no",season_no);


                    startActivity(i);
                });

            }
        };
        recyclerView.setAdapter(seasongettesetteSeasonsholderFirebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    public static class Seasonsholder extends RecyclerView.ViewHolder{
        View view;


        Typeface custom_font;


        public Seasonsholder(View itemView) {
            super(itemView);
            view=itemView;
            custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");

        }
        void setSeason_title(String season_title){
            TextView seasontitletextView=(TextView)view.findViewById(R.id.seasontitleid);
            seasontitletextView.setTypeface(custom_font);
            seasontitletextView.setText(season_title);


        }

        void setSeason_Image(Context imgctx, String season_image){
            ImageView seasonimage=(ImageView)view.findViewById(R.id.seasonimageid);
            Picasso.with(imgctx).load(season_image).placeholder(R.drawable.placeholder).into(seasonimage);


        }
        void setNo_of_episodes(String no_of_episodes){
            TextView noofepisodetextview=(TextView)view.findViewById(R.id.no_of_episode_text_id);
            noofepisodetextview.setTypeface(custom_font);
            noofepisodetextview.setText(no_of_episodes);
        }

    }


}
