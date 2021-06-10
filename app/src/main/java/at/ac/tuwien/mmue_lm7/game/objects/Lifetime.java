package at.ac.tuwien.mmue_lm7.game.objects;

import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.Game;

/**
 * kills given gameobject after given amount of frames have passed
 * if null is passed as gameobject, then the object kills itself
 * @author simon
 */
public class Lifetime extends GameObject {
    private int frameTime;
    public GameObject objectToKill;

    public Lifetime(int frames) {
        this(frames,null);
    }

    public Lifetime(int frames, GameObject objectToKill) {
        this.frameTime = frames;
        this.objectToKill = objectToKill;
    }

    @Override
    public void init() {
        super.init();

        Game.get().getTimingSystem().addDelayedAction(this::lifeTimeOver,frameTime);
    }

    private void lifeTimeOver() {
        if(objectToKill==null)
            objectToKill = this;
        objectToKill.kill();
    }
}
