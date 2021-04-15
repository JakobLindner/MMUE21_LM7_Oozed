package at.ac.tuwien.mmue_lm7.game;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Platform;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class ObjectFactories {



    public static GameObject makeOoze(int x, int y, float rot, boolean lookingLeft) {
        GameObject ooze = new GameObject();
        ooze.position.set(x, y);
        ooze.rotation = rot;
        ooze.mirrored = lookingLeft;

        AnimatedSprite runningSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.oozeRun);
        runningSprite.position.set(0, 0);
        ooze.addChild(runningSprite);

        // TODO Add AABB

        return ooze;
    }

    public static GameObject makeBlocker(int x, int y, float rot, boolean lookingRight) {
        GameObject blocker = new GameObject();
        blocker.position.set(x, y);
        blocker.rotation = rot;
        blocker.mirrored = lookingRight;

        AnimatedSprite idleSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.blockerIdle);
        idleSprite.position.set(0, 0);
        blocker.addChild(idleSprite);

        // TODO Add AABB

        return blocker;
    }

    public static class PlatformParams {
        public Vec2 pos;
        public Vec2 size;
    }

    public static GameObject makePlatform(int x, int y) {
        Platform platform = new Platform();
        platform.position.set(x, y);
        return platform;
    }

    public static GameObject makePlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y, 0, false, sprite);
    }

    public static GameObject makePlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        GameObject tile = new GameObject();
        tile.position.set(x, y);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        // TODO Fix AABB
        AABB aabb = new AABB(0.5f, 0.5f);
        //tile.addChild(aabb);

        return tile;
    }

    public static GameObject makeBackground(int x, int y) {
        Sprite sprite = new Sprite(ResourceSystem.SpriteEnum.background);
        sprite.position.set(x, y);
        return sprite;
    }

    public static GameObject makeText(int x, int y, String text) {
        Text t = new Text(text);
        t.position.set(x, y);
        return t;
    }
}