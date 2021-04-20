package at.ac.tuwien.mmue_lm7.game;

import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class ObjectFactories {



    public static GameObject makeOoze(int x, int y, float rotation, boolean lookingLeft) {
        GameObject ooze = new GameObject();
        ooze.position.set(x, y);
        ooze.rotation = rotation;
        ooze.mirrored = lookingLeft;

        AnimatedSprite runningSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.oozeRun);
        runningSprite.position.set(0, 0);
        ooze.addChild(runningSprite);

        return ooze;
    }

    public static GameObject makeBlocker(int x, int y, float rotation, boolean lookingRight) {
        GameObject blocker = new GameObject();
        blocker.position.set(x, y);
        blocker.rotation = rotation;
        blocker.mirrored = lookingRight;

        AnimatedSprite idleSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.blockerIdle);
        idleSprite.position.set(0, 0);
        blocker.addChild(idleSprite);

        return blocker;
    }

    public static class PlatformParams {
        public Vec2 pos;
        public Vec2 size;
    }

    // TODO
    public static GameObject makePlatform(PlatformParams params) {
        return null;
    }

    public static GameObject makeBackground(int x, int y) {
        Sprite sprite = new Sprite(ResourceSystem.SpriteEnum.background);
        sprite.position.set(x, y);
        return sprite;
    }
}
