package com.example.duan1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.duan1.fragment.ContractFragment;
import com.example.duan1.fragment.InvoiceFragment;
import com.example.duan1.fragment.MemberFragment;
import com.example.duan1.fragment.ServiceFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new ServiceFragment();
            case 1: return new MemberFragment();
            case 2: return new InvoiceFragment();
            default: return new ContractFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
