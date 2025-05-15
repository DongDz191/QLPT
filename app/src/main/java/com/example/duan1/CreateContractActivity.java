package com.example.duan1;

import static com.example.duan1.base.BaseCheckValid.checkEmptyString;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.duan1.Interface.IClickItemMember;
import com.example.duan1.adapter.MemberAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ActivityCreateContractBinding;
import com.example.duan1.databinding.DialogMemberBinding;
import com.example.duan1.databinding.DialogMemberDetailBinding;
import com.example.duan1.model.Account;
import com.example.duan1.model.Contract;
import com.example.duan1.model.Invoice;
import com.example.duan1.model.Member;
import com.example.duan1.model.Room;
import com.example.duan1.model.RoomType;
import com.example.duan1.model.SessionAccount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateContractActivity extends AppCompatActivity {
    ActivityCreateContractBinding binding;
    ApiService apiService;
    MemberAdapter adapterMember;
    List<Member> listMember = new ArrayList<>();
    Contract contract;
    String pathImage;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance();
    Date date;
    Account account;

    private static final String TAG = "CreateContractActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_contract);
        apiService = ApiClient.getApiService();

        binding.imgBack.setOnClickListener(view -> finish());
        Intent intent = getIntent();
        Room room = (Room) intent.getSerializableExtra("room");
        binding.setRoom(room);
        SessionAccount sessionAccount = new SessionAccount(this);
        account = sessionAccount.fetchAccount();

        // Lấy RoomType từ API
        apiService.getRoomTypeById(room.getIdRoomType()).enqueue(new Callback<RoomType>() {
            @Override
            public void onResponse(Call<RoomType> call, Response<RoomType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RoomType roomType = response.body();
                    binding.setRoomType(roomType);
                } else {
                    Log.e(TAG, "Lỗi khi lấy RoomType: " + response.code());
                    Toast.makeText(CreateContractActivity.this, "Lỗi khi lấy RoomType: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RoomType> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy RoomType: " + t.getMessage());
                Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Tạo Contract tạm qua API
        date = calendar.getTime();
        String startDate = format.format(date);
        Contract tempContract = new Contract(startDate, "", 1, room.getRoomCode());
        apiService.insertContract(tempContract).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Lấy Contract mới nhất từ API
                    apiService.getContractNewest().enqueue(new Callback<Contract>() {
                        @Override
                        public void onResponse(Call<Contract> call, Response<Contract> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                contract = response.body();
                                Log.d(TAG, "Contract mới nhất được lấy: " + contract.toString());
                                // Lấy danh sách Member liên quan đến Contract
                                loadMembers();
                            } else {
                                Log.e(TAG, "Lỗi khi lấy Contract mới nhất: " + response.code());
                                Toast.makeText(CreateContractActivity.this, "Lỗi khi lấy Contract mới nhất: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Contract> call, Throwable t) {
                            Log.e(TAG, "Lỗi kết nối API khi lấy Contract mới nhất: " + t.getMessage());
                            Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "Lỗi khi tạo Contract: " + response.code());
                    Toast.makeText(CreateContractActivity.this, "Lỗi khi tạo Contract: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi tạo Contract: " + t.getMessage());
                Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapterMember = new MemberAdapter(this, listMember, new IClickItemMember() {
            @Override
            public void onClickDelete(Member member, int i) {
                handleItemMemberDelete(member, i);
            }

            @Override
            public void onClickEdit(Member member, int position) {
                handleItemMemberEdit(member, position);
            }

            @Override
            public void onClickItem(Member member, int position) {
                handleItemMemberClick(member);
            }
        });

        binding.rvMember.setAdapter(adapterMember);
        binding.rvMember.setLayoutManager(new LinearLayoutManager(this));

        binding.imgAdd.setOnClickListener(view -> handleAddMember());

        binding.btnCancel.setOnClickListener(view -> onBackPressed());

        binding.btnCreateContract.setOnClickListener(view -> handleCreateContract());

        binding.imgNext.setOnClickListener(view -> {
            String s = binding.edNumber.getText().toString();
            if (!s.isEmpty()) {
                int number = Integer.parseInt(s) + 1;
                binding.edNumber.setText(String.valueOf(number));
            }
        });

        binding.imgPrevious.setOnClickListener(view -> {
            String s = binding.edNumber.getText().toString();
            if (!s.isEmpty()) {
                int number = Integer.parseInt(s);
                if (number > 0) {
                    number--;
                }
                binding.edNumber.setText(String.valueOf(number));
            }
        });
    }

    private void loadMembers() {
        Log.d(TAG, "Đang tải danh sách thành viên cho contract ID: " + contract.getIdContract());
        apiService.getMemberByIdContract(contract.getIdContract()).enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listMember.clear();
                    listMember.addAll(response.body());
                    Log.d(TAG, "Danh sách thành viên được tải: " + listMember.size() + " thành viên");
                    adapterMember.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Lỗi khi lấy danh sách thành viên: " + response.code());
                    Toast.makeText(CreateContractActivity.this, "Lỗi khi lấy danh sách thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi lấy danh sách thành viên: " + t.getMessage());
                Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleItemMemberClick(Member member) {
        Log.d(TAG, "Xem chi tiết thành viên: " + member.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        DialogMemberDetailBinding bindingDialog = DialogMemberDetailBinding.inflate(getLayoutInflater());
        builder.setView(bindingDialog.getRoot());
        AlertDialog dialog = builder.create();
        bindingDialog.setMember(member);
        Glide.with(this).load(Uri.parse(member.getImage())).into(bindingDialog.imgAvatar);
        bindingDialog.btnClose.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void handleCreateContract() {
        String stringNumber = binding.edNumber.getText().toString();
        String stringElectricNumber = binding.edElectricNumber.getText().toString();
        String stringWaterNumber = binding.edWaterNumber.getText().toString();
        Log.d(TAG, "Số tháng: " + stringNumber + ", Số điện: " + stringElectricNumber + ", Số nước: " + stringWaterNumber);
        if (!checkEmptyString(stringNumber, stringElectricNumber, stringWaterNumber)) {
            Log.w(TAG, "Một hoặc nhiều trường bị trống khi tạo hợp đồng");
            Toast.makeText(this, "Vui lòng không để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        int number = Integer.parseInt(stringNumber);
        int electronic = Integer.parseInt(stringElectricNumber);
        int water = Integer.parseInt(stringWaterNumber);
        calendar.add(Calendar.MONTH, number);
        String endDate = format.format(calendar.getTime());

        contract.setEndingDate(endDate);
        Log.d(TAG, "Cập nhật hợp đồng với ngày kết thúc: " + endDate);
        apiService.updateContract(contract.getIdContract(), contract).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Tạo Invoice qua API
                    Invoice invoice = new Invoice(electronic, water, 2, contract.getIdContract(), account.getUsername());
                    Log.d(TAG, "Tạo Invoice: " + invoice.toString());
                    apiService.insertInvoice(invoice).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "Tạo Invoice thành công, chuyển đến RoomManageActivity");
                                startActivity(new Intent(CreateContractActivity.this, RoomManageActivity.class));
                                Toast.makeText(CreateContractActivity.this, "Tạo hợp đồng thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.e(TAG, "Lỗi khi tạo Invoice: " + response.code());
                                Toast.makeText(CreateContractActivity.this, "Lỗi khi tạo Invoice: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Lỗi kết nối API khi tạo Invoice: " + t.getMessage());
                            Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "Lỗi khi cập nhật Contract: " + response.code());
                    Toast.makeText(CreateContractActivity.this, "Lỗi khi cập nhật Contract: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi cập nhật Contract: " + t.getMessage());
                Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAddMember() {
        Log.d(TAG, "Bắt đầu thêm thành viên mới");
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        DialogMemberBinding bindingMember = DialogMemberBinding.inflate(getLayoutInflater());
        builder.setView(bindingMember.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        bindingMember.edBirthday.setOnClickListener(view1 -> {
            Log.d(TAG, "Mở DatePickerDialog để chọn ngày sinh");
            DatePickerDialog dp = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
                try {
                    String date2 = i + "-" + (i1 + 1) + "-" + i2;
                    Date date1 = format.parse(date2);
                    bindingMember.edBirthday.setText(format.format(date1));
                    Log.d(TAG, "Ngày sinh được chọn: " + format.format(date1));
                } catch (ParseException e) {
                    Log.e(TAG, "Lỗi khi parse ngày sinh: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 1999, 1, 1);
            dp.show();
        });

        bindingMember.imgMember.setOnClickListener(view1 -> {
            Log.d(TAG, "Mở TedImagePicker để chọn ảnh thành viên");
            TedImagePicker.with(this).start(uri -> {
                Glide.with(this).load(uri).into(bindingMember.imgMember);
                bindingMember.imgMember.setMaxHeight(150);
                pathImage = uri.toString();
                Log.d(TAG, "Ảnh được chọn: " + pathImage);
            });
        });

        bindingMember.btnAdd.setOnClickListener(view1 -> {
            String name = bindingMember.edName.getText().toString();
            String birthday = bindingMember.edBirthday.getText().toString();
            String citizenIdentification = bindingMember.edCitizenIdentification.getText().toString();
            String phone = bindingMember.edPhone.getText().toString();
            String hometown = bindingMember.edHometown.getText().toString();
            Log.d(TAG, "Thông tin thành viên: Tên=" + name + ", Ngày sinh=" + birthday + ", CMND/CCCD=" + citizenIdentification + ", SĐT=" + phone + ", Quê quán=" + hometown);

            if (!checkEmptyString(name, birthday, citizenIdentification, phone, hometown)) {
                Log.w(TAG, "Một hoặc nhiều trường bị trống khi thêm thành viên");
                Toast.makeText(this, "Vui lòng không để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            Member member = new Member(name, birthday, citizenIdentification, pathImage, phone, hometown, contract.getIdContract());
            Log.d(TAG, "Tạo đối tượng Member: " + member.toString());

            apiService.insertMember(member).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Thêm thành viên thành công: " + member.getName());
                        Toast.makeText(CreateContractActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        loadMembers();
                        dialog.dismiss();
                    } else {
                        Log.e(TAG, "Lỗi khi thêm thành viên: " + response.code() + ", Message: " + response.message());
                        if (response.errorBody() != null) {
                            try {
                                Log.e(TAG, "Chi tiết lỗi: " + response.errorBody().string());
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage());
                            }
                        }
                        Toast.makeText(CreateContractActivity.this, "Lỗi khi thêm thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Lỗi kết nối API khi thêm thành viên: " + t.getMessage());
                    Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        bindingMember.btnCancel.setOnClickListener(view1 -> {
            Log.d(TAG, "Hủy bỏ thêm thành viên");
            dialog.dismiss();
        });
    }

    private void handleItemMemberDelete(Member member, int i) {
        Log.d(TAG, "Bắt đầu xóa thành viên: " + member.getName() + ", Vị trí: " + i);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa thành viên?");
        builder.setPositiveButton("Xác nhận", (dialogInterface, i1) -> {
            apiService.deleteMember(member.getIdMember()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Xóa thành viên thành công: " + member.getName());
                        listMember.remove(i);
                        adapterMember.notifyItemRemoved(i);
                        adapterMember.notifyItemRangeChanged(i, adapterMember.getItemCount());
                    } else {
                        Log.e(TAG, "Lỗi xóa thành viên: " + response.code());
                        Toast.makeText(CreateContractActivity.this, "Lỗi xóa thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Lỗi kết nối API khi xóa thành viên: " + t.getMessage());
                    Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            dialogInterface.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialogInterface, i1) -> {
            Log.d(TAG, "Hủy bỏ xóa thành viên");
            dialogInterface.dismiss();
        });
        builder.create().show();
    }

    private void handleItemMemberEdit(Member member, int position) {
        Log.d(TAG, "Bắt đầu chỉnh sửa thành viên: " + member.getName() + ", Vị trí: " + position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogMemberBinding bindingMember = DialogMemberBinding.inflate(getLayoutInflater());
        bindingMember.btnAdd.setText("Cập nhật");
        builder.setView(bindingMember.getRoot());
        bindingMember.edName.setText(member.getName());
        bindingMember.edBirthday.setText(member.getBirthday());
        Glide.with(this).load(Uri.parse(member.getImage())).into(bindingMember.imgMember);
        bindingMember.imgMember.setMaxHeight(150);
        bindingMember.edHometown.setText(member.getHometown());
        bindingMember.edPhone.setText(member.getPhone());
        bindingMember.edCitizenIdentification.setText(member.getCitizenIdentification());
        AlertDialog dialog = builder.create();
        dialog.show();

        bindingMember.edBirthday.setOnClickListener(view -> {
            Log.d(TAG, "Mở DatePickerDialog để chỉnh sửa ngày sinh");
            DatePickerDialog dp = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
                try {
                    String date2 = i + "-" + (i1 + 1) + "-" + i2;
                    Date date1 = format.parse(date2);
                    bindingMember.edBirthday.setText(format.format(date1));
                    Log.d(TAG, "Ngày sinh được cập nhật: " + format.format(date1));
                } catch (ParseException e) {
                    Log.e(TAG, "Lỗi khi parse ngày sinh: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 1999, 1, 1);
            dp.show();
        });

        bindingMember.imgMember.setOnClickListener(view -> {
            Log.d(TAG, "Mở TedImagePicker để chỉnh sửa ảnh thành viên");
            TedImagePicker.with(this).start(uri -> {
                bindingMember.imgMember.setImageURI(uri);
                bindingMember.imgMember.setMaxHeight(200);
                pathImage = uri.toString();
                Log.d(TAG, "Ảnh được cập nhật: " + pathImage);
            });
        });

        bindingMember.btnAdd.setOnClickListener(view -> {
            String name = bindingMember.edName.getText().toString();
            String birthday = bindingMember.edBirthday.getText().toString();
            String citizenIdentification = bindingMember.edCitizenIdentification.getText().toString();
            String phone = bindingMember.edPhone.getText().toString();
            String hometown = bindingMember.edHometown.getText().toString();
            Log.d(TAG, "Thông tin chỉnh sửa: Tên=" + name + ", Ngày sinh=" + birthday + ", CMND/CCCD=" + citizenIdentification + ", SĐT=" + phone + ", Quê quán=" + hometown);

            if (checkEmptyString(name, birthday, citizenIdentification, phone, hometown)) {
                member.setName(name);
                member.setBirthday(birthday);
                member.setCitizenIdentification(citizenIdentification);
                member.setPhone(phone);
                member.setHometown(hometown);
                member.setImage(pathImage != null ? pathImage : member.getImage());
                Log.d(TAG, "Đối tượng Member sau khi chỉnh sửa: " + member.toString());
                apiService.updateMember(member.getIdMember(), member).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Cập nhật thành viên thành công: " + member.getName());
                            Toast.makeText(CreateContractActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            listMember.set(position, member);
                            adapterMember.notifyItemChanged(position);
                            dialog.dismiss();
                        } else {
                            Log.e(TAG, "Lỗi khi cập nhật thành viên: " + response.code());
                            Toast.makeText(CreateContractActivity.this, "Lỗi khi cập nhật thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Lỗi kết nối API khi cập nhật thành viên: " + t.getMessage());
                        Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w(TAG, "Một hoặc nhiều trường bị trống khi chỉnh sửa thành viên");
                Toast.makeText(this, "Vui lòng không để trống", Toast.LENGTH_SHORT).show();
            }
        });

        bindingMember.btnCancel.setOnClickListener(view -> {
            Log.d(TAG, "Hủy bỏ chỉnh sửa thành viên");
            dialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Hủy bỏ hợp đồng, xóa contract ID: " + contract.getIdContract());
        apiService.deleteContract(contract.getIdContract()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Hủy hợp đồng thành công");
                    super.getClass(); // Gọi phương thức onBackPressed() của lớp cha để đóng activity
                } else {
                    Log.e(TAG, "Lỗi hủy bỏ hợp đồng: " + response.code());
                    Toast.makeText(CreateContractActivity.this, "Lỗi hủy bỏ hợp đồng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API khi hủy hợp đồng: " + t.getMessage());
                Toast.makeText(CreateContractActivity.this, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}