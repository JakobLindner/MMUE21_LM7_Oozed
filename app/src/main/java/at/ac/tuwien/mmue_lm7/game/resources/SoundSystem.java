package at.ac.tuwien.mmue_lm7.game.resources;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class SoundSystem {
    private static final String TAG = "SoundSystem";
    private static SoundSystem INSTANCE;


    public static void load(Context context) {
        if (INSTANCE != null)
            Log.w(TAG, "SoundSystem has already been loaded");
        else
            INSTANCE = new SoundSystem(context);
    }

    public static SoundSystem get() {
        return INSTANCE;
    }

    public static final float DEFAULT_MUSIC_VOLUME = 0.1f;
    public static final float DEFAULT_SOUND_VOLUME = 1f;
    public static final int SOUND_PRIORITY = 1;//same value as in lecture slides
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
    private boolean muted = false;

    private SoundPool soundPool;
    private float soundVolume = DEFAULT_SOUND_VOLUME;

    //prevent instantiations
    private SoundSystem(Context context) {
        this.context = context;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        setMusicVolume(DEFAULT_MUSIC_VOLUME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            loadAudioAttributes();

        initSoundPool();
    }

    private void initSoundPool() {
        if (soundPool != null) {
            Log.w(TAG, "Sound pool already initialized");
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
     * Plays sound with given sound id loaded with loadSound
     */
    public void playSound(int soundId) {
        soundPool.play(soundId, soundVolume, soundVolume, SOUND_PRIORITY, 0, 1);
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
        if (currentMusic == null) {
            Log.w(TAG, "No music loaded to stop");
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

        //pause or resume music
        if (currentMusic != null) {
            if (muted)
                currentMusic.pause();
            else
                currentMusic.start();
        }
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
        this.soundVolume = volume;
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
