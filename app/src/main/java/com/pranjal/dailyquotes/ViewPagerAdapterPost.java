package com.pranjal.dailyquotes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterPost extends FragmentStateAdapter{

    public ViewPagerAdapterPost(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = QuotesFragment.newInstance();
                break;

            case 1:
                fragment = PostFragment.newInstance();
                break;

        }
        return fragment;
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}