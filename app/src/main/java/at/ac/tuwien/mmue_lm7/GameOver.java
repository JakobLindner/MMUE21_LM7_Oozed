package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Shows highscore and buttons to navigate to a new game activity or root main activity
 */
public class GameOver extends AppCompatActivity {
    //Names for the expected data input
    public static final String SCORE_KEY = "score";
    public static final String TIME_KEY = "time";
    public static final String GAME_COMPLETED_KEY = "gameCompleted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        //set highscore text
        TextView highscore = findViewById(R.id.highscore);
        highscore.setText(getResources().getString(R.string.highscore,123));//TODO replace 123 with real highscore

        //TODO make "New Highscore" text invisible if no new highscore has been achieved
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
}