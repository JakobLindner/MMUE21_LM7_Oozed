package io.itch.jaknak72.oozed.game.resources;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Utility methods to (un)load audio files and play them
 * Muted setting is stored in preferences
 * @author simon
 */
public class SoundSystem {
    private static final String TAG = "SoundSystem";
    private static SoundSystem INSTANCE;


    /**
     * needs to be called once before retrieving singleton
     */
    public static void load(Context context) {
        if (INSTANCE != null) {
            //Log.w(TAG, "SoundSystem has already been loaded");
        }
        else
            INSTANCE = new SoundSystem(context);
    }

    /**
     * load(Context) needs to be called once before
     * @return The singleton soundsystem instance
     */
    public static SoundSystem get() {
        return INSTANCE;
    }

    public static final float DEFAULT_MUSIC_VOLUME = 0.1f;
    public static final float DEFAULT_SOUND_VOLUME = 0.5f;
    public static final int SOUND_PRIORITY = 1;//same value as in lecture slides
    public static final String MUTE_KEY = "muted";
    /**
     * Max number of sounds, that can be played in parallel
     */
    public static final int MAX_SOUNDS = 5;

    private Context context;

    private AudioManager audioManager;
    private int audioSessionId;
    private AudioAttributes musicAttributes;
    private AudioAttributes soundAttributes;
    private MediaPlayer currentMusic;
    private int currentMusicId = -1;
    private boolean muted;

    private SoundPool soundPool;

    //prevent instantiations
    private SoundSystem(Context context) {
        this.context = context;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            loadAudioAttributes();

        initSoundPool();

        muted = context.getSharedPreferences(SoundSystem.TAG,Context.MODE_PRIVATE).getBoolean(MUTE_KEY,false);
    }

    private void initSoundPool() {
        if (soundPool != null) {
            //Log.w(TAG, "Sound pool already initialized");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(soundAttributes)
                    .setMaxStreams(MAX_SOUNDS)
                    .build();
        } else {
            soundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
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

    /**
     * Loads given sound
     *
     * @return sound id that can be used for playback and unloading
     */
    public int loadSound(int resId) {
        return soundPool.load(context, resId, SOUND_PRIORITY);
    }

    public void unloadSound(int soundId) {
        soundPool.unload(soundId);
    }

    /**
     * Plays sound with given sound id loaded with loadSound with 100% of default sound volume
     */
    public void playSound(int soundId) {
        playSound(soundId,1);
    }

    /**
     * Plays sound with given sound id loaded with loadSound
     * @param soundVolume multiplier for sound volume
     */
    public void playSound(int soundId, float soundVolume) {
        soundPool.play(soundId, DEFAULT_SOUND_VOLUME*soundVolume, DEFAULT_SOUND_VOLUME*soundVolume, SOUND_PRIORITY, 0, 1);
    }

    public void playMusic(int id) {
        if (currentMusicId == id) {
            //resume if paused
            if(!currentMusic.isPlaying())
                currentMusic.start();
            return;
        }

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
            currentMusic.setVolume(DEFAULT_MUSIC_VOLUME,DEFAULT_MUSIC_VOLUME);
            if(!muted)
                currentMusic.start();
        }
    }

    public void pauseMusic() {
        if (currentMusic == null) {
            //Log.w(TAG, "No music loaded to pause");
        } else
            currentMusic.pause();
    }

    public void resumeMusic() {
        if (currentMusic == null) {
            //Log.w(TAG, "No music loaded to resume");
        } else if(!muted)
            currentMusic.start();
    }

    public void stopMusic() {
        if (currentMusic == null) {
            //Log.w(TAG, "No music loaded to stop");
        } else {
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

        //store in shared preferences
        SharedPreferences.Editor editor = context.getSharedPreferences(TAG,Context.MODE_PRIVATE).edit();
        editor.putBoolean(MUTE_KEY,muted);
        editor.commit();

        //pause or resume music
        if (currentMusic != null) {
            if (muted)
                currentMusic.pause();
            else
                currentMusic.start();
        }
    }

    /*private void setVolume(int streamType, float volume) {
        int max = audioManager.getStreamMaxVolume(streamType);
        int min = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            min = audioManager.getStreamMinVolume(streamType);
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round((max - min) * volume) + min, 0);
    }*/
}
