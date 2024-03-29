package io.itch.jaknak72.oozed;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.view.GestureDetectorCompat;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.GameConstants;
import io.itch.jaknak72.oozed.utils.Utils;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Custom surface view to render the game
 * Forwards input events to game and manages the game loop thread
 * @author Simon
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = GameSurfaceView.class.getName();

    private GameLoop gameLoop;
    private Game game;
    private Thread gameMainThread;

    private GestureDetectorCompat gestureDetector;

    private boolean surfaceCreatedOnce = false;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);

        String startLevel = getActivity().getIntent().getStringExtra(GameActivity.START_LEVEL_KEY);
        game = new Game(context,startLevel==null||startLevel.isEmpty()?"1":startLevel);
        gestureDetector = new GestureDetectorCompat(context,new GameGestureListener());
        //Log.d(TAG, "Initialize GameSurfaceView");
    }

    private void resumeGame(SurfaceHolder holder) {
        game.resume();

        if(gameLoop==null) {
            gameLoop = new GameLoop(holder, this, game);
            gameMainThread = new Thread(gameLoop);
            gameMainThread.start();
        }
        else {
            gameLoop.setPaused(false);
        }
    }

    private void pauseGame() {
        gameLoop.setPaused(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.d(TAG, "Surface created");
        if(!surfaceCreatedOnce) {
            game.init();
            surfaceCreatedOnce = true;
        }
        resumeGame(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.d(TAG, "Surface destroyed");
        pauseGame();
    }

    /**
     * called by activity to signal that the game should be cleaned up
     */
    public void onDestroy() {
        gameLoop.setRunning(false);
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

        //Log.i(TAG, String.format("Setting dimensions of game view to %dx%d", desiredWidth, desiredHeight));
        //setMeasuredDimension(desiredWidth, desiredHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(desiredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        game.render(canvas);
    }

    public Game getGame() {
        return game;
    }

    /**
     * utility method from https://stackoverflow.com/a/32973351
     * @return the hosting activity
     */
    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private class GameGestureListener extends GestureDetector.SimpleOnGestureListener {
        private Vec2 position = new Vec2();
        private Vec2 direction = new Vec2();

        /**
         * If the player performs a long press at the top left corner in this radius then debug rendering is toggled
         */
        private static final float DEBUG_TOGGLE_RADIUS = 3;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log.d(TAG, "Single Tap Confirmed: "+e.toString());
            position.set(e.getX(),e.getY());
            screenToWorld();

            game.tap(position);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Log.d(TAG, "Fling: "+e1.toString()+", motion: "+e2.toString());
            position.set(e1.getX(), e1.getY());
            screenToWorld();

            game.swipe(position,
                    direction.set(velocityX,velocityY).norm());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //Log.d(TAG, String.format("Long press: "+e.toString()));

            //if there is a long press on the bottom left corner => toggle debug render
            position.set(e.getX(), e.getY());
            screenToWorld();
            position.y = GameConstants.GAME_HEIGHT-position.y;

            //check if the long press is close enough to the top left corner
            if(position.len2()<DEBUG_TOGGLE_RADIUS*DEBUG_TOGGLE_RADIUS) {
                //Log.i(TAG, "Long press at bottom left corner detected, toggling debug render");
                game.toggleDebugRender();
            }
        }

        /**
         * Transforms position from screen to world coords
         */
        private void screenToWorld() {
            //transform to game resolution
            position.scl(GameConstants.GAME_RES_WIDTH, GameConstants.GAME_RES_HEIGHT).div(getWidth(),getHeight());
            //transform to game world coords
            Utils.screenToWorld(position);
        }
    }
}