package at.ac.tuwien.mmue_lm7.game;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.level.builder.LevelBuilder;
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
    private final AssetManager assetManager;

    public LevelLoader(Context context) {
        this.assetManager = context.getAssets();
    }

    @FunctionalInterface
    public interface LevelFactory {
        void create(GameObject root);
    }

    public static HashMap<String, LevelFactory> levelsByName = new HashMap<String, LevelFactory>() {{
        put("7", LevelLoader::createLevel1);
        put("2", LevelLoader::createLevel2);
        put("3", LevelLoader::fivePlatforms);
        put("5", LevelLoader::tightSlalom);
    }};


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
        root.addChild(ObjectFactories.makeOoze(1, 5, Direction.UP, true));

        // === ENEMIES ===
        //root.addChild(ObjectFactories.makeBlocker(18, 0, Direction.RIGHT, false));
        //root.addChild(ObjectFactories.makeBlocker(8, 0, Direction.LEFT, true));
        root.addChild(ObjectFactories.makeBlocker(1, 7, Direction.DOWN, true, true));
        //root.addChild(ObjectFactories.makeBlocker(23, 14, Direction.RIGHT, true));


        // === PLATFORM 1 ===
        GameObject platform1 = ObjectFactories.makePlatform(3, 12);
        platform1.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platform1.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform1.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform1.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform1.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platform1.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        platform1.addChild(ObjectFactories.makeBigPlatformTile(4, 1, 0, true, ResourceSystem.SpriteEnum.platformBigGears));
        root.addChild(platform1);

        // === PLATFORM WRAP 1 ===
        // wraps with itself
        GameObject platformWrap1 = ObjectFactories.makePlatform(0, 4);
        platformWrap1.addChild(ObjectFactories.makePlatformTile(-1, 0, ResourceSystem.SpriteEnum.platformPipeCross)); // out of screen for wrap
        platformWrap1.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(5, 0, ResourceSystem.SpriteEnum.platformCircuit));

        platformWrap1.addChild(ObjectFactories.makePlatformTile(29, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(30, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(31, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap1.addChild(ObjectFactories.makePlatformTile(32, 0, ResourceSystem.SpriteEnum.platformCircuit)); // out of screen for wrap
        root.addChild(platformWrap1);

        // === PLATFORM WRAP 2 Left ===
        // wraps together with platform 2 right
        GameObject platformWrap2_left = ObjectFactories.makePlatform(0, 8);
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(-1, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap2_left.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        root.addChild(platformWrap2_left);

        // === PLATFORM WRAP 2 Right ===
        GameObject platformWrap2_right = ObjectFactories.makePlatform(32 - 6, 8);
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(5, 0, 180, false, ResourceSystem.SpriteEnum.platformCircuit));
        platformWrap2_right.addChild(ObjectFactories.makePlatformTile(6, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        root.addChild(platformWrap2_right);

        // === PLATFORM 2 ===
        GameObject platform2 = ObjectFactories.makePlatform(12, 8);
        platform2.addChild(ObjectFactories.makeBigPlatformTile(0, 0, 0, true, ResourceSystem.SpriteEnum.platformBigGears));
        platform2.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform2.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform2.addChild(ObjectFactories.makeBigPlatformTile(2, 1, ResourceSystem.SpriteEnum.platformBigGears));
        platform2.addChild(ObjectFactories.makePlatformTile(0, 2, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform2.addChild(ObjectFactories.makePlatformTile(1, 2, ResourceSystem.SpriteEnum.platformPipeCross));
        root.addChild(platform2);

        // === PLATFORM 3 ===
        GameObject platform3 = ObjectFactories.makePlatform(20, 11);
        platform3.addChild(ObjectFactories.makeBigPlatformTile(0, 0, ResourceSystem.SpriteEnum.platformBigGears));
        platform3.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platform3.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformIce));
        platform3.addChild(ObjectFactories.makeBigPlatformTile(2, 1, ResourceSystem.SpriteEnum.platformBigGears));
        platform3.addChild(ObjectFactories.makePlatformTile(0, 2, ResourceSystem.SpriteEnum.platformPipeOpen));
        platform3.addChild(ObjectFactories.makePlatformTile(1, 2, ResourceSystem.SpriteEnum.platformPipeCross));
        root.addChild(platform3);

        // === PLATFORM TEXT ===
        GameObject platformText = ObjectFactories.makePlatform(16, 1);
        // Bottom
        platformText.addChild(ObjectFactories.makePlatformTile(0, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(1, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(2, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(3, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(4, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(5, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(6, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-1, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-2, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-3, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(-4, 0, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-5, 0, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-6, 0, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-7, 0, ResourceSystem.SpriteEnum.platformPipeCross));
        // Top
        platformText.addChild(ObjectFactories.makePlatformTile(0, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(1, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(2, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(3, 6, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(4, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(5, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(6, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 6, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-1, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-2, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-3, 6, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(-4, 6, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-5, 6, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-6, 6, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-7, 6, ResourceSystem.SpriteEnum.platformPipeCross));
        // Left
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 5, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 4, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 3, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 2, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(-8, 1, ResourceSystem.SpriteEnum.platformPipeCross));
        // Right
        platformText.addChild(ObjectFactories.makePlatformTile(7, 5, ResourceSystem.SpriteEnum.platformCircuit));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 4, ResourceSystem.SpriteEnum.platformIce));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 3, ResourceSystem.SpriteEnum.platformPipeOpen));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 2, ResourceSystem.SpriteEnum.platformPipeCross));
        platformText.addChild(ObjectFactories.makePlatformTile(7, 1, ResourceSystem.SpriteEnum.platformPipeCross));
        root.addChild(platformText);

        // === BACKGROUND ===
        root.addChild(ObjectFactories.makeBackground());
    }

    public static void createLevel2(GameObject root) {
        Level level = new LevelBuilder("2")
                .outerWall()
                    .thickness(2)
                    .holeX(6,7,24,25)
                    .holeY(8,9)
                    .horizontalPattern("G#")
                    .verticalPattern("B###")
                .platform()
                    .at(10, 11)
                    .size(8, 3)
                    .pattern("PCIOG#II" +
                             "G#PO##OP" +
                             "##COPIOP")
                .player()
                    .at(18, 11)
                    .orient(Direction.RIGHT, false)
                .blocker()
                    .at(15, 10)
                    .orient(Direction.DOWN, true)
                    .dynamic(false)
                //.spikes()
                //    .at(13, 9)
                //    .dir(Direction.DOWN)
                .build();

        level.build(root);
    }

    public static void fivePlatforms(GameObject root) {
        final int D = 3;//corner platform dist
        Level level = new LevelBuilder("Five platforms")
                .outerWall()
                .platform()
                    .at(D,D)
                    .size(6,3)
                    .pattern("CIOPG#"+
                             "G#IO##"+
                             "##PICC")
                .copy()
                    .at(32-D-6,18-D-3)
                .platform()
                    .at(32-D-6,D)
                    .size(6,3)
                    .pattern("B#PIOP"+
                             "##CIB#"+
                             "OOPC##")
                .copy()
                    .at(D,18-D-3)
                .platform()
                    .at(8,8)
                    .size(16,2)
                    .pattern("POICO")
                .player()
                    .at(5,6)
                    .orient(Direction.UP,true)
                .blocker()
                    .at(15,7)
                    .orient(Direction.DOWN,false)
                .blocker()
                    .at(32-5,6)
                    .orient(Direction.UP,true)
                    .dynamic(false)
                .copy()
                    .at(4,15)
                .blocker()
                    .at(22,4)
                    .orient(Direction.LEFT,false)
                    .dynamic(false)
                .blocker()
                    .at(9,18-6)
                    .orient(Direction.RIGHT,false)
                    .dynamic(false)
                .jumper()
                    .at(18,10)
                    .orient(Direction.UP)
                .jumper()
                    .at(12,18-2)
                    .orient(Direction.DOWN)
                //.copter().at(20,2)
                .spikes()
                    .at(14,1)
                    .dir(Direction.UP)
                .copy().at(15,1)
                .build();

        level.build(root);
    }

    public static void tightSlalom(GameObject root) {
        Level level = new LevelBuilder("Tight Slalom")
                .outerWall()
                    .thickness(2)
                    .horizontalPattern("B#")
                    .verticalPattern("B###")
                .platform()
                    .at(6,2)
                    .size(32-8,2)
                    .pattern("B#")
                .copy().at(6,4)
                .copy().at(6,6)
                .copy()
                    .at(2,14)
                    .size(32-4,2)
                .copy().at(2,12)
                .copy().at(2,10)
                .platform()
                    .at(3,4)
                    .size(2,2)
                    .pattern("G")
                //.platform()
                //    .at(2,6)
                //    .size(32-4,1)
                //    .pattern("P")
                //.copy().at(2,11)
                .player()
                    .at(3,3)
                    .orient(Direction.DOWN,false)
                .blocker()
                    .at(15,8)
                    .orient(Direction.UP,false)
                    .dynamic(false)
                .copy().at(25,8)
                .blocker()
                    .at(10,9)
                    .orient(Direction.DOWN, true)
                    .dynamic(false)
                .copy().at(20,9)
                .build();

        level.build(root);
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
        if (!levelsByName.containsKey(name)) {
            //check if there is a level json
            try {
                //loading text from asset: https://stackoverflow.com/a/16110044
                InputStream is = assetManager.open(String.format("levels/%s.json", name));
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                br.close();

                //deserialize level from json and load
                try {
                    JSONObject json = (JSONObject) new JSONTokener(sb.toString()).nextValue();
                    Level level = Level.fromJSON(json);
                    addText(root, name);
                    level.build(root);
                    return true;
                } catch (JSONException e) {
                    Log.e(TAG, "Unable to load level json", e);
                    return false;
                }
            } catch (IOException e) {
                Log.e(TAG, String.format("Exception while opening level %s", name), e);
            }
            return false;
        }

        levelsByName.get(name).create(root);
        return true;
    }

    /**
     * add help dialog text to specified levels
     * @param root
     * @param name
     * @return true if something was added
     */
    private boolean addText(GameObject root, String name) {
        if (name.contentEquals("1")) {
            root.addChild(ObjectFactories.makeText(16, 5, "Tap to Jump!"));
            root.addChild(ObjectFactories.makeText(16, 3, "Swipe to Dash!"));
            return true;
        }
        
        return false;
    }
}
