package com.fudan.stress.common;

public interface ICallBack {
    void onSuccess();

    void onSuccess(String result);

    void onFailed();
}
