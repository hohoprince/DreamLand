package com.example.dreamland;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.dreamland.MySimpleDateFormat.sdf1;
import static com.example.dreamland.MySimpleDateFormat.sdf3;

public class HomeFragment extends Fragment {

    Button startButton;
    TimePicker timePicker;
    Context context;
    Button selButton1;
    Button selButton2;
    Button selButton3;
    AlertDialog selDiaglog;
    ImageView ivPredicPos;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startButton = (Button) view.findViewById(R.id.startButton);
        timePicker = view.findViewById(R.id.timePicker);
        context = getContext();


        // 수면 시작 버튼
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).isConnected) {
                    showDialog();
                } else {
                    Toast.makeText(context, "블루투스 연결을 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 수면 시작 함수
    private void startSleep(int selectedMenu) {
        ((MainActivity) getActivity()).adjMode = selectedMenu;
        Intent intent = new Intent(getContext(), SleepingActivity.class);
        intent.putExtra("hour", timePicker.getHour());
        intent.putExtra("minute", timePicker.getMinute());
        Calendar calendar = Calendar.getInstance();
        String whenStart = sdf1.format(calendar.getTime());
        ((MainActivity) context).sleep.setWhenStart(whenStart);
        if (calendar.get(Calendar.HOUR_OF_DAY) < 6) { // 자정이 지나면 전날로 표기
            calendar.roll(Calendar.HOUR_OF_DAY, 7);
        }
        String sleepDate = sdf3.format(calendar.getTime());
        ((MainActivity) context).sleep.setSleepDate(sleepDate);
        Log.d(MainActivity.STATE_TAG, "측정 시작 / " + sleepDate + " " + whenStart);
        ((MainActivity) getActivity()).startActivityForResult(intent, 2000);
        ((MainActivity) getActivity()).overridePendingTransition(R.anim.down_in, R.anim.stop);
    }

    // 교정방식 선택 화면
    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        final View dlgView = getLayoutInflater().from(getContext()).inflate(
                R.layout.dialog_before_sleep, null);
        selButton1 = dlgView.findViewById(R.id.selButton1);
        selButton2 = dlgView.findViewById(R.id.selButton2);
        selButton3 = dlgView.findViewById(R.id.selButton3);
        ivPredicPos = dlgView.findViewById(R.id.ivPredicPos);

        switch (((MainActivity) getActivity()).mode) {
            case 1:
            case 2:
                if ((int) (Math.random() * 2) == 0) {
                    ivPredicPos.setImageResource(R.drawable.pos1);  // 왼쪽으로 교정
                    ((MainActivity) getActivity()).act = MainActivity.ACT_LEFT;
                } else {
                    ivPredicPos.setImageResource(R.drawable.pos2);  // 오른쪽으로 교정
                    ((MainActivity) getActivity()).act = MainActivity.ACT_RIGHT;
                }
                break;
            case 3:
                //TODO: 질환별 자세로 이미지를 변경
                switch (((MainActivity) getActivity()).settingFragment.diseaseIndex) {
                    case 0:
                        ivPredicPos.setImageResource(R.drawable.pos5);
                        break;
                    case 1:
                        ivPredicPos.setImageResource(R.drawable.pos5);
                        break;
                    case 2:
                        ivPredicPos.setImageResource(R.drawable.pos5);
                        break;
                    case 3:
                        ivPredicPos.setImageResource(R.drawable.pos5);
                        break;
                    default:
                }
                break;
            default:
        }


        selDiaglog = builder.setView(dlgView).create();
        selDiaglog.show();

        // 수면 중 교정
        selButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selDiaglog.dismiss();
                startSleep(1);
            }
        });

        // 수면 중 한 번 교정
        selButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selDiaglog.dismiss();
                startSleep(2);
            }
        });

        // 즉시 교정
        selButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selDiaglog.dismiss();
                startSleep(3);
            }
        });
    }
}
