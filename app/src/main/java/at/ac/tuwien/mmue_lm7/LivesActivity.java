package at.ac.tuwien.mmue_lm7;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LivesActivity extends AppCompatActivity {
    public static final String LIVES_KEY = "lives";
    public static final String LEVEL_KEY = "level";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lives);

        //get inputs from intent
        int lives = getIntent().getIntExtra(LIVES_KEY,0);
        String level = getIntent().getStringExtra(LEVEL_KEY);

        //replace placeholders in text views
        TextView livesText = findViewById(R.id.livesText);
        livesText.setText(getResources().getString(R.string.lives,lives));

        TextView levelText = findViewById(R.id.levelText);
        levelText.setText(getResources().getString(R.string.level,level));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP) {
            this.finish();
            return true;
        }
        return super.onTouchEvent(event);
    }
}