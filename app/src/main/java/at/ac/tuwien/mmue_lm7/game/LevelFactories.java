package at.ac.tuwien.mmue_lm7.game;

import java.util.Dictionary;
import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;

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
        //GameObject platform = ObjectFactories.makePlatform(new ObjectFactories.PlatformParams());
        //...

        //root.addChild(platform);
        //...
        // TODO use ObjectFactory

        // TODO remove this
        // === SOME TEXT AND AABB ===
        AABB aabb = new AABB(1,1);
        aabb.position.set(15,8);
        root.addChild(aabb);
        Text text = new Text("There is a green rectangle.");
        text.position.set(15,4);
        root.addChild(text);

        // === CHARACTER ===
        GameObject character = new GameObject();
        root.addChild(character);
        AnimatedSprite animatedSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.oozeRun);
        animatedSprite.position.set(3,6);
        animatedSprite.mirrored = true;
        //animatedSprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.oozeRun));
        character.addChild(animatedSprite);

        // === ENEMIES ===
        GameObject blocker1 = new GameObject();
        root.addChild(blocker1);
        AnimatedSprite blocker1Sprite = new AnimatedSprite(ResourceSystem.SpriteEnum.blockerIdle);
        blocker1Sprite.position.set(5,6);
        //blocker1Sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.blockerIdle));
        blocker1.addChild(blocker1Sprite);

        // === PLATFORM 1 ===
        GameObject platform1 = new GameObject(); // TODO make this Platform class object
        root.addChild(platform1);

        GameObject pipe1 = new GameObject();
        platform1.addChild(pipe1);
        Sprite pipe1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformPipe);
        pipe1Sprite.position.set(3,7);
        //pipe1Sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.platformPipe));
        pipe1.addChild(pipe1Sprite);

        GameObject pipeOpen1 = new GameObject();
        platform1.addChild(pipeOpen1);
        Sprite pipeOpen1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformPipeOpen);
        pipeOpen1Sprite.position.set(4,7);
        //pipeOpen1Sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.platformPipeOpen));
        pipeOpen1.addChild(pipeOpen1Sprite);

        GameObject circuit1 = new GameObject();
        platform1.addChild(circuit1);
        Sprite circuit1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformCircuit);
        circuit1Sprite.position.set(5,7);
        //circuit1Sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.platformCircuit));
        circuit1.addChild(circuit1Sprite);

        GameObject ice1 = new GameObject();
        platform1.addChild(ice1);
        Sprite ice1Sprite = new Sprite(ResourceSystem.SpriteEnum.platformIce);
        ice1Sprite.position.set(6,7);
        //ice1Sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.platformIce));
        ice1.addChild(ice1Sprite);

        // === BACKGROUND ===
        GameObject bg = new GameObject();
        root.addChild(bg);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 8; j++) {
                Sprite bgSprite = new Sprite(ResourceSystem.SpriteEnum.background);
                bgSprite.position.set(i * 2 + 1, j * 2 + 1);
                //bgSprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.background));
                bg.addChild(bgSprite);
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
