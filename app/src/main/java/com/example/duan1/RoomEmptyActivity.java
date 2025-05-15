package com.example.duan1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityRoomEmptyBinding;
import com.example.duan1.model.Room;
import com.example.duan1.model.RoomType;
import com.example.duan1.model.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomEmptyActivity extends AppCompatActivity {
    ActivityRoomEmptyBinding binding;
    ApiService apiService;
    RoomType roomType;

    private static final String TAG = "RoomEmptyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        binding = ActivityRoomEmptyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();

        Room room = (Room) getIntent().getSerializableExtra("room");
        if (room == null) {
            Log.e(TAG, "Room object is null!");
            Toast.makeText(this, "Không tìm thấy thông tin phòng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Log the Room object details
        Log.d(TAG, "Room object: " + room.toString());
        Log.d(TAG, "Room Code: " + room.getRoomCode());
        Log.d(TAG, "Room IdRoomType: " + room.getIdRoomType());
        Log.d(TAG, "Room Image: " + room.getImage());
        Log.d(TAG, "Room floor: " + room.getFloor());
        Log.d(TAG, "Room description: " + room.getDescribe());

        // Validate and fix the Room object if necessary
        if (room.getRoomCode() == null || room.getIdRoomType() == 0 || room.getDescribe() == null || room.getImage() == null) {
            Log.w(TAG, "Room object has missing or invalid fields, attempting to fetch from API");
            fetchRoomFromApi(room.getRoomCode());
        } else {
            proceedWithRoom(room);
        }
    }

    private void fetchRoomFromApi(String roomCode) {
        if (roomCode == null) {
            Log.e(TAG, "RoomCode is null, cannot fetch from API");
            Toast.makeText(this, "Mã phòng không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Fetching Room from API with roomCode: " + roomCode);
        // Sử dụng getRoomByRoomCode thay vì checkRoom
        apiService.getRoomByRoomCode(roomCode).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Room updatedRoom = response.body();
                    Log.d(TAG, "Updated Room object from API: " + updatedRoom.toString());
                    proceedWithRoom(updatedRoom);
                } else {
                    Log.e(TAG, "Failed to fetch Room from API. Response code: " + response.code());
                    Toast.makeText(RoomEmptyActivity.this, "Không thể lấy thông tin phòng từ API! Response code: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, "API error while fetching Room: " + t.getMessage());
                Toast.makeText(RoomEmptyActivity.this, "Lỗi kết nối API khi lấy thông tin phòng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void proceedWithRoom(Room room) {
        Log.d(TAG, "Proceeding with Room: " + room.toString());
        binding.setRoom(room);

        // Load RoomType from API
        loadRoomType(room);

        // btn Back
        binding.imgBack.setOnClickListener(view -> finish());

        // Image room
        if (room.getImage() != null && !room.getImage().isEmpty()) {
            Log.d(TAG, "Loading room image: " + room.getImage());
            try {
                Glide.with(this).load(Uri.parse(room.getImage())).into(binding.imgRoom);
            } catch (Exception e) {
                Log.e(TAG, "Glide error while loading room image: " + e.getMessage());
                Toast.makeText(this, "Không thể tải ảnh phòng!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "Room image is null or empty");
        }

        // Delete
        binding.btnDeleteRoom.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            builder.setTitle("Xóa phòng");
            builder.setMessage("Bạn có chắc xóa phòng?");
            builder.setNegativeButton("Xác nhận", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                Log.d(TAG, "Attempting to delete room with roomCode: " + room.getRoomCode());
                apiService.deleteRoom(room.getRoomCode()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Room deleted successfully");
                            Toast.makeText(RoomEmptyActivity.this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Failed to delete room. Response code: " + response.code());
                            Toast.makeText(RoomEmptyActivity.this, "Lỗi xóa phòng: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "API error while deleting room: " + t.getMessage());
                        Toast.makeText(RoomEmptyActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setPositiveButton("Hủy bỏ", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.create().show();
        });

        // Create contract
        binding.btnCreateContract.setOnClickListener(view -> {
            Log.d(TAG, "Navigating to CreateContractActivity with room: " + room.getRoomCode());
            Intent intent = new Intent(this, CreateContractActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });
    }

    private void loadRoomType(Room room) {
        Log.d(TAG, "Loading RoomType for idRoomType: " + room.getIdRoomType());
        apiService.getRoomTypeById(room.getIdRoomType()).enqueue(new Callback<RoomType>() {
            @Override
            public void onResponse(Call<RoomType> call, Response<RoomType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    roomType = response.body();
                    Log.d(TAG, "RoomType loaded: " + roomType.toString());
                    binding.setRoomType(roomType);
                    showUtility();
                } else {
                    Log.e(TAG, "Failed to load RoomType. Response code: " + response.code());
                    Toast.makeText(RoomEmptyActivity.this, "Loại phòng không tồn tại! Vui lòng kiểm tra dữ liệu.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RoomType> call, Throwable t) {
                Log.e(TAG, "API error while loading RoomType: " + t.getMessage());
                Toast.makeText(RoomEmptyActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showUtility() {
        if (roomType == null) {
            Log.w(TAG, "RoomType is null, cannot load utilities");
            return;
        }

        Log.d(TAG, "Loading utilities for RoomType ID: " + roomType.getIdRoomType());
        apiService.getUtilitiesByRoomType(roomType.getIdRoomType()).enqueue(new Callback<List<Utility>>() {
            @Override
            public void onResponse(Call<List<Utility>> call, Response<List<Utility>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Utility> utilityList = response.body();
                    Log.d(TAG, "Utilities loaded: " + utilityList.size() + " items");
                    for (Utility u : utilityList) {
                        Log.d(TAG, "Utility ID: " + u.getIdUtility() + ", Name: " + u.getName());
                        switch (u.getIdUtility()) {
                            case 1:
                                binding.imgMoneyBag.setImageResource(R.drawable.money_bag2);
                                break;
                            case 2:
                                binding.imgAlarm.setImageResource(R.drawable.alarm2);
                                break;
                            case 3:
                                binding.imgCCTV.setImageResource(R.drawable.cctv_camera2);
                                break;
                            case 4:
                                binding.imgCooking.setImageResource(R.drawable.cooking2);
                                break;
                            case 5:
                                binding.imgFan.setImageResource(R.drawable.fan2);
                                break;
                            case 6:
                                binding.imgBed.setImageResource(R.drawable.bed2);
                                break;
                            case 7:
                                binding.imgFidge.setImageResource(R.drawable.fridge2);
                                break;
                            case 8:
                                binding.imgTelevision.setImageResource(R.drawable.television2);
                                break;
                            case 9:
                                binding.imgWifi.setImageResource(R.drawable.wifi2);
                                break;
                            case 10:
                                binding.imgMotorcycle.setImageResource(R.drawable.motorcycle2);
                                break;
                            case 11:
                                binding.imgWashingMachine.setImageResource(R.drawable.washing_machine2);
                                break;
                            case 12:
                                binding.imgAirConditioner.setImageResource(R.drawable.air_conditioner2);
                                break;
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to load utilities. Response code: " + response.code());
                    Toast.makeText(RoomEmptyActivity.this, "Lỗi khi lấy danh sách tiện ích: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Utility>> call, Throwable t) {
                Log.e(TAG, "API error while loading utilities: " + t.getMessage());
                Toast.makeText(RoomEmptyActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}