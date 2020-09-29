package com.fudan.stress;

import android.app.Application;

import com.huawei.hiresearch.bridge.HiResearchBridgeStack;
import com.huawei.hiresearch.sensor.SensorManager;
import com.huawei.hiresearch.sensor.config.SensorConfig;

import net.danlew.android.joda.JodaTimeAndroid;

/*****************************************************************************************
 * Copyright (c) 2020, Huawei Technologies Co.,Ltd.All Rights Reserved.
 * File name: MainApplication
 * Author: weishuhan
 * Version: 1.0
 * Create DATE: 2020/6/2
 * Description:
 * Other: If you want to use this module,please retain this comment.
 *        You can change any code of this file and add your name to the developers list,
 *        but you cannot delete this comment or modify all content above.
 * Here's the developers list:
 *        1.weishuhan
 *****************************************************************************************/
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //HiResearch初始化
        HiResearchBridgeStack.init(this, new StudyStack(this, "c8644ab6",
                "https://hiresearch-kit.cbg-app.huawei.com/"));

        //设置读取设备数据超时时间
        SensorConfig sensorConfig = new SensorConfig(60000);
        //初始化Sensor
        SensorManager.init(this, sensorConfig);
        JodaTimeAndroid.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SensorManager.destrory();
    }
}