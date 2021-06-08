package at.ac.tuwien.mmue_lm7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import static at.ac.tuwien.mmue_lm7.GameOver.GAME_COMPLETED_KEY;
import static at.ac.tuwien.mmue_lm7.GameOver.SCORE_KEY;
import static at.ac.tuwien.mmue_lm7.GameOver.TIME_KEY;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_win);
    }

    public void onContinueButtonClicked(View view) {
        //Switch to game over activity
        Intent intent = new Intent(this, GameOver.class);
        intent.putExtra(SCORE_KEY,getIntent().getIntExtra(SCORE_KEY,0));
        intent.putExtra(TIME_KEY,getIntent().getIntExtra(TIME_KEY,0));
        intent.putExtra(GAME_COMPLETED_KEY, getIntent().getBooleanExtra(GAME_COMPLETED_KEY,false));
        //finish this activity first, so the game over activity replaces this activity
        this.finish();
        startActivity(intent);
    }
}