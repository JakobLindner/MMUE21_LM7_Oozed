package at.ac.tuwien.mmue_lm7.game;

import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;

/**
 * @author jakob
 * creates levels by creating and placing all game objects for them
 */
public class LevelFactories {
    @FunctionalInterface
    public interface LevelFactory {
        void create(GameObject root);
    }

    public static HashMap<String,LevelFactory> levelsByName = new HashMap<String, LevelFactory>(){{
        put("1",LevelFactories::createLevel1);
        put("2",LevelFactories::createLevel2);
    }} ;

    // TODO tutorial level

    public static void createLevel1(GameObject root) {

        // TODO remove this
        // === AABB FOR TESTING ===
        AABB aabb = new AABB(1,1, (short) 0, CollisionLayers.PLATFORM);
        aabb.position.set(15,8);
        root.addChild(aabb);

        // === TEXT ===
        root.addChild(ObjectFactories.makeText(16, 4, "Tap to Jump!"));

        // === OOZE ===
        root.addChild(ObjectFactories.makeOoze(3, 13, 0, false));

        // === ENEMIES ===
        root.addChild(ObjectFactories.makeBlocker(13, 11, 0, true));
        root.addChild(ObjectFactories.makeBlocker(22, 10, 180, false));
        root.addChild(ObjectFactories.makeBlocker(23, 14, 0, false));

        // === PLATFORM 1 ===
        GameObject platform1 = ObjectFactories.makePlatform(3, 12);
        platform1.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipe));
        platform1.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform1.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform1.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform1.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipe));
        platform1.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));

        root.addChild(platform1);

        // === PLATFORM 2 ===
        GameObject platform2 = ObjectFactories.makePlatform(12, 8);
        platform2.addChild(ObjectFactories.makeBigPlatformTile(0, 0, 0, true, ResourceSystem.SpriteEnum.bigPlatformGears));
        platform2.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform2.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform2.addChild(ObjectFactories.makeBigPlatformTile(2, 1, ResourceSystem.SpriteEnum.bigPlatformGears));
        platform2.addChild(ObjectFactories.makePlatformTile(0, 2, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform2.addChild(ObjectFactories.makePlatformTile(1, 2, ResourceSystem.SpriteEnum.platformPipe));
        root.addChild(platform2);

        // === PLATFORM 3 ===
        GameObject platform3 = ObjectFactories.makePlatform(20, 11);
        platform3.addChild(ObjectFactories.makeBigPlatformTile(0, 0, ResourceSystem.SpriteEnum.bigPlatformGears));
        platform3.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform3.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform3.addChild(ObjectFactories.makeBigPlatformTile(2, 1, ResourceSystem.SpriteEnum.bigPlatformGears));
        platform3.addChild(ObjectFactories.makePlatformTile(0, 2, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform3.addChild(ObjectFactories.makePlatformTile(1, 2, ResourceSystem.SpriteEnum.platformPipe));
        root.addChild(platform3);


        // === BACKGROUND ===
        root.addChild(ObjectFactories.makeBackground());
    }

    public static void createLevel2(GameObject root) {
        // === OOZE ===
        root.addChild(ObjectFactories.makeOoze(3, 6, 0, false));
    }

    public static void loadLevel(GameObject root, int id) {
        loadLevel(root, "" + id);
    }

    public static void loadLevel(GameObject root, String name) {
        levelsByName.get(name).create(root);
    }
}
