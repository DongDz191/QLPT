package com.example.duan1.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.duan1.R;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ItemAccountBinding;
import com.example.duan1.model.Account;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {
    private Context context;
    private ImageView editImgAccount;
    private String strImage;
    private List<Account> list;
    private ApiService apiService;
    private static final String TAG = "AccountAdapter";

    public AccountAdapter(Context context, List<Account> list) {
        Log.d(TAG, "AccountAdapter initialized with " + list.size() + " accounts");
        this.context = context;
        this.list = list;
        this.apiService = ApiClient.getApiService();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        return new MyViewHolder(ItemAccountBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position " + position);
        Account account = list.get(position);
        try {
            holder.binding.imgAccount.setImageURI(Uri.parse(account.getImage().trim()));
            Log.d(TAG, "Loaded image for account: " + account.getUsername() + ", image URI: " + account.getImage());
        } catch (Exception e) {
            Log.e(TAG, "Error loading image for account: " + account.getUsername() + ", Error: " + e.getMessage(), e);
            Toast.makeText(context, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        holder.binding.tvuser.setText(account.getUsername());
        holder.binding.tvpass.setText(account.getPassword());
        holder.binding.tvname.setText(account.getName());
        holder.binding.tvphone.setText(account.getPhone());
        holder.binding.tvtitle.setText(account.getTitle());

        holder.binding.imvDelete.setOnClickListener(v -> {
            Log.d(TAG, "Delete button clicked for account: " + account.getUsername());
            deleteAccount(account, position);
        });

        holder.binding.imgEdit.setOnClickListener(v -> {
            Log.d(TAG, "Edit button clicked for account: " + account.getUsername());
            editAccount(account, position);
        });
    }

    private void deleteAccount(Account account, int position) {
        Log.d(TAG, "Deleting account via API: " + account.getUsername());
        apiService.deleteAccount(account.getUsername()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Account deleted successfully: " + account.getUsername());
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    Toast.makeText(context, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Lỗi khi xóa tài khoản: HTTP Code: " + response.code() + ", Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                        }
                    }
                    Log.d(TAG, "Raw response: " + response.raw().toString());
                    Toast.makeText(context, "Lỗi khi xóa tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi xóa tài khoản: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editAccount(Account account, int position) {
        Log.d(TAG, "Opening edit dialog for account: " + account.getUsername());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_edit_account, null);
        builder.setView(view);

        EditText edtuser = view.findViewById(R.id.edtuser);
        EditText edtpass = view.findViewById(R.id.edtPass);
        EditText edtname = view.findViewById(R.id.edtName);
        EditText edtphone = view.findViewById(R.id.edtPhone);
        editImgAccount = view.findViewById(R.id.editImgAccount);

        edtuser.setText(account.getUsername());
        edtpass.setText(account.getPassword());
        edtname.setText(account.getName());
        edtphone.setText(account.getPhone());
        editImgAccount.setImageURI(Uri.parse(account.getImage()));
        editImgAccount.setOnClickListener(v -> {
            Log.d(TAG, "Edit image clicked");
            addImage();
        });

        builder.setNegativeButton("Sửa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Confirm edit button clicked for account: " + account.getUsername());
                String edituser = edtuser.getText().toString().trim();
                String editpass = edtpass.getText().toString().trim();
                String editname = edtname.getText().toString().trim();
                String editphone = edtphone.getText().toString().trim();
                String strImg = strImage != null ? strImage : account.getImage();

                account.setImage(strImg);
                account.setUsername(edituser);
                account.setPassword(editpass);
                account.setName(editname);
                account.setPhone(editphone);
                Log.d(TAG, "Updated account details: " + account.toString());

                apiService.updateAccount(account.getUsername(), account).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Account updated successfully: " + account.getUsername());
                            list.set(position, account);
                            loadData();
                            Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Lỗi khi cập nhật tài khoản: HTTP Code: " + response.code() + ", Message: " + response.message());
                            if (response.errorBody() != null) {
                                try {
                                    Log.e(TAG, "Chi tiết lỗi từ errorBody: " + response.errorBody().string());
                                } catch (Exception e) {
                                    Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                                }
                            }
                            Log.d(TAG, "Raw response: " + response.raw().toString());
                            Toast.makeText(context, "Lỗi khi cập nhật tài khoản: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối API khi cập nhật tài khoản: " + t.getMessage(), t);
                        Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Edit dialog closed without saving");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemAccountBinding binding;

        public MyViewHolder(ItemAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void addImage() {
        Log.d(TAG, "Adding image via TedImagePicker");
        RequestManager requestManager = Glide.with(context);
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "Permissions granted for image picker");
                TedImagePicker.with(context).start(uri -> {
                    requestManager.load(uri).into(editImgAccount);
                    Log.i(TAG, "Image selected: Uri: " + uri.toString());
                    strImage = uri.toString();
                });
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Log.w(TAG, "Permissions denied: " + deniedPermissions.toString());
                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission, you cannot use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .check();
    }

    private void loadData() {
        Log.d(TAG, "Refreshing data in adapter");
        notifyDataSetChanged();
    }
}