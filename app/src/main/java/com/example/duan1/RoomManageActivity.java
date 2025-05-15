package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.example.duan1.adapter.FloorAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityRoomManageBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.Floor;
import com.example.duan1.model.Room;
import com.example.duan1.model.SessionAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomManageActivity extends AppCompatActivity {
    ActivityRoomManageBinding binding;
    List<Room> listRoom;
    List<Floor> listFloor;
    FloorAdapter floorAdapter;
    ApiService apiService;
    Account account;
    private static final String TAG = "RoomManageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");
        binding = ActivityRoomManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiService = ApiClient.getApiService();

        // Fetch account from SessionAccount
        SessionAccount sessionAccount = new SessionAccount(this);
        account = sessionAccount.fetchAccount();
        if (account == null) {
            Log.e(TAG, "Account is null!");
            Toast.makeText(this, "Không tìm thấy thông tin tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "Account fetched: " + account.toString());

        binding.imgBack.setOnClickListener(view -> finish());

        listRoom = new ArrayList<>();
        listFloor = new ArrayList<>();

        fetchRooms();

        floorAdapter = new FloorAdapter(this, listFloor, (room, status) -> {
            Toast.makeText(this, "Phòng được chọn: " + room.getRoomCode(), Toast.LENGTH_SHORT).show();
            // status true - room available, false - room empty
            Intent intent;
            if (status) {
                intent = new Intent(this, RoomAvalibleActivity.class);
                intent.putExtra("roomCode", room.getRoomCode());
                intent.putExtra("account", account);
                Log.d(TAG, "Starting RoomAvalibleActivity with roomCode: " + room.getRoomCode() + ", account: " + account.toString());
            } else {
                intent = new Intent(this, RoomEmptyActivity.class);
                intent.putExtra("room", room);
                Log.d(TAG, "Starting RoomEmptyActivity with room: " + room.toString());
            }
            startActivity(intent);
        });

        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.rv.setLayoutManager(manager);
        binding.rv.setAdapter(floorAdapter);

        binding.cboFilter.setOnCheckedChangeListener((compoundButton, b) -> {
            String keyword = binding.edSearch.getText().toString();
            fetchRoomsWithFilterOrSearch(keyword, b);
        });

        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String keyword = charSequence.toString();
                fetchRoomsWithFilterOrSearch(keyword, binding.cboFilter.isChecked());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.fabAdd.setOnClickListener(v -> startActivity(new Intent(this, CreateRoomActivity.class)));
    }

    private void fetchRooms() {
        Log.d(TAG, "Fetching all rooms from API");
        apiService.getAllRooms().enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listRoom.clear();
                    listRoom.addAll(response.body());
                    Log.d(TAG, "Fetched " + listRoom.size() + " rooms from API");
                    subListRoom();
                    floorAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Lỗi khi lấy danh sách phòng: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage());
                        }
                    }
                    Toast.makeText(RoomManageActivity.this, "Lỗi khi lấy danh sách phòng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy danh sách phòng: " + t.getMessage());
                Toast.makeText(RoomManageActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRoomsWithFilterOrSearch(String keyword, boolean isFilter) {
        Call<List<Room>> call;
        if (isFilter) {
            Log.d(TAG, "Filtering rooms with keyword: " + keyword);
            call = apiService.filterRoom(keyword);
        } else {
            Log.d(TAG, "Searching rooms with keyword: " + keyword);
            call = apiService.searchRoom(keyword);
        }

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listRoom.clear();
                    listRoom.addAll(response.body());
                    Log.d(TAG, "Fetched " + listRoom.size() + " rooms from API (filter/search)");
                    subListRoom();
                    floorAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Lỗi khi " + (isFilter ? "lọc" : "tìm kiếm") + " phòng: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Chi tiết lỗi: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage());
                        }
                    }
                    Toast.makeText(RoomManageActivity.this, "Lỗi khi " + (isFilter ? "lọc" : "tìm kiếm") + " phòng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi " + (isFilter ? "lọc" : "tìm kiếm") + " phòng: " + t.getMessage());
                Toast.makeText(RoomManageActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subListRoom() {
        listFloor.clear();
        if (listRoom.isEmpty()) {
            return;
        }
        Collections.sort(listRoom, (room1, room2) -> Integer.compare(room1.getFloor(), room2.getFloor()));
        List<Room> listSub = new ArrayList<>();
        int floor = -999;
        for (Room room : listRoom) {
            if (room.getFloor() != floor) {
                if (listSub.size() != 0) {
                    listFloor.add(new Floor(floor, listSub));
                }
                listSub = new ArrayList<>();
                listSub.add(room);
                floor = room.getFloor();
            } else {
                listSub.add(room);
            }
        }
        if (listSub.size() != 0) {
            listFloor.add(new Floor(floor, listSub));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Refreshing room list");
        fetchRooms();
    }
}