package com.sp.socialapp.ui.tabUiViewPager2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sp.socialapp.R;
import com.sp.socialapp.ui.tab_fragments.ImageFragment;
import com.sp.socialapp.ui.tab_fragments.VideoFragments;

public class DemoCollectionAdapter extends FragmentStateAdapter {
    public DemoCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    public DemoCollectionAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public static int[] tabs = {R.string.tab_text_1,R.string.tab_text_2,R.string.tab_text_3};

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment ;

        switch (position){
            case 0: return new ImageFragment();
            case 1: return new VideoFragments();
            case 2: return new DemoObjectFragment();
            default: fragment = new DemoObjectFragment();
        }

        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(DemoObjectFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return tabs.length;
    }
}
