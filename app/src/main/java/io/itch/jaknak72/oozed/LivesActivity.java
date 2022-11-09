package io.itch.jaknak72.oozed;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * WARNING: This activity is currently unused and now implemented as a in-game screen, due to being much simpler
 *
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * @author simon
 */
public class LivesActivity extends FullscreenActivity {
    public static final String LIVES_KEY = "lives";
    public static final String LEVEL_KEY = "level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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