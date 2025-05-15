package com.example.duan1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duan1.adapter.Member2Adapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.model.Contract;
import com.example.duan1.model.Member;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowMemberContract extends AppCompatActivity {
    TextView id_phong, id_clock_start, id_clock_stop, id_contract, id_room;
    Button btn_back_contract;
    RecyclerView rcv;
    ApiService apiService;
    List<Member> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_member_contract);

        id_phong = findViewById(R.id.id_phong);
        id_clock_start = findViewById(R.id.id_clock_start);
        id_clock_stop = findViewById(R.id.id_clock_stop);
        id_contract = findViewById(R.id.id_contract);
        id_room = findViewById(R.id.id_room);
        btn_back_contract = findViewById(R.id.btn_back_contract);
        rcv = findViewById(R.id.rcvHopDong);

        apiService = ApiClient.getApiService();
        memberList = new ArrayList<>();

        Contract contract = (Contract) getIntent().getSerializableExtra("contract");
        if (contract == null) {
            Toast.makeText(this, "Không tìm thấy hợp đồng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy danh sách Member qua API
        apiService.getMemberByIdContract(contract.getIdContract()).enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    memberList.clear();
                    memberList.addAll(response.body());
                    Member2Adapter adapter = new Member2Adapter(ShowMemberContract.this, memberList);
                    rcv.setLayoutManager(new LinearLayoutManager(ShowMemberContract.this));
                    rcv.setAdapter(adapter);
                } else {
                    Toast.makeText(ShowMemberContract.this, "Lỗi khi lấy danh sách thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
                Toast.makeText(ShowMemberContract.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Hiển thị thông tin hợp đồng
        id_phong.setText(String.valueOf(contract.getIdContract()));
        id_clock_start.setText(contract.getStatingDate());
        id_clock_stop.setText(contract.getEndingDate());
        id_contract.setText(contract.getStatus() == 1 ? "Hiệu lực" : "Hết hạn");
        id_room.setText(contract.getRoomCode());

        btn_back_contract.setOnClickListener(v -> finish());
    }
}