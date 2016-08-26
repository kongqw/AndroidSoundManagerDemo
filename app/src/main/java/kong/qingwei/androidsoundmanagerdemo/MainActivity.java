package kong.qingwei.androidsoundmanagerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "MainActivity";
    private PlayThread mPlayThread;
    private SeekBar mBalance;
    private PlayThread mChannelLeftPlayer;
    private PlayThread mChannelRightPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button play = (Button) findViewById(R.id.bt_play);
        Button pause = (Button) findViewById(R.id.tb_pause);
        Button replay = (Button) findViewById(R.id.bt_replay);
        Button stop = (Button) findViewById(R.id.bt_stop);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        replay.setOnClickListener(this);
        stop.setOnClickListener(this);

        mBalance = (SeekBar) findViewById(R.id.sb_balance);
        mBalance.setOnSeekBarChangeListener(this);

        Button channelLeft = (Button) findViewById(R.id.channel_left);
        Button channelRight = (Button) findViewById(R.id.channel_right);
        Button channelDefault = (Button) findViewById(R.id.channel_default);
        Button sendDiffData = (Button) findViewById(R.id.send_diff_data);
        channelLeft.setOnClickListener(this);
        channelRight.setOnClickListener(this);
        channelDefault.setOnClickListener(this);
        sendDiffData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_play: // 开始播放
                if (null != mPlayThread) {
                    mPlayThread.stopp();
                    mPlayThread = null;
                }
                mPlayThread = new PlayThread(this, "tts1.pcm");
                mPlayThread.start();
                break;
            case R.id.tb_pause: // 暂停
                mPlayThread.pause();
                break;
            case R.id.bt_replay: // 继续播放
                mPlayThread.play();
                break;
            case R.id.bt_stop: // 停止
                mPlayThread.stopp();
                mPlayThread = null;
                break;
            case R.id.channel_left: // 禁用左声道
                if (null != mPlayThread)
                    mPlayThread.setChannel(false, true);
                mBalance.setProgress(0);
                break;
            case R.id.channel_right: // 禁用右声道
                if (null != mPlayThread)
                    mPlayThread.setChannel(true, false);
                mBalance.setProgress(mBalance.getMax());
                break;
            case R.id.channel_default: // 恢复左右声道
                if (null != mPlayThread)
                    mPlayThread.setChannel(true, true);
                mBalance.setProgress(mBalance.getMax() / 2);
                break;
            case R.id.send_diff_data: // 左右声道发送不同的数据
                if (null != mChannelLeftPlayer) {
                    mChannelLeftPlayer.stopp();
                    mChannelLeftPlayer = null;
                }
                if (null != mChannelRightPlayer) {
                    mChannelRightPlayer.stopp();
                    mChannelRightPlayer = null;
                }

                mChannelLeftPlayer = new PlayThread(this, "tts1.pcm");
                mChannelRightPlayer = new PlayThread(this, "tts2.pcm");

                mChannelLeftPlayer.setChannel(true, false);
                mChannelRightPlayer.setChannel(false, true);

                mChannelLeftPlayer.start();
                mChannelRightPlayer.start();
                break;
            default:
                break;
        }
    }

    // SeekBar滑动画漫画的监听 Start
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int max = seekBar.getMax();
        if (null != mPlayThread)
            mPlayThread.setBalance(max, progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    // SeekBar滑动画漫画的监听 End
}
