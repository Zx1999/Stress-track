package com.fudan.stress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.huawei.hiresearch.common.model.metadata.system.HwUserBasicInfo;

public class PersonalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        HwUserBasicInfo userBasicInfo = new HwUserBasicInfo("17826856326", 163, 53, 23, 1, "上海市", "上海市");
        Intent intent = new Intent(PersonalInfoActivity.this, UploadPersonalInfoActivity.class);
        intent.putExtra("userBasicInfo", new Gson().toJson(userBasicInfo));
        startActivity(intent);
    }
}
