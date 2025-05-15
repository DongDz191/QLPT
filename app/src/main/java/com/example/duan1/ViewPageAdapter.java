package com.example.duan1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.duan1.fragment.DichvuFragment;
import com.example.duan1.fragment.DoanhThuFragment;

public class ViewPageAdapter extends FragmentStatePagerAdapter {

    public ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

           case 0:
               return new DoanhThuFragment() ;

           case 1:
               return  new DichvuFragment();
           default:
               return new DoanhThuFragment() ;

       }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = "";
        switch (position){

            case 0:
                title = "Doanh Thu";
                break;

            case 1:
                title = "Top Dịch Vụ";
                break;

        }

        return title;

    }
}


//
//    @Override
//    public int getItemCount() {
//        return 2;
//    }
//
//    public void setAdapter(ViewPageAdapter viewPageAdapter) {
//    }
//
//
//    @Nullable
//    @Override
//    public CharSequence getItemViewType(int position) {
//        String title = "";
//        switch (position){
//
//            case 0:
//                title = "Doanh Thu";
//                break;
//
//            case 1:
//                title = "Top Dịch Vụ";
//                break;
//
//        }
//
//        return title;
//    }
//}
