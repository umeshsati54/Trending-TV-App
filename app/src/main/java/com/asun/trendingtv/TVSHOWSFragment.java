package com.asun.trendingtv;


import android.content.Context;
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


public class TVSHOWSFragment extends All_Tab {
    TabLayout tabLayout;
    Context mContext;


    public TVSHOWSFragment(){

    }

    public static TVSHOWSFragment newInstance(){
        return new TVSHOWSFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_tv_show, container, false);


        MoviesSectionsPagerAdapter mSectionsPagerAdapter = new MoviesSectionsPagerAdapter(getChildFragmentManager());
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.movie_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) view.findViewById(R.id.movies_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setHasOptionsMenu(true);








        return view;
    }




    private class MoviesSectionsPagerAdapter extends FragmentPagerAdapter {


        MoviesSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {



            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below)
            switch (position) {
                case 0:

                    return new All_Tab();
                case 1:
                    return new TV_Action_Tab();
                    case 2:
                        return new TV_adventure();
                    case 3:
                        return new TV_comedy();
                    case 4:
                        return new TV_sci_fi();

                case 5:
                    return new TV_crime();
                case 6:
                    return new TV_drama();

                case 7:
                    return new TV_fantasy();

                case 8:
                    return new TV_horror();

                case 9:
                    return new TV_mystery();

                case 10:
                    return new TV_thriller();

                case 11:
                    return new TV_animation();


                case 12:
                    return new Fav_Tab();


                    default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 13;
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
                    return "Comedy";
                case 4:
                    return "Sci-Fi";
                case 5:
                    return "Crime";
                case 6:
                    return "Drama";
                case 7:
                    return "Fantasy";
                case 8:
                    return "Horror";
                case 9:
                    return "Mystery";
                case 10:
                    return "Thriller";
                case 11:
                    return "Animation";
                case 12:
                    return "Favorites";

            }
            return null;
        }
    }


}
