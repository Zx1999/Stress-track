package com.fudan.stress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fudan.stress.common.Constant;
import com.google.gson.Gson;
import com.huawei.hiresearch.bridge.BridgeManager2;
import com.huawei.hiresearch.bridge.model.authentication.HWSignIn;
import com.huawei.hiresearch.bridge.model.bridge.HWJoinInfo;
import com.huawei.hiresearch.bridge.model.consent.InformedConsent;
import com.huawei.hiresearch.bridge.provider.AuthenticationProvider;
import com.huawei.hiresearch.bridge.provider.StudyProjectProvider;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/*****************************************************************************************
 * Copyright (c) 2020, Huawei Technologies Co.,Ltd.All Rights Reserved.
 * File name: LoginActivity
 * Author: weishuhan
 * Version: 1.0
 * Create DATE: 2020/5/25
 * Description:
 * Other: If you want to use this module,please retain this comment.
 *        You can change any code of this file and add your name to the developers list,
 *        but you cannot delete this comment or modify all content above.
 * Here's the developers list:
 *        1.weishuhan
 *****************************************************************************************/
public class LoginActivity extends Activity {

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    //private String projectCode;
    private AuthenticationProvider authenticationProvider;
    private StudyProjectProvider studyProjectProvider;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }


    private void init() {
        Log.d(TAG, "init");
        //projectCode = "c8644ab6";
        authenticationProvider = BridgeManager2.getInstance(Constant.PROJECT_CODE).getAuthenticationProvider();
        studyProjectProvider = BridgeManager2.getInstance(Constant.PROJECT_CODE).getStudyProjectProvider();
        if (!authenticationProvider.isSignedIn()) {
            //未登录
            //用户知情同意，签署《用户协议》与《隐私声明》
            Log.d(TAG, "未登录, 用户知情同意，签署《用户协议》与《隐私声明》");
            informedConsent();
        } else {
            //已登录，判断是否已经加入研究项目
            if (studyProjectProvider.isConsented()) {
                Log.d(TAG, "已登录，已经加入研究项目");
                //TODO:已经登录并且已加入研究项目,跳转研究APP首页
                Intent intent = new Intent(LoginActivity.this, SportHealthActivity.class);
                startActivity(new Intent(intent));
            } else {
                //加入研究项目
                joinStudy((HWSignIn) authenticationProvider.getSign());
            }
        }
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivityForResult(intent, Constant.SIGN_IN_HW);
    }


    //用户知情同意，签署《用户协议》与《隐私声明》
    private void informedConsent() {
        mDisposable.add(studyProjectProvider.getInformedConsents()
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        List<InformedConsent> data = resp.getData();
                        if (null == data || data.size() < 1) {
                            //TODO: 项目管理员在HiResearch 平台未配置用户知情同意相关文件
                            Log.e(TAG, "informedConsent: 项目管理员在HiResearch 平台未配置用户知情同意相关文件");
                        } else {
                            for (InformedConsent item : data) {
                                //发布文件类型： 0是《知情同意》 1是《隐私申明》
                                int type = item.getType();
                                //《用户协议》或《隐私声明》 发布地址
                                String url = item.getUrl();
                                Log.d(TAG, type + "");
                                Log.d(TAG, url);
                            }
                            //TODO：APP弹出《用户协议》与《隐私声明》 让用户确定签署
                            //用户确定后调用华为账号登录
                            signByHuaweiAccount();
                        }
                    } else {
                        Log.e(TAG, "informedConsent: 获取用户知情同意失败");
                        //TODO：获取用户知情失败
                        int statusCode = resp.getStatusCode(); //HTTP状态码
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "statusCode:" + statusCode + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    //TODO:获取用户知情异常
                }));
    }


    //华为账号登录
    private void signByHuaweiAccount() {
        //T请参阅华为帐号服务 完成APP华为账号登录
        //华为账号服务帮助文档 https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/account-introduction-v4
        //华为账号登录成功后通过回调 可以获取到 AuthHuaweiId 对象
        //TODO:在登录成功回调中  调用sign2HiResearch 完成HiResearch 二次鉴权校验
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, Constant.SIGN_IN_HW);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " " + resultCode + " ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.SIGN_IN_HW) {
            if(resultCode == Constant.RESULT_OK) {
                if(data != null) {
                    AuthHuaweiId authHuaweiId = new Gson().fromJson(data.getStringExtra("huaweiAccount"), AuthHuaweiId.class);
                    //String result = data.getStringExtra("huaweiAccount");
                    sign2HiResearch(authHuaweiId);
                }
            }
         }
    }


    //登录至HiResearch
    private void sign2HiResearch(AuthHuaweiId authHuaweiId) {
        HWSignIn signIn = new HWSignIn(Constant.PROJECT_CODE, authHuaweiId.getOpenId(), authHuaweiId.getAccessToken(),
                authHuaweiId.getAuthorizationCode());
        mDisposable.add(authenticationProvider.signIn(signIn)
                .subscribe(resp -> {
                    Log.i(TAG, "sign2HiResearch: 登录成功");
                    if (resp.getSuccess()) {
                        //登录成功加入研究项目
                        joinStudy(signIn);
                    } else {
                        //TODO：登录失败
                        Log.i(TAG, "sign2HiResearch: 登录失败");
                        int statusCode = resp.getStatusCode(); //HTTP状态码
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                    }
                }, err -> {
                    Log.e(TAG, "sign2HiResearch: 登录异常");
                    //TODO:登录异常
                }));
    }


    //加入研究项目
    private void joinStudy(HWSignIn signIn) {
        mDisposable.add(studyProjectProvider.join(new HWJoinInfo(signIn.getHwOpenId()))
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        //TODO:加入研究项目成功，跳转研究APP首页
                        Log.i(TAG, "sign2HiResearch: 加入研究项目成功，跳转研究APP首页");
                    } else {
                        //TODO:加入研究项目失败
                        int statusCode = resp.getStatusCode(); //HTTP状态码
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                    }
                }, err -> {
                    //TODO:加入研究项目异常
                }));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
