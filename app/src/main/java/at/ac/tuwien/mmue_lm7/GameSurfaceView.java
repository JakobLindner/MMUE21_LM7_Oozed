package at.ac.tuwien.mmue_lm7;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.view.GestureDetectorCompat;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameSurfaceView.class.getName();

    private GameLoop gameLoop;
    private Game game;
    private Thread gameMainThread;

    private GestureDetectorCompat gestureDetector;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);

        //TODO pass parameters to game coming from somewhere
        game = new Game(context);
        gestureDetector = new GestureDetectorCompat(context,new GameGestureListener());
    }

    private void startGame(SurfaceHolder holder) {
        gameLoop = new GameLoop(holder, this, game);
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
        gestureDetector.onTouchEvent(e);
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

    /**
     * Overwrites width and height to achieve desired aspect ratio
     * dimensions are set such that nothing is rendered off screen & black bars are minimized
     * solution inspired from: https://stackoverflow.com/a/10772572
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
        float originalRatio = originalWidth / (float) originalHeight;
        float desiredRatio = GameConstants.GAME_RES_WIDTH / (float) GameConstants.GAME_RES_HEIGHT;

        int desiredWidth;
        int desiredHeight;
        if (originalRatio > desiredRatio) {
            //if screen is wider, than height is the limiting factor
            desiredHeight = originalHeight;
            desiredWidth = (desiredHeight * GameConstants.GAME_RES_WIDTH) / GameConstants.GAME_RES_HEIGHT;
        } else {
            //if screen is broader, then width is the limiting factor
            desiredWidth = originalWidth;
            desiredHeight = (desiredWidth * GameConstants.GAME_RES_HEIGHT) / GameConstants.GAME_RES_WIDTH;
        }

        Log.i(TAG, String.format("Setting dimensions of game view to %dx%d", desiredWidth, desiredHeight));
        //setMeasuredDimension(desiredWidth, desiredHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        game.render(canvas);
    }

    private class GameGestureListener extends GestureDetector.SimpleOnGestureListener {
        private Vec2 position = new Vec2();
        private Vec2 direction = new Vec2();

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "Single Tap Confirmed: "+e.toString());
            position.set(e.getX(),e.getY());

            game.tap(position);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "Fling: "+e1.toString()+", motion: "+e2.toString());
            game.swipe(position.set(e1.getX(), e1.getY()),
                    direction.set(velocityX,velocityY).norm());
            return true;
        }
    }
}