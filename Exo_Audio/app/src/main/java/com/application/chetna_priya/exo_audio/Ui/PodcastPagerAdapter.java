package com.application.chetna_priya.exo_audio.Ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.chetna_priya.exo_audio.R;

class PodcastPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = PodcastPagerAdapter.class.getSimpleName();
    private Context mContext;

    PodcastPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a MyPlaceholderFragment (defined as a static inner class below).
      //  Log.d(TAG, "INSTANIATEEEEEEEEEEEE "+position);
        switch (position){
            case 1:
                return new FeaturedFragment();
            case 2:
                return new CategoriesFragment();
            case 3:
                return new NetworksFragment();
        }
        return null;
     //   return MyPlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.featured);
            case 1:
                return mContext.getString(R.string.categories);
            case 2:
                return mContext.getString(R.string.networks);
        }
        return null;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MyPlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public MyPlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MyPlaceholderFragment newInstance(int sectionNumber) {
            MyPlaceholderFragment fragment = new MyPlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            Log.d(TAG, "CREATEEEEEEEEEEE VIEWWWWWWWWWWWWWWWWWWWWW"+rootView);
            return rootView;
        }
    }
}

