package at.ac.tuwien.mmue_lm7.game;

import java.util.Dictionary;
import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
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
        // === SOME TEXT AND AABB ===
        AABB aabb = new AABB(1,1);
        aabb.position.set(15,8);
        root.addChild(aabb);
        Text text = new Text("There is a green rectangle.");
        text.position.set(15,4);
        root.addChild(text);

        // === OOZE ===
        root.addChild(ObjectFactories.makeOoze(3, 6, 0, false));

        // === ENEMIES ===
        root.addChild(ObjectFactories.makeBlocker(5, 6, 0, false));

        // === PLATFORM 1 ===

        //GameObject platform = ObjectFactories.makePlatform(new ObjectFactories.PlatformParams());
        //...

        //root.addChild(platform);
        //...

        GameObject platform1 = new GameObject(); // TODO make this Platform class object
        root.addChild(platform1);
        platform1.position.set(3, 7);
        //platform1.rotation = 90; // TODO broken

        GameObject pipe1 = new GameObject();
        platform1.addChild(pipe1);
        Sprite pipe1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformPipe);
        pipe1Sprite.position.set(0,0);
        pipe1.addChild(pipe1Sprite);

        GameObject pipeOpen1 = new GameObject();
        platform1.addChild(pipeOpen1);
        Sprite pipeOpen1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformPipeOpen);
        pipeOpen1Sprite.position.set(1,0);
        pipeOpen1.addChild(pipeOpen1Sprite);

        GameObject circuit1 = new GameObject();
        platform1.addChild(circuit1);
        Sprite circuit1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformCircuit);
        circuit1Sprite.position.set(2,0);
        circuit1.addChild(circuit1Sprite);

        GameObject ice1 = new GameObject();
        platform1.addChild(ice1);
        Sprite ice1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformIce);
        ice1Sprite.position.set(3,0);
        ice1.addChild(ice1Sprite);

        // === BACKGROUND ===
        GameObject background = new GameObject();
        root.addChild(background);
        for (int i = 0; i < GameConstants.GAME_WIDTH / 2; i++) {
            for (int j = 0; j < GameConstants.GAME_RES_HEIGHT / 2; j++) {
                background.addChild(ObjectFactories.makeBackground(i * 2 + 1, j * 2 + 1));
            }
        }
    }

    public static void createLevel2(GameObject root) {

    }

    public static void loadLevel(GameObject root, int id) {
        loadLevel(root, "" + id);
    }

    public static void loadLevel(GameObject root, String name) {
        levelsByName.get(name).create(root);
    }
}
