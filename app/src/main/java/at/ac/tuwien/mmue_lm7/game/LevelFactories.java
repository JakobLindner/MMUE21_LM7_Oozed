package at.ac.tuwien.mmue_lm7.game;

import java.util.Dictionary;
import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.objects.GameObject;

public class LevelFactories {
    @FunctionalInterface
    public interface LevelFactory {
        void create(GameObject root);
    }

    public HashMap<String,LevelFactory> levelsByName = new HashMap<String, LevelFactory>(){{
        put("1",LevelFactories::createLevel1);
    }} ;


    public static void createLevel1(GameObject root) {
        GameObject platform = ObjectFactories.makePlatform(new ObjectFactories.PlatformParams());
        //...

        root.addChild(platform);
        //...
    }
}
