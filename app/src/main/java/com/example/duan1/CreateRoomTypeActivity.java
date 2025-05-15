package com.example.duan1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.base.BaseCheckValid;
import com.example.duan1.databinding.ActivityCreateRoomTypeBinding;
import com.example.duan1.model.RoomType;
import com.example.duan1.model.UtilityDetail;
import com.example.duan1.model.UtilityStatus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRoomTypeActivity extends AppCompatActivity {

    ActivityCreateRoomTypeBinding binding;
    ApiService apiService;
    UtilityStatus utilityStatus = new UtilityStatus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateRoomTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService();

        binding.imgAlarm.setOnClickListener(v -> hanldelUtilityClick(1));
        binding.imgAirConditioner.setOnClickListener(v -> hanldelUtilityClick(2));
        binding.imgMotorcycle.setOnClickListener(v -> hanldelUtilityClick(3));
        binding.imgMoneyBag.setOnClickListener(v -> hanldelUtilityClick(4));
        binding.imgCCTV.setOnClickListener(v -> hanldelUtilityClick(5));
        binding.imgFan.setOnClickListener(v -> hanldelUtilityClick(6));
        binding.imgCooking.setOnClickListener(v -> hanldelUtilityClick(7));
        binding.imgTelevision.setOnClickListener(v -> hanldelUtilityClick(8));
        binding.imgBed.setOnClickListener(v -> hanldelUtilityClick(9));
        binding.imgFidge.setOnClickListener(v -> hanldelUtilityClick(10));
        binding.imgWashingMachine.setOnClickListener(v -> hanldelUtilityClick(11));
        binding.imgWifi.setOnClickListener(v -> hanldelUtilityClick(12));

        binding.imgBack.setOnClickListener(view -> finish());

        binding.btnSave.setOnClickListener(v -> {
            String nameTypeRoom = binding.edten.getText().toString();
            String numberElectronic = binding.eddien.getText().toString();
            String numberWater = binding.ednuoc.getText().toString();
            String rentCost = binding.edgiaphong.getText().toString();
            String area = binding.edArea.getText().toString();

            if (BaseCheckValid.checkEmptyString(nameTypeRoom, numberElectronic, numberWater, rentCost, area)) {
                RoomType roomType = new RoomType(
                        nameTypeRoom,
                        Integer.parseInt(rentCost),
                        Integer.parseInt(area),
                        Integer.parseInt(numberElectronic),
                        Integer.parseInt(numberWater)
                );

                // Gọi API để thêm RoomType
                apiService.insertRoomType(roomType).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Gọi API để lấy RoomType mới nhất và lấy idRoomType
                            apiService.getRoomTypeNewest().enqueue(new Callback<RoomType>() {
                                @Override
                                public void onResponse(Call<RoomType> call, Response<RoomType> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        int idRoomType = response.body().getIdRoomType();

                                        handleInsertUtility(idRoomType);
                                    } else {
                                        Toast.makeText(CreateRoomTypeActivity.this, "Lỗi khi lấy RoomType mới nhất: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<RoomType> call, Throwable t) {
                                    Toast.makeText(CreateRoomTypeActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(CreateRoomTypeActivity.this, "Thêm loại phòng thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(CreateRoomTypeActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Vui lòng không để trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleInsertUtility(int idRoomType) {
        List<UtilityDetail> utilityDetailsList = new ArrayList<>();
        if (UtilityStatus.deposit) utilityDetailsList.add(new UtilityDetail(idRoomType, 1));
        if (UtilityStatus.comfortableTime) utilityDetailsList.add(new UtilityDetail(idRoomType, 2));
        if (UtilityStatus.cctv) utilityDetailsList.add(new UtilityDetail(idRoomType, 3));
        if (UtilityStatus.cooking) utilityDetailsList.add(new UtilityDetail(idRoomType, 4));
        if (UtilityStatus.fan) utilityDetailsList.add(new UtilityDetail(idRoomType, 5));
        if (UtilityStatus.bed) utilityDetailsList.add(new UtilityDetail(idRoomType, 6));
        if (UtilityStatus.fridge) utilityDetailsList.add(new UtilityDetail(idRoomType, 7));
        if (UtilityStatus.television) utilityDetailsList.add(new UtilityDetail(idRoomType, 8));
        if (UtilityStatus.wifi) utilityDetailsList.add(new UtilityDetail(idRoomType, 9));
        if (UtilityStatus.parking) utilityDetailsList.add(new UtilityDetail(idRoomType, 10));
        if (UtilityStatus.washingMachine) utilityDetailsList.add(new UtilityDetail(idRoomType, 11));
        if (UtilityStatus.airConditioning) utilityDetailsList.add(new UtilityDetail(idRoomType, 12));

        if (utilityDetailsList.isEmpty()) {
            Toast.makeText(CreateRoomTypeActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gọi API để thêm từng UtilityDetail
        for (UtilityDetail utilityDetail : utilityDetailsList) {
            apiService.insertUtilityDetail(utilityDetail).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Kiểm tra xem đây có phải là UtilityDetail cuối cùng không
                        if (utilityDetail == utilityDetailsList.get(utilityDetailsList.size() - 1)) {
                            Toast.makeText(CreateRoomTypeActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(CreateRoomTypeActivity.this, "Thêm tiện ích thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CreateRoomTypeActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void hanldelUtilityClick(int i) {
        switch (i) {
            case 1:
                if (UtilityStatus.comfortableTime) {
                    binding.imgAlarm.setImageResource(R.drawable.alarm);
                    UtilityStatus.comfortableTime = false;
                } else {
                    binding.imgAlarm.setImageResource(R.drawable.alarm2);
                    UtilityStatus.comfortableTime = true;
                }
                break;
            case 2:
                if (UtilityStatus.airConditioning) {
                    binding.imgAirConditioner.setImageResource(R.drawable.air_conditioner);
                    UtilityStatus.airConditioning = false;
                } else {
                    binding.imgAirConditioner.setImageResource(R.drawable.air_conditioner2);
                    UtilityStatus.airConditioning = true;
                }
                break;
            case 3:
                if (UtilityStatus.parking) {
                    binding.imgMotorcycle.setImageResource(R.drawable.motorcycle);
                    UtilityStatus.parking = false;
                } else {
                    binding.imgMotorcycle.setImageResource(R.drawable.motorcycle2);
                    UtilityStatus.parking = true;
                }
                break;
            case 4:
                if (UtilityStatus.deposit) {
                    binding.imgMoneyBag.setImageResource(R.drawable.money_bag);
                    UtilityStatus.deposit = false;
                } else {
                    binding.imgMoneyBag.setImageResource(R.drawable.money_bag2);
                    UtilityStatus.deposit = true;
                }
                break;
            case 5:
                if (UtilityStatus.cctv) {
                    binding.imgCCTV.setImageResource(R.drawable.cctv_camera);
                    UtilityStatus.cctv = false;
                } else {
                    binding.imgCCTV.setImageResource(R.drawable.cctv_camera2);
                    UtilityStatus.cctv = true;
                }
                break;
            case 6:
                if (UtilityStatus.fan) {
                    binding.imgFan.setImageResource(R.drawable.fan);
                    UtilityStatus.fan = false;
                } else {
                    binding.imgFan.setImageResource(R.drawable.fan2);
                    UtilityStatus.fan = true;
                }
                break;
            case 7:
                if (UtilityStatus.cooking) {
                    binding.imgCooking.setImageResource(R.drawable.cooking);
                    UtilityStatus.cooking = false;
                } else {
                    binding.imgCooking.setImageResource(R.drawable.cooking2);
                    UtilityStatus.cooking = true;
                }
                break;
            case 8:
                if (UtilityStatus.television) {
                    binding.imgTelevision.setImageResource(R.drawable.television);
                    UtilityStatus.television = false;
                } else {
                    binding.imgTelevision.setImageResource(R.drawable.television2);
                    UtilityStatus.television = true;
                }
                break;
            case 9:
                if (UtilityStatus.bed) {
                    binding.imgBed.setImageResource(R.drawable.bed);
                    UtilityStatus.bed = false;
                } else {
                    binding.imgBed.setImageResource(R.drawable.bed2);
                    UtilityStatus.bed = true;
                }
                break;
            case 10:
                if (UtilityStatus.fridge) {
                    binding.imgFidge.setImageResource(R.drawable.fridge);
                    UtilityStatus.fridge = false;
                } else {
                    binding.imgFidge.setImageResource(R.drawable.fridge2);
                    UtilityStatus.fridge = true;
                }
                break;
            case 11:
                if (UtilityStatus.washingMachine) {
                    binding.imgWashingMachine.setImageResource(R.drawable.washing_machine);
                    UtilityStatus.washingMachine = false;
                } else {
                    binding.imgWashingMachine.setImageResource(R.drawable.washing_machine2);
                    UtilityStatus.washingMachine = true;
                }
                break;
            case 12:
                if (UtilityStatus.wifi) {
                    binding.imgWifi.setImageResource(R.drawable.wifi);
                    UtilityStatus.wifi = false;
                } else {
                    binding.imgWifi.setImageResource(R.drawable.wifi2);
                    UtilityStatus.wifi = true;
                }
                break;
        }
    }
}