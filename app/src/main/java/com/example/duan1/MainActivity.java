package com.example.duan1;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.duan1.databinding.ActivityMainBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.Room;
import com.example.duan1.model.RoomType;
import com.example.duan1.model.Service;
import com.example.duan1.model.SessionAccount;
import com.example.duan1.model.Utility;
import com.example.duan1.model.UtilityDetail;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityResultLauncher<String[]> permissionRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SessionAccount sessionManage = new SessionAccount(this);
        sessionManage.dropAccount();
        //Insert data
        try {
        }catch (Exception e){
            Toast.makeText(this, "Loi insert du lieu vao database!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        requestPermissions();
    }

    private void requestPermissions() {
        TedPermission.Builder builderTed = TedPermission.create();
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Chú ý");
                builder.setMessage("Bạn cần cấp quyền thì mới sử dụng được ứng dụng");
                builder.setNegativeButton("Cấp quyến", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    builderTed.check();
                });
                builder.setPositiveButton("Thoát", (dialogInterface, i) -> System.exit(0));
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        builderTed.setPermissionListener(permissionlistener)
        .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        .check();
    }

}
