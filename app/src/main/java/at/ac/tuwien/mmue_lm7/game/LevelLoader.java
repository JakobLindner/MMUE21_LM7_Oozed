package at.ac.tuwien.mmue_lm7.game;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.KillEnemiesObjective;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;

/**
 * creates levels by creating and placing all game objects for them
 *
 * @author jakob
 */
public class LevelLoader {
    private static final String TAG = "LevelFactories";
    private AssetManager assetManager;

    public LevelLoader(Context context) {
        this.assetManager = context.getAssets();
    }

    @FunctionalInterface
    public interface LevelFactory {
        void create(GameObject root);
    }

    public static HashMap<String,LevelFactory> levelsByName = new HashMap<String, LevelFactory>(){{
        put("1", LevelLoader::createLevel1);
    }} ;


    // TODO tutorial level

    public static void createLevel1(GameObject root) {

        // === OBJECTIVE ===
        root.addChild(new KillEnemiesObjective());

        // === OBSTACLES ===
        root.addChild(ObjectFactories.makeSpikes(20, 8, Direction.UP));

        // === TEXT ===
        root.addChild(ObjectFactories.makeText(16, 5, "Tap to Jump!"));
        root.addChild(ObjectFactories.makeText(16, 3, "Swipe to Dash!"));

        // === OOZE ===
        root.addChild(ObjectFactories.makeOoze(1, 5, Direction.RIGHT, true));

        // === ENEMIES ===
        root.addChild(ObjectFactories.makeBlocker(18, 0, Direction.RIGHT, false));
        root.addChild(ObjectFactories.makeBlocker(8, 0, Direction.LEFT, true));
        root.addChild(ObjectFactories.makeBlocker(1, 7, Direction.LEFT, true));
        root.addChild(ObjectFactories.makeBlocker(23, 14, Direction.RIGHT, true));


        // === PLATFORM 1 ===
        GameObject platform1 = ObjectFactories.makePlatform(3, 12);
        platform1.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipe));
        platform1.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform1.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform1.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform1.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipe));
        platform1.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        platform1.addChild(ObjectFactories.makeBigPlatformTile(4, 1, 0, true, ResourceSystem.SpriteEnum.platformBigGears));
        root.addChild(platform1);

        // === PLATFORM WRAP 1 ===
        // wraps with itself
        GameObject platformWrap1 = ObjectFactories.makePlatform(0, 4);
        platformWrap1.addChild(ObjectFactories.makePlatformTile(-1, 0, ResourceSystem.SpriteEnum.platformPipe)); // out of screen for wrap
        platformWrap1.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(5, 0, ResourceSystem.SpriteEnum.platformCircuit));

        platformWrap1.addChild(ObjectFactories.makePlatformTile(29, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(30, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(31, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(32, 0, ResourceSystem.SpriteEnum.platformCircuit)); // out of screen for wrap
        root.addChild(platformWrap1);

        // === PLATFORM WRAP 2 Left ===
        // wraps together with platform 2 right
        GameObject platformWrap2_left = ObjectFactories.makePlatform(0, 8);
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(-1, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        root.addChild(platformWrap2_left);

        // === PLATFORM WRAP 2 Right ===
        GameObject platformWrap2_right = ObjectFactories.makePlatform(32 - 6, 8);
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(6, 0, ResourceSystem.SpriteEnum.platformPipe));
        root.addChild(platformWrap2_right);

        // === PLATFORM 2 ===
        GameObject platform2 = ObjectFactories.makePlatform(12, 8);
        platform2.addChild(ObjectFactories.makeBigPlatformTile(0, 0, 0, true, ResourceSystem.SpriteEnum.platformBigGears));
        platform2.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform2.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform2.addChild(ObjectFactories.makeBigPlatformTile(2, 1, ResourceSystem.SpriteEnum.platformBigGears));
        platform2.addChild(ObjectFactories.makePlatformTile(0, 2, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform2.addChild(ObjectFactories.makePlatformTile(1, 2, ResourceSystem.SpriteEnum.platformPipe));
        root.addChild(platform2);

        // === PLATFORM 3 ===
        GameObject platform3 = ObjectFactories.makePlatform(20, 11);
        platform3.addChild(ObjectFactories.makeBigPlatformTile(0, 0, ResourceSystem.SpriteEnum.platformBigGears));
        platform3.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform3.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform3.addChild(ObjectFactories.makeBigPlatformTile(2, 1, ResourceSystem.SpriteEnum.platformBigGears));
        platform3.addChild(ObjectFactories.makePlatformTile(0, 2, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform3.addChild(ObjectFactories.makePlatformTile(1, 2, ResourceSystem.SpriteEnum.platformPipe));
        root.addChild(platform3);

        // === PLATFORM TEXT ===
        GameObject platformText = ObjectFactories.makePlatform(16, 1);
        // Bottom
        platformText.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(5, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(6, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-1, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-2, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-3, 0, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(-4, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-5, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-6, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-7, 0, ResourceSystem.SpriteEnum.platformPipe));
        // Top
        platformText.addChild(ObjectFactories.makePlatformTile(0, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(1, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(2, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(3, 6, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(4, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(5, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(6, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 6, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-1, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-2, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-3, 6, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(-4, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-5, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-6, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-7, 6, ResourceSystem.SpriteEnum.platformPipe));
        // Left
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 5, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 4, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 3, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 2, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 1, ResourceSystem.SpriteEnum.platformPipe));
        // Right
        platformText.addChild(ObjectFactories.makePlatformTile(7, 5, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 4, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 3, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 2, ResourceSystem.SpriteEnum.platformPipe));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 1, ResourceSystem.SpriteEnum.platformPipe));
        root.addChild(platformText);

        // === BACKGROUND ===
        root.addChild(ObjectFactories.makeBackground());
    }

    public boolean loadLevel(GameObject root, int id) {
        return loadLevel(root, "" + id);
    }

    /**
     * @param root !=null
     * @param name != null
     * @return true if level could be loaded, false otherwise
     */
    public boolean loadLevel(GameObject root, String name) {
        //check if there is a level building procedure
        if(!levelsByName.containsKey(name)) {
            //check if there is a level json
            try {
                //loading text from asset: https://stackoverflow.com/a/16110044
                InputStream is = assetManager.open(String.format("levels/%s.json",name));
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8 ));
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                br.close();

                //deserialize level from json and load
                try {
                    JSONObject json = (JSONObject) new JSONTokener(sb.toString()).nextValue();
                    Level level = Level.fromJSON(json);
                    level.build(root);
                    return true;
                }
                catch(JSONException e) {
                    Log.e(TAG, "Unable to load level json", e);
                    return false;
                }
            } catch (IOException e) {
                Log.e(TAG,String.format("Exception while opening level %s",name),e);
            }
            return false;
        }

        levelsByName.get(name).create(root);
        return true;

    }
}
