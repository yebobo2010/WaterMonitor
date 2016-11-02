package github.hellocsl.smartmonitor.state.Impl;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;
import java.util.List;

import github.hellocsl.smartmonitor.AppApplication;
import github.hellocsl.smartmonitor.state.IMonitorService;
import github.hellocsl.smartmonitor.state.MonitorState;
import github.hellocsl.smartmonitor.utils.LogUtils;

import static github.hellocsl.smartmonitor.utils.AppUtils.isListEmpty;

/**
 * 视频聊天状态，找到视频电话按钮并点击
 * Created by chensuilun on 16-10-9.
 */
public class StartVideoState extends MonitorState {
    private static final String TAG = "StartVideoState";

    public StartVideoState(IMonitorService contextService) {
        super(contextService);
    }

    @Override
    public void handle(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo nodeInfo = mContextService.getWindowNode();
        if (nodeInfo == null) {
            LogUtils.v(TAG, "handle: null nodeInfo");
            return;
        }
        LogUtils.v(TAG, "handle:");
        if (startVideoChat(nodeInfo)) {
            LogUtils.d(TAG, "handle: start suc");

            new Thread(){
                @Override
                public void run() {
                    while(true) {
                        setMinVolume();
                        closeSpeaker();
                    }
                }
            }.start();

            mContextService.setState(new EndingSate(mContextService));
        } else {
            mContextService.setState(new QQChatState(mContextService));
        }
    }

    private void setMinVolume() {
        AudioManager audio = (AudioManager) AppApplication.getContext().getSystemService(Service.AUDIO_SERVICE);

        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audio.setStreamVolume(AudioManager.STREAM_DTMF, 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    /**
     * 点击视频聊天按钮
     * @param nodeInfo
     * @return 是否成功
     */
    private boolean startVideoChat(AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> videoTextNodes = nodeInfo.findAccessibilityNodeInfosByText("视频电话");
        if (!isListEmpty(videoTextNodes)) {
            for (AccessibilityNodeInfo textNode : videoTextNodes) {
                if (textNode.getClassName().toString().contains(RelativeLayout.class.getName())) { //contextDesc
                    textNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean closeSpeaker(AccessibilityNodeInfo nodeInfo) {
        List<AccessibilityNodeInfo> videoTextNodes = nodeInfo.findAccessibilityNodeInfosByText("扬声器");
        if (!isListEmpty(videoTextNodes)) {
            for (AccessibilityNodeInfo textNode : videoTextNodes) {
                if (textNode.getClassName().toString().contains(RelativeLayout.class.getName())) { //contextDesc
                    textNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) AppApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            setSpeakerphoneOn(false, audioManager);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSpeakerphoneOn(boolean on, AudioManager audioManager) {
        try {
            //获得当前类
            Class audioSystemClass = Class.forName("android.media.AudioSystem");
            //得到这个方法
            Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);

            if (on) {
                audioManager.setMicrophoneMute(false);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                // setForceUse.invoke(null, 1, 1);
            } else {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                setForceUse.invoke(null, 0, 0);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
