package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.duan1.FragmentNav.HomeFragmnet;
import com.example.duan1.FragmentNav.InfoAccount;
import com.example.duan1.FragmentNav.rePassAccount;
import com.example.duan1.FragmentNav.DoanhThuFragment;
import com.learnoset.material.ui.learnosetnavigationbar.LearnosetNavItem;
import com.learnoset.material.ui.learnosetnavigationbar.LearnosetNavigationBar;
import com.learnoset.material.ui.learnosetnavigationbar.NavigationEventListener;

import java.util.List;

public class MenuMainActivity extends AppCompatActivity {

    ImageView imgNav;

    DrawerLayout drawerLayout;
    LearnosetNavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);

        navigationBar = findViewById(R.id.navigatioNabr);

        drawerLayout = findViewById(R.id.drawerlayout);

        imgNav = findViewById(R.id.navdrawer);
        LearnosetNavItem homeItem = new LearnosetNavItem();
        homeItem.setTitle("Trang Chủ");
        homeItem.setIcon(com.learnoset.learnosetbottombar.R.drawable.home_icon);
        homeItem.setFragment(new HomeFragmnet(), R.id.fragmentcontainer);
        homeItem.setSelected(true);
        navigationBar.addNavItem(homeItem);

        LearnosetNavItem profileItem = new LearnosetNavItem();
        profileItem.setTitle("Thông Tin Tài Khoản");
        profileItem.setIcon(com.learnoset.learnosetbottombar.R.drawable.profile_icon);
        profileItem.setFragment(new InfoAccount(), R.id.fragmentcontainer);
        navigationBar.addNavItem(profileItem);


        LearnosetNavItem rePassItem = new LearnosetNavItem();
        rePassItem.setTitle("Đổi Mật Khẩu");
        rePassItem.setIcon(com.learnoset.learnosetbottombar.R.drawable.privacy_policy_icon);
        rePassItem.setFragment(new rePassAccount(), R.id.fragmentcontainer);
        navigationBar.addNavItem(rePassItem);




        navigationBar.setEventListener(new NavigationEventListener() {
            @Override
            public void onItemSelected(int position, LearnosetNavItem selectedNavItem) {
                Log.i("TAG", "onItemSelected: " + position);
            }

            @Override
            public void onLogOutBtnClick() {
                Intent intent = new Intent(MenuMainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        navigationBar.setDrawerLayout(drawerLayout, LearnosetNavigationBar.DrawerGravity.LEFT);

        navigationBar.setHeaderData("Quản Trị", R.drawable.banh_my_trung);
        imgNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
}
