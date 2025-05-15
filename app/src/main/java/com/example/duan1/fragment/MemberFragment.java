package com.example.duan1.fragment;

import static com.example.duan1.base.BaseCheckValid.checkEmptyString;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.duan1.Interface.IClickItemMember;
import com.example.duan1.R;
import com.example.duan1.adapter.MemberAdapter;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.DialogMemberBinding;
import com.example.duan1.databinding.DialogMemberDetailBinding;
import com.example.duan1.databinding.FragmentMemberBinding;
import com.example.duan1.model.Contract;
import com.example.duan1.model.Member;
import com.example.duan1.viewmodel.ContractViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberFragment extends Fragment {
    FragmentMemberBinding binding;
    Contract contract;
    ApiService apiService;
    List<Member> listMember = new ArrayList<>();
    MemberAdapter adapterMember;
    String pathImage;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentMemberBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getApiService();
        binding.rvMember.setLayoutManager(new LinearLayoutManager(getContext()));

        // Contract ViewModel
        new ViewModelProvider(getActivity()).get(ContractViewModel.class).getContract().observe(getViewLifecycleOwner(), o -> {
            this.contract = (Contract) o;
            handleObserve();
        });
    }

    private void handleObserve() {
        // Fetch members from API
        apiService.getMemberByIdContract(contract.getIdContract()).enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listMember.clear();
                    listMember.addAll(response.body());
                    setupAdapter();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.imgAdd.setOnClickListener(view1 -> handleAddMember());
    }

    private void setupAdapter() {
        adapterMember = new MemberAdapter(getContext(), listMember, new IClickItemMember() {
            @Override
            public void onClickDelete(Member member, int position) {
                handleItemMemberDelete(member, position);
            }

            @Override
            public void onClickEdit(Member member, int position) {
                handleItemMemberEdit(member, position);
            }

            @Override
            public void onClickItem(Member member, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                DialogMemberDetailBinding bindingDialog = DialogMemberDetailBinding.inflate(getLayoutInflater());
                builder.setView(bindingDialog.getRoot());
                AlertDialog dialog = builder.create();
                bindingDialog.setMember(member);
                bindingDialog.imgAvatar.setImageURI(Uri.parse(member.getImage()));
                bindingDialog.btnClose.setOnClickListener(view -> dialog.dismiss());
                dialog.show();
            }
        });
        binding.rvMember.setAdapter(adapterMember);
    }

    private void handleItemMemberDelete(Member member, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa thành viên?");
        builder.setPositiveButton("Xác nhận", (dialogInterface, i1) -> {
            apiService.deleteMember(member.getIdMember()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        int currentPosition = position;
                        if (currentPosition != RecyclerView.NO_POSITION) {
                            listMember.remove(currentPosition);
                            adapterMember.notifyItemRemoved(currentPosition);
                            adapterMember.notifyItemRangeChanged(currentPosition, adapterMember.getItemCount());
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi xóa thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            dialogInterface.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialogInterface, i1) -> dialogInterface.dismiss());
        builder.create().show();
    }

    private void handleItemMemberEdit(Member member, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        DialogMemberBinding bindingMember = DialogMemberBinding.inflate(getLayoutInflater());
        bindingMember.btnAdd.setText("Cập nhật");
        builder.setView(bindingMember.getRoot());
        bindingMember.edName.setText(member.getName());
        bindingMember.edBirthday.setText(member.getBirthday());
        Glide.with(getContext()).load(Uri.parse(member.getImage())).into(bindingMember.imgMember);
        bindingMember.imgMember.setMaxHeight(150);
        bindingMember.edHometown.setText(member.getHometown());
        bindingMember.edPhone.setText(member.getPhone());
        bindingMember.edCitizenIdentification.setText(member.getCitizenIdentification());
        AlertDialog dialog = builder.create();
        dialog.show();

        bindingMember.edBirthday.setOnClickListener(view -> {
            DatePickerDialog dp = new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> {
                try {
                    String date2 = i + "-" + (i1 + 1) + "-" + i2;
                    Date date1 = format.parse(date2);
                    bindingMember.edBirthday.setText(format.format(date1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, 1999, 1, 1);
            dp.show();
        });

        bindingMember.imgMember.setOnClickListener(view -> TedImagePicker.with(getContext()).start(uri -> {
            Glide.with(getContext()).load(uri).into(bindingMember.imgMember);
            bindingMember.imgMember.setMaxHeight(200);
            pathImage = uri.toString();
        }));

        bindingMember.btnAdd.setOnClickListener(view -> {
            String name = bindingMember.edName.getText().toString();
            String birthday = bindingMember.edBirthday.getText().toString();
            String citizenIdentification = bindingMember.edCitizenIdentification.getText().toString();
            String phone = bindingMember.edPhone.getText().toString();
            String hometown = bindingMember.edHometown.getText().toString();
            if (checkEmptyString(name, birthday, citizenIdentification, phone, hometown)) {
                member.setName(name);
                member.setBirthday(birthday);
                member.setCitizenIdentification(citizenIdentification);
                member.setPhone(phone);
                member.setHometown(hometown);
                if (pathImage != null && !pathImage.isEmpty()) {
                    member.setImage(pathImage);
                }

                // Update member via API
                apiService.updateMember(member.getIdMember(), member).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            int currentPosition = position;
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                listMember.set(currentPosition, member);
                                adapterMember.notifyItemChanged(currentPosition);
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi cập nhật dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Vui lòng không để trống", Toast.LENGTH_SHORT).show();
            }
        });

        bindingMember.btnCancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void handleAddMember() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        DialogMemberBinding bindingMember = DialogMemberBinding.inflate(getLayoutInflater());
        builder.setView(bindingMember.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        bindingMember.edBirthday.setOnClickListener(view1 -> {
            DatePickerDialog dp = new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> {
                try {
                    String date2 = i + "-" + (i1 + 1) + "-" + i2;
                    Date date1 = format.parse(date2);
                    bindingMember.edBirthday.setText(format.format(date1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, 1999, 1, 1);
            dp.show();
        });

        bindingMember.imgMember.setOnClickListener(view1 -> TedImagePicker.with(getContext()).start(uri -> {
            Glide.with(getContext()).load(uri).into(bindingMember.imgMember);
            bindingMember.imgMember.setMaxHeight(200);
            pathImage = uri.toString();
        }));

        bindingMember.btnAdd.setOnClickListener(view1 -> {
            String name = bindingMember.edName.getText().toString();
            String birthday = bindingMember.edBirthday.getText().toString();
            String citizenIdentification = bindingMember.edCitizenIdentification.getText().toString();
            String phone = bindingMember.edPhone.getText().toString();
            String hometown = bindingMember.edHometown.getText().toString();
            if (!checkEmptyString(name, birthday, citizenIdentification, phone, hometown)) {
                Toast.makeText(getContext(), "Vui lòng không để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            Member member = new Member(name, birthday, citizenIdentification, pathImage, phone, hometown, contract.getIdContract());
            apiService.insertMember(member).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        // Refresh the member list
                        apiService.getMemberByIdContract(contract.getIdContract()).enqueue(new Callback<List<Member>>() {
                            @Override
                            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    listMember.clear();
                                    listMember.addAll(response.body());
                                    adapterMember.notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi lấy danh sách thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Member>> call, Throwable t) {
                                Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm thành viên: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        bindingMember.btnCancel.setOnClickListener(view1 -> dialog.dismiss());
    }
}