package com.asun.trendingtv;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private AlertDialog.Builder builder;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog mProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // DatabaseReference  mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Likes");
    //DatabaseReference  mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("tvshow");
    //DatabaseReference  mDatabaseBookMark=FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark");
    //DatabaseReference  mDatabaseReferenceMovie = FirebaseDatabase.getInstance().getReference().child("movies");
    //DatabaseReference  mDatabaseBookMarkMovieBookmark=FirebaseDatabase.getInstance().getReference().child("movies_bookmark");
    private static final DatabaseReference tvDatabaseReference = FirebaseDatabase.getInstance().getReference().child("tvshow");
    private static final DatabaseReference movieDatabaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
    private static final DatabaseReference movieBookmarkReference = FirebaseDatabase.getInstance().getReference().child("movies_bookmark");
    private static final DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child("Likes");
    private static final DatabaseReference tvBookmarkReference = FirebaseDatabase.getInstance().getReference().child("tvshow_bookmark");
    private static final DatabaseReference mLatestTVDatabaseReference = FirebaseDatabase.getInstance().getReference().child("latest_tvshow");
    private static final DatabaseReference mLatestHollywoodMovieReference = FirebaseDatabase.getInstance().getReference().child("latest_hollywood");
    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT = 0;
    private final static int MULTIPLE_PERMISSION = 10;
    static InterstitialAd mInterstitialAd;
    boolean per;
    private static   RewardedVideoAd rewardedVideoAd;


    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        MobileAds.initialize(this, "ca-app-pub-7968822252801553~1331251025");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7968822252801553/1490224620");
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);

        rewardedVideoAd.loadAd("ca-app-pub-7968822252801553/7238183821", new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());


            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());

            }
        });

        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);



        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            per= Settings.System.canWrite(MainActivity.this);
        }

        if (!per){
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                Intent intent=new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:"+MainActivity.this.getPackageName()));
                MainActivity.this.startActivityForResult(intent,20);
            }

        }


        //  new Handler().postDelayed(new Runnable() {
        //  @Override
        //    public void run() {
               if((ContextCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                  PackageManager.PERMISSION_GRANTED)) {

                }
                else {
                    checkReadPermission();
                }


        //       }
        // },20000);


        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(savedInstanceState);


        mAuthStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {


                tvDatabaseReference.keepSynced(true);
                movieDatabaseReference.keepSynced(true);
                movieBookmarkReference.keepSynced(true);
                tvBookmarkReference.keepSynced(true);
                likeReference.keepSynced(true);
                mLatestTVDatabaseReference.keepSynced(true);
                mLatestHollywoodMovieReference.keepSynced(true);


                FirebaseDatabase.getInstance().goOnline();


            }


        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, connectionResult -> Toast.makeText(getApplicationContext(), "Sign in Failed, TRY AGAIN", Toast.LENGTH_LONG).show()).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        if (mAuth.getCurrentUser() == null) {
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Starting Sign In....");

            mProgressDialog.show();

            signIn();
        }


    }

    private void loadActivity() {
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        navigation.setOnNavigationItemSelectedListener(item -> {
            android.support.v4.app.Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_tvshows:
                    selectedFragment = TVSHOWSFragment.newInstance();
                    break;
                case R.id.navigation_movies:
                    selectedFragment = Movies_fragment.newInstance();

                    break;
               /* case R.id.navigation_youtube:
                    selectedFragment= You_Tube.newInstance();
                    break; */
            }
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameid, selectedFragment);
            transaction.commit();

            return true;
        });
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameid, HomeFragment.newInstance());
        transaction.commit();
//this is
        if (mAuth.getCurrentUser() != null) {
            if (!isNetworkAvaiable()) {
                builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("No Internet Connection");
                builder.setMessage("Abe Garib Internet Nahi Hai Kya,Internet ON kr aur fir Retry daba");
                builder.setPositiveButton("Retry", (dialog, which) -> loadActivity());
                // builder.show();


                Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private boolean isNetworkAvaiable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    @Override
    protected void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgressDialog.setCancelable(false);

            mProgressDialog.setMessage("Starting Sign In....");

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                checkUserExist();


            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "failed to login", Toast.LENGTH_LONG).show();
                signIn();
            }
        }

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        mProgressDialog.dismiss();
                        checkUserExist();


                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();


                    }


                    // ...
                });
    }

    private void signIn() {

        if (!isNetworkAvaiable()) {
            builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("SignIn Failed");
            builder.setMessage("Hey, Don't worry just open the app again and check INTERNET connection");
            builder.show();


            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();

        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);


        }

    }

    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);

    }

    public void showIntersititialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

        }

    }

    public void showVideoAd() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();

        }

        else
            rewardedVideoAd.loadAd("ca-app-pub-7968822252801553/7238183821", new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());

    }


    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();


    }



  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    checkReadPermission();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {

            } else {


                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }


        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();


    }


    @Override
    public void onRewardedVideoAdLoaded() {

        Toast.makeText(this,"adloaded",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this,"onRewardedVideoAdOpened",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        rewardedVideoAd.loadAd("ca-app-pub-7968822252801553/7238183821",new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());


    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        rewardedVideoAd.loadAd("ca-app-pub-7968822252801553/7238183821",new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

        Toast.makeText(this,"failed to load",Toast.LENGTH_SHORT).show();
        rewardedVideoAd.loadAd("ca-app-pub-7968822252801553/7238183821",new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());


    }
}
