package com.fudan.stress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fudan.stress.R;
import com.fudan.stress.util.LogUtil;

import io.reactivex.functions.Consumer;

import com.fudan.stress.widget.SetPermissionDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.annotation.Nullable;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermisson();
            }
        }, 100);
        LogUtil.d(new String(Character.toChars(0x1F60E)));
    }

//    @Override
//    protected void onDestroy() {
//        if (dialog != null) {
//            dialog.dismiss();
//            dialog = null;
//        }
//        super.onDestroy();
//    }

    private void requestPermisson(){
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
        .request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        .subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    startActivity(new Intent(SplashActivity.this, ChatActivity.class));
                    finish();
                } else {
                    SetPermissionDialog mSetPermissionDialog = new SetPermissionDialog(SplashActivity.this);
                    mSetPermissionDialog.show();
                    mSetPermissionDialog.setConfirmCancelListener(new SetPermissionDialog.OnConfirmCancelClickListener() {
                        @Override
                        public void onLeftClick() {

                            finish();
                        }

                        @Override
                        public void onRightClick() {

                            finish();
                        }
                    });
                }
            }
        });
    }
}
