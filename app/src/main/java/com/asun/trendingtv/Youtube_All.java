package com.asun.trendingtv;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Youtube_All extends Fragment {

    ImageButton close_btn;
    Parcelable state;
   static String video_id;
    RecyclerView recyclerView;
    All_Tab.MLinearLayoutManager mLayoutManager;
    public DatabaseReference mDatabaseReference;
    public DatabaseReference mDatabaseLike;
    public DatabaseReference mDatabaseBookMark;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    private static final int ANIMATION_DURATION_MILLIS = 450;


    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private View videoBox;
    YouTubePlayer player;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    @SuppressLint("StaticFieldLeak")
    public static ImageView youTubeThumbnailView;

    public Youtube_All() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_youtube__all, container,false);
        final View view1=inflater.inflate(R.layout.youtube_all_list,container,false);


        final NativeExpressAdView adView=(NativeExpressAdView)view.findViewById(R.id.native_ad_id_youtube_all);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                adView.setVisibility(View.GONE);

            }
        });
        adView.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());



        youTubeThumbnailView = (ImageView) view1.findViewById(R.id.thumbnail);
       youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.unique);
        Toolbar toolbar=(Toolbar)view.findViewById(R.id.toolbar_youtube_all);
        toolbar.setTitle("Search Here>>>>");
        progressBar=(ProgressBar)view.findViewById(R.id.progress_id_youtube_all);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("youtube").child("all_vedio");
       // mDatabaseReference.keepSynced(true);
        recyclerView=(RecyclerView)view.findViewById(R.id.youtube_all_tab_recyclerview);
        mLayoutManager=new All_Tab.MLinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        videoBox = view.findViewById(R.id.video_box);
        videoBox.setVisibility(View.INVISIBLE);
        player=new YouTubePlayer() {
            @Override
            public void release() {

            }

            @Override
            public void cueVideo(String s) {

            }

            @Override
            public void cueVideo(String s, int i) {

            }

            @Override
            public void loadVideo(String s) {

            }

            @Override
            public void loadVideo(String s, int i) {

            }

            @Override
            public void cuePlaylist(String s) {

            }

            @Override
            public void cuePlaylist(String s, int i, int i1) {

            }

            @Override
            public void loadPlaylist(String s) {

            }

            @Override
            public void loadPlaylist(String s, int i, int i1) {

            }

            @Override
            public void cueVideos(List<String> list) {

            }

            @Override
            public void cueVideos(List<String> list, int i, int i1) {

            }

            @Override
            public void loadVideos(List<String> list) {

            }

            @Override
            public void loadVideos(List<String> list, int i, int i1) {

            }

            @Override
            public void play() {

            }

            @Override
            public void pause() {

            }

            @Override
            public boolean isPlaying() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public void next() {

            }

            @Override
            public void previous() {

            }

            @Override
            public int getCurrentTimeMillis() {
                return 0;
            }

            @Override
            public int getDurationMillis() {
                return 0;
            }

            @Override
            public void seekToMillis(int i) {

            }

            @Override
            public void seekRelativeMillis(int i) {

            }

            @Override
            public void setFullscreen(boolean b) {

            }

            @Override
            public void setOnFullscreenListener(OnFullscreenListener onFullscreenListener) {

            }

            @Override
            public void setFullscreenControlFlags(int i) {

            }

            @Override
            public int getFullscreenControlFlags() {
                return 0;
            }

            @Override
            public void addFullscreenControlFlag(int i) {

            }

            @Override
            public void setPlayerStyle(PlayerStyle playerStyle) {

            }

            @Override
            public void setShowFullscreenButton(boolean b) {

            }

            @Override
            public void setManageAudioFocus(boolean b) {

            }

            @Override
            public void setPlaylistEventListener(PlaylistEventListener playlistEventListener) {

            }

            @Override
            public void setPlayerStateChangeListener(PlayerStateChangeListener playerStateChangeListener) {

            }

            @Override
            public void setPlaybackEventListener(PlaybackEventListener playbackEventListener) {

            }
        };
        checkYouTubeApi();
        close_btn=(ImageButton)view.findViewById(R.id.close_button);
        close_btn.setOnClickListener(this::onClickClose);


        FirebaseRecyclerAdapter<Tvshow, YoutubeAllholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Tvshow, YoutubeAllholder>(
                Tvshow.class,
                R.layout.youtube_all_list,
                YoutubeAllholder.class,
                mDatabaseReference


        ) {
            @Override
            public void populateViewHolder(final YoutubeAllholder viewHolder, Tvshow model, final int position) {

                final String videokey = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());

               // viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                mDatabaseReference.keepSynced(true);
                progressBar.setVisibility(View.GONE);

                mDatabaseReference.child(videokey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        viewHolder.setThumbnail(dataSnapshot.child("video_id").getValue().toString());



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });


                viewHolder.view.setOnClickListener(v -> {
                    mDatabaseReference.child(videokey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                       String key =     getRef(position).getKey();
                            video_id=dataSnapshot.child("video_id").getValue().toString();
                        String s=    dataSnapshot.getKey();


                            VideoFragment videoFragment =
                                    (VideoFragment) getChildFragmentManager().findFragmentById(R.id.unique);
                            videoFragment.setVideoId(video_id);
                            if(s.equals(key)){
                                viewHolder.view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            }
                                viewHolder.view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if (videoBox.getVisibility() != View.VISIBLE) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            // Initially translate off the screen so that it can be animated in from below.
                            videoBox.setTranslationY(videoBox.getHeight());
                        }
                        videoBox.setVisibility(View.VISIBLE);
                    }

                    // If the fragment is off the screen, we animate it in.
                    if (videoBox.getTranslationY() > 0) {
                        videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
                    }


                });



            }


        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;

    }


    // @Override
  //  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

  //      youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
   //     youTubePlayer.setPlaybackEventListener(playbackEventListener);
   //     if(!b){
   //         youTubePlayer.cueVideo(video_id);
   //     }

  //  }

  //  @Override
   // public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

//    }

    public static class YoutubeAllholder extends RecyclerView.ViewHolder {
        View view;
        YouTubeThumbnailView youTubeThumbnailView;
        private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
        private final ThumbnailListener thumbnailListener;


        public YoutubeAllholder(View itemView) {
            super(itemView);
            view = itemView;
            youTubeThumbnailView=(YouTubeThumbnailView)view.findViewById(R.id.thumbnail);
            thumbnailViewToLoaderMap = new HashMap<>();
            thumbnailListener = new ThumbnailListener();
        }



        public void setTitle(String title) {
            TextView titletextView = (TextView) view.findViewById(R.id.video_title);
            titletextView.setText(title);
        }


        public void setImage(Context imgctx, String image) {
            ImageView tvimage = (ImageView) view.findViewById(R.id.imageid);
            Picasso.with(imgctx).load(image).into(tvimage);

        }
        void setThumbnail(String video_id){
            youTubeThumbnailView.setTag(video_id);
            youTubeThumbnailView.initialize(DeveloperKey.DEVELOPER_KEY,thumbnailListener);
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(youTubeThumbnailView);
            if (loader == null) {
                // 2) The view is already created, and is currently being initialized. We store the
                //    current videoId in the tag.
                youTubeThumbnailView.setTag(video_id);
            } else {
                // 3) The view is already created and already initialized. Simply set the right videoId
                //    on the loader.
                youTubeThumbnailView.setImageResource(R.drawable.active_dot);
                loader.setVideo(video_id);}


        }
        private final class ThumbnailListener implements
                YouTubeThumbnailView.OnInitializedListener,
                YouTubeThumbnailLoader.OnThumbnailLoadedListener {

            @Override
            public void onInitializationSuccess(
                    YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
                loader.setOnThumbnailLoadedListener(this);
                thumbnailViewToLoaderMap.put(view, loader);
                view.setImageResource(R.drawable.no_thumbnail);
                String videoId = (String) view.getTag();
                loader.setVideo(videoId);
            }

            @Override
            public void onInitializationFailure(
                    YouTubeThumbnailView view, YouTubeInitializationResult loader) {
                view.setImageResource(R.drawable.no_thumbnail);

            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {

            }

            @Override
            public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
                view.setImageResource(R.drawable.no_thumbnail);
            }
        }

    }
    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getContext());
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog((Activity) getContext(), RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }
    public void onClickClose(@SuppressWarnings("unused") View view) {
        player.pause();
        ViewPropertyAnimator animator = videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(ANIMATION_DURATION_MILLIS);
        runOnAnimationEnd(animator, () -> videoBox.setVisibility(View.INVISIBLE));
    }
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 17) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        // Toast.makeText(getContext(),"onPause save"+state,Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (state != null) {
            mLayoutManager.onRestoreInstanceState(state);
            // Toast.makeText(getContext(),"onResume save"+state,Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        youTubePlayerFragment=(YouTubePlayerSupportFragment)getChildFragmentManager().findFragmentById(R.id.unique);
        if(youTubePlayerFragment!=null){
            getChildFragmentManager().beginTransaction().remove(youTubePlayerFragment).commitAllowingStateLoss();

        }
        youTubePlayerFragment=null;


    }
    public static final class VideoFragment extends YouTubePlayerSupportFragment
            implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initialize(DeveloperKey.DEVELOPER_KEY, this);


        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.cueVideo(videoId);
                }
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            if (!restored && videoId != null) {
                player.cueVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }




    }




}
