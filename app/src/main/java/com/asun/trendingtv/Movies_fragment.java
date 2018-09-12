package com.asun.trendingtv;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class Movies_fragment extends Fragment {
    //DatabaseReference mDatabaseLike,mDatabaseReference,mDatabaseBookMark;


    public Movies_fragment(){

}
    public static Movies_fragment newInstance(){
        return new Movies_fragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_movie_tab, container, false);
       // mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Likes");
        //mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("movies");
        //mDatabaseBookMark=FirebaseDatabase.getInstance().getReference().child("movies_bookmark");
        //mDatabaseBookMark.keepSynced(false);
        //mDatabaseReference.keepSynced(false);
        //mDatabaseLike.keepSynced(false);


        TVshowSectionsPagerAdapter mSectionsPagerAdapter = new TVshowSectionsPagerAdapter(getChildFragmentManager());
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.tv_show_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tv_show_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return view;
    }
    private class TVshowSectionsPagerAdapter extends FragmentPagerAdapter {


        TVshowSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {



            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below)
            switch (position) {
                case 0:
                    return new Movies_All_Tab();
                case 1:
                    return new Movie_action_tab();
                case 2:
                    return new Movies_adventure_tab();
                case 3:
                    return new Movie_bollywood_tab();
                case 4:
                    return new Movie_animation_tab();
                case 5:
                    return new Movie_comedy_tab();
                case 6:
                    return new Movie_sci_tab();
                case 7:
                    return new Movie_crime_tab();
                case 8:
                    return new Movie_drama_tab();
                case 9:
                    return new Movie_fantasy_tab();
                case 10:
                    return new Movie_horror_tab();
                case 11:
                    return new Movie_mystery_tab();
                case 12:
                    return new Movie_thriller_tab();
                case 13:
                    return new Movie_fav_tab();

                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return 14;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All";
                case 1:
                    return "Action";
                case 2:
                    return "Adventure";
                case 3:
                    return "bollywood";
                case 4:
                    return "Animation";
                case 5:
                    return "Comedy";
                case 6:
                    return "Sci-fi";
                case 7:
                    return "Crime";
                case 8:
                    return "Drama";
                case 9:
                    return "Fantasy";
                case 10:
                    return "Horror";
                case 11:
                    return "Mystery";
                case 12:
                    return "Thriller";
                case 13:
                    return "Favorites";
            }
            return null;
        }
    }


}
