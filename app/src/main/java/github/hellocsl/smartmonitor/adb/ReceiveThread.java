package github.hellocsl.smartmonitor.adb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import github.hellocsl.smartmonitor.adb.core.AdbStream;
import github.hellocsl.smartmonitor.utils.LogUtils;

/**
 * Created by yebo on 2016/11/1.
 */

public class ReceiveThread extends Thread {

    private static final String TAG = ReceiveThread.class.getSimpleName();

    private AdbStream mAdbStream;

    public ReceiveThread(AdbStream stream) {
        mAdbStream = stream;
    }

    @Override
    public void run() {
        while (!mAdbStream.isClosed())
            try {
                // Print each thing we read from the shell stream
                String result = new String(mAdbStream.read(), "US-ASCII");
                LogUtils.i(TAG, result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
