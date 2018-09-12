package com.asun.trendingtv;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.plus.PlusOneButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
   private ViewPager viewPager;
    private static final DatabaseReference trendingDatabaseReference= FirebaseDatabase.getInstance().getReference().child("trending");
    private static long count;
    private static String[]   image_urls;
    private FragmentActivity fragmentActivity=new MainActivity();
    private LinearLayout sliderDotspanel;
    private ImageView[] dots;
    private static final DatabaseReference mTrendingTvshowDatabaseReference=FirebaseDatabase.getInstance().getReference().child("tvshow");
    private static final DatabaseReference mTrendingHollywoodMovieReference=FirebaseDatabase.getInstance().getReference().child("movies");
    private static final DatabaseReference requestMovieReference=FirebaseDatabase.getInstance().getReference().child("requests").child("movies");
    private ImageView blur_tv_image,blur_movie_image;
    private static final DatabaseReference mLatestTVDatabaseReference=FirebaseDatabase.getInstance().getReference().child("latest_tvshow");
    private static final DatabaseReference mLatestHollywoodMovieReference=FirebaseDatabase.getInstance().getReference().child("latest_hollywood");
    private ImageView tv_image,movie_image;
    private ProgressBar progressBar,movieProgressBar;
    private TextView single_TV_title,single_hollywood_movie_title;
    private TextView tv_info,hollywood_movie_info;
   private PlusOneButton mPlusOneButton;
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    private static final String url="https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad";
   private NativeExpressAdView adView2;
    private static final FirebaseAuth mFirebaseAuth=FirebaseAuth.getInstance();
    private static final DatabaseReference updateReference=FirebaseDatabase.getInstance().getReference().child("VERSION");
    private TextView update_text2;


    public HomeFragment(){

    }

public static Fragment newInstance(){
    return new HomeFragment();

}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_home, container, false);


        final RelativeLayout update_layout=(RelativeLayout)view.findViewById(R.id.update_layout);
        update_text2=(TextView)view.findViewById(R.id.update_text2);
        Button no_thanks = (Button) view.findViewById(R.id.nothanks_btn);
        no_thanks.setOnClickListener(v -> {
            YoYo.with(Techniques.FadeOutLeft).playOn(update_layout);
            update_layout.setVisibility(View.GONE);
        });

        updateReference.keepSynced(false);


        PackageManager packageManager=getContext().getPackageManager();
        try {
            PackageInfo info=packageManager.getPackageInfo(getContext().getPackageName(),0);
            final String versionCurrent=info.versionName;

            updateReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        String update_version = dataSnapshot.child("update").getValue().toString();
                    update_text2.setText("For best experience, update to latest v"+update_version);

                    if(!versionCurrent.equals(update_version)){
                        YoYo.with(Techniques.FadeInDown).playOn(update_layout);
                        update_layout.setVisibility(View.VISIBLE);
                    }
                    else {
                        update_layout.setVisibility(View.GONE);

                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        // mPlusOneButton = (PlusOneButton) view.findViewById(R.id.plus_one_button);
        Typeface custom_font = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/canaro_extra_bold.otf");
        TextView trendin_movie_textview=(TextView)view.findViewById(R.id.trending_movie_textview);
        trendin_movie_textview.setTypeface(custom_font);

        final TextView ttvtv=(TextView)view.findViewById(R.id.ttvtv);
        final TextView tmtv=(TextView)view.findViewById(R.id.tmtv);
        TextView toolbar_text =(TextView)view.findViewById(R.id.home_toolbar_text);
        toolbar_text.setTypeface(custom_font);
        toolbar_text.setText("TRENDING");
        ttvtv.setTypeface(custom_font);
        tmtv.setTypeface(custom_font);

        final Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_dialog);
        ImageView dialog_imageview=(ImageView)dialog.findViewById(R.id.profile_image_dialog);
        final TextView  email_textview=(TextView)dialog.findViewById(R.id.email_textview);
        final EditText  request_edittext=(EditText)dialog.findViewById(R.id.request_edittext);
        final Button    requeet_btn=(Button)dialog.findViewById(R.id.request_btn);
        TextView trending=(TextView)dialog.findViewById(R.id.trending);
        TextView must_watch=(TextView)dialog.findViewById(R.id.must_watch);
        TextView airing=(TextView)dialog.findViewById(R.id.airing);
        trending.setTypeface(custom_font);
        must_watch.setTypeface(custom_font);
        airing.setTypeface(custom_font);


        requeet_btn.setTypeface(custom_font);


        ImageView profile_image = (ImageView) view.findViewById(R.id.profile_image);
        if(mFirebaseAuth.getCurrentUser()!=null) {
            String profile_photo = mFirebaseAuth.getCurrentUser().getPhotoUrl().toString();
            String email_id=mFirebaseAuth.getCurrentUser().getDisplayName();
            email_textview.setText(email_id);
            email_textview.setTypeface(custom_font);
            request_edittext.setTypeface(custom_font);

            Picasso.with(profile_image.getContext()).load(profile_photo).error(R.drawable.account_circle).placeholder(R.drawable.account_circle).transform(new CropCircleTransformation()).into(profile_image);
            Picasso.with(dialog_imageview.getContext()).load(profile_photo).error(R.drawable.account_circle).placeholder(R.drawable.account_circle).transform(new CropCircleTransformation()).into(dialog_imageview);
        }

        profile_image.setOnClickListener(v -> dialog.show());

        requeet_btn.setOnClickListener(v -> {
            if(request_edittext.getText().toString().trim().length()==0){
                YoYo.with(Techniques.Tada).playOn(request_edittext);

            }
            else {

                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Sending request....");
                    progressDialog.setCancelable(false);
                final String s=request_edittext.getText().toString();
                request_edittext.clearComposingText();
                request_edittext.clearFocus();
                request_edittext.setText("");
                    progressDialog.show();
                if(mFirebaseAuth.getCurrentUser()!=null) {

                    requestMovieReference.push().setValue(s + " -->> " +"'"+mFirebaseAuth.getCurrentUser().getEmail()+"' "+"'"+ FirebaseInstanceId.getInstance().getToken()+"'");
                }

                new Handler().postDelayed(() -> {
                    try {
                        progressDialog.dismiss();

                    }

                    finally{

                        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setTitle("Request sent");
                        builder.setMessage("Request is accepted for '" +s+ "', priority will be given to most requested movies and TV shows");

                        builder.setPositiveButton("ok", (dialog1, which) -> dialog1.cancel());
                        builder.show();


                    }

                },2000);


            }
        });







        adView2=(NativeExpressAdView)view.findViewById(R.id.home_native_id);




        final AdRequest adRequest=new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build();

        adView2.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adView2.setVisibility(View.GONE);

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView2.loadAd(adRequest);


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
                adView2.setVisibility(View.VISIBLE);
            }
        });
        adView2.loadAd(adRequest);




        single_hollywood_movie_title=(TextView)view.findViewById(R.id.single_hollywood_movie_title);
        hollywood_movie_info=(TextView)view.findViewById(R.id.hollywood_movie_info);
        single_hollywood_movie_title.setVisibility(View.GONE);
        hollywood_movie_info.setVisibility(View.GONE);

        TextView trending_tv_textview = (TextView) view.findViewById(R.id.trending_tv_textview);

        single_hollywood_movie_title.setTypeface(custom_font);
        hollywood_movie_info.setTypeface(custom_font);
        trending_tv_textview.setTypeface(custom_font);



        trending_tv_textview.setTextColor(getResources().getColor(R.color.white));
        mLatestHollywoodMovieReference.keepSynced(true);

        mLatestTVDatabaseReference.keepSynced(true);
        trendingDatabaseReference.keepSynced(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        GridLayoutManager mgridLayoutManager = new GridLayoutManager(getContext(), 1);
        viewPager=(ViewPager)view.findViewById(R.id.viewpager) ;
        sliderDotspanel =(LinearLayout)view.findViewById(R.id.dots);

        RecyclerView trendingTvRecyclerView = (RecyclerView) view.findViewById(R.id.trendingTv_recycler);
        trendingTvRecyclerView.setLayoutManager(mgridLayoutManager);
        RecyclerView trendingMovieRecyclerView = (RecyclerView) view.findViewById(R.id.trending_hollywood_movies_recycler);
        trendingMovieRecyclerView.setLayoutManager(gridLayoutManager);

        mgridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);

        trendingMovieRecyclerView.setHasFixedSize(true);
        trendingMovieRecyclerView.setNestedScrollingEnabled(true);



        trendingTvRecyclerView.setHasFixedSize(true);
        trendingTvRecyclerView.setNestedScrollingEnabled(false);
        blur_tv_image=(ImageView)view.findViewById(R.id.blur_tv_image);
        tv_image=(ImageView)view.findViewById(R.id.tv_image);
        movie_image=(ImageView)view.findViewById(R.id.hollywood_movie_image);

        blur_movie_image=(ImageView)view.findViewById(R.id.blur_hollywood_image);

        progressBar=(ProgressBar)view.findViewById(R.id.tv_recycler_progress);

        movieProgressBar=(ProgressBar)view.findViewById(R.id.movie_recycler_progress);

        single_TV_title=(TextView)view.findViewById(R.id.single_TV_title);
        tv_info=(TextView)view.findViewById(R.id.tv_info);
        single_TV_title.setVisibility(View.GONE);
        tv_info.setVisibility(View.GONE);
        single_TV_title.setTypeface(custom_font);
        tv_info.setTypeface(custom_font);



        mTrendingHollywoodMovieReference.keepSynced(true);
        mTrendingTvshowDatabaseReference.keepSynced(true);







        trendingDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                count= dataSnapshot.getChildrenCount();


              image_urls   = new String[]{dataSnapshot.child("1").getValue().toString(),
                            dataSnapshot.child("2").getValue().toString(),
                            dataSnapshot.child("3").getValue().toString(),
                            dataSnapshot.child("4").getValue().toString(),
                            dataSnapshot.child("5").getValue().toString(),
                            dataSnapshot.child("6").getValue().toString()
              };

                class ViewPagerAdapter extends PagerAdapter {
                    private Context context;
                    private LayoutInflater layoutInflater;
                    private ViewPagerAdapter(Context context) {
                        this.context = context;
                    }

                    @Override
                    public int getCount() {
                        return 6;
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view==object;
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, final int position) {
                        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                       View view=layoutInflater.inflate(R.layout.custom_layout,null);
                        final ImageView imageView=(ImageView)view.findViewById(R.id.image_id);

                        Picasso.with(context).load(String.valueOf(image_urls[position])).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(context).load(String.valueOf(image_urls[position])).into(imageView);


                            }
                        });



                      view.setOnClickListener(v -> {
                          if(position == 0){
                              String key1=dataSnapshot.child("1key").getValue().toString();
                              Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                              i.putExtra("tv_id", key1);
                              startActivity(i);

                          } else if(position == 1){
                              String key2=dataSnapshot.child("2key").getValue().toString();
                              Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                              i.putExtra("movie_id", key2);
                              startActivity(i);

                          } else if(position==2) {
                              String key3=dataSnapshot.child("3key").getValue().toString();
                              Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                              i.putExtra("tv_id", key3);
                              startActivity(i);

                          }
                         else if(position==3) {
                              String key4=dataSnapshot.child("4key").getValue().toString();
                              Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                              i.putExtra("movie_id", key4);
                              startActivity(i);

                          }
                          else if(position==4) {
                              String key5=dataSnapshot.child("5key").getValue().toString();
                              Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                              i.putExtra("tv_id", key5);
                              startActivity(i);

                          }
                          else  {
                              String key6=dataSnapshot.child("6key").getValue().toString();
                              Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                              i.putExtra("movie_id", key6);
                              startActivity(i);

                          }



                      });
                        ViewPager vp=(ViewPager)container;
                        vp.addView(view,0);
                        return view;
                    }

                    @Override
                    public void destroyItem(ViewGroup container, int position, Object object) {
                        ViewPager vp=(ViewPager)container;
                        View view=(View)object;
                        vp.removeView(view);
                    }
                }

                ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getContext());
                viewPagerAdapter.notifyDataSetChanged();
                viewPager.setAdapter(viewPagerAdapter);
                dots = new ImageView[6];
                for(int i = 0; i < 6; i++){

                    dots[i] = new ImageView(viewPager.getContext());
                    dots[i].setImageResource(R.drawable.nonactive_dot);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }
                dots[0].setImageResource(R.drawable.active_dot);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                        for(int i = 0; i< 6; i++){
                            dots[i].setImageResource(R.drawable.nonactive_dot);
                        }

                        dots[position].setImageResource(R.drawable.active_dot);

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                Timer timer=new Timer();
                timer.scheduleAtFixedRate(new MyTimer(),4000,4500);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLatestHollywoodMovieReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String moviekey=  dataSnapshot.child("key").getValue().toString();
                final String title=dataSnapshot.child("title").getValue().toString();
                final String info=dataSnapshot.child("info").getValue().toString();
                final String image1=dataSnapshot.child("image").getValue().toString();

                Picasso.with(blur_movie_image.getContext()).load(image1).transform(new BlurTransformation(blur_movie_image.getContext(),26)).networkPolicy(NetworkPolicy.OFFLINE).into(blur_movie_image, new Callback() {
                    @Override
                    public void onSuccess() {


                    }

                    @Override
                    public void onError() {

                        Picasso.with(blur_movie_image.getContext()).load(image1).transform(new BlurTransformation(blur_movie_image.getContext(),26)).into(blur_movie_image);                    }
                });

                Picasso.with(movie_image.getContext()).load(image1).transform(new RoundedCornersTransformation(5,5)).networkPolicy(NetworkPolicy.OFFLINE).into(movie_image, new Callback() {
                    @Override
                    public void onSuccess() {


                        single_hollywood_movie_title.setText(title);
                        hollywood_movie_info.setText(info);
                        single_hollywood_movie_title.setVisibility(View.VISIBLE);
                        hollywood_movie_info.setVisibility(View.VISIBLE);
                        tmtv.setVisibility(View.VISIBLE);



                    }

                    @Override
                    public void onError() {

                        Picasso.with(movie_image.getContext()).load(image1).transform(new RoundedCornersTransformation(5,5)).into(movie_image);
                        single_hollywood_movie_title.setText(title);
                        hollywood_movie_info.setText(info);
                        single_hollywood_movie_title.setVisibility(View.VISIBLE);
                        hollywood_movie_info.setVisibility(View.VISIBLE);
                        tmtv.setVisibility(View.VISIBLE);

                    }
                });


                movie_image.setOnClickListener(v -> {
                    Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                    i.putExtra("movie_id", moviekey);
                    startActivity(i);

                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mLatestTVDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String tvkey=  dataSnapshot.child("key").getValue().toString();
                final String title=dataSnapshot.child("title").getValue().toString();
                final String info=dataSnapshot.child("info").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                // mLatestHollywoodMovieReference.keepSynced(true);

                Picasso.with(blur_tv_image.getContext()).load(image).transform(new BlurTransformation(blur_tv_image.getContext(),26)).networkPolicy(NetworkPolicy.OFFLINE).into(blur_tv_image, new Callback() {
                    @Override
                    public void onSuccess() {


                    }

                    @Override
                    public void onError() {

                        Picasso.with(blur_tv_image.getContext()).load(image).transform(new BlurTransformation(blur_tv_image.getContext(),26)).into(blur_tv_image);                    }
                });

                Picasso.with(tv_image.getContext()).load(image).transform(new RoundedCornersTransformation(5,5)).networkPolicy(NetworkPolicy.OFFLINE).into(tv_image, new Callback() {
                    @Override
                    public void onSuccess() {


                        single_TV_title.setText(title);
                        tv_info.setText(info);
                        single_TV_title.setVisibility(View.VISIBLE);
                        tv_info.setVisibility(View.VISIBLE);
                        tmtv.setVisibility(View.VISIBLE);



                    }

                    @Override
                    public void onError() {

                        Picasso.with(tv_image.getContext()).load(image).transform(new RoundedCornersTransformation(5,5)).into(tv_image);
                        single_TV_title.setText(title);
                        tv_info.setText(info);
                        single_TV_title.setVisibility(View.VISIBLE);
                        tv_info.setVisibility(View.VISIBLE);
                        tmtv.setVisibility(View.VISIBLE);

                    }
                });


                tv_image.setOnClickListener(v -> {
                    Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                    i.putExtra("tv_id", tvkey);
                    startActivity(i);

                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        final Query query=mTrendingTvshowDatabaseReference.orderByChild("trending").equalTo("yes");
        FirebaseRecyclerAdapter<Tvshow, TrendingTvshowholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, TrendingTvshowholder>(
                Tvshow.class,
                R.layout.trending_tvshow_movie_image,
                TrendingTvshowholder.class,
                query
        ) {
            @Override
            public void populateViewHolder(TrendingTvshowholder viewHolder, Tvshow model, int position) {
                final String tvkey = getRef(position).getKey();


                viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setShawdowTitle(model.getTitle());
                progressBar.setVisibility(View.GONE);
                viewHolder.view.setOnClickListener(v -> {

                    //  Toast.makeText(getApplicationContext(),tvkey,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity().getApplicationContext(), DetailTVshow.class);
                    i.putExtra("tv_id", tvkey);
                    startActivity(i);





                });



            }

        };
        trendingTvRecyclerView.setAdapter(firebaseRecyclerAdapter);

        final Query query1=mTrendingHollywoodMovieReference.orderByChild("trending").equalTo("yes");
        FirebaseRecyclerAdapter<Tvshow, TrendingTvshowholder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<Tvshow, TrendingTvshowholder>(
                Tvshow.class,
                R.layout.trending_tvshow_movie_image,
                TrendingTvshowholder.class,
                query1
        ) {
            @Override
            public void populateViewHolder(TrendingTvshowholder viewHolder, Tvshow model, int position) {
                final String moviekey = getRef(position).getKey();
                //query1.keepSynced(true);


                viewHolder.setImage(getContext(), model.getImage());
                viewHolder.setShawdowTitle(model.getTitle());
                movieProgressBar.setVisibility(View.GONE);
               // mTrendingTvshowDatabaseReference.keepSynced(true);
                viewHolder.view.setOnClickListener(v -> {

                    //  Toast.makeText(getApplicationContext(),tvkey,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity().getApplicationContext(), Detail_movie.class);
                    i.putExtra("movie_id", moviekey);
                    startActivity(i);





                });



            }

        };
        trendingMovieRecyclerView.setAdapter(firebaseRecyclerAdapter1);


        return view;
    }
    private class MyTimer extends TimerTask {

        @Override
        public void run() {
            fragmentActivity.runOnUiThread(() -> {
                if(viewPager.getCurrentItem()==0){
                    viewPager.setCurrentItem(1);
                }
                else if(viewPager.getCurrentItem()==1){
                    viewPager.setCurrentItem(2);
                }
                else if(viewPager.getCurrentItem()==2){
                    viewPager.setCurrentItem(3);
                }
                else if(viewPager.getCurrentItem()==3){
                    viewPager.setCurrentItem(4);
                }
                else if(viewPager.getCurrentItem()==4){
                    viewPager.setCurrentItem(5);
                }
                else {
                    viewPager.setCurrentItem(0);
                }           });
        }
    }
    public static class TrendingTvshowholder extends RecyclerView.ViewHolder {
        View view;


        public TrendingTvshowholder(View itemView) {
            super(itemView);
            view = itemView;
            }



        public void setImage(final Context imgctx, final String image) {
            final ImageView tvimage = (ImageView) view.findViewById(R.id.trending_tv_image);
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

        public void setShawdowTitle(String title){
           Typeface custom_font= Typeface.createFromAsset(view.getContext().getAssets(),  "fonts/canaro_extra_bold.otf");

            TextView textView=(TextView)view.findViewById(R.id.shawdow_textview);
            textView.setText(title);
            textView.setTypeface(custom_font);

        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}



