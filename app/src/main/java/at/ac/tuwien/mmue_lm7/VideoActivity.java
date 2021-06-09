package at.ac.tuwien.mmue_lm7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import at.ac.tuwien.mmue_lm7.game.ScoreDatabase;
import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

/**
 * Plays video at game launch and transitions to main menu
 * @author jakob
 */
public class VideoActivity extends FullscreenActivity {

     VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //INIT TASKS FOR FIRST ACTIVITY
        //open database for later use
        ScoreDatabase.get(getApplicationContext());
        //initialize sound system
        SoundSystem.load(getApplicationContext());

        videoView = findViewById(R.id.videoView);
        videoView.setOnPreparedListener(mp -> {
            if(SoundSystem.get().isMuted())
                mp.setVolume(0,0);
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer mp)
            {
                skip();
            }
        });

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.oozed_intro);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    public void onSkipButtonClicked(View view) {
        skip();
    }

    private void skip(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}