package at.ac.tuwien.mmue_lm7.game.resources;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class SoundSystem {
    private static final String TAG = "SoundSystem";
    private static SoundSystem INSTANCE;

    public static SoundSystem get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SoundSystem(context);
        }
        return INSTANCE;
    }

    public static final float DEFAULT_MUSIC_VOLUME = 0.1f;

    private Context context;

    private AudioManager audioManager;
    private int audioSessionId;
    private AudioAttributes musicAttributes;
    private AudioAttributes soundAttributes;
    private MediaPlayer currentMusic;
    private int currentMusicId = -1;
    private boolean muted = false;

    //prevent instantiations
    private SoundSystem(Context context) {
        this.context = context;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        setMusicVolume(DEFAULT_MUSIC_VOLUME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            loadAudioAttributes();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadAudioAttributes() {
        musicAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        soundAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        audioSessionId = audioManager.generateAudioSessionId();
    }

    public void playMusic(int id) {
        if (currentMusicId == id)
            return;

        this.currentMusicId = id;
        //stop current music
        if (currentMusic != null) {
            currentMusic.stop();
        }

        //initialize media player
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            currentMusic = MediaPlayer.create(context, id, musicAttributes, audioSessionId);
        else
            currentMusic = MediaPlayer.create(context, id);

        //null check since creation may fail
        if (currentMusic != null) {
            currentMusic.setLooping(true);
            currentMusic.start();
        }
    }

    public void pauseMusic() {
        if (currentMusic == null) {
            Log.w(TAG, "No music loaded to pause");
        } else
            currentMusic.pause();
    }

    public void resumeMusic() {
        if (currentMusic == null) {
            Log.w(TAG, "No music loaded to resume");
        } else
            currentMusic.start();
    }

    public void stopMusic() {
        if(currentMusic == null) {
            Log.w(TAG, "No music loaded to stop");
        }
        else {
            currentMusic.stop();
            currentMusicId = -1;
            currentMusic = null;
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void toggleMuted() {
        muted = !muted;
    }


    /**
     * @param volume in percent, this is clamped between to [0,1]
     */
    public void setMusicVolume(float volume) {
        setVolume(AudioManager.STREAM_MUSIC, volume);
    }

    /**
     * @param volume in percent, this is clamped between to [0,1]
     */
    public void setSoundVolume(float volume) {
        //TODO
    }

    private void setVolume(int streamType, float volume) {
        int max = audioManager.getStreamMaxVolume(streamType);
        int min = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            min = audioManager.getStreamMinVolume(streamType);
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round((max - min) * volume) + min, 0);
    }
}
