package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.ac.tuwien.mmue_lm7.game.Score;
import at.ac.tuwien.mmue_lm7.game.ScoreDAO;
import at.ac.tuwien.mmue_lm7.game.ScoreDatabase;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Shows highscore and buttons to navigate to a new game activity or root main activity
 */
public class GameOver extends AppCompatActivity {
    //Names for the expected data input
    public static final String SCORE_KEY = "score";
    public static final String TIME_KEY = "time";
    public static final String GAME_COMPLETED_KEY = "gameCompleted";

    private TextView highscoreText;
    private TextView newHighScoreText;

    //data input
    private int score;
    private int time;
    private boolean gameCompleted;

    //database access
    private ExecutorService es;
    private ScoreDAO scoreDAO;
    private ScoreDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //get input from intent
        score = getIntent().getIntExtra(SCORE_KEY,0);
        time = getIntent().getIntExtra(TIME_KEY,0);
        gameCompleted = getIntent().getBooleanExtra(GAME_COMPLETED_KEY,false);

        //set score text
        TextView scoreText = findViewById(R.id.score);
        scoreText.setText(getResources().getString(R.string.your_score,score));

        //get highscore text
        highscoreText = findViewById(R.id.highscore);
        newHighScoreText = findViewById(R.id.new_highscore);

        //access score database
        db = ScoreDatabase.get(getApplicationContext());
        scoreDAO = db.scoreDAO();
        es = Executors.newSingleThreadExecutor();
        es.execute(() -> {
            Score s = scoreDAO.getScore();
            runOnUiThread(() -> loadHighscore(s));
        });
    }

    private void loadHighscore(Score highscore) {
        //check if current score is a new highscore
        if(highscore == null || highscore.getScore()<score) {
            highscore = new Score(score,time,gameCompleted);
            //store new highscore in database
            es.execute(() -> {
                scoreDAO.clearScore();
                scoreDAO.insertScore(new Score(score,time,gameCompleted));
            });
        }
        else {
            //make "New Highscore" text invisible if no new highscore has been achieved
            newHighScoreText.setVisibility(View.INVISIBLE);
        }

        //set highscore text
        highscoreText.setText(getResources().getString(R.string.highscore,highscore.getScore()));
    }

    public void onMainMenuButtonClicked(View view) {
        //Switch to main menu activity, remove all activities on top
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onRetryButtonClicked(View view) {
        //Switch to game activity
        Intent intent = new Intent(this, GameActivity.class);
        //finish this activity first, so the game activity replaces this activity
        this.finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}