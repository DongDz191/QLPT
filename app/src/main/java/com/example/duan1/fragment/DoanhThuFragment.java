package com.example.duan1.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.duan1.R;
import com.example.duan1.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;


public class DoanhThuFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPageAdapter viewPageAdapter;



    public DoanhThuFragment() {
        // Required empty public constructor
    }


    public static DoanhThuFragment newInstance(String param1, String param2) {
        DoanhThuFragment fragment = new DoanhThuFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_doanh_thu, container, false);
        return v;
    }
}