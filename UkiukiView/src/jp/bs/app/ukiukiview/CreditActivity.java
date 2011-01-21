package jp.bs.app.ukiukiview;

import java.util.Timer;
import java.util.TimerTask;

import jp.bs.app.ukiukiview.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CreditActivity extends Activity implements SuperCreditView.DisplayListener {
    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MainActivity";

    /**
     * クレジット・レイアウトファイルの設定
     * <p>
     * クレジットとして表示するレイアウトファイルのidを指定する
     * </p>
     */
    private static final int CREDIT_LAYOUT = R.layout.credit_main;

    /**
     * クレジットに表示する一覧.
     * <p>
     * setContentViewで設定するレイアウトファイルに表示されるViewに関連づけた"android:id"を配列として持つ.
     * </p>
     */
    private static final int[] CREDIT_ID_ARRAY = {
        R.id.ukiukiview,
        R.id.brilliant,
        R.id.c_lis,
        R.id.rockrin,
        R.id.ak1,
        R.id.ak3,
        R.id.ak4,
        R.id.ak5,
    };

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトル無し・フルスクリーン・スクリーンを常にＯＮ
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(CREDIT_LAYOUT);

        // SuperCreditViewを取得
        mSuperCreditView = (SuperCreditView) findViewById(R.id.super_credit_view);
    }

    private SuperCreditView mSuperCreditView = null;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();

        // クレジットの描画開始タスク実行
        mTimer.schedule(startTask, 3 * 1000);
    }

    private final Timer mTimer = new Timer();
    private final TimerTask startTask = new TimerTask() {
        @Override
        public void run() {
            // 初期化処理
            initBitmap();

            // 動作開始
            startAction(idx, SuperCreditView.MODE_RESTORE);
        }
    };

    private final static int HANDLE_START_ANIM = 0x01;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // アニメーションの開始
                case HANDLE_START_ANIM:
                    if (mSuperCreditView.mMode == SuperCreditView.MODE_NOT_SET) {
                        mSuperCreditView.restore();

                    } else if (mSuperCreditView.mMode == SuperCreditView.MODE_RESTORE) {
                        if (DEBUG_FLG) Log.d(LOG_TAG, "splash");
                        mSuperCreditView.splash();

                    } else if (mSuperCreditView.mMode == SuperCreditView.MODE_SPLASH) {
                        if (DEBUG_FLG) Log.d(LOG_TAG, "restore");
                        mSuperCreditView.restore();

                    }

                    // アニメーション開始
                    mSuperCreditView.start(CreditActivity.this);
                    break;
            }
        };
    };

    private Bitmap[] mCaches = new Bitmap[CREDIT_ID_ARRAY.length];
    private int idx = 0;

    /**
     * クレジット表示内容をBitmapとして取得する
     */
    private void initBitmap() {
        int len = mCaches.length;
        for (int i = 0; i < len; i++) {
            findViewById(CREDIT_ID_ARRAY[i]).setDrawingCacheEnabled(true);
            mCaches[i] = findViewById(CREDIT_ID_ARRAY[i]).getDrawingCache();
        }

    }

    private Bitmap mTempBitmap = null;
    private int mMode = SuperCreditView.MODE_NOT_SET;

    /**
     * 動作の開始
     *
     * @param idx
     * @param mode
     */
    void startAction(int idx, final int mode) {
        mTempBitmap = mCaches[idx];
        mMode = mode;
        new Thread(mStartActionRunnable).start();
    }

    private final Runnable mStartActionRunnable = new Runnable() {
        public void run() {
            // 指定ビットマップで初期化
            mSuperCreditView.init(mTempBitmap);
            if (mMode == SuperCreditView.MODE_SPLASH) {
                synchronized (this) {
                    try {
                        this.wait(3 * 1000);
                    } catch (InterruptedException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }
            // アニメーションのスタート
            mHandler.sendEmptyMessage(HANDLE_START_ANIM);
        }

    };

    /*
     * (non-Javadoc)
     *
     * @see
     * jp.bs.app.UkiukiView.SuperCreditView.DisplayListener#onFinish(int)
     */
    public void onFinish(int mode) {
        switch (mode) {
            case SuperCreditView.MODE_RESTORE:
                if (DEBUG_FLG) Log.d(LOG_TAG, "MODE_RESTORE is ok");
                startAction(idx, SuperCreditView.MODE_SPLASH);
                break;
            case SuperCreditView.MODE_SPLASH:
                if (DEBUG_FLG) Log.d(LOG_TAG, "MODE_SPLASH is ok");
                idx = (idx + 1) % CREDIT_ID_ARRAY.length;
                if (DEBUG_FLG) Log.d(LOG_TAG, "idx is " + idx);
                startAction(idx, SuperCreditView.MODE_RESTORE);
                break;
        }
    }

}