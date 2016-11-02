package github.hellocsl.smartmonitor.adb;

import android.text.TextUtils;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import github.hellocsl.smartmonitor.AppApplication;
import github.hellocsl.smartmonitor.adb.core.AdbBase64;
import github.hellocsl.smartmonitor.adb.core.AdbConnection;
import github.hellocsl.smartmonitor.adb.core.AdbCrypto;
import github.hellocsl.smartmonitor.adb.core.AdbStream;
import github.hellocsl.smartmonitor.utils.LogUtils;

/**
 * Created by yebo on 2016/11/1.
 */

public class CommandThread extends Thread {
    private static final String TAG = CommandThread.class.getSimpleName();

    private AdbConnection mAdbConnection;
    private Socket mSocket;
    private AdbCrypto mCrypto;
    private AdbStream mAdbStream;

    private String mIP;
    private boolean isStop = false;
    private String mCmd;

    public CommandThread(String ip) {
        mIP = ip;
    }

    public void sendCmd(String cmd) {
        mCmd = cmd;
    }

    public static AdbBase64 getBase64Impl() {
        return new AdbBase64() {
            @Override
            public String encodeToString(byte[] arg0) {
                return Base64.encodeBase64String(arg0);
            }
        };
    }

    private static AdbCrypto setupCrypto(String pubKeyFile, String privKeyFile)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String path = "/data/data/" + AppApplication.getContext().getPackageName();
        File pub = new File(path, pubKeyFile);
        File priv = new File(path, privKeyFile);
        AdbCrypto c = null;

        // Try to load a key pair from the files
        if (pub.exists() && priv.exists()) {
            try {
                c = AdbCrypto.loadAdbKeyPair(getBase64Impl(), priv, pub);
            } catch (IOException e) {
                // Failed to read from file
                c = null;
            } catch (InvalidKeySpecException e) {
                // Key spec was invalid
                c = null;
            } catch (NoSuchAlgorithmException e) {
                // RSA algorithm was unsupported with the crypo packages available
                c = null;
            }
        }

        if (c == null) {
            // We couldn't load a key, so let's generate a new one
            c = AdbCrypto.generateAdbKeyPair(getBase64Impl());
            // Save it
            c.saveAdbKeyPair(priv, pub);
            LogUtils.i(TAG, "Generated new keypair");
        } else {
            LogUtils.i(TAG, "Loaded existing keypair");
        }

        return c;
    }

    @Override
    public void run() {
        try {
            mCrypto = setupCrypto("pub.key", "priv.key");
            mSocket = new Socket(mIP, 5555);
            mAdbConnection = AdbConnection.create(mSocket, mCrypto);
            mAdbConnection.connect();
            mAdbStream = mAdbConnection.open("shell:");

            new ReceiveThread(mAdbStream).start();
            // We become the sending thread
            while(!isStop) {
                if (!TextUtils.isEmpty(mCmd)) {
                    mAdbStream.write(mCmd + '\n');
                    mCmd = null;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (null != mSocket) {
                try {
                    mSocket.close();
                    mSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != mAdbConnection) {
                try {
                    mAdbConnection.close();
                    mAdbConnection = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != mAdbStream) {
                try {
                    mAdbStream.close();
                    mAdbStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void close() {
        isStop = true;
    }

}
