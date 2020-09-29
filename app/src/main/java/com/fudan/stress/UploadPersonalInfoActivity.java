package com.fudan.stress;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.fudan.stress.common.Constant;
import com.huawei.hiresearch.bridge.BridgeManager2;
import com.huawei.hiresearch.bridge.listeners.OnMetadataUploadProgressChanged;
import com.huawei.hiresearch.bridge.model.dataupload.BinaryProgressStatus;
import com.huawei.hiresearch.bridge.model.dataupload.MetadataCompressResultInfo;
import com.huawei.hiresearch.bridge.provider.BridgeDataProvider;
import com.huawei.hiresearch.common.model.metadata.system.HwUserBasicInfo;

import io.reactivex.disposables.CompositeDisposable;

/*****************************************************************************************
 * Copyright (c) 2020, Huawei Technologies Co.,Ltd.All Rights Reserved.
 * File name: UploadPersonalInfoActivity
 * Author: weishuhan
 * Version: 1.0
 * Create DATE: 2020/6/3
 * Description:
 * Other: If you want to use this module,please retain this comment.
 *        You can change any code of this file and add your name to the developers list,
 *        but you cannot delete this comment or modify all content above.
 * Here's the developers list:
 *        1.weishuhan
 *****************************************************************************************/

public class UploadPersonalInfoActivity extends Activity {

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        HwUserBasicInfo userBasicInfo = new Gson().fromJson(intent.getStringExtra("userBasicInfo"), HwUserBasicInfo.class);
        HwUserBasicInfo userBasicInfo = new HwUserBasicInfo("17826856326", 163, 53, 23, 1, "上海市", "上海市");
        uploadData(Constant.PROJECT_CODE, userBasicInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    //上传HiResearch标准化个人数据
    private void uploadData(String projectCode, HwUserBasicInfo data) {
        BridgeDataProvider bridgeDataProvider = BridgeManager2.getInstance(projectCode).getBridgeDataProvider();
        mDisposable.add(bridgeDataProvider.upload(data, new OnMetadataUploadProgressChanged() {
            @Override
            public void onCompressProgress(BinaryProgressStatus status) {
                //TODO：压缩进度
                //总进度值
                int maxValue = status.getMaxValue();
                //当前进度值
                int currentValue = status.getCurrentValue();
            }

            @Override
            public void onCompressComplete(MetadataCompressResultInfo compressResp) {
                //TODO：压缩完成
            }

            @Override
            public void onUploadProgress(BinaryProgressStatus status) {
                //TODO：元数据上传进度
                //总进度值
                int maxValue = status.getMaxValue();
                //当前进度值
                int currentValue = status.getCurrentValue();
            }
        }).subscribe(resp -> {
            if (resp.getSuccess()) {
                //TODO:上传成功
            } else {
                //TODO：上传失败
                int statusCode = resp.getStatusCode(); //HTTP状态码
                String code = resp.getCode(); //内部错误码
                String msg = resp.getMessage(); //错误信息描述
            }
        }, err -> {
            //TODO:上传元数据异常
        }));
    }

}
