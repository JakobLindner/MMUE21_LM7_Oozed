package at.ac.tuwien.mmue_lm7.game;

import java.util.Dictionary;
import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Platform;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class LevelFactories {
    @FunctionalInterface
    public interface LevelFactory {
        void create(GameObject root);
    }

    public static HashMap<String,LevelFactory> levelsByName = new HashMap<String, LevelFactory>(){{
        put("1",LevelFactories::createLevel1);
        put("2",LevelFactories::createLevel2);
    }} ;


    public static void createLevel1(GameObject root) {

        // TODO remove this
        // === AABB FOR TESTING ===
        AABB aabb = new AABB(1,1, (short) 0, CollisionLayers.PLATFORM);
        aabb.position.set(15,8);
        root.addChild(aabb);

        // === TEXT ===
        root.addChild(ObjectFactories.makeText(16, 4, "Tap to Jump!"));

        // === OOZE ===
        root.addChild(ObjectFactories.makeOoze(3, 6, 0, false));

        // === ENEMIES ===
        root.addChild(ObjectFactories.makeBlocker(7, 6, 0, false));

        // === PLATFORM 1 ===
        GameObject platform1 = ObjectFactories.makePlatform(3, 7);
        platform1.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipe));
        platform1.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform1.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform1.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform1.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipe));
        platform1.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        root.addChild(platform1);

        // === BACKGROUND ===
        root.addChild(ObjectFactories.makeBackground(16, 9));
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
