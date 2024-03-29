package io.itch.jaknak72.oozed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.itch.jaknak72.oozed.game.Score;
import io.itch.jaknak72.oozed.game.ScoreDAO;
import io.itch.jaknak72.oozed.game.ScoreDatabase;
import io.itch.jaknak72.oozed.game.resources.SoundSystem;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Shows highscore and buttons to navigate to a new game activity or root main activity
 * @author simon
 */
public class GameOver extends FullscreenActivity {
    //Names for the expected data input
    public static final String SCORE_KEY = "score";
    public static final String TIME_KEY = "time";
    public static final String GAME_COMPLETED_KEY = "gameCompleted";

    //views
    private TextView titleText;
    private TextView highscoreText;
    private TextView scoreText;
    private TextView newHighScoreText;
    private View buttonGroup;

    //animations
    private Animation titleAnim;
    private Animation newHighscoreAnim;
    private Animation gameOverButtonsAnim;
    private AnimationSet animations;

    //data input
    private int score;
    private int time;
    private boolean gameCompleted;

    //database access
    private ExecutorService es;
    private ScoreDAO scoreDAO;
    private ScoreDatabase db;

    private int buttonSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //get input from intent
        score = getIntent().getIntExtra(SCORE_KEY,0);
        time = getIntent().getIntExtra(TIME_KEY,0);
        gameCompleted = getIntent().getBooleanExtra(GAME_COMPLETED_KEY,false);

        //set score text
        scoreText = findViewById(R.id.score);
        scoreText.setText(getResources().getString(R.string.your_score,score));

        //get views
        titleText = findViewById(R.id.win_title);
        highscoreText = findViewById(R.id.highscore);
        newHighScoreText = findViewById(R.id.new_highscore);
        buttonGroup = findViewById(R.id.game_over_button_group);

        //load animations
        titleAnim = AnimationUtils.loadAnimation(this,R.anim.game_over_title);
        newHighscoreAnim = AnimationUtils.loadAnimation(this,R.anim.new_highscore);
        gameOverButtonsAnim = AnimationUtils.loadAnimation(this,R.anim.game_over_button);

        scoreText.setAnimation(titleAnim);
        highscoreText.setAnimation(gameOverButtonsAnim);
        buttonGroup.setAnimation(gameOverButtonsAnim);

        animations = new AnimationSet(false);
        animations.addAnimation(titleAnim);
        animations.addAnimation(gameOverButtonsAnim);

        //access score database
        db = ScoreDatabase.get(getApplicationContext());
        scoreDAO = db.scoreDAO();
        es = Executors.newSingleThreadExecutor();
        es.execute(() -> {
            Score s = scoreDAO.getScore();
            runOnUiThread(() -> loadHighscore(s));
        });

        buttonSoundId = SoundSystem.get().loadSound(R.raw.button);
    }

    private void loadHighscore(Score highscore) {
        //check if current score is a new highscore
        if(highscore == null || //there is no highscore yet
           highscore.getScore()<score || //player reached a higher level than last time
           (gameCompleted && !highscore.gameCompleted()) || //player completed game for first time
           (gameCompleted && highscore.getTime()>time)) { //player completed game faster than before
            highscore = new Score(score,time,gameCompleted);
            //store new highscore in database
            es.execute(() -> {
                scoreDAO.clearScore();
                scoreDAO.insertScore(new Score(score,time,gameCompleted));
            });

            //add animations
            newHighScoreText.setAnimation(newHighscoreAnim);
            animations.addAnimation(newHighscoreAnim);
        }
        else {
            //make "New Highscore" text invisible if no new highscore has been achieved
            newHighScoreText.setVisibility(View.INVISIBLE);

            //decrease game over button start offset
            gameOverButtonsAnim.setStartOffset(gameOverButtonsAnim.getStartOffset()-newHighscoreAnim.getStartOffset());
        }

        //set highscore text
        highscoreText.setText(getResources().getString(R.string.highscore,highscore.getScore()));

        animations.start();
    }

    public void onMainMenuButtonClicked(View view) {
        //Switch to main menu activity, remove all activities on top
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        SoundSystem.get().playSound(buttonSoundId);
    }

    public void onRetryButtonClicked(View view) {
        //Switch to game activity
        Intent intent = new Intent(this, GameActivity.class);
        //finish this activity first, so the game activity replaces this activity
        this.finish();
        startActivity(intent);

        SoundSystem.get().playSound(buttonSoundId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SoundSystem.get().unloadSound(R.raw.button);
    }
}