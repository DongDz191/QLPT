package com.example.duan1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.duan1.fragmentContract.EffectFragment;
import com.example.duan1.fragmentContract.ExpireFragment;
import com.example.duan1.fragmentContract.WaitFragment;

public class ViewPager2Adapter extends FragmentStateAdapter {
    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new WaitFragment();
            case 1: return new EffectFragment();
            default: return new ExpireFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
