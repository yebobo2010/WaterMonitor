package github.hellocsl.smartmonitor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.hellocsl.smartmonitor.R;
import github.hellocsl.smartmonitor.ui.widget.SettingItem;
import github.hellocsl.smartmonitor.utils.AppUtils;
import github.hellocsl.smartmonitor.utils.Constant;
import github.hellocsl.smartmonitor.utils.LogUtils;
import github.hellocsl.smartmonitor.utils.RootCmd;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.setting_start_service)
    SettingItem mSettingService;
    @BindView(R.id.setting_root)
    SettingItem mSettingRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSettingRoot.setCheck(RootCmd.haveRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume: ");
        mSettingRoot.setCheck(RootCmd.sHaveRoot);
        boolean isAccessibility = AppUtils.checkAccessibility(Constant.ACCESSIBILITY_SERVICE);
        mSettingService.setCheck(isAccessibility);
        if (!isAccessibility && RootCmd.haveRoot()) {
            AppUtils.gotoAccessibilitySettings(this);
//            CommandThread commandThread = new CommandThread("10.150.150.100");
//            commandThread.start();
//            commandThread.sendCmd("sleep 0.1 && input tap 564 774");
//            commandThread.sendCmd("sleep 0.1 && input tap 640 270");
//            commandThread.sendCmd("sleep 0.1 && input tap 800 1820");

            RootCmd.execRootCmdSilent("sleep 0.1 && input tap 564 774");
            RootCmd.execRootCmdSilent("sleep 0.1 && input tap 640 270");
            RootCmd.execRootCmdSilent("sleep 0.1 && input tap 800 1820");
            RootCmd.execRootCmdSilent("input keyevent " + KeyEvent.KEYCODE_HOME);
        }
    }

    @OnClick(R.id.setting_start_service)
    public void onClickService() {
        boolean pending = !mSettingService.isCheck();
        AppUtils.gotoAccessibilitySettings(this);
        mSettingService.setCheck(pending);
    }

    @OnClick(R.id.setting_root)
    public void onClickRoot() {
        if (!mSettingRoot.isCheck()) {
            mSettingRoot.setCheck(RootCmd.haveRoot());
        }
    }
}
