package com.example.duan1;

import static androidx.databinding.DataBindingUtil.setContentView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class FragmentDoanhThu extends AppCompatActivity {


    private TabLayout tabLayout;
    private View viewAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_doanh_thu);
        tabLayout = findViewById(R.id.tab_layout);
        viewAdapter= findViewById(R.id.view_layout);

//    ViewPageAdapter viewPageAdapter= new ViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//    viewPageAdapter.setAdapter(viewPageAdapter);
//    tabLayout.setupWithViewPager((ViewPager) viewAdapter);

//        ViewPageAdapter viewPageAdapter= new ViewPageAdapter(getSupportFragmentManager(),FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
//        viewAdapter.setAdapter(viewPageAdapter);
//        tabLayout.setupWithViewPager((ViewPager) viewAdapter);


    }
}


//
//
//    public void DoanhthuFragment() {
//        // Required empty public constructor
//    }
//
//
//    public static FragmentDoanhThu newInstance() {
//        FragmentDoanhThu fragment = new FragmentDoanhThu();
//
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.activity_fragment_doanh_thu, container, false);
////        dbhelper = new Room(getActivity());
//        TextView tv_tien = view.findViewById(R.id.coin_all);
//        EditText ed_tungay = view.findViewById(R.id.txt_tuNgay);
//        EditText ed_denNgay = view.findViewById(R.id.txt_denNgay);
//        Button btn_tuNgay = view.findViewById(R.id.btn_tuNgay);
//        Button btn_denNgay = view.findViewById(R.id.btn_denNgay);
//        Button btnThongke = view.findViewById(R.id.btn_doanhThu);
//        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//        btn_tuNgay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month,dayOfMonth);
//                        ed_tungay.setText(df.format(gregorianCalendar.getTime()));
//                    }
//                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
//                dialog.show();
//            }
//        });
//        btn_denNgay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month,dayOfMonth);
//                        ed_denNgay.setText(df.format(gregorianCalendar.getTime()));
//                    }
//                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
//                dialog.show();
//            }
//        });
//        btnThongke.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                double tongtien = ThongKe(ed_tungay.getText().toString(), ed_denNgay.getText().toString());
//                tv_tien.setText("Doanh thu từ ngày" + ed_tungay.getText().toString()+ " đến ngày " + ed_denNgay.getText().toString() + ": "+tongtien+"vnd");
//            }
//        });
//        return view;
//    }
//
//    @SuppressLint("Range")
//    public double ThongKe(String st, String end) {
//        String sql = "SELECT SUM(tien_thue) as DoanhThu FROM Phieu_Muon WHERE ngay_thue BETWEEN ? AND ?";
//        Cursor cursor = dbhelper.getReadableDatabase().rawQuery(sql, new String[]{st, end});
//        List<Double> list = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            list.add(Double.parseDouble(cursor.getString(cursor.getColumnIndex("DoanhThu"))));
//        }
//        return list.get(0);
//    }
//}