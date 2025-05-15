package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.duan1.adapter.ViewPager2Adapter;
import com.example.duan1.databinding.ActivityContractManageBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class ContractManageActivity extends AppCompatActivity {
    final String arrTitle[] = new String[]{"Chờ xác nhận", "Hiệu lực", "Hết hạn"};
    ActivityContractManageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContractManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set viewpager tablayout
        ViewPager2Adapter pagerAdapter = new ViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(pagerAdapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(arrTitle[position]);
        });
        mediator.attach();

        binding.imgBack.setOnClickListener(view -> finish());
    }
}