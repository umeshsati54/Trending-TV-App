package com.asun.trendingtv;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import org.apache.xmlrpc.XmlRpcException;

import java.io.File;

public class DetailSeasonepisodes extends AppCompatActivity {
    private static String tvshowtitle;
    private static String seasonimage;
    private DatabaseReference detailseasonDatabaseReference;
    private static String episode_key;
    private Context context=this;
    private long mydownloadreferenc;
    private DownloadManager downloadManager;
    private BroadcastReceiver receiverDownloadComplete;
    private BroadcastReceiver receivernotificatioCliked;
    private ProgressBar progressBar;
    private MainActivity mainActivity=new MainActivity();
    private ProgressDialog progressDialog;
    private static String lang1= "";
    private NativeExpressAdView adView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_seasonepisodes);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        adView = (NativeExpressAdView)findViewById(R.id.detail_season_native_id);
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



        final Typeface custom_font= Typeface.createFromAsset(getApplicationContext().getAssets(),  "fonts/canaro_extra_bold.otf");

        final TextView detail_toolbar_text=(TextView)findViewById(R.id.detail_toolbar_text_view);

        final File file=new File(Environment.getExternalStorageDirectory()+"/Realproject/TV Shows");
        if (!file.exists()){
            file.mkdirs();
        }

        progressBar=(ProgressBar)findViewById(R.id.progress_id);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.seasons_fragment_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String seasonkey = getIntent().getExtras().getString("season_id");
        String tvshowkey = getIntent().getExtras().getString("tvshow_id");
        tvshowtitle=getIntent().getExtras().getString("show_title");
        downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        assert tvshowkey != null;
        assert seasonkey != null;
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("tvshow").child(tvshowkey).child("season").child(seasonkey);
        detailseasonDatabaseReference= FirebaseDatabase.getInstance().getReference().child("tvshow").child(tvshowkey).child("season").child(seasonkey).child("episode");


        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String seasontitle=dataSnapshot.child("season_title").getValue().toString();
               seasonimage =dataSnapshot.child("season_image").getValue().toString();
                detail_toolbar_text.setText(tvshowtitle+" "+seasontitle);
                detail_toolbar_text.setTypeface(custom_font);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<EpisodeGetterSetter,Episodesholder> seasongettesetteSeasonsholderFirebaseRecyclerAdapter=new FirebaseRecyclerAdapter<EpisodeGetterSetter, Episodesholder>(
                EpisodeGetterSetter.class,
                R.layout.episode_card,
                Episodesholder.class,
                detailseasonDatabaseReference



        ) {
            @Override
            protected void populateViewHolder(final Episodesholder viewHolder, final EpisodeGetterSetter model, int position) {
                episode_key = getRef(position).getKey();
              viewHolder.setEpisode_title(model.getEpisode_title());
                viewHolder.setEpisode_length(model.getEpisode_length());
                viewHolder.setEpisode_Image(getApplicationContext(),model.getEpisode_image());
                final String episode_no= String.valueOf(viewHolder.getAdapterPosition()+1);
                final String season_no=getIntent().getExtras().getString("season_no");


                progressBar.setVisibility(View.GONE);
                final DatabaseReference mPlayDatabaseReference=detailseasonDatabaseReference.child(episode_key);

                final Dialog dialog=new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                final Dialog subtitle_dialog=new Dialog(context);
                subtitle_dialog.setContentView(R.layout.subtitle_select_dialog);
                subtitle_dialog.setCancelable(false);

                TextView note =(TextView)dialog.findViewById(R.id.note);
                note.setTypeface(custom_font);

                final RadioGroup radioGroup=(RadioGroup)subtitle_dialog.findViewById(R.id.sub_radio_grp);
                final RadioButton radioButton=(RadioButton)subtitle_dialog.findViewById(radioGroup.getCheckedRadioButtonId());
                final Button ok_btn=(Button)subtitle_dialog.findViewById(R.id.sub_ok_btn);

                 lang1=    radioButton.getTag().toString();

                ok_btn.setOnClickListener(v -> {
                    final RadioButton radioButton1 =(RadioButton)subtitle_dialog.findViewById(radioGroup.getCheckedRadioButtonId());

                   lang1 =    radioButton1.getTag().toString();
                    subtitle_dialog.cancel();




                });
                progressDialog.setMessage("preparing to play...");


                viewHolder.btn_480.setOnClickListener(v -> {

                    ImageButton play_button=(ImageButton)dialog.findViewById(R.id.play_btn);
                    ImageButton download_btn=(ImageButton)dialog.findViewById(R.id.download_btn);
                    final RadioButton openwith=(RadioButton)dialog.findViewById(R.id.otherid);
                    final RadioButton exoplayer_radio=(RadioButton)dialog.findViewById(R.id.exoplayer_id);
                    Button subtitle_btn=(Button)dialog.findViewById(R.id.subtitle_btn);
                    subtitle_btn.setTypeface(custom_font);

                    subtitle_btn.setOnClickListener(v1 -> subtitle_dialog.show());

                    TextView quality_tv=(TextView)dialog.findViewById(R.id.quality_tv);
                    openwith.setTypeface(custom_font);
                    exoplayer_radio.setTypeface(custom_font);
                    quality_tv.setTypeface(custom_font);
                    quality_tv.setText("480p");
                    play_button.setOnClickListener(v12 -> {
                        progressDialog.show();

                        mPlayDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                final String url_480p=dataSnapshot.child("episode_480p").getValue().toString();
                                final String player_title=dataSnapshot.child("player_title").getValue().toString();


                                if(url_480p.equals("null")){
                                    progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(!isPackageExisted("org.videolan.vlc")&&!isPackageExisted("com.mxtech.videoplayer.ad")&& !isPackageExisted("com.mxtech.videoplayer.pro")){
                                        checkPlayer();
                                    }
                                    else {
                                        final byte b1 = 2;
                                        if(openwith.isChecked()) {






                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }

                                                try {


                                                    String s = openSubtitle.getTvSeriesSubs(tvshowtitle, season_no, episode_no, "1",lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }

                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse(url_480p), "video/*");
                                                    intent.putExtra("title", player_title);
                                                    intent.putExtra("subs", new Parcelable[]{Uri.parse(s)});
                                                    intent.putExtra("decode_mode", b1);
                                                    intent.putExtra("subtitles_location",s);
                                                   // mainActivity.showVideoAd();
                                                    startActivity(intent);


                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }


                                                openSubtitle.logOut();
                                                progressDialog.dismiss();
                                            });
                                            thread.start();










                                            }

                                        if (exoplayer_radio.isChecked()){




                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();
                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }



                                                try {

                                                    String   s = openSubtitle.getTvSeriesSubs(tvshowtitle,season_no,episode_no,"1",lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }

                                                      Intent  intent=new Intent(DetailSeasonepisodes.this,VideoPlayer_Activity.class);
                                                        intent.putExtra("video_url",url_480p);
                                                        intent.putExtra("title", player_title);
                                                        intent.putExtra("subtitle_url",s);
                                                        startActivity(intent);


                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }


                                                openSubtitle.logOut();
                                                progressDialog.dismiss();

                                            });
                                            thread.start();
                                           // mainActivity.showVideoAd();




                                           }
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    });


                    download_btn.setOnClickListener(v13 -> mPlayDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url_480p=dataSnapshot.child("episode_480p").getValue().toString();
                            String player_title=dataSnapshot.child("player_title").getValue().toString();
                            if (url_480p.equals("null")){
                                Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                            }

                            else {
                                String ext=url_480p.substring(url_480p.lastIndexOf("."));

                                Toast.makeText(getApplicationContext(),"Download started,see progress in Notification panel",Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse(url_480p);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                request.setDescription("Trending TV").setTitle(player_title+" 480p");
                                request.setDestinationInExternalFilesDir(getApplicationContext(), "/Realproject/TV Shows", player_title+" 480p"+ext);
                                request.allowScanningByMediaScanner();
                                request.setVisibleInDownloadsUi(true);
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                mydownloadreferenc = downloadManager.enqueue(request);
                                mainActivity.showVideoAd();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }));



                                dialog.show();


                });
                viewHolder.btn_720.setOnClickListener(v -> {
                    ImageButton play_button=(ImageButton)dialog.findViewById(R.id.play_btn);
                    ImageButton download_btn=(ImageButton)dialog.findViewById(R.id.download_btn);
                    final RadioButton openwith=(RadioButton)dialog.findViewById(R.id.otherid);
                    final RadioButton exoplayer_radio=(RadioButton)dialog.findViewById(R.id.exoplayer_id);
                    Button subtitle_btn=(Button)dialog.findViewById(R.id.subtitle_btn);

                    subtitle_btn.setOnClickListener(v14 -> subtitle_dialog.show());

                    TextView quality_tv=(TextView)dialog.findViewById(R.id.quality_tv);
                    openwith.setTypeface(custom_font);
                    exoplayer_radio.setTypeface(custom_font);
                    quality_tv.setTypeface(custom_font);

                    quality_tv.setText("720p");

                    play_button.setOnClickListener(v15 -> {
                        progressDialog.show();

                        mPlayDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String url_720p=dataSnapshot.child("episode_720p").getValue().toString();
                                final String player_title=dataSnapshot.child("player_title").getValue().toString();


                                if(url_720p.equals("null")){
                                    progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(!isPackageExisted("org.videolan.vlc")&&!isPackageExisted("com.mxtech.videoplayer.ad")&& !isPackageExisted("com.mxtech.videoplayer.pro")){
                                        checkPlayer();
                                    }
                                    else {
                                        final byte b12 = 2;
                                        if(openwith.isChecked()) {

                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }

                                                try {


                                                    String s = openSubtitle.getTvSeriesSubs(tvshowtitle, season_no, episode_no, "1", lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }


                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse(url_720p), "video/*");
                                                    intent.putExtra("title", player_title);
                                                    intent.putExtra("subs", new Parcelable[]{Uri.parse(s)});
                                                    intent.putExtra("decode_mode", b12);
                                                    intent.putExtra("subtitles_location",s);
                                                    //mainActivity.showVideoAd();
                                                    startActivity(intent);


                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }


                                                openSubtitle.logOut();
                                                progressDialog.dismiss();
                                            });
                                            thread.start();





                                        }
                                        if (exoplayer_radio.isChecked()){

                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }



                                                try {

                                                    String   s = openSubtitle.getTvSeriesSubs(tvshowtitle,season_no,episode_no,"1",lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }


                                                    Intent  intent=new Intent(DetailSeasonepisodes.this,VideoPlayer_Activity.class);
                                                    intent.putExtra("video_url",url_720p);
                                                    intent.putExtra("title", player_title);
                                                    intent.putExtra("subtitle_url",s);
                                                   // mainActivity.showVideoAd();
                                                    startActivity(intent);


                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }


                                                openSubtitle.logOut();
                                                progressDialog.dismiss();

                                            });
                                            thread.start();  }

                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    });


                    download_btn.setOnClickListener(v16 -> mPlayDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url_720p=dataSnapshot.child("episode_720p").getValue().toString();
                            String player_title=dataSnapshot.child("player_title").getValue().toString();
                            if (url_720p.equals("null")){
                                Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                            }
                            else {
                                String ext=url_720p.substring(url_720p.lastIndexOf("."));
                                Toast.makeText(getApplicationContext(),"Download started,see progress in Notification panel",Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse(url_720p);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                request.setDescription("Trending TV").setTitle(player_title+" 720p");
                                request.setDestinationInExternalFilesDir(getApplicationContext(), "/Realproject/TV Shows", player_title+" 720p"+ext);
                                request.allowScanningByMediaScanner();
                                request.setVisibleInDownloadsUi(true);
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                mydownloadreferenc = downloadManager.enqueue(request);
                               // mainActivity.showVideoAd();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }));


                                dialog.show();


                });
                viewHolder.btn_1080.setOnClickListener(v -> {
                    ImageButton play_button=(ImageButton)dialog.findViewById(R.id.play_btn);
                    ImageButton download_btn=(ImageButton)dialog.findViewById(R.id.download_btn);
                    final RadioButton openwith=(RadioButton)dialog.findViewById(R.id.otherid);
                    final RadioButton exoplayer_radio=(RadioButton)dialog.findViewById(R.id.exoplayer_id);
                    Button subtitle_btn=(Button)dialog.findViewById(R.id.subtitle_btn);

                    subtitle_btn.setOnClickListener(v17 -> subtitle_dialog.show());

                    TextView quality_tv=(TextView)dialog.findViewById(R.id.quality_tv);
                    openwith.setTypeface(custom_font);
                    exoplayer_radio.setTypeface(custom_font);
                    quality_tv.setTypeface(custom_font);

                    quality_tv.setText("1080p");

                    play_button.setOnClickListener(v18 -> {
                        progressDialog.show();

                        mPlayDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String url_1080p=dataSnapshot.child("episode_1080p").getValue().toString();
                                final String player_title=dataSnapshot.child("player_title").getValue().toString();
                                if(url_1080p.equals("null")){
                                    progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(!isPackageExisted("org.videolan.vlc")&&!isPackageExisted("com.mxtech.videoplayer.ad")&& !isPackageExisted("com.mxtech.videoplayer.pro")){
                                        checkPlayer();
                                    }
                                    else {
                                        final byte b13 = 2;
                                        if(openwith.isChecked()) {

                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }

                                                try {


                                                    String s = openSubtitle.getTvSeriesSubs(tvshowtitle, season_no, episode_no, "1",lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }

                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.parse(url_1080p), "video/*");
                                                    intent.putExtra("title", player_title);
                                                    intent.putExtra("subs", new Parcelable[]{Uri.parse(s)});
                                                    intent.putExtra("decode_mode", b13);
                                                    intent.putExtra("subtitles_location",s);
                                                    //mainActivity.showVideoAd();
                                                    startActivity(intent);


                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }


                                                openSubtitle.logOut();
                                                progressDialog.dismiss();
                                            });
                                            thread.start();





                                        }

                                        if (exoplayer_radio.isChecked()){

                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }



                                                try {

                                                    String   s = openSubtitle.getTvSeriesSubs(tvshowtitle,season_no,episode_no,"1",lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }


                                                    Intent  intent=new Intent(DetailSeasonepisodes.this,VideoPlayer_Activity.class);
                                                    intent.putExtra("video_url",url_1080p);
                                                    intent.putExtra("title", player_title);
                                                    intent.putExtra("subtitle_url",s);
                                                   // mainActivity.showVideoAd();
                                                    startActivity(intent);


                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }


                                                openSubtitle.logOut();
                                                progressDialog.dismiss();

                                            });
                                            thread.start(); }

                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    });


                    download_btn.setOnClickListener(v19 -> mPlayDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String url_1080p=dataSnapshot.child("episode_1080p").getValue().toString();
                            String player_title=dataSnapshot.child("player_title").getValue().toString();
                            if (url_1080p.equals("null")){
                                Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                            }
                            else {
                                String ext=url_1080p.substring(url_1080p.lastIndexOf("."));
                                Toast.makeText(getApplicationContext(),"Download started,see progress in Notification panel",Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse(url_1080p);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                request.setDescription("Trending TV").setTitle(player_title+" 1080p");
                                request.setDestinationInExternalFilesDir(getApplicationContext(),"/Realproject/TV Shows",player_title+" 1080p"+ext);
                                request.allowScanningByMediaScanner();
                                request.setVisibleInDownloadsUi(true);
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                mydownloadreferenc = downloadManager.enqueue(request);
                               // mainActivity.showVideoAd();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }));



                                dialog.show();


                });

            }
        };
        recyclerView.setAdapter(seasongettesetteSeasonsholderFirebaseRecyclerAdapter);

    }


    public static class Episodesholder extends RecyclerView.ViewHolder{
        View view;
        Button btn_480;
        Button btn_720;
        Button btn_1080;
        Typeface custom_font;

        public Episodesholder(View itemView) {
            super(itemView);
            view=itemView;
            btn_480=(Button)view.findViewById(R.id.btn_480p);
            btn_720=(Button)view.findViewById(R.id.btn_720p);
            btn_1080=(Button)view.findViewById(R.id.btn_1080p);
            custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");
            btn_480.setTypeface(custom_font);
            btn_1080.setTypeface(custom_font);
            btn_720.setTypeface(custom_font);


        }

       private void setEpisode_title(String episode_title){
            TextView episodetitletextView=(TextView)view.findViewById(R.id.episodetitleid);
            episodetitletextView.setTypeface(custom_font);
            episodetitletextView.setText(episode_title);


        }

        void setEpisode_Image(Context imgctx, String season_image){
            ImageView episodeimage=(ImageView)view.findViewById(R.id.episodeimageid);

            Picasso.with(imgctx).load(season_image).placeholder(R.drawable.placeholder).into(episodeimage);




        }
        void setEpisode_length(String episode_length){
            TextView episodelengthtextview=(TextView)view.findViewById(R.id.episode_length_id);
            episodelengthtextview.setTypeface(custom_font);
            episodelengthtextview.setText(episode_length);
        }

    }



    private boolean isPackageExisted(String targetpackage){
        PackageManager pm =getPackageManager();
        try{
            PackageInfo info=pm.getPackageInfo(targetpackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
    private void checkPlayer(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setCancelable(false);
        builder.setTitle("Get MX Player App");
        builder.setMessage("This video won't run without the MX Player App, which is missing from your device");
        builder.setPositiveButton("Get MX player App", (dialog, which) -> {
            Uri uri = Uri.parse("market://details?id=com.mxtech.videoplayer.ad");
            // Toast.makeText(getApplicationContext(), "Please Install The MX player From playstore", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent1);


        });
        builder.setNegativeButton("Get VLC App", (dialog, which) -> {
            Uri uri = Uri.parse("market://details?id=org.videolan.vlc");
            //  Toast.makeText(getApplicationContext(), "Please Install The MX player From playstore", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent1);

        });
        builder.show();

    }



    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        receivernotificatioCliked=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraIDs=DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] refs=intent.getLongArrayExtra(extraIDs);
                for (long reference:refs){
                    if (reference==mydownloadreferenc){

                    }
                }
            }
        };
        registerReceiver(receivernotificatioCliked,intentFilter);





        IntentFilter filter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiverDownloadComplete=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ref=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(mydownloadreferenc==ref){
                    DownloadManager.Query query=new DownloadManager.Query();
                    query.setFilterById(ref);
                    Cursor cursor=downloadManager.query(query);
                    cursor.moveToFirst();

                }
            }
        };
        registerReceiver(receiverDownloadComplete,filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverDownloadComplete);
        unregisterReceiver(receivernotificatioCliked);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.destroy();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
       // mainActivity.showIntersititialAd();
        mainActivity.showVideoAd();

    }



}
