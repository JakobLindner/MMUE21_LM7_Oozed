package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import at.ac.tuwien.mmue_lm7.game.LevelEvent;
import at.ac.tuwien.mmue_lm7.game.Score;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * @author simon
 */
public class GameActivity extends FullscreenActivity {
    private static final String TAG = "GameActivity";

    public static final String START_LEVEL_KEY = "startLevel";

    private GameSurfaceView gameView;

    /**
     * True when lives activity has been started and is on top of this activity
     * if true, then game is automatically resumed when onResume is called
     */
    private boolean livesActivityOnTop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.fullscreen_content);
        gameView.getGame().onGameOver.addListener(this,this::onGameOver);
        //gameView.getGame().onLevelLoaded.addListener(this,this::onLevelLoaded);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        gameView.getGame().pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if(livesActivityOnTop) {
            gameView.getGame().resumeGame();
            livesActivityOnTop = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        gameView.onDestroy();

        gameView.getGame().onGameOver.removeListener(this);
        //gameView.getGame().onLevelLoaded.removeListener(this);
    }

    /**
     * replaces this activity with game over activity
     */
    private boolean onGameOver(Score event) {
        //Switch to game over activity
        Intent intent = new Intent(this, GameOver.class);
        intent.putExtra(GameOver.SCORE_KEY,event.getScore());
        intent.putExtra(GameOver.TIME_KEY,event.getTime());
        intent.putExtra(GameOver.GAME_COMPLETED_KEY, event.gameCompleted());
        //finish this activity first, so the game activity replaces this activity
        this.finish();
        startActivity(intent);

        return true;
    }

    /**
     * Creates LiveActivity to show current lives
     */
    private boolean onLevelLoaded(LevelEvent event) {
        Intent intent = new Intent(this, LivesActivity.class);
        intent.putExtra(LivesActivity.LIVES_KEY,gameView.getGame().getPlayerLives());
        intent.putExtra(LivesActivity.LEVEL_KEY,event.getLevel());
        startActivity(intent);
        livesActivityOnTop = true;
        return true;
    }
}