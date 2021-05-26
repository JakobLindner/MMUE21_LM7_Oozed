package at.ac.tuwien.mmue_lm7;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;

import static at.ac.tuwien.mmue_lm7.Constants.FIXED_DELTA;
import static at.ac.tuwien.mmue_lm7.Constants.FIXED_DELTA_MS;
import static at.ac.tuwien.mmue_lm7.Constants.UPDATE_TIME_ACCUM_MAX_MS;

/**
 * Thread for updating a game instance
 * renders game on own lowres bitmap, which is then rendered onto the whole game surface view
 * @author simon
 */
public class GameLoop implements Runnable {
    private static final String TAG = "GameLoop";

    private SurfaceHolder surfaceHolder;
    private GameSurfaceView gameSurfaceView;
    private boolean running;
    private boolean paused = false;
    private Object pauseLock = new Object();
    private Game game;
    /**
     * The bitmap the game is rendered onto
     */
    private Bitmap gameBitmap;
    private Rect gameBitmapSrc;
    private Rect gameBitmapDst = new Rect();
    private Paint gameBitmapPaint = new Paint();
    /**
     * Canvas used for rendering onto gameBitmap
     */
    private Canvas gameCanvas;
    private PaintFlagsDrawFilter noAntiAliasingFlags = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG,0);

    public GameLoop(SurfaceHolder surfaceHolder, GameSurfaceView gameSurfaceView, Game game) {
        this.surfaceHolder = surfaceHolder;
        this.gameSurfaceView = gameSurfaceView;
        this.game = game;
        this.gameBitmap = Bitmap.createBitmap(GameConstants.GAME_RES_WIDTH,GameConstants.GAME_RES_HEIGHT, Bitmap.Config.ARGB_8888);
        this.gameBitmapSrc = new Rect(0,0,gameBitmap.getWidth(),gameBitmap.getHeight());
        this.gameCanvas = new Canvas(this.gameBitmap);
        this.gameCanvas.setDrawFilter(noAntiAliasingFlags);

        gameBitmapPaint.setFilterBitmap(false);
        gameBitmapPaint.setAntiAlias(false);
        gameBitmapPaint.setDither(false);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;

        if(!running) {
            synchronized (pauseLock) {
                pauseLock.notify();
            }
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if(!paused) {
            synchronized (pauseLock) {
                pauseLock.notify();
            }
        }
    }

    @Override
    public void run() {
        setRunning(true);

        long lastTime = getTime();
        long currentTime = 0;
        long accumulator = FIXED_DELTA_MS;
        long delta = 0;

        while (running){
            while(!paused && running) {
                //calculate delta time
                currentTime = getTime();
                delta = currentTime - lastTime;
                lastTime = currentTime;

                //increase accumulator
                accumulator += delta;

                //cap accumulated time to prevent spiral of death (=cannot keep up with target update rate)
                if (accumulator > UPDATE_TIME_ACCUM_MAX_MS)
                    accumulator = UPDATE_TIME_ACCUM_MAX_MS;

                //do update if necessary
                boolean redraw = accumulator >= FIXED_DELTA_MS;
                while (accumulator >= FIXED_DELTA_MS) {
                    game.update();
                    accumulator -= FIXED_DELTA_MS;
                }

                if (redraw)
                    render();
            }
            game.pause();

            if(running) {
                try {
                    synchronized (pauseLock) {
                        pauseLock.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted game loop", e);
                    break;
                }
            }
        }

        game.cleanup();
    }

    //@SuppressLint("WrongCall")
    private void render() {
        Canvas canvas = null;
        try{
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder){
                if(canvas == null) return;

                //render game onto bitmap
                gameSurfaceView.draw(gameCanvas);

                //render bitmap onto view
                gameBitmapDst.set(0,0,canvas.getWidth(),canvas.getHeight());
                canvas.drawBitmap(gameBitmap,gameBitmapSrc,gameBitmapDst,gameBitmapPaint);
            }
        } finally {
            if(canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    /**
     * @return current time in ms
     */
    private static long getTime() {
       return SystemClock.uptimeMillis();
    }

}
