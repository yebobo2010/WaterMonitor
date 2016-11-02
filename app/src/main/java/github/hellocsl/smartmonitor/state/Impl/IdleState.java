package github.hellocsl.smartmonitor.state.Impl;

import android.app.Notification;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.hellocsl.smartmonitor.AppApplication;
import github.hellocsl.smartmonitor.state.IMonitorService;
import github.hellocsl.smartmonitor.state.MonitorState;
import github.hellocsl.smartmonitor.utils.AppUtils;
import github.hellocsl.smartmonitor.utils.Constant;
import github.hellocsl.smartmonitor.utils.LogUtils;
import github.hellocsl.smartmonitor.utils.RootCmd;
import github.hellocsl.smartmonitor.utils.UnLockUtils;

import static android.view.accessibility.AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
import static github.hellocsl.smartmonitor.utils.Constant.MONITOR_TAG;

/**
 * 初始状态，等待来电处理
 * change to monitor QQ new message (LockScreen, Notification , QQ App)
 * Created by chensuilun on 16-10-9.
 */
public class IdleState extends MonitorState {

    private static final String TAG = IdleState.class.getSimpleName();

    private String qqNumber;

    public IdleState(IMonitorService contextService) {
        super(contextService);
    }

    @Override
    public void handle(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo nodeInfo = mContextService.getWindowNode();

        LogUtils.v(TAG, "------handle AccessibilityNodeInfo : " + nodeInfo);
        if (nodeInfo == null) {
            return;
        }
        boolean lockScreenMonitorMsg = isLockScreenMonitorMsg(nodeInfo, accessibilityEvent);
        boolean notificationMonitorMsg = isNotificationMonitorMsg(accessibilityEvent);
        if (lockScreenMonitorMsg || notificationMonitorMsg) {
            LogUtils.d(TAG, "handle: monitor msg");
            if (AppUtils.isInLockScreen()) {
                LogUtils.d(TAG, "handle: unlock screen");
                //back press
                RootCmd.execRootCmd("input keyevent " + KeyEvent.KEYCODE_BACK);
                RootCmd.execRootCmd("sleep 0.1 && input keyevent " + KeyEvent.KEYCODE_HOME);
                unlockScreen(nodeInfo);
            }

            qqNumber = retrieveQQNumber(nodeInfo, accessibilityEvent);
            Pattern pattern = Pattern.compile("^[1-9][0-9]{4,} $");
            Matcher matcher = pattern.matcher(qqNumber);
            if (!matcher.matches()) {
                qqNumber = Constant.QQ_NUMBER;
            }
            mContextService.setState(new QQChatState(mContextService));
            AppApplication.postDelay(new Runnable() {
                @Override
                public void run() {
                    AppUtils.openQQChat(qqNumber);
                }
            }, 1000);
        }
    }

    /**
     * retract monitor cmd from notification
     *
     * @param accessibilityEvent
     * @return
     */
    private boolean isNotificationMonitorMsg(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == TYPE_NOTIFICATION_STATE_CHANGED) {
            LogUtils.d(TAG, "-----isNotificationMonitorMsg: ");
            Parcelable data = accessibilityEvent.getParcelableData();
            if (data instanceof Notification) {
                if (((Notification) data).tickerText != null) {
                    String tickerText = ((Notification) data).tickerText.toString();
                    return tickerText.endsWith(Constant.MONITOR_CMD_VIDEO);
                }
            }
        }
        return false;
    }

    /**
     * @param nodeInfo
     * @param accessibilityEvent
     * @return If from notification ,msg format :{@link Constant#MONITOR_TAG} + ":real QQ No: "+{@link Constant#MONITOR_CMD_VIDEO}
     */
    private String retrieveQQNumber(AccessibilityNodeInfo nodeInfo, AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable data = accessibilityEvent.getParcelableData();
            if (data instanceof Notification) {
                if (((Notification) data).tickerText != null) {
                    String qqNickName = ((Notification) data).tickerText.toString().split(":")[1];
                    return qqNickName.trim();
                }
            }
        } else {
            List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(MONITOR_TAG);
            if (!AppUtils.isListEmpty(nodeInfos)) {
                String tag;
                for (AccessibilityNodeInfo info : nodeInfos) {
                    LogUtils.d(TAG, "retrieveQQNumber: " + info.getText());
                    tag = (String) info.getText();
                    if (!TextUtils.isEmpty(tag) && tag.contains(MONITOR_TAG)) {
                        return tag.substring(Constant.MONITOR_TAG.length());
                    }
                }
            }
        }
        return Constant.QQ_NUMBER;
    }

    /**
     * receive monitor cmd in LockScreen
     *
     * @param nodeInfo
     * @param accessibilityEvent
     * @return
     */
    private boolean isLockScreenMonitorMsg(AccessibilityNodeInfo nodeInfo, AccessibilityEvent accessibilityEvent) {
        LogUtils.d(TAG, "isMonitorMsg: pkg:" + nodeInfo.getPackageName());
        if (AppUtils.isInLockScreen() && Constant.QQ_PKG.equals(nodeInfo.getPackageName()) && TYPE_WINDOW_CONTENT_CHANGED == accessibilityEvent.getEventType()) {
            if (!AppUtils.isListEmpty(nodeInfo.findAccessibilityNodeInfosByText(MONITOR_TAG))
                    && !AppUtils.isListEmpty(nodeInfo.findAccessibilityNodeInfosByText(Constant.MONITOR_CMD_VIDEO))) {
                return true;
            }
        }
        return false;
    }


    /**
     * 解锁魅族
     *
     * @param nodeInfo
     */
    private void unlockScreen(AccessibilityNodeInfo nodeInfo) {
        UnLockUtils.unlock();
    }


}
