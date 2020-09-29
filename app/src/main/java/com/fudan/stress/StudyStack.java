package com.fudan.stress;

import android.content.Context;

import com.huawei.hiresearch.bridge.BridgeManager2;
import com.huawei.hiresearch.bridge.HiResearchBridgeStack;
import com.huawei.hiresearch.bridge.config.BridgeConfig;
import com.huawei.hiresearch.bridge.config.HttpClientConfig;
import com.huawei.hiresearch.research.ResearchManager2;
import com.huawei.hiresearch.research.config.ResearchConfig;

import java.util.concurrent.TimeUnit;

/*****************************************************************************************
 * Copyright (c) 2020, Huawei Technologies Co.,Ltd.All Rights Reserved.
 * File name: StudyStack
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
public class StudyStack extends HiResearchBridgeStack {

    public StudyStack(Context context, String projectCode, String baseUrl) {

        BridgeManager2.init(context);
        ResearchManager2.init(context);

        // http client 配置
        HttpClientConfig httpClientConfig = new HttpClientConfig(30, 30, 30, TimeUnit.SECONDS);
        // bridge配置
        BridgeConfig bridgeConfig = new BridgeConfig(context);
        // bridge 服务地址
        bridgeConfig.setBaseUrl(baseUrl);
        // 设置研究项目id
        bridgeConfig.setProjectCode(projectCode);
        BridgeManager2.getInstance().addStudyProject(httpClientConfig, bridgeConfig);
        ResearchManager2.getInstance().addStudyProject(new ResearchConfig(projectCode));
    }
}
