package com.baidu.mapautosdk.ui.cruiser;

import java.lang.ref.SoftReference;
import java.util.Locale;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.cruiser.event.CruiserEvent;
import com.baidu.mapframework.nirvana.module.Module;
import com.baidu.platform.comapi.util.BMEventBus;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * AutoCruiserControls
 *
 * @author Baidu
 * @version 1.0.0
 * @date 2022/7/11 19:08
 * @description
 */
public class AutoCruiserControls implements BMEventBus.OnEvent {
    /**
     * 速度的更新时间为1秒
     */
    private static final int SPEED_UPDATE_TIME = 1000;
    private static final String BREAK_CAMERA_STR = "前方%d米有%s";
    private View mRootView;

    private RelativeLayout mCruiserSpeedLayout;
    private TextView mCruiserSpeedText;

    private LinearLayout mCruiserBreakLayout;
    private ProgressBar mCruiserProgressBar;
    private TextView mCruiserBreakText;

    private Handler mHandler;
    private RunSpeedUpdate mRunSpeedUpdate;

    public AutoCruiserControls(View group) {
        mRootView = group;
        mHandler = new Handler(Looper.getMainLooper());
        mRunSpeedUpdate = new RunSpeedUpdate(this);
        initViews();
    }

    private void initViews() {
        if (mRootView == null) {
            return;
        }
        mCruiserSpeedText = mRootView.findViewById(R.id.speed);
        mCruiserSpeedLayout = mRootView.findViewById(R.id.speed_layout);

        mCruiserBreakText = mRootView.findViewById(R.id.cruise_txt);
        mCruiserBreakLayout = mRootView.findViewById(R.id.cruise_bg);
        mCruiserProgressBar = mRootView.findViewById(R.id.progress);
    }

    public void onResume() {
        // 如果rootView为空，则整个event都不会走
        if (mRootView == null) {
            return;
        }
        BMEventBus.getInstance().regist(this, Module.MAP_FRAME_MODULE, CruiserEvent.class);
        startSpeed();
    }

    public void onPause() {
        if (mRootView == null) {
            return;
        }
        BMEventBus.getInstance().unregist(this);
        // 退到后台会执行这个方法，因此退到后台关闭速度刷新，提高性能
        stopSpeed();
    }

    public void onDestroy() {
        // 销毁掉线程，否则内存泄漏
        if (!mRunSpeedUpdate.isStop()) {
            mRunSpeedUpdate.stop();
        }
        mHandler.removeCallbacks(mRunSpeedUpdate);
    }

    @Override
    public void onEvent(Object event) {
        if (event instanceof CruiserEvent) {
            onMainThread((CruiserEvent) event);
        }
    }

    private void onMainThread(CruiserEvent event) {
        switch (event.getType()) {
            case CruiserEvent.TYPE_CRUISER_LOC_START:
                break;
            case CruiserEvent.TYPE_CRUISER_LOC_STOP:
                break;
            case CruiserEvent.TYPE_CRUISER_START:
                startSpeed();
                break;
            case CruiserEvent.TYPE_CRUISER_STOP:
                stopSpeed();
                break;
            case CruiserEvent.TYPE_CRUISER_SHOW:
                mCruiserProgressBar.setMax(event.getDistance());
                updateBreakAssistUpdate(event);
                break;
            case CruiserEvent.TYPE_CRUISER_UPDATE:
                updateBreakAssistUpdate(event);
                break;
            case CruiserEvent.TYPE_CRUISER_HIDE:
                updateBreakAssistHide();
                break;
            default:
                break;
        }
    }

    private void startSpeed() {
        if (!BDAutoMapFactory.getCruiserManager().isStarted()) {
            return;
        }
        showCruiserLayoutShow(true);
        if (mRunSpeedUpdate.isStop()) {
            mRunSpeedUpdate.start();
            startSpeedUpdate();
        }
    }

    private void stopSpeed() {
        mRunSpeedUpdate.stop();
        stopSpeedUpdate();
        showCruiserLayoutShow(false);
        //        CommutingEvent event = new CommutingEvent();
        //        event.setCommuting(HomeCompanyDisplay.getInstance().isCommuting());
        //        BMEventBus.getInstance().post(event);
    }

    private void startSpeedUpdate() {
        updateSpeedIconText();
        mHandler.removeCallbacks(mRunSpeedUpdate);
        mHandler.postDelayed(mRunSpeedUpdate, SPEED_UPDATE_TIME);
    }

    private void stopSpeedUpdate() {
        mHandler.removeCallbacks(mRunSpeedUpdate);
    }

    private void showCruiserLayoutShow(boolean isDisplay) {
        if (mRootView == null) {
            return;
        }
        if (mRootView.getVisibility() != View.VISIBLE && isDisplay) {
            mRootView.setVisibility(View.VISIBLE);
        } else if (mRootView.getVisibility() != View.GONE && !isDisplay) {
            mRootView.setVisibility(View.GONE);
        }
        // 电子狗隐藏的时候关闭违章设置的显示，下一次不用处理
        if (mCruiserBreakLayout.getVisibility() == View.VISIBLE && !isDisplay) {
            mCruiserBreakLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 更新速度
     */
    private void updateSpeedIconText() {
        if (mCruiserSpeedText == null) {
            return;
        }
        float speed = BDAutoMapFactory.getCruiserManager().getSpeed();
        speed = speed > 999 ? 999 : speed;
        String speedStatic = String.valueOf((int) speed);
        if (Looper.getMainLooper() == Looper.myLooper()) {
            mCruiserSpeedText.setText(speedStatic);
        } else {
            updateSpeedIconTextNoUIThread(speedStatic);
        }
    }

    /**
     * 推倒主线程里执行
     *
     * @param speed
     */
    private void updateSpeedIconTextNoUIThread(final String speed) {
        if (mCruiserSpeedText == null) {
            return;
        }
        mCruiserSpeedText.post(new Runnable() {
            @Override
            public void run() {
                mCruiserSpeedText.setText(speed);
            }
        });
    }

    private void updateBreakAssistUpdate(CruiserEvent event) {
        startSpeed();
        if (mRootView.getVisibility() != View.VISIBLE) {
            mRootView.setVisibility(View.VISIBLE);
        }
        if (mCruiserBreakLayout.getVisibility() != View.VISIBLE) {
            mCruiserBreakLayout.setVisibility(View.VISIBLE);
        }
        if (mCruiserProgressBar.getMax() == 0) {
            mCruiserProgressBar.setMax(event.getDistance());
        }
        int progress = mCruiserProgressBar.getMax() - event.getDistance();
        progress = progress < 0 ? 0 : progress;
        progress =
                progress > mCruiserProgressBar.getMax() ? mCruiserProgressBar.getMax() : progress;
        mCruiserProgressBar.setProgress(progress);
        String message = String.format(Locale.CHINA, BREAK_CAMERA_STR,
                event.getDistance(), event.getAssistType());
        mCruiserBreakText.setText(message);
    }

    private void updateBreakAssistHide() {
        if (mRootView.getVisibility() != View.VISIBLE) {
            mRootView.setVisibility(View.VISIBLE);
        }
        if (mCruiserBreakLayout.getVisibility() != View.GONE) {
            mCruiserBreakLayout.setVisibility(View.GONE);
        }
        mCruiserProgressBar.setMax(0);
    }

    /**
     * 刷新速度的线程
     */
    private static class RunSpeedUpdate implements Runnable {
        private SoftReference<AutoCruiserControls> mReference;
        private volatile boolean isStop = true;

        public RunSpeedUpdate(AutoCruiserControls cruiserControls) {
            mReference = new SoftReference<>(cruiserControls);
        }

        @Override
        public void run() {
            if (isStop) {
                return;
            }
            if (mReference.get() != null) {
                mReference.get().startSpeedUpdate();
            }
        }

        public void start() {
            isStop = false;
        }

        public void stop() {
            isStop = true;
        }

        public boolean isStop() {
            return isStop;
        }
    }
}
