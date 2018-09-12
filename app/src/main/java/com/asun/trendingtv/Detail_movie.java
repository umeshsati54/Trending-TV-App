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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class Detail_movie extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
     static String movie_title;
    private TextView title_text,timing,rating;
    private TextView description;
    private TextView star1;
    private TextView star2;
    private TextView lang_text;
    private Button btn_480p,btn_720p,btn_1080p;
    private long mydownloadreferenc;
    private DownloadManager downloadManager;
    private BroadcastReceiver receiverDownloadComplete;
    private Context context=this;
    private BroadcastReceiver receivernotificatioCliked;
    private NativeExpressAdView tAdView;
    private MainActivity mainActivity=new MainActivity();
    private ProgressDialog progressDialog;
    private String lang1= "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);



        //Toast.makeText(getApplicationContext(), "detail oncreate", Toast.LENGTH_SHORT).show();
        final Typeface custom_font= Typeface.createFromAsset(getApplicationContext().getAssets(),  "fonts/canaro_extra_bold.otf");

        File file=new File(Environment.getExternalStorageDirectory().getPath()+"/Realproject/Movies");
        if (!file.exists()){
            file.mkdirs();
        }






        tAdView=(NativeExpressAdView) findViewById(R.id.adView_top);
        final AdRequest adRequest1=new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build();

        tAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                tAdView.setVisibility(View.GONE);

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                tAdView.loadAd(adRequest1);
              //  Toast.makeText(getApplicationContext(),"banner onAdFailedToLoad",Toast.LENGTH_SHORT).show();
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
               // Toast.makeText(getApplicationContext(),"banner onAdLoaded",Toast.LENGTH_SHORT).show();

                tAdView.setVisibility(View.VISIBLE);
               // Toast.makeText(getApplicationContext(),"banner onAdLoaded visible",Toast.LENGTH_SHORT).show();

            }
        });
        tAdView.loadAd(adRequest1);


        ImageView share_image = (ImageView) findViewById(R.id.image_share);
        share_image.setOnClickListener(this);

        downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);




        description=(TextView)findViewById(R.id.movie_descripition);
        star1=(TextView)findViewById(R.id.star1);
        star2=(TextView)findViewById(R.id.star2);
        btn_480p=(Button)findViewById(R.id.btn_480p);
        btn_720p=(Button)findViewById(R.id.btn_720p);
        btn_1080p=(Button)findViewById(R.id.btn_1080p);
        btn_1080p.setTypeface(custom_font);
        btn_720p.setTypeface(custom_font);
        btn_480p.setTypeface(custom_font);

        TextView starcast = (TextView) findViewById(R.id.star_cast);

       // ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_id);
        title_text=(TextView)findViewById(R.id.movie_title_text);
        timing=(TextView)findViewById(R.id.movie_timing_text);
        rating=(TextView)findViewById(R.id.rating_text);
        lang_text=(TextView)findViewById(R.id.lang_text);

        description.setTypeface(custom_font);
        star1.setTypeface(custom_font);
        star2.setTypeface(custom_font);
        starcast.setTypeface(custom_font);
        timing.setTypeface(custom_font);
        rating.setTypeface(custom_font);
        lang_text.setTypeface(custom_font);
        title_text.setTypeface(custom_font);

        imageView=(ImageView)findViewById(R.id.collepImage);
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
        String moviekey = getIntent().getExtras().getString("movie_id");

        assert moviekey != null;
        mDatabaseReference.child(moviekey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                movie_title =dataSnapshot.child("Title").getValue().toString();
                String movie_desc=dataSnapshot.child("Description").getValue().toString();
                String movie_image=dataSnapshot.child("Image").getValue().toString();
                String movie_time=dataSnapshot.child("time").getValue().toString();
                String movie_rating=dataSnapshot.child("rating").getValue().toString();
                String _star1=dataSnapshot.child("star1").getValue().toString();
                String _star2=dataSnapshot.child("star2").getValue().toString();
                final String url_480p=dataSnapshot.child("movie_480p").getValue().toString();
                final String url_720p=dataSnapshot.child("movie_720p").getValue().toString();
                final String url_1080p=dataSnapshot.child("movie_1080p").getValue().toString();
                final String lang=dataSnapshot.child("language").getValue().toString();
                title_text.setText(movie_title);
                description.setText(movie_desc);
                star1.setText(_star1);
                star2.setText(_star2);
                lang_text.setText(lang);
                timing.setText(movie_time);
                rating.setText(movie_rating);
                Picasso.with(getApplicationContext()).load(movie_image).placeholder(R.drawable.placeholder).into(imageView);
                final Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);
                TextView note_txtView=(TextView)dialog.findViewById(R.id.note);
                note_txtView.setTypeface(custom_font);

                final Dialog subtitle_dialog=new Dialog(context);
                subtitle_dialog.setContentView(R.layout.subtitle_select_dialog);
                subtitle_dialog.setCancelable(false);
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
                btn_480p.setOnClickListener(v -> {

                    ImageButton play_button=(ImageButton)dialog.findViewById(R.id.play_btn);
                    final ImageButton download_btn=(ImageButton)dialog.findViewById(R.id.download_btn);
                    final RadioButton openwith=(RadioButton)dialog.findViewById(R.id.otherid);
                    final RadioButton exoplayer_radio=(RadioButton)dialog.findViewById(R.id.exoplayer_id);
                    openwith.setVisibility(View.GONE);
                    exoplayer_radio.setVisibility(View.GONE);

                    Button subtitle_btn=(Button)dialog.findViewById(R.id.subtitle_btn);
                    subtitle_btn.setTypeface(custom_font);

                    subtitle_btn.setOnClickListener(v1 -> subtitle_dialog.show());


                    TextView quality_tv=(TextView)dialog.findViewById(R.id.quality_tv);
                    quality_tv.setText("480p");
                    quality_tv.setTypeface(custom_font);

                    play_button.setOnClickListener(v12 -> {
                        progressDialog.show();


                        if(url_480p.equals("null")){
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(!isPackageExisted("org.videolan.vlc")&&!isPackageExisted("com.mxtech.videoplayer.ad")&& !isPackageExisted("com.mxtech.videoplayer.pro")){
                                        checkPlayer();
                                    }
                                    else {
                                        final byte b = 2;
                                        if (openwith.isChecked()) {



                                            Thread thread=new Thread(() -> {
                                                final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                                try {
                                                    openSubtitle.login();
                                                } catch (XmlRpcException e) {
                                                    e.printStackTrace();
                                                }



                                                try {

                                                    String   s = openSubtitle.getMovieSubsByName(movie_title, "1",lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }

                                                        //  Parcelable[] parcelables={Uri.parse(subTitle)};
                                                    Intent    intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.parse(url_480p), "video/*");
                                                         intent.putExtra("subs", new Parcelable[]{Uri.parse(s)});
                                                    intent.putExtra("title", movie_title);
                                                        intent.putExtra("decode_mode", b);
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

                                                    String   s = openSubtitle.getMovieSubsByName(movie_title, "1", lang1);

                                                    if(!s.equals("")) {
                                                        s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                                    }

                                                     Intent   intent=new Intent(Detail_movie.this,VideoPlayer_Activity.class);
                                                        intent.putExtra("video_url",url_480p);
                                                    intent.putExtra("title", movie_title);
                                                        intent.putExtra("subtitle_url",s);
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

                                    }


                                }


                            });


                    download_btn.setOnClickListener(v13 -> {


                                if (url_480p.equals("null")){
                                    Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                                }

                                else {

                                    Toast.makeText(getApplicationContext(),"Download started,see progress in Notification panel",Toast.LENGTH_LONG).show();
                                    Uri uri = Uri.parse(url_480p);
                                    String ext=url_480p.substring(url_480p.lastIndexOf("."));
                                   // Toast.makeText(getApplicationContext(),ext, Toast.LENGTH_SHORT).show();
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                    request.setDescription("Trending TV").setTitle(movie_title+" 480p");
                                    request.setDestinationInExternalFilesDir(getApplicationContext(), "/Realproject/Movies", movie_title+" 480p"+ext);
                                    request.allowScanningByMediaScanner();
                                    request.setVisibleInDownloadsUi(true);
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    mydownloadreferenc = downloadManager.enqueue(request);
                                   // mainActivity.showVideoAd();
                                }

                            });




                                dialog.show();




                });


                btn_720p.setOnClickListener(v -> {
                    ImageButton play_button=(ImageButton)dialog.findViewById(R.id.play_btn);
                    final ImageButton download_btn=(ImageButton)dialog.findViewById(R.id.download_btn);
                    TextView quality_tv=(TextView)dialog.findViewById(R.id.quality_tv);
                    final RadioButton openwith=(RadioButton)dialog.findViewById(R.id.otherid);
                    final RadioButton exoplayer_radio=(RadioButton)dialog.findViewById(R.id.exoplayer_id);
                    openwith.setTypeface(custom_font);
                    exoplayer_radio.setTypeface(custom_font);
                    openwith.setVisibility(View.GONE);
                    exoplayer_radio.setVisibility(View.GONE);

                    Button subtitle_btn=(Button)dialog.findViewById(R.id.subtitle_btn);

                    subtitle_btn.setOnClickListener(v14 -> subtitle_dialog.show());

                    quality_tv.setText("720p");
                    quality_tv.setTypeface(custom_font);

                    play_button.setOnClickListener(v15 -> {
                        progressDialog.show();

                        if(url_720p.equals("null")){
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(!isPackageExisted("org.videolan.vlc")&&!isPackageExisted("com.mxtech.videoplayer.ad")&& !isPackageExisted("com.mxtech.videoplayer.pro")){
                                checkPlayer();
                            }
                            else {
                                final byte b = 2;
                                if(openwith.isChecked()) {
                                    Thread thread=new Thread(() -> {
                                        final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                        try {
                                            openSubtitle.login();
                                        } catch (XmlRpcException e) {
                                            e.printStackTrace();
                                        }



                                        try {

                                            String   s = openSubtitle.getMovieSubsByName(movie_title, "1", lang1);

                                            if(!s.equals("")) {
                                                s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                            }

                                                //  Parcelable[] parcelables={Uri.parse(subTitle)};
                                                Intent    intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(Uri.parse(url_720p), "video/*");
                                                intent.putExtra("subs", new Parcelable[]{Uri.parse(s)});
                                            intent.putExtra("title", movie_title);
                                                intent.putExtra("decode_mode", b);
                                            intent.putExtra("subtitles_location",s);
                                          //  mainActivity.showVideoAd();
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

                                            String   s = openSubtitle.getMovieSubsByName(movie_title, "1", lang1);

                                            if(!s.equals("")) {
                                                s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                            }

                                                Intent   intent=new Intent(Detail_movie.this,VideoPlayer_Activity.class);
                                                intent.putExtra("video_url",url_720p);
                                            intent.putExtra("title", movie_title);
                                                intent.putExtra("subtitle_url",s);
                                            //mainActivity.showVideoAd();
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


                    });


                    download_btn.setOnClickListener(v16 -> {
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
                            request.setDescription("Trending TV").setTitle(movie_title+" 720p");
                            request.setDestinationInExternalFilesDir(getApplicationContext(),"/Realproject/Movies", movie_title+" 720p"+ext);
                            request.allowScanningByMediaScanner();
                            request.setVisibleInDownloadsUi(true);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                            mydownloadreferenc = downloadManager.enqueue(request);
                          //  mainActivity.showVideoAd();
                        }

                    });


                                dialog.show();

                });



                btn_1080p.setOnClickListener(v -> {
                    ImageButton play_button=(ImageButton)dialog.findViewById(R.id.play_btn);
                    final ImageButton download_btn=(ImageButton)dialog.findViewById(R.id.download_btn);
                    TextView quality_tv=(TextView)dialog.findViewById(R.id.quality_tv);
                    final RadioButton openwith=(RadioButton)dialog.findViewById(R.id.otherid);
                    final RadioButton exoplayer_radio=(RadioButton)dialog.findViewById(R.id.exoplayer_id);
                    openwith.setTypeface(custom_font);
                    exoplayer_radio.setTypeface(custom_font);
                    openwith.setVisibility(View.GONE);
                    exoplayer_radio.setVisibility(View.GONE);

                    Button subtitle_btn=(Button)dialog.findViewById(R.id.subtitle_btn);

                    subtitle_btn.setOnClickListener(v17 -> subtitle_dialog.show());

                    quality_tv.setText("1080p");
                    quality_tv.setTypeface(custom_font);
                    play_button.setOnClickListener(v18 -> {
                        progressDialog.show();

                        if(url_1080p.equals("null")){
                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(!isPackageExisted("org.videolan.vlc")&&!isPackageExisted("com.mxtech.videoplayer.ad")&& !isPackageExisted("com.mxtech.videoplayer.pro")){
                                checkPlayer();
                            }
                            else {
                                final byte b = 2;
                                if(openwith.isChecked()) {

                                    Thread thread=new Thread(() -> {
                                        final OpenSubtitle23 openSubtitle = new OpenSubtitle23();


                                        try {
                                            openSubtitle.login();
                                        } catch (XmlRpcException e) {
                                            e.printStackTrace();
                                        }



                                        try {

                                            String   s = openSubtitle.getMovieSubsByName(movie_title, "1", lang1);

                                            if(!s.equals("")) {
                                                s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");
                                            }

                                                Intent    intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(Uri.parse(url_1080p), "video/*");
                                                intent.putExtra("subs", new Parcelable[]{Uri.parse(s)});
                                            intent.putExtra("title", movie_title);
                                                intent.putExtra("decode_mode", b);
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

                                            String   s = openSubtitle.getMovieSubsByName(movie_title, "1", lang1);

                                            if(!s.equals("")) {
                                                s = s.replace(s.substring(s.lastIndexOf(".")), ".srt");

                                            }
                                                Intent   intent=new Intent(Detail_movie.this,VideoPlayer_Activity.class);
                                                intent.putExtra("video_url",url_1080p);
                                            intent.putExtra("title", movie_title);
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


                    });


                    download_btn.setOnClickListener(v19 -> {
                        if (url_1080p.equals("null")){
                            Toast.makeText(getApplicationContext(),"This video is unavailable now",Toast.LENGTH_LONG).show();
                        }

                        else {
                            Toast.makeText(getApplicationContext(),"Download started,see progress in Notification panel",Toast.LENGTH_LONG).show();
                            Uri uri = Uri.parse(url_1080p);
                            String ext=url_1080p.substring(url_1080p.lastIndexOf("."));
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                            request.setDescription("Trending TV").setTitle(movie_title+" 1080p");
                            request.setDestinationInExternalFilesDir(getApplicationContext(),"/Realproject/Movies", movie_title+" 1080p"+ext);
                            request.allowScanningByMediaScanner();
                            request.setVisibleInDownloadsUi(true);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                            mydownloadreferenc = downloadManager.enqueue(request);
                           // mainActivity.showVideoAd();
                        }

                    });


                                dialog.show();

                });


                 }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {

        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"App Name");
        intent.putExtra(Intent.EXTRA_TEXT,"Download and Watch movie '"+movie_title+"' and more movies and TV shows on your Android for free. App name- link");
        startActivity(Intent.createChooser(intent,"Share Via"));


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
    protected void onRestart() {
        super.onRestart();
       // mainActivity.showIntersititialAd();
        mainActivity.showVideoAd();



    }





}
