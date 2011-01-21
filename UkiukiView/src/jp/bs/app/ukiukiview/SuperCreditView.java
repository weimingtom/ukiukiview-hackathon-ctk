package jp.bs.app.ukiukiview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * SuperCreditView
 *
 * <p>
 * クレジットのピクセルを粒子化して、集合と拡散を繰り返すビュー
 * </p>
 */
public class SuperCreditView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "SuperCreditView";

    /**
     * コンストラクタ
     *
     * @param context
     */
    public SuperCreditView(Context context) {
        this(context, null);
    }

    /**
     * コンストラクタ
     * <p>
     * レイアウトXMLから呼び出す際に利用される
     * </p>
     *
     * @param context
     * @param attr
     */
    public SuperCreditView(Context context, AttributeSet attr) {
        super(context, attr);

        // SurfaceHolderの初期化
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.RGBA_8888);
        mHolder.addCallback(this);

        // 描画用のオブジェクト
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mBackPaint = new Paint();
        mBackPaint.setColor(Color.BLACK);

    }

    private SurfaceHolder mHolder = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
     * )
     */
    public void surfaceCreated(SurfaceHolder holder) {
        if (DEBUG_FLG) Log.d(LOG_TAG, "surfaceCreated");

    }

    private Bitmap mFinalBitmap = null;

    /**
     * 初期化処理
     * <p>
     * Bitmapを構成するドットに分割して移動可能な状態に設定する
     * </p>
     *
     * @param bmp
     *            粒子化するBitmapオブジェクト
     */
    public void init(Bitmap bmp) {
        if (DEBUG_FLG) Log.d(LOG_TAG, "init bitmap");

        mFinalBitmap = bmp;

        // 粒子化処理
        generateParticles(bmp);
    }

    private DisplayListener mListener = null;

    /**
     * アニメーションの開始
     *
     * @param listener
     *            アニメーションの完了を監視するリスナ
     */
    public void start(DisplayListener listener) {
        mListener = listener;

        // 移動処理スレッドを停止
        if (mMovingThread != null) mMovingThread.setStopFlg(true);

        if (mDrawingThread == null) {
            // 描画スレッドを開始
            mDrawingThread = new Thread(this);
            mDrawingThread.start();
        }

        // 移動処理スレッドを開始
        mMovingThread = new MovingThread();
        mMovingThread.start();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
     * , int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (DEBUG_FLG) Log.d(LOG_TAG, "surfaceChanged");

    }

    private Thread mDrawingThread = null;
    private MovingThread mMovingThread = null;

    private Paint mPaint = null;
    private Paint mBackPaint = null;

    /*
     * (non-Javadoc)
     *
     * @seeandroid.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
     * SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {}

    Particle[] mParticle = null;

    int cLeft = 0;
    int cTop = 0;

    /**
     * ２つの粒子の配列を統合する
     *
     * @param from
     * @param to
     */
    void conversion(Particle[] from, Particle[] to) {
        int len = from.length;
        System.arraycopy(from, 0, to, 0, len);
    }

    /**
     * 画像に応じた粒子オブジェクトを生成
     *
     * @param bmp
     * @return
     */
    private void generateParticles(Bitmap bmp) {

        synchronized (mParticleLockObject) {

            mLimitIndex = countParticle(bmp);
            if (mLimitIndex <= 0) return;

            if (DEBUG_FLG) Log.d(LOG_TAG, "particle is " + mLimitIndex);

            if (mParticle == null) {
                mParticle = new Particle[mLimitIndex];
            } else if (mParticle.length < mLimitIndex) {
                Particle[] tmp = mParticle;
                mParticle = new Particle[mLimitIndex];
                conversion(tmp, mParticle);
            } else if (mParticle.length > mLimitIndex) {
                for (int i = mLimitIndex; i < mParticle.length; i++) {
                    mParticle[i].init();
                }
            }

            for (int i = 0; i < mParticle.length; i++) {
                if (mParticle[i] == null) mParticle[i] = new Particle();
            }
        }

        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int parentWidth = getWidth();
        int parentHeight = getHeight() / 2;

        cLeft = (parentWidth - width) / 2;
        cTop = (parentHeight - height) / 2;

        int threshold = 128;

        int idx = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = bmp.getPixel(x, y);
                if (check(color, threshold)) {
                    mParticle[idx].setValues(cLeft + x, cTop + y, color, width, height,
                            parentWidth, parentHeight);
                    idx++;
                }
            }
        }
    }

    /**
     * 粒子化するピクセルを判定
     *
     * @param color
     * @return
     */
    private boolean check(int color) {
        return check(color, 128);
    }

    /**
     * 粒子化するピクセルを判定
     *
     * @param color
     * @param threshold
     * @return
     */
    private boolean check(int color, int threshold) {
        if (color == 0x0) return false;
        if (color == 0xffffffff) return true;

        int alpha = (color & 0xff000000) >>> 24;
        int red = (color & 0x00ff0000) >>> 16;
        int green = (color & 0x0000ff00) >>> 8;
        int blue = (color & 0x000000ff);

        if (alpha > 250 && (red > threshold || green > threshold || blue > threshold)) return true;
        return false;
    }

    /**
     * 粒子化するピクセル数を計算するＳ
     *
     * @return
     */
    private int countParticle(Bitmap bmp) {
        if (bmp == null) return -1;

        int result = 0;
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (check(bmp.getPixel(x, y))) result++;
            }
        }
        return result;
    }

    private int mLimitIndex = 0;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while (true) {

            Canvas canvas = mHolder.lockCanvas();
            if (canvas == null) continue;

            // 塗りつぶし
            canvas.drawColor(Color.argb(32, 0, 0, 0));

            boolean flg = false;
            synchronized (mParticleLockObject) {
                for (int i = 0; i < mLimitIndex; i++) {
                    mParticle[i].draw(canvas);
                    if (mParticle[i].status != Particle.STATUS_COMPLETED) flg = true;
                }
            }

            int x, y;
            if (flg == false && mMode == MODE_RESTORE) {
                x = cLeft;
                y = cTop;
                canvas.drawBitmap(mFinalBitmap, x, y, mBackPaint);

            } else {
                x = 0;
                y = 0;
            }

            // 描画
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }

            if (flg == false) mHandler.post(onFinish);

            synchronized (this) {
                try {
                    this.wait(1000 / DISPLAY_UPDATE_FRAME);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }

        }
    }

    //
    private static final int DISPLAY_UPDATE_FRAME = 30;

    private final Object mParticleLockObject = new Object();

    /**
     * Particleを移動させるスレッド
     *
     */
    private class MovingThread extends Thread {
        private boolean stopFlg = false;

        private void setStopFlg(boolean flg) {
            this.stopFlg = flg;
        }

        public void run() {
            while (stopFlg == false) {
                if (DEBUG_FLG) Log.d(LOG_TAG, "MovingThread run while");
                boolean flg = false;

                synchronized (mParticleLockObject) {
                    for (int i = 0; i < mLimitIndex; i++) {
                        mParticle[i].proc();
                        if (mParticle[i].status != Particle.STATUS_COMPLETED) flg = true;
                    }
                }

                if (flg == false) break;
                synchronized (this) {
                    try {
                        this.wait(1000 / MOVING_FRAME);
                    } catch (InterruptedException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }
            Log.e(LOG_TAG, "MovingThread update done.");
        }
    }

    //
    private static final int MOVING_FRAME = 30;

    private final Handler mHandler = new Handler();
    private final Runnable onFinish = new Runnable() {
        public void run() {
            if (mListener != null) mListener.onFinish(mMode);
            mListener = null;
        }
    };

    /**
     * 破裂させる
     */
    public void splash() {
        synchronized (this) {
            mMode = MODE_SPLASH;
            for (int i = 0; i < mLimitIndex; i++) {
                mParticle[i].splash();
            }
        }
    }

    /**
     * 復元する
     */
    public void restore() {
        synchronized (this) {
            mMode = MODE_RESTORE;
            for (int i = 0; i < mLimitIndex; i++) {
                mParticle[i].restore();
            }
        }
    }

    public static final int MODE_NOT_SET = -1;
    public static final int MODE_SPLASH = 0;
    public static final int MODE_RESTORE = 1;

    public int mMode = MODE_NOT_SET;

    /**
     * 粒子クラス
     * <p>
     * 粒子１個に対応するクラス
     * </p>
     */
    private class Particle {
        /** 初期位置 */
        public float baseX = 0.0f;
        public float baseY = 0.0f;

        /** 移動目標位置 */
        public float targetX = 0.0f;
        public float targetY = 0.0f;

        /** 現在位置 */
        public float nowX = -1.0f;
        public float nowY = -1.0f;

        /** 現在位置 */
        public static final int STATUS_NOT_START = 0;

        /** 現在位置 */
        public static final int STATUS_MOVING = 1;

        /** 現在位置 */
        public static final int STATUS_COMPLETED = 2;

        /** 状態 */
        public int status = STATUS_NOT_START;

        private int parentWidth = 0;
        private int parentHeight = 0;

        /**
         * 各種値を設定
         *
         * @param x
         *            現在のx座標
         * @param y
         *            現在のy座標
         * @param color
         *            色
         * @param width
         *            Width
         * @param height
         *            Height
         * @param pWidth
         *            親ViewのWidth
         * @param pHeight
         *            親ViewのHeight
         */
        public void setValues(int x, int y, int color, int width, int height, int pWidth,
                int pHeight) {
            baseX = x;
            baseY = y;
            targetX = x;
            targetY = y;
            parentWidth = pWidth;
            parentHeight = pHeight;

            if (initialized == false) {
                nowX = (float) Math.round(Math.random() * parentWidth);
                nowY = (float) Math.round(Math.random() * parentHeight);
                initialized = true;
            }
            paint.setColor(color);
        }

        private final Paint paint = new Paint();

        /**
         * 現在位置を初期化する
         */
        public void init() {
            nowX = (float) Math.round(Math.random() * parentWidth);
            nowY = (float) Math.round(Math.random() * parentHeight);
            initialized = true;
        }

        private boolean initialized = false;

        /**
         * 破裂させる
         */
        public synchronized void splash() {

            ite = BASE_ITE / 10;
            float yValue = (int) Math.round(Math.random() * 10);
            float xValue = (int) Math.round(Math.random() * 10);

            if (xValue % 2 == 0) {
                targetX += xValue;
            } else {
                targetX -= xValue;
            }

            if (yValue % 2 == 0) {
                targetY += yValue;
            } else {
                targetY -= yValue;
            }
            status = STATUS_NOT_START;

        }

        /**
         * 復元する
         */
        public synchronized void restore() {
            ite = BASE_ITE;
            targetX = baseX;
            targetY = baseY;
            status = STATUS_NOT_START;
        }

        // 移動量の閾値
        private static final int BASE_ITE = 100;

        // 現在の移動量
        private int ite = BASE_ITE;

        /**
         * 一回動作させる
         */
        public void proc() {
            if (status == STATUS_COMPLETED) return;
            if (status == STATUS_NOT_START) status = STATUS_MOVING;

            if (DEBUG_FLG) Log.d(LOG_TAG, "proc()");

            if (targetX > nowX) {
                float lim = (targetX - nowX) / ite;
                nowX += lim;
            } else if (targetX < nowX) {
                float lim = (nowX - targetX) / ite;
                nowX -= lim;
            }
            if (targetY > nowY) {
                float lim = (targetY - nowY) / ite;
                nowY += lim;
            } else if (targetY < nowY) {
                float lim = (nowY - targetY) / ite;
                nowY -= lim;
            }

            if (nowX == targetX && nowY == targetY) {
                if (DEBUG_FLG) Log.d(LOG_TAG, "status is STATUS_COMPLETED");
                status = STATUS_COMPLETED;
            } else {
                ite = ite * 95 / 100;
            }
        }

        /**
         * 描画する
         *
         * @param canvas
         */
        public void draw(Canvas canvas) {
            canvas.drawRect((int) Math.ceil(nowX), (int) Math.ceil(nowY),
                    (int) Math.ceil(nowX) + 1, (int) Math.ceil(nowY) + 1, paint);
        }
    }

    /**
     * アニメーションの完了を監視するリスナ
     */
    interface DisplayListener {
        /**
         * アニメーションの完了
         *
         * @param mode
         *            完了した動作(SPLASHまたはRESTORE)
         */
        void onFinish(int mode);
    }
}
