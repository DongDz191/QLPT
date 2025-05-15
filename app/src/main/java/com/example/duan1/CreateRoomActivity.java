package com.example.duan1;

import static com.example.duan1.base.BaseCheckValid.checkEmptyString;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1.adapter.SpRoomTypeAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityCreateRoomBinding;
import com.example.duan1.model.Room;
import com.example.duan1.model.RoomType;

import java.util.ArrayList;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRoomActivity extends AppCompatActivity {

    ActivityCreateRoomBinding binding;
    ApiService apiService;
    String pathImage = "";
    SpRoomTypeAdapter spRoomTypeAdapter;
    List<RoomType> roomTypeList = new ArrayList<>();
    List<String> listUtility = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();
        spRoomTypeAdapter = new SpRoomTypeAdapter(this, roomTypeList);
        binding.spRoomType.setAdapter(spRoomTypeAdapter);

        // Lấy danh sách RoomType từ API
        loadRoomTypes();

        binding.spRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hanleItemSpinnerSelect(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.imgBack.setOnClickListener(view -> finish());
        binding.btnType.setOnClickListener(v -> startActivity(new Intent(CreateRoomActivity.this, CreateRoomTypeActivity.class)));

        binding.imgRoom.setOnClickListener(v -> {
            TedImagePicker.with(CreateRoomActivity.this).start(uri -> {
                Glide.with(CreateRoomActivity.this).load(uri).into(binding.imgRoom);
                binding.imgRoom.setMaxHeight(250);
                pathImage = uri.toString();
            });
        });

        binding.btnCreateRoom.setOnClickListener(v -> {
            String floor = binding.edtang.getText().toString();
            String codeRoom = binding.edRoomcode.getText().toString();
            String describe = binding.edDescribe.getText().toString();
            RoomType roomType = binding.getRoomType();

            if (checkEmptyString(floor, codeRoom, describe, pathImage)) {
                if (roomType == null) {
                    Toast.makeText(CreateRoomActivity.this, "Vui lòng chọn loại phòng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Room room = new Room(codeRoom, Integer.parseInt(floor), describe, pathImage, roomType.getIdRoomType());
                apiService.insertRoom(room).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CreateRoomActivity.this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateRoomActivity.this, "Mã phòng bị trùng!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CreateRoomActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CreateRoomActivity.this, "Bạn vui lòng không bỏ trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRoomTypes() {
        apiService.getAllRoomTypes().enqueue(new Callback<List<RoomType>>() {
            @Override
            public void onResponse(Call<List<RoomType>> call, Response<List<RoomType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    roomTypeList.clear();
                    roomTypeList.addAll(response.body());
                    spRoomTypeAdapter.notifyDataSetChanged();

                    if (roomTypeList.size() > 0) {
                        hanleItemSpinnerSelect(0);
                    }
                } else {
                    Toast.makeText(CreateRoomActivity.this, "Lỗi khi lấy danh sách loại phòng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RoomType>> call, Throwable t) {
                Toast.makeText(CreateRoomActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hanleItemSpinnerSelect(int position) {
        RoomType roomType = roomTypeList.get(position);
        binding.setRoomType(roomType);

        // Lấy danh sách tiện ích từ API
        apiService.getUtilityNamesByRoomType(roomType.getIdRoomType()).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listUtility.clear();
                    listUtility.addAll(response.body());
                    String utility = "";
                    for (String s : listUtility) {
                        utility += s + ", ";
                    }
                    if (!utility.isEmpty()) {
                        utility = utility.substring(0, utility.length() - 2);
                    }
                    binding.tvListUtility.setText("Tiện ích: " + utility);
                } else {
                    binding.tvListUtility.setText("Tiện ích: Không có tiện ích");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                binding.tvListUtility.setText("Tiện ích: Lỗi khi lấy dữ liệu");
                Toast.makeText(CreateRoomActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoomTypes();
    }
}