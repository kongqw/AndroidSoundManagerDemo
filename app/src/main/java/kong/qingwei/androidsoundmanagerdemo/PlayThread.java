package kong.qingwei.androidsoundmanagerdemo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kqw on 2016/8/26.
 * 播放音乐的线程
 */
public class PlayThread extends Thread {

    // 采样率
    private int mSampleRateInHz = 16000;
    // 单声道
    private int mChannelConfig = AudioFormat.CHANNEL_OUT_MONO;
    // 双声道（立体声）
    // private int mChannelConfig = AudioFormat.CHANNEL_OUT_STEREO;

    private static final String TAG = "PlayThread";
    private Activity mActivity;
    private AudioTrack mAudioTrack;
    private byte[] data;
    private String mFileName;


    public PlayThread(Activity activity, String fileName) {
        mActivity = activity;
        mFileName = fileName;

        int bufferSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                mSampleRateInHz,
                mChannelConfig,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
    }

    @Override
    public void run() {
        super.run();
        try {
            if (null != mAudioTrack)
                mAudioTrack.play();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream inputStream = mActivity.getResources().getAssets().open(mFileName);

            // 缓冲区
            byte[] buffer = new byte[1024];
            // 播放进度
            int playIndex = 0;
            // 是否缓冲完成
            boolean isLoaded = false;
            // 缓冲 + 播放
            while (null != mAudioTrack && AudioTrack.PLAYSTATE_STOPPED != mAudioTrack.getPlayState()) {
                // 字符长度
                int len;
                if (-1 != (len = inputStream.read(buffer))) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    data = byteArrayOutputStream.toByteArray();
                    Log.i(TAG, "run: 已缓冲 : " + data.length);
                } else {
                    // 缓冲完成
                    isLoaded = true;
                }

//                if (AudioTrack.PLAYSTATE_PAUSED == mAudioTrack.getPlayState()) {
//                    // 已经暂停
//                }
                if (AudioTrack.PLAYSTATE_PLAYING == mAudioTrack.getPlayState()) {
                    Log.i(TAG, "run: 开始从 " + playIndex + " 播放");
                    playIndex += mAudioTrack.write(data, playIndex, data.length - playIndex);
                    Log.i(TAG, "run: 播放到了 : " + playIndex);
                    if (isLoaded && playIndex == data.length) {
                        Log.i(TAG, "run: 播放完了");
                        mAudioTrack.stop();
                    }

                    if (playIndex < 0) {
                        Log.i(TAG, "run: 播放出错");
                        mAudioTrack.stop();
                        break;
                    }
                }
            }
            Log.i(TAG, "run: play end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置左右声道平衡
     *
     * @param max     最大值
     * @param balance 当前值
     */
    public void setBalance(int max, int balance) {
        float b = (float) balance / (float) max;
        Log.i(TAG, "setBalance: b = " + b);
        if (null != mAudioTrack)
            mAudioTrack.setStereoVolume(1 - b, b);
    }

    /**
     * 设置左右声道是否可用
     *
     * @param left  左声道
     * @param right 右声道
     */
    public void setChannel(boolean left, boolean right) {
        if (null != mAudioTrack) {
            mAudioTrack.setStereoVolume(left ? 1 : 0, right ? 1 : 0);
            mAudioTrack.play();
        }
    }

    public void pause() {
        if (null != mAudioTrack)
            mAudioTrack.pause();
    }

    public void play() {
        if (null != mAudioTrack)
            mAudioTrack.play();
    }

    public void stopp() {
        releaseAudioTrack();
    }

    private void releaseAudioTrack() {
        if (null != mAudioTrack) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

}
