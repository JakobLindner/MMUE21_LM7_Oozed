package at.ac.tuwien.mmue_lm7;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;

import at.ac.tuwien.mmue_lm7.game.Game;

import static at.ac.tuwien.mmue_lm7.Constants.FIXED_DELTA;
import static at.ac.tuwien.mmue_lm7.Constants.FIXED_DELTA_MS;

public class GameLoop implements Runnable {

    private SurfaceHolder surfaceHolder;
    private GameSurfaceView gameSurfaceView;
    private boolean running;
    private Game game;

    public GameLoop(SurfaceHolder surfaceHolder, GameSurfaceView gameSurfaceView, Game game) {
        this.surfaceHolder = surfaceHolder;
        this.gameSurfaceView = gameSurfaceView;
        this.game = game;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        setRunning(true);

        game.init();

        long lastTime = getTime();
        long currentTime = 0;
        long accumulator = FIXED_DELTA_MS;
        long delta = 0;

        while (running){
            //calculate delta time
            currentTime = getTime();
            delta = currentTime-lastTime;
            lastTime = currentTime;

            //increase accumulator
            accumulator +=delta;

            //TODO cap accumulated time to prevent spiral of death

            //do update if necessary
            while(accumulator>=FIXED_DELTA)
            {
                game.update();
                accumulator-=FIXED_DELTA;
            }

            render();
        }
    }

    //@SuppressLint("WrongCall")
    private void render() {
        Canvas canvas = null;
        try{
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder){
                if(canvas == null) return;

                gameSurfaceView.draw(canvas);
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
