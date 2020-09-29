package com.fudan.stress.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fudan.stress.R;
import com.fudan.stress.SportHealthActivity;
import com.fudan.stress.adapter.ChatAdapter;
import com.fudan.stress.bean.Message;
import com.fudan.stress.bean.MsgSendStatus;
import com.fudan.stress.bean.MsgType;
import com.fudan.stress.bean.TextMsgBody;
import com.fudan.stress.common.Constant;
import com.fudan.stress.util.ChatUiHelper;
import com.fudan.stress.widget.StateButton;
import com.google.gson.Gson;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.common_toolbar_back)
    RelativeLayout commonToolbarBack;
    @BindView(R.id.common_toolbar_title)
    TextView commonToolbarTitle;
    @BindView(R.id.common_toolbar)
    RelativeLayout commonToolbar;
    @BindView(R.id.rv_chat_list)
    RecyclerView rvChatList;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_send)
    StateButton btnSend;
    @BindView(R.id.llContent)
    LinearLayout llContent;


    SwipeRefreshLayout mSwipeRefresh;//下拉刷新
    private ChatAdapter mAdapter;
    public static final String 	  mSenderId = "right";
    public static final String    mTargetId = "left";
    public static final int       REQUEST_CODE_IMAGE = 0000;
    public static final int       REQUEST_CODE_VEDIO = 1111;
    public static final int       REQUEST_CODE_FILE = 2222;
    private static final String  TAG =  "ChatActivity";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initContent();
    }

    protected void initContent() {
        ButterKnife.bind(this);
        mAdapter = new ChatAdapter(this, new ArrayList<>());
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
        Log.i("ChatActivity", "rvChatList: " + rvChatList);
        Log.i("ChatActivity", "llContent: " + llContent);
        Log.i("ChatActivity", "etContent: " + etContent);
        rvChatList.setLayoutManager(mLinearLayout);
        rvChatList.setAdapter(mAdapter);
        initChatUi();
        startActivityForResult(new Intent(ChatActivity.this, SportHealthActivity.class), Constant.REQUEST_SLEEP_DATA);

//        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
//            @Override
//            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//
//                final boolean isSend = mAdapter.getItem(position).getSenderId().equals(ChatActivity.mSenderId);
//                if (ivAudio != null) {
//                    if (isSend){
//                        ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
//                    }else {
//                        ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
//                    }
//                    ivAudio = null;
//                    MediaManager.reset();
//                }else{
//                    ivAudio = view.findViewById(R.id.ivAudio);
//                    MediaManager.reset();
//                    if (isSend){
//                        ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
//                    }else {
//                        ivAudio.setBackgroundResource(R.drawable.audio_animation_left_list);
//                    }
//                    AnimationDrawable drawable = (AnimationDrawable) ivAudio.getBackground();
//                    drawable.start();
//                    MediaManager.playSound(ChatActivity.this,((AudioMsgBody)mAdapter.getData().get(position).getBody()).getLocalPath(), new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            if (isSend){
//                                ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
//                            }else {
//                                ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
//                            }
//
//                            MediaManager.release();
//                        }
//                    });
//                }
//            }
//        });
    }




    public void initReceive(String simpleSleepReport) {
        //下拉刷新模拟获取历史消息
        List<Message> mReceiveMsgList=new ArrayList<Message>();
        //构建欢迎文本消息
        Message welcomeMes = getBaseReceiveMessage(MsgType.TEXT);
        TextMsgBody welcomeTextMsgBody = new TextMsgBody();
        welcomeTextMsgBody.setMessage("欢迎使用睡眠聊天机器人~");
        welcomeMes.setBody(welcomeTextMsgBody);
        mReceiveMsgList.add(welcomeMes);
        //构建睡眠简报文本消息
        Message sleepMes = getBaseReceiveMessage(MsgType.TEXT);
        TextMsgBody sleepTextMsgBody = new TextMsgBody();
        sleepTextMsgBody.setMessage(simpleSleepReport);
        sleepMes.setBody(sleepTextMsgBody);
        mReceiveMsgList.add(sleepMes);
        mAdapter.addData(0,mReceiveMsgList);
        //构建图片消息
//        Message mMessgaeImage=getBaseReceiveMessage(MsgType.IMAGE);
//        ImageMsgBody mImageMsgBody=new ImageMsgBody();
//        mImageMsgBody.setThumbUrl("https://c-ssl.duitang.com/uploads/item/201208/30/20120830173930_PBfJE.thumb.700_0.jpeg");
//        mMessgaeImage.setBody(mImageMsgBody);
//        mReceiveMsgList.add(mMessgaeImage);
//        //构建文件消息
//        Message mMessgaeFile=getBaseReceiveMessage(MsgType.FILE);
//        FileMsgBody mFileMsgBody=new FileMsgBody();
//        mFileMsgBody.setDisplayName("收到的文件");
//        mFileMsgBody.setSize(12);
//        mMessgaeFile.setBody(mFileMsgBody);
//        mReceiveMsgList.add(mMessgaeFile);
//        mAdapter.addData(0,mReceiveMsgList);
//        mSwipeRefresh.setRefreshing(false);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, requestCode + " " + resultCode + " " + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SLEEP_DATA) {
            Log.i(TAG, "" + requestCode);
            if(resultCode == Constant.RESULT_OK) {
                Log.i(TAG, "" + resultCode);
                if(data != null) {
                    String simpleSleepReport = data.getStringExtra("result");
                    Log.i(TAG, simpleSleepReport);
                    initReceive(simpleSleepReport);
                }
            }
        }
    }

    private void initChatUi(){
        //mBtnAudio
        final ChatUiHelper mUiHelper= ChatUiHelper.with(this);
        mUiHelper.bindContentLayout(llContent)
                .bindttToSendButton(btnSend)
                .bindEditText(etContent);
        //底部布局弹出,聊天列表上滑
        rvChatList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvChatList.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter.getItemCount() > 0) {
                                rvChatList.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });
        //点击空白区域关闭键盘
        rvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                return false;
            }
        });

//        ((RecordButton) mBtnAudio).setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
//            @Override
//            public void onFinishedRecord(String audioPath, int time) {
//                LogUtil.d("录音结束回调");
//                File file = new File(audioPath);
//                if (file.exists()) {
//                    sendAudioMessage(audioPath,time);
//                }
//            }
//        });

    }

//    @OnClick({R.id.btn_send,R.id.rlPhoto,R.id.rlVideo,R.id.rlLocation,R.id.rlFile})
    @OnClick({R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                sendTextMsg(etContent.getText().toString());
                etContent.setText("");
                break;
//            case R.id.rlPhoto:
//                PictureFileUtil.openGalleryPic(ChatActivity.this,REQUEST_CODE_IMAGE);
//                break;
//            case R.id.rlVideo:
//                PictureFileUtil.openGalleryAudio(ChatActivity.this,REQUEST_CODE_VEDIO);
//                break;
//            case R.id.rlFile:
//                PictureFileUtil.openFile(ChatActivity.this,REQUEST_CODE_FILE);
//                break;
//            case R.id.rlLocation:
//                break;
        }
    }

    //文本消息
    private void sendTextMsg(String hello)  {
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(hello);
        mMessgae.setBody(mTextMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }


    private Message getBaseSendMessage(MsgType msgType){
        Message mMessgae=new Message();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private Message getBaseReceiveMessage(MsgType msgType){
        Message mMessgae=new Message();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mTargetId);
        mMessgae.setTargetId(mSenderId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private void updateMsg(final Message mMessgae) {
        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
        //模拟2秒后发送成功
        new Handler().postDelayed(new Runnable() {
            public void run() {
                int position=0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i=0;i<mAdapter.getData().size();i++){
                    Message mAdapterMessage=mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())){
                        position=i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 1000);

    }

}
