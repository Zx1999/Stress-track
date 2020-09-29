package com.fudan.stress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fudan.stress.common.Constant;
import com.fudan.stress.common.Tools;
import com.google.gson.Gson;
import com.huawei.hiresearch.bridge.BridgeManager2;
import com.huawei.hiresearch.bridge.listeners.OnMetadataUploadProgressChanged;
import com.huawei.hiresearch.bridge.model.dataupload.BinaryProgressStatus;
import com.huawei.hiresearch.bridge.model.dataupload.MetadataCompressResultInfo;
import com.huawei.hiresearch.bridge.provider.BridgeDataProvider;
import com.huawei.hiresearch.common.model.base.SensorData;
import com.huawei.hiresearch.common.model.health.BloodPressureData;
import com.huawei.hiresearch.common.model.health.BloodSugarData;
import com.huawei.hiresearch.common.model.health.BodyCompositionData;
import com.huawei.hiresearch.common.model.health.HeartRateData;
import com.huawei.hiresearch.common.model.health.RRIData;
import com.huawei.hiresearch.common.model.health.SleepData;
import com.huawei.hiresearch.common.model.health.Spo2Data;
import com.huawei.hiresearch.common.model.health.realtime.HeartRateRealTimeData;
import com.huawei.hiresearch.common.model.health.realtime.RRIRealTimeData;
import com.huawei.hiresearch.common.model.sport.SingleSportData;
import com.huawei.hiresearch.common.model.sport.SportSumData;
import com.huawei.hiresearch.common.model.sport.realtime.SportRealtimeData;
import com.huawei.hiresearch.common.model.user.UserProfileInfo;
import com.huawei.hiresearch.sensor.SensorManager;
import com.huawei.hiresearch.sensor.config.HiResearchDataPermissionConfig;
import com.huawei.hiresearch.sensor.config.hihealth.DeviceDataPermissionConfig;
import com.huawei.hiresearch.sensor.config.hihealth.HealthDataPermissionConfig;
import com.huawei.hiresearch.sensor.config.hihealth.PersonalDataPermissionConfig;
import com.huawei.hiresearch.sensor.config.hihealth.SportDataPermissionConfig;
import com.huawei.hiresearch.sensor.listener.RealTimeDataReadListener;
import com.huawei.hiresearch.sensor.listener.RealTimeDataStopListener;
import com.huawei.hiresearch.sensor.listener.RealTimeSportDataReadListener;
import com.huawei.hiresearch.sensor.model.bean.device.WearDeviceInfo;
import com.huawei.hiresearch.sensor.model.bean.query.DataQuery;
import com.huawei.hiresearch.sensor.provider.DeviceProvider;
import com.huawei.hiresearch.sensor.provider.HealthProvider;
import com.huawei.hiresearch.sensor.provider.SportProvider;
import com.huawei.hiresearch.sensor.provider.UserProvider;
import com.huawei.hiresearch.sensor.service.query.QueryOption;

import net.danlew.android.joda.JodaTimeAndroid;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.fudan.stress.common.Tools;

import io.reactivex.disposables.CompositeDisposable;

/*****************************************************************************************
 * Copyright (c) 2020, Huawei Technologies Co.,Ltd.All Rights Reserved.
 * File name: SportHealthActivity
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
public class SportHealthActivity extends Activity {

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private final static String TAG = "SportHealthActivity";
    private String startDate = "2020-06-08 00:00:00";
    private long startTime = Tools.stringToTimestamp(startDate);
    private long endTime = new Date().getTime();
//    private long endTime = Tools.stringToTimestamp("2020-06-09 00:00:00");

    HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
//        setContentView(R.layout.activity_sport_health);
//        long endTime = new Date().getTime();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        long startTime = calendar.getTime().getTime(); //当前日期前一天
        requestAuth();
        querySleepData(startTime, endTime);

//        queryWearDeviceInfo();
//        queryProfileInfo();
//        querySportSum();
//        querySingleSportRun();
//        queryBodyComposition();
//        querySleepData();
//        queryHeartRate();
//        startRealtimeSport();
//        startRealtimeHeartRate();
//        stopRealtimeHeartRate();
//        startRealtimeRRI();
    }

    /**
     * 请求鉴权
     */
    private void requestAuth() {
        SensorManager sensorManager = SensorManager.getInstance();

        //配置运动健康数据权限，需与开发者联盟申请“申请运动健康服务”时保持一致
        //目前HiResearch 只提供运动健康读数据，不提供写操作
        //设置个人数据访问权限
        PersonalDataPermissionConfig userProfile = new PersonalDataPermissionConfig();
        //赋予身高、体重和运动目标数据 只读权限
        userProfile.userProfileFeature();
        //赋予华为账号中的性别和年龄 只读权限
        userProfile.userSexAge();

        //设置健康数据访问权限
        HealthDataPermissionConfig healthDataPermissionConfig = new HealthDataPermissionConfig();
        //赋予睡眠只读权限
        healthDataPermissionConfig.sleep();
        //赋予体重只读权限
        healthDataPermissionConfig.weight();
        //赋予心率只读权限
        healthDataPermissionConfig.heartRate();
//        //赋予血压只读权限
//        healthDataPermissionConfig.bloodPressure();
//        //赋予血糖只读权限
//        healthDataPermissionConfig.bloodSugar();
//        //赋予血氧只读权限
//        healthDataPermissionConfig.spo2();
        //赋予实时心率只读权限
        healthDataPermissionConfig.heartRealTime();


        //设置运动数据访问权限
        SportDataPermissionConfig sportDataPermissionConfig = new SportDataPermissionConfig();
        //赋予运动量只读权限
        sportDataPermissionConfig.sportSum();
        //赋予单次运动只读权限
        sportDataPermissionConfig.motion();
        //赋予实时运动只读权限
        sportDataPermissionConfig.sportRealTime();

        //4.设置设备数据访问权限
//        DeviceDataPermissionConfig dataPermissionConfig = new DeviceDataPermissionConfig();
        //赋予运动健康APP已绑定设备的信息只读权限
//        dataPermissionConfig.deviceInfomation();


        HiResearchDataPermissionConfig permissionConfig = new HiResearchDataPermissionConfig();
        permissionConfig.setUserProfile(userProfile);
        permissionConfig.setHealth(healthDataPermissionConfig);
        permissionConfig.setSport(sportDataPermissionConfig);
//        permissionConfig.setDevice(dataPermissionConfig);

        //请求鉴权
        mDisposable.add(sensorManager.requestAuth(permissionConfig)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        //TODO:请求鉴权成功
                        Log.i(TAG, "请求鉴权成功");
                    } else {
                        //TODO:请求鉴权失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.i(TAG, "请求鉴权失败" + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    Log.i(TAG, "请求鉴权异常");
                    //TODO:请求鉴权异常
                }));
    }

    /**
     * 查询连接运动健康App的设备数据
     */
    private void queryWearDeviceInfo() {
        DeviceProvider deviceProvider = SensorManager.getInstance().getDeviceProvider();
        mDisposable.add(deviceProvider.getAllDeviceList()
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        //TODO:成功
                        Log.i(TAG, "查询设备数据成功");
                        List<WearDeviceInfo> data = resp.getData();
                        if (null != data) {
                            for (WearDeviceInfo item : data) {
                                //设备唯一编号
                                String id = item.getId();
                                Log.i(TAG, "id: " + id);
                                //设备名称
                                String deviceName = item.getDeviceName();
                                Log.i(TAG, "deviceName: " + deviceName);
                                //设备版本
                                String version = item.getVersion();
                                Log.i(TAG, "version: " + version);
                                //设备类型
                                int productType = item.getProductType();
                                Log.i(TAG, "productType: " + productType);
                                //设备连接状态
                                int deviceConnectState = item.getDeviceConnectState();
                                Log.i(TAG, "deviceConnectState: " + deviceConnectState);
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "请求设备数据失败" + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    //TODO：异常
                })
        );
    }

    /**
     * 查看华为账号中的性别和年龄
     */
    private void queryProfileInfo() {
        UserProvider userProvider = SensorManager.getInstance().getUserProvider();
        mDisposable.add(userProvider.queryProfileInfo()
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        //TODO:成功
                        Log.i(TAG, "查询账号信息成功");
                        UserProfileInfo userProfileInfo = resp.getData();
                        if (null != userProfileInfo) {
                            //年龄
                            int birthday = userProfileInfo.getBirthday();
                            Log.i(TAG, "birthday: " + birthday);
                            //性别
                            int gender = userProfileInfo.getGender();
                            Log.i(TAG, "gender: " + gender);
                            //身高
                            int height = userProfileInfo.getHeight();
                            Log.i(TAG, "height: " + height);
                            //体重
                            float weight = userProfileInfo.getWeight();
                            Log.i(TAG, "weight: " + weight);
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "请求账号信息失败" + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    //TODO：异常
                })
        );
    }

    /**
     * 查询运动量
     */
    private void querySportSum() {
        SportProvider sportProvider = SensorManager.getInstance().getSportProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(startTime); //2020.01.01 00:00:00
        //设置查询终止时间戳
        dataQuery.setEndTime(endTime);   //2020.06.01 00:00:00
        //设置查询选项
        QueryOption queryOption = new QueryOption();
        //设置启始查询偏移量（用于分页查询）
        queryOption.setOffset(0);
        //设置每次查询最多记录数
        queryOption.setLimit(100);
        //设置排序 0:默认升序 1：降序
        queryOption.setOrder(0);
        dataQuery.setOption(queryOption);

        mDisposable.add(sportProvider.querySportSum(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        Log.i(TAG, "查询运动量成功");
                        //TODO:成功
                        List<SportSumData> data = resp.getData();
                        if (null != data) {
                            for (SportSumData item : data) {
                                //当日总步数
                                int steps = item.getSteps();
                                //当日运动距离
                                int distance = item.getDistance();
                                //当日消耗卡路里
                                int calories = item.getCalories();
                                //当日运动强度
                                int intensity = item.getIntensity();
                                Log.i(TAG, "steps: " + steps + " distance: " + distance + " calories: " + calories + " intensity: " + intensity);
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "查询运动量失败" + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    //TODO：异常
                })
        );
    }

    /**
     * 查询单次跑步运动数据
     */
    private void querySingleSportRun() {
        SportProvider sportProvider = SensorManager.getInstance().getSportProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(1577808000000L);
        //设置查询终止时间戳
        dataQuery.setEndTime(1590940800000L);
        //设置查询选项
        QueryOption queryOption = new QueryOption();
        //设置启始查询偏移量（用于分页查询）
        queryOption.setOffset(0);
        //设置每次查询最多记录数
        queryOption.setLimit(100);
        //设置排序 0:默认升序 1：降序
        queryOption.setOrder(0);
        dataQuery.setOption(queryOption);
        mDisposable.add(sportProvider.querySingleSportRun(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        Log.i(TAG, "查询单次跑步运动数据成功");
                        //TODO:成功
                        List<SingleSportData> data = resp.getData();
                        if (null != data) {
                            for (SingleSportData item : data) {
                                //距离
                                int distance = item.getDistance();
                                //卡路里
                                int calories = item.getCalories();
                                //运动时长
                                int totalTime = item.getTotalTime();
                                //平均速度
                                double avgSpeed = item.getAvgSpeed();
                                //平均配速
                                float avgPace = item.getAvgPace();
                                //平均步频
                                int avgStepRage = item.getAvgStepRage();
                                //步幅
                                double stepDistance = item.getStepDistance();
                                //步数
                                int step = item.getStep();
                                //平均心率
                                int avgHeartRate = item.getAvgHeartRate();
                                //总高度
                                float toatalAltitude = item.getTotalAltitude();
                                //总偏移
                                float totalDescent = item.getTotalDescent();
                                Log.i(TAG, "distance: " + distance + " calories: " + calories + " totalTime: " + totalTime + " avgSpeed: " + avgSpeed + " avgPace: " + avgPace
                                + " avgStepRage: " + avgStepRage + " stepDistance: " + stepDistance + " step: " + step + " avgHeartRate: " + avgHeartRate + " toatalAltitude: " + toatalAltitude + "totalDescent: " + totalDescent);
                            }
                        }
                    } else {
                        //TODO:失败
                        Log.e(TAG, "查询单次跑步运动数据失败");
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                    }
                }, err -> {
                    //TODO：异常
                })
        );
    }

    /**
     * 查询睡眠数据
     */
    private List<SleepData> querySleepData(long startTime, long endTime) {

        final List<SleepData>[] data = new List[]{new ArrayList<>()};

        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(startTime);
        //设置查询终止时间戳
        dataQuery.setEndTime(endTime);
        //设置查询选项，用于分页查询
        QueryOption queryOption = new QueryOption();
        //分页即限制返回记录条数
        queryOption.setLimit(100);
        //排序，0默认排序（升序）,1降序
        queryOption.setOrder(1);
        dataQuery.setOption(queryOption);
        mDisposable.add(healthProvider.querySleepData(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        Log.i(TAG, "查询睡眠数据成功");
                        data[0] = resp.getData();
                        //sleepDataList[0] = data;
//                        Intent intent = new Intent(SportHealthActivity.this, DataUploadActivity.class);
//                        intent.putExtra("SleepDataList", new Gson().toJson(data));
//                        startActivity(intent);
//                        uploadData(Constant.PROJECT_CODE, data);
                        getSimpleSleepReport(data[0].get(0));
                        if (null != data) {
                            for (SleepData item : data[0]) {
                                //日期
                                int date = item.getDate();
                                // 睡眠得分
                                int sleepScore = item.getSleepScore();
                                // 入睡时间
                                long sleepStartTime = item.getStartTime();
                                //出睡时间
                                long sleepEndTime = item.getEndTime();
                                //浅睡时长(分钟)
                                int lightSleep = item.getLightSleep();
                                //深睡时长(分钟)
                                int deepSleep = item.getDeepSleep();
                                //睡梦时长(分钟)
                                int dreamTime = item.getDreamTime();
                                //深睡连续性得分
                                int deepSleepScore = item.getDeepSleepScore();
                                //夜间睡眠时长(分钟)
                                int nightSleepTime = item.getNightSleepTime();
                                //睡眠总时长(分钟)
                                int sleepTotalTime = item.getSleepTotalTime();
                                //清醒次数
                                int wakeUpTimes = item.getWakeUpTimes();
                                Log.i(TAG, "date：" + date + " sleepStartTime: " + Tools.timestampToString(sleepStartTime)  + " sleepEndTime: " + Tools.timestampToString(sleepEndTime) + " sleepScore: " + sleepScore + " lightSleep: " + lightSleep + " deepSleep: " + deepSleep + " dreamTime: " + dreamTime + " deepSleepScore: " + deepSleepScore + " nightSleepTime: " + nightSleepTime + " sleepTotalTime: " + sleepTotalTime + " wakeUpTimes: " + wakeUpTimes);
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "查询睡眠数据失败" + " code: " + code + " msg: " + msg);
                    }

                }, err -> {
                    //TODO:异常
                }));
        return data[0];
    }

    //生成睡眠简短报告
    public void getSimpleSleepReport(SleepData data) {
        Log.i(TAG, "getSimpleSleepReport: " + data.getSleepScore());
        int date = data.getDate();
        // 睡眠得分
        int sleepScore = data.getSleepScore();
        // 入睡时间
        long sleepStartTime = data.getStartTime();
        //出睡时间
        long sleepEndTime = data.getEndTime();
        //浅睡时长(分钟)
        int lightSleep = data.getLightSleep();
        //深睡时长(分钟)
        int deepSleep = data.getDeepSleep();
        //睡梦时长(分钟)
        int dreamTime = data.getDreamTime();
        //深睡连续性得分
        int deepSleepScore = data.getDeepSleepScore();
        //夜间睡眠时长(分钟)
        int nightSleepTime = data.getNightSleepTime();
        //睡眠总时长(分钟)
        int sleepTotalTime = data.getSleepTotalTime();
        //清醒次数
        int wakeUpTimes = data.getWakeUpTimes();


        String descriptionOfSleepScore = "";
        if(sleepScore >= 90 && sleepScore <= 100) {
            descriptionOfSleepScore = "棒棒哒";
        } else if(sleepScore >= 80 && sleepScore <= 89) {
            descriptionOfSleepScore = "还不错哦";
        } else if(sleepScore >= 70 && sleepScore <= 79) {
            descriptionOfSleepScore = "有待提高哦";
        } else {
            descriptionOfSleepScore = "要引起注意啦";
        }

        List<String> sleepProblems = new ArrayList<>();
//        sleepProblems.add("睡眠时长过短");    //小于6小时，美国国家睡眠基金会建议成年人可能适合的睡眠时长为6-10小时
//        sleepProblems.add("睡眠时长过长");    //大于10小时
//        sleepProblems.add("入睡时间过晚");    //晚于1点，能否让用户自己设定
//        sleepProblems.add("深睡比例偏低");    //小于20%
//        sleepProblems.add("深睡比例偏高");    //大于60%
//        sleepProblems.add("浅睡比例偏高");    //大于55%
//        sleepProblems.add("快速眼动比例偏低");    //小于10%
//        sleepProblems.add("快速眼动比例偏高");    //大于30%
//        sleepProblems.add("深睡连续性偏低");    //小于70分
//        sleepProblems.add("清醒次数偏高");    //大于3次

        if(nightSleepTime < 6 * 60) {
            sleepProblems.add("睡眠时间有点短, 只有" + nightSleepTime / 60 + "小时" + nightSleepTime % 60 + "分钟。");
        } else if(nightSleepTime > 10 * 60) {
            sleepProblems.add("睡眠时间有点长, 有" + nightSleepTime / 60 + "小时" + nightSleepTime % 60 + "分钟。");
        }

        int hour = Tools.getHourByTimestamp(sleepStartTime);
        int minute = Tools.getMinuteByTimeStamp(sleepStartTime);
        if(hour >= 1 && hour <= 5) {
            sleepProblems.add("入睡时间是" + hour + "点" + minute + "分, 有点晚哦。");
        }

        int deepProp = Math.round((float)deepSleep / sleepTotalTime * 100);
        if(deepProp < 20) {
            sleepProblems.add("深睡时间比例偏低。");
        } else if (deepProp > 60) {
            sleepProblems.add("深睡时间比例偏高。");
        }

        int lightProp = Math.round((float)lightSleep / sleepTotalTime * 100);
        if(lightProp > 55) {
            sleepProblems.add("浅睡时间比例偏高。");
        }

        int dreamProp = Math.round((float)dreamTime / sleepTotalTime * 100);
        if(dreamProp < 10) {
            sleepProblems.add("快速眼动比例偏低。");
        } else if (dreamProp > 30){
            sleepProblems.add("快速眼动比例偏高");
        }

        if(deepSleepScore < 70) {
            sleepProblems.add("深睡连续性偏低。");
        }

        if(wakeUpTimes >= 3) {
            sleepProblems.add("睡觉期间您一共醒来" + wakeUpTimes + "次, 安静、无光的睡眠环境能让您睡得更香哦。");
        }

        String simpleSleepReport = "您昨晚的睡眠得分是" + data.getSleepScore() + "分, " + descriptionOfSleepScore + "。";
        for (String problem : sleepProblems) {
            simpleSleepReport += problem;
        }
        Log.i(TAG, "getSimpleSleepReport: " + simpleSleepReport);
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", simpleSleepReport);
        //设置返回数据
        SportHealthActivity.this.setResult(Constant.RESULT_OK, intent);
        //关闭Activity
        SportHealthActivity.this.finish();
    }

    //上传标准化运动健康数据
//    public <T extends SensorData> void uploadData(String projectCode, List<T > data) {
    public void uploadData(String projectCode, List<SleepData> data) {
        BridgeDataProvider bridgeDataProvider = BridgeManager2.getInstance(projectCode).getBridgeDataProvider();
        mDisposable.add(bridgeDataProvider.uploadSportHealth(data, new OnMetadataUploadProgressChanged() {
            @Override
            public void onCompressProgress(BinaryProgressStatus status) {
                //TODO：压缩进度
                //总进度值
                int maxValue = status.getMaxValue();
                //当前进度值
                int currentValue =status.getCurrentValue();
                Log.i(TAG, "压缩进度 " + "maxValue: " + maxValue + " currentValue: " + currentValue);
            }

            @Override
            public void onCompressComplete(MetadataCompressResultInfo compressResp) {
                //TODO：压缩完成
                Log.i(TAG, "压缩完成");
            }

            @Override
            public void onUploadProgress(BinaryProgressStatus status) {
                //TODO：标准化运动健康数据上传进度
                //总进度值
                int maxValue = status.getMaxValue();
                //当前进度值
                int currentValue =status.getCurrentValue();
                Log.i(TAG, "上传进度 " + "maxValue: " + maxValue + " currentValue: " + currentValue);
            }
        }).subscribe(resp -> {
            if (resp.getSuccess()) {
                //TODO:上传成功
                Log.i(TAG, "上传成功");
            } else {
                //TODO：上传失败
                int statusCode = resp.getStatusCode(); //HTTP状态码
                String code = resp.getCode(); //内部错误码
                String msg = resp.getMessage(); //错误信息描述
                Log.e(TAG, "上传失败 " + "statusCode: " + statusCode + " code: " + code + " msg: " + msg);
            }
        }, err -> {
            //TODO:上传标准化运动健康数据异常
            Log.e(TAG, "上传标准化运动健康数据异常" + err);
        }));
    }


    /**
     * 查询体成分数据
     */
    private void queryBodyComposition() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(1577808000000L);
        //设置查询终止时间戳
        dataQuery.setEndTime(new Date().getTime());
        QueryOption queryOption = new QueryOption();
        //分页即限制返回记录条数
        queryOption.setLimit(10);
        //排序，0默认排序（升序）,1降序
        queryOption.setOrder(1);
        dataQuery.setOption(queryOption);
        mDisposable.add(healthProvider.queryBodyComposition(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        Log.i(TAG, "查询体成分数据成功");
                        List<BodyCompositionData> data = resp.getData();
                        if (null != data) {
                            for (BodyCompositionData item : data) {
                                //水分
                                double moisture = item.getMoisture();
                                //肌肉量
                                double muscles = item.getMuscles();
                                //体重
                                double weight = item.getWeight();
                                //bmi
                                double bmi = item.getBmi();
                                //体脂
                                double bodyFat = item.getBodyFat();
                                //bmr 基础代谢率
                                double bmr = item.getBmr();
                                //水分率
                                double moistureRate = item.getMoistureRate();
                                //内脏脂肪等级
                                double fatLevel = item.getFatLevel();
                                //骨盐量
                                double boneMineral = item.getBoneMineral();
                                //蛋白质
                                double protein = item.getProtein();
                                //身体年龄
                                int bodyAge = item.getBodyAge();
                                // 身体得分
                                int bodyScore = item.getBodyScore();
                                //骨骼肌量
                                double impedance = item.getImpedance();
                                Log.i(TAG, "moisture: " + moisture + " muscles: " + muscles + " weight: " + weight + " bmi: " + bmi + " bodyFat: " + bodyFat + " bmr: " + bmr + " moistureRate: " + moistureRate
                                + " fatLevel: " + fatLevel + " boneMineral: " + boneMineral + " protein: " + protein + " bodyAge: " + bodyAge + " bodyScore: " + bodyScore + " impedance: " + impedance);
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "查询体成分数据失败" + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    //TODO:异常
                }));
    }

    /**
     * 查询心率数据
     */
    private void queryHeartRate() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(1577808000000L);
        //设置查询终止时间戳
        dataQuery.setEndTime(1590940800000L);
        QueryOption queryOption = new QueryOption();
        //分页即限制返回记录条数
        queryOption.setLimit(10);
        //排序，0默认排序（升序）,1降序
        queryOption.setOrder(1);
        dataQuery.setOption(queryOption);
        mDisposable.add(healthProvider.queryHeartRate(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        Log.i(TAG, "请求心率数据成功");
                        List<HeartRateData> data = resp.getData();
                        if (null != data) {
                            for (HeartRateData item : data) {
                                //最高心率
                                int maxHeartRate = item.getMaxHeartRate();
                                //最低心率
                                int minHeartRate = item.getMinHeartRate();
                                //静息心率
                                float restHeartRate = item.getRestHeartRate();
                                Log.i(TAG, "maxHeartRate: " + maxHeartRate + " minHeartRate: " + minHeartRate + " restHeartRate: " + restHeartRate);
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                        Log.e(TAG, "请求心率数据失败" + " code: " + code + " msg: " + msg);
                    }
                }, err -> {
                    //TODO:异常
                }));
    }

    /**
     * 查询血压数据
     */
    private void queryBloodPressure() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(1577808000000L);
        //设置查询终止时间戳
        dataQuery.setEndTime(1590940800000L);
        QueryOption queryOption = new QueryOption();
        //分页即限制返回记录条数
        queryOption.setLimit(10);
        //排序，0默认排序（升序）,1降序
        queryOption.setOrder(1);
        dataQuery.setOption(queryOption);
        mDisposable.add(healthProvider.queryBloodPressure(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        List<BloodPressureData> data = resp.getData();
                        if (null != data) {
                            for (BloodPressureData item : data) {
                                //收缩压
                                int systolic = item.getSystolic();
                                //舒张压
                                int diastolic = item.getDiastolic();
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                    }
                }, err -> {
                    //TODO:异常
                }));
    }

    /**
     * 查询血糖数据
     */
    private void queryBloodSugar() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(1577808000000L);
        //设置查询终止时间戳
        dataQuery.setEndTime(1590940800000L);
        QueryOption queryOption = new QueryOption();
        //分页即限制返回记录条数
        queryOption.setLimit(10);
        //排序，0默认排序（升序）,1降序
        queryOption.setOrder(1);
        dataQuery.setOption(queryOption);
        mDisposable.add(healthProvider.queryBloodSugar(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        List<BloodSugarData> data = resp.getData();
                        if (null != data) {
                            for (BloodSugarData item : data) {
                                //早餐前 血糖
                                float beforeBreakfast = item.getBeforeBreakfast();
                                //早餐后 血糖
                                float afterBreakfast = item.getAfterBreakfast();
                                //午餐前 血糖
                                float beforeLunch = item.getBeforeLunch();
                                //午餐后 血糖
                                float afterLunch = item.getAfterLunch();
                                //晚餐前 血糖
                                float beforeDinner = item.getBeforeDinner();
                                //晚餐后 血糖
                                float afterDinner = item.getAfterDinner();
                                //睡前 血糖
                                float beforeSleep = item.getBeforeSleep();
                                //凌晨 血糖
                                float earlyMorning = item.getEarlyMorning();
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                    }
                }, err -> {
                    //TODO:异常
                }));
    }

    /**
     * 查询血氧数据
     */
    private void querySpo2() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        //构建查询条件
        DataQuery dataQuery = new DataQuery();
        //设置查询起始时间戳
        dataQuery.setStartTime(1577808000000L);
        //设置查询终止时间戳
        dataQuery.setEndTime(1590940800000L);
        QueryOption queryOption = new QueryOption();
        //分页即限制返回记录条数
        queryOption.setLimit(10);
        //排序，0默认排序（升序）,1降序
        queryOption.setOrder(1);
        dataQuery.setOption(queryOption);
        mDisposable.add(healthProvider.querySpo2(dataQuery)
                .subscribe(resp -> {
                    if (resp.getSuccess()) {
                        List<Spo2Data> data = resp.getData();
                        if (null != data) {
                            for (Spo2Data item : data) {
                                //最大血氧值
                                int maxSpo2 = item.getMaxSpo2();
                                //最低血氧
                                int minSpo2 = item.getMinSpo2();
                                //平均血氧
                                int meanSpo2 = item.getMeanSpo2();
                                //每日最后一个血氧值
                                int lastSpo2 = item.getLastSpo2();
                            }
                        }
                    } else {
                        //TODO:失败
                        String code = resp.getCode(); //内部错误码
                        String msg = resp.getMessage(); //错误信息描述
                    }
                }, err -> {
                    //TODO:异常
                }));
    }

    //开始采集实时运动数据

    private void startRealtimeSport() {
        SportProvider sportProvider = SensorManager.getInstance().getSportProvider();
        sportProvider.startRealtimeSport(new RealTimeSportDataReadListener<SportRealtimeData>() {
            /**
             * 实时上报
             * @param data
             */
            @Override
            public void onRunning(SportRealtimeData data) {
                //运动状态：0空闲，1运动中，2运动暂停，3运动停止
                int sportState = data.getSportState();
                // 距离
                int distance = data.getDistance();
                //跑步时长
                int duration = data.getDuration();
                //心率
                int heartRate = data.getHeartRate();
                //速度
                float speed = data.getSpeed();
            }

            /**
             * 实时数据采集暂停
             */
            @Override
            public void onPause() {

            }

            /**
             * 继续采集
             */
            @Override
            public void onContinue() {

            }

            /**
             * 停止
             */
            @Override
            public void onStop() {

            }

            /**
             * 实时数据采集异常
             * @param code 错误码
             * @param msg  错误信息
             */
            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    //停止实时运动数据采集
    private void stopRealtimeSport() {
        SportProvider sportProvider = SensorManager.getInstance().getSportProvider();
        sportProvider.stopRealtimeSport(new RealTimeDataStopListener() {
            /**
             * 停止成功
             */
            @Override
            public void onSuccess() {

            }

            /**
             * 停止异常
             * @param errorCode
             * @param msg
             */
            @Override
            public void onError(int errorCode, String msg) {

            }
        });
    }


    /**
     * 开始读取心率实时数据
     */
    private void startRealtimeHeartRate() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        healthProvider.startRealtimeHeartRate(new RealTimeDataReadListener<HeartRateRealTimeData>() {
            /**
             * 读取实时心率
             * @param data
             */
            @Override
            public void onDataChange(HeartRateRealTimeData data) {
                //心率值
                int hr = data.getHr();
                //信号量
                int sqi = data.getSqi();
                //时间戳
                long timeStamp = data.getTimeStamp();
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeStamp);
                Log.i(TAG, "onDataChange: " + " hr: " + hr + " sqi: " + sqi + " timeStamp" + time);
            }

            /**
             * 实时心率采集异常
             * @param errorCode 错误码
             * @param msg 错误描述信息
             */
            @Override
            public void onError(int errorCode, String msg) {

            }
        });
    }

    /**
     * 停止读取心率实时数据
     */
    private void stopRealtimeHeartRate() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        healthProvider.stopRealtimeHeartRate(new RealTimeDataStopListener() {
            /**
             * 停止成功
             */
            @Override
            public void onSuccess() {

            }

            /**
             * 异常
             * @param errorCode
             * @param msg
             */
            @Override
            public void onError(int errorCode, String msg) {

            }
        });
    }

    /**
     * 开始读取RRI
     */
    private void startRealtimeRRI() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        healthProvider.startRealtimeRRI(new RealTimeDataReadListener<RRIRealTimeData>() {
            /**
             * 读取实时RRI
             * @param data
             */
            @Override
            public void onDataChange(RRIRealTimeData data) {
                //时间戳
                long timeStamp = data.getTimeStamp();
                //强度
                int intensity = data.getIntensity();
                //rri 列表
                List<RRIData> rriList = data.getRriList();
            }

            /**
             * 实时数据读取异常
             * @param errorCode
             * @param msg
             */
            @Override
            public void onError(int errorCode, String msg) {

            }
        });
    }

    /**
     * 停止读取RRI
     */
    private void stopRealtimeRRI() {
        HealthProvider healthProvider = SensorManager.getInstance().getHealthProvider();
        healthProvider.stopRealtimeRRI(new RealTimeDataStopListener() {
            /**
             * 停止成功
             */
            @Override
            public void onSuccess() {

            }

            /**
             * 停止异常
             * @param errorCode
             * @param msg
             */
            @Override
            public void onError(int errorCode, String msg) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
