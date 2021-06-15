package at.ac.tuwien.mmue_lm7;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;
import at.ac.tuwien.mmue_lm7.utils.Utils;

import static at.ac.tuwien.mmue_lm7.GameOver.GAME_COMPLETED_KEY;
import static at.ac.tuwien.mmue_lm7.GameOver.SCORE_KEY;
import static at.ac.tuwien.mmue_lm7.GameOver.TIME_KEY;

/**
 * Win screen that get's shown after all levels are completed
 * @author jakob
 */
public class WinActivity extends AppCompatActivity {

    private int buttonSoundId;

    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_win);


        //set background image without anti-aliasing
        ImageView backgroundView = findViewById(R.id.win_background);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.menu, options);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), background);
        drawable.getPaint().setFilterBitmap(false);

        backgroundView.setImageDrawable(drawable);

        //set prof image without anti-aliasing
        ImageView profView = findViewById(R.id.win_prof);
        Bitmap prof = BitmapFactory.decodeResource(getResources(), R.drawable.prof, options);
        drawable = new BitmapDrawable(getResources(), prof);
        drawable.getPaint().setFilterBitmap(false);
        profView.setImageDrawable(drawable);

        //set ooze image without anti-aliasing
        ImageView oozeView = findViewById(R.id.win_ooze);
        Bitmap ooze = BitmapFactory.decodeResource(getResources(), R.drawable.ooze_single, options);
        drawable = new BitmapDrawable(getResources(), ooze);
        drawable.getPaint().setFilterBitmap(false);
        oozeView.setImageDrawable(drawable);

        //set smoke image without anti-aliasing
        ImageView smokeView = findViewById(R.id.win_smoke);
        Bitmap smoke = BitmapFactory.decodeResource(getResources(), R.drawable.smoke, options);
        drawable = new BitmapDrawable(getResources(), smoke);
        drawable.getPaint().setFilterBitmap(false);
        smokeView.setImageDrawable(drawable);

        Animation smokeAnim = AnimationUtils.loadAnimation(this, R.anim.smoke);
        //smokeAnim.setFillBefore(false);
        smokeAnim.setFillAfter(true);

        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.new_highscore);

        smokeView.setAnimation(smokeAnim);

        TextView winText = findViewById(R.id.win_title);
        winText.setAnimation(textAnim);
        Button button = findViewById(R.id.continue_button);
        button.setAnimation(textAnim);

        AnimationSet animations = new AnimationSet(false);
        animations.addAnimation(smokeAnim);
        animations.addAnimation(textAnim);

        //insert time
        time = getIntent().getIntExtra(TIME_KEY,0);
        TextView timeText = findViewById(R.id.timeText);
        timeText.setText(getString(R.string.time, Utils.getMinutes(time), Utils.getLeftoverSeconds(time)));

        //load button sound
        buttonSoundId = SoundSystem.get().loadSound(R.raw.button);
    }

    public void onContinueButtonClicked(View view) {
        //Switch to game over activity
        Intent intent = new Intent(this, GameOver.class);
        intent.putExtra(SCORE_KEY,getIntent().getIntExtra(SCORE_KEY,0));
        intent.putExtra(TIME_KEY,time);
        intent.putExtra(GAME_COMPLETED_KEY, getIntent().getBooleanExtra(GAME_COMPLETED_KEY,false));
        //finish this activity first, so the game over activity replaces this activity
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