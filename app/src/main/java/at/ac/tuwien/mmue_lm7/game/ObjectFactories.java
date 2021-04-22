package at.ac.tuwien.mmue_lm7.game;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Platform;
import at.ac.tuwien.mmue_lm7.game.objects.Player;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;

/**
 * @author jakob
 * creates all game objects for easy instanciation from the LevelFactories
 * all x and y parameters are the lower left corner of the object
 */
public class ObjectFactories {

    public static Player makeOoze(int x, int y, Direction direction, boolean runningCW) {
        Player ooze = new Player(direction, runningCW);//TODO horizontally flip this or ooze sprites
        ooze.position.set(x + 0.5f, y + 0.5f);

        AnimatedSprite runningSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.oozeRun);
        runningSprite.position.set(0, 0);
        ooze.addChild(runningSprite);

        // TODO Add AABB

        return ooze;
    }

    public static GameObject makeBlocker(int x, int y, float rot, boolean lookingRight) {
        GameObject blocker = new GameObject();
        blocker.position.set(x + 0.5f, y + 0.5f);
        blocker.rotation = rot;
        blocker.mirrored = lookingRight;

        AnimatedSprite idleSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.blockerIdle);
        idleSprite.position.set(0, 0);
        blocker.addChild(idleSprite);

        // TODO Add AABB

        return blocker;
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
        tile.position.set(x + 0.5f, y + 0.5f);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        AABB aabb = new AABB(0.5f, 0.5f,(short) 0, CollisionLayers.PLATFORM);
        tile.addChild(aabb);

        return tile;
    }

    public static GameObject makeBigPlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makeBigPlatformTile(x, y, 0, false, sprite);
    }

    public static GameObject makeBigPlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        GameObject tile = new GameObject();
        tile.position.set(x + 1.0f, y + 1.0f);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        AABB aabb = new AABB(1.0f, 1.0f,(short) 0, CollisionLayers.PLATFORM);
        tile.addChild(aabb);

        return tile;
    }

    public static GameObject makeBackground() {
        Sprite sprite = new Sprite(ResourceSystem.SpriteEnum.background);
        sprite.position.set(16, 9);
        return sprite;
    }

    public static GameObject makeText(int x, int y, String text) {
        Text t = new Text(text);
        t.position.set(x, y);
        return t;
    }
}
