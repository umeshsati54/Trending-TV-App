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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class You_Tube extends Fragment {
    TabLayout tabLayout;
    DatabaseReference mDatabaseReference;

public You_Tube(){

}
    public static You_Tube newInstance(){
        return new You_Tube();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_you__tube, container, false);
        // Inflate the layout for this fragment
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("youtube").child("all_vedio");
       // mDatabaseReference.keepSynced(true);


        YouTubeSectionsPagerAdapter mSectionsPagerAdapter = new YouTubeSectionsPagerAdapter(getChildFragmentManager());
        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.youtube_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) view.findViewById(R.id.youtube_tabs);
        tabLayout.setupWithViewPager(mViewPager);


        return view;
    }
    private class YouTubeSectionsPagerAdapter extends FragmentPagerAdapter {


        YouTubeSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {



            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below)
            switch (position) {
                case 0:

                    return new Youtube_All();
                case 1:
                    return new Youtube_fragmen2();
                case 2:
                    return new Yutube_fragment3();
                case 3:
                    return new Youtube_fragment4();
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All";
                case 1:
                    return "Action";
                case 2:
                    return "Comedy";
                case 3:
                    return "Drama";
            }
            return null;
        }
    }


}
