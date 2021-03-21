package at.ac.tuwien.mmue_lm7;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import at.ac.tuwien.mmue_lm7.game.Game;

/**
 * TODO: document your custom view class.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private GameLoop gameLoop;
    private Game game;
    private Thread gameMainThread;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);

        //TODO pass parameters to game coming from somewhere
        game = new Game();
    }

    private void startGame(SurfaceHolder holder) {
        gameLoop = new GameLoop(holder, this,game);
        gameMainThread = new Thread(gameLoop);
        gameMainThread.start();
    }

    private void endGame() {
        gameLoop.setRunning(false);
        try {
            gameMainThread.join();
        } catch (InterruptedException e) {
            Log.e("Error", e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //TODO detect gestures and pass to game
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startGame(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        endGame();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        game.render(canvas);
    }
}