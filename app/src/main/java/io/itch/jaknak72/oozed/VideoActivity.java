package io.itch.jaknak72.oozed;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import io.itch.jaknak72.oozed.game.ScoreDatabase;
import io.itch.jaknak72.oozed.game.resources.SoundSystem;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Plays video at game launch and transitions to main menu
 * @author jakob
 */
public class VideoActivity extends FullscreenActivity {
    public static final float VIDEO_VOLUME = 0.75f;

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
            else
                mp.setVolume(VIDEO_VOLUME,VIDEO_VOLUME);
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