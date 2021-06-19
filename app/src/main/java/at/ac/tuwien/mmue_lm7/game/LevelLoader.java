package at.ac.tuwien.mmue_lm7.game;

import android.content.Context;
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

import at.ac.tuwien.mmue_lm7.R;
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
    //public constant shortcuts
    public static final int W = (int)GameConstants.GAME_WIDTH;
    public static final int H = (int) GameConstants.GAME_HEIGHT;

    private static final String TAG = "LevelFactories";
    private final Context context;

    public LevelLoader(Context context) {
        this.context = context;
    }

    @FunctionalInterface
    public interface LevelFactory {
        void create(GameObject root, Context context);
    }

    public static HashMap<String, LevelFactory> levelsByName = new HashMap<String, LevelFactory>() {{
        //put("1", LevelLoader::movementTutorial);
        //put("7", LevelLoader::createLevel1);
        //put("2", LevelLoader::createLevel2);
        //put("3", LevelLoader::fivePlatforms);
        put("3", LevelLoader::tightSlalom);
        put("Dash",LevelLoader::dashJumpTutorial);
        put("Climb",LevelLoader::theClimb);
        put("Gravity Tut", LevelLoader::gravityTutorial);
        put("Copter Test", LevelLoader::copterTest);
    }};

    public static void copterTest(GameObject root, Context context) {
        Level level = new LevelBuilder("Copter Test")
                .json(getJSON(context,"6"))
                .copter()
                    .at(4,H-1)
                    .orient(Direction.DOWN)
                    .noHover(true)
                .copter()
                    .at(2,H/2)
                    .orient(Direction.LEFT)
                .copter()
                    .at(W/2,H-4)
                    .orient(Direction.LEFT)
                    .noHover(true)
                .copter()
                    .at(W-7,H/2)
                    .orient(Direction.UP)
                    .noHover(true)
                .copter()
                    .at(W/2,2)
                    .orient(Direction.RIGHT)
                    .noHover(true)
                .build();

        level.build(root,context);
    }

    public static void gravityTutorial(GameObject root, Context context) {
        final int START_PLATFORM_HEIGHT = 6;
        final int START_X = W-5-6;
        final int SPIKE_WALL_X = START_X-3;
        final int TOP_SPIKE_START = SPIKE_WALL_X;
        Level level = new LevelBuilder("Gravity Tutorial")
                //start platform
                .platform()
                    .at(START_X,3)
                    .size(2,START_PLATFORM_HEIGHT)
                    .pattern("+p"+
                             "+p"+
                             "++"+
                             "++"+
                             "++"+
                             "++"+
                             "++"+
                             "++")
                //middle spike wall
                .platform()
                    .at(SPIKE_WALL_X,0)
                    .size(2,START_PLATFORM_HEIGHT+6)
                    .pattern("p>")
                //bottom spike wall
                .platform()
                    .at(SPIKE_WALL_X+2,0)
                    .size(W-(SPIKE_WALL_X+2),1)
                    .pattern("+")
                .copy()
                    .y(1)
                    .pattern("^")
                //top wall
                .platform()
                    .at(0,H-2)
                    .size(SPIKE_WALL_X-2,2)
                    .pattern("P#")
                //top spikes
                .platform()
                    .at(TOP_SPIKE_START,H-2)
                    .size(W-TOP_SPIKE_START,2)
                    .pattern("P#")
                .platform()
                    .at(TOP_SPIKE_START,H-3)
                    .size(W-TOP_SPIKE_START,1)
                    .pattern("v")
                //left side
                .platform()
                    .at(0,2)
                    .size(2,H-2-2)
                    .pattern("P###")
                .copy().x(2)
                .copy().x(4)
                .copy().x(6)
                //bottom wall
                .platform()
                    .at(0,0)
                    .size(SPIKE_WALL_X-2,2)
                    .pattern("P#")
                .player()
                    .at(START_X-1,START_PLATFORM_HEIGHT)
                    .orient(Direction.LEFT,false)
                .blocker()
                    .at(8,H/2)
                    .orient(Direction.RIGHT,false)
                .build();

        level.build(root,context);
    }

    public static void theClimb(GameObject root, Context context) {
        final int JUMPER_PLATFORM_HEIGHT = 11;
        Level level = new LevelBuilder("The Climb")
                //ground
                .platform()
                    .at(0,0)
                    .size(W,2)
                    .pattern("P#")
                //spike walls
                .platform()
                    .at(0,4)
                    .size(2,H-4-2)
                    .pattern("p>")
                .copy()
                    .at(W-2,4)
                    .pattern("<p")
                .platform()
                    .at(0,H-1)
                    .size(W,1)
                    .pattern("p")
                .platform()
                    .at(1,H-2)
                    .size(W-14,1)
                    .pattern("v")
                //bottom platforms
                .platform()
                    .at(4,4)
                    .size(4,2)
                .copy().x(W-4-4)
                //middle platform
                .platform()
                    .at(12,8)
                    .size(3,2)
                .copy().at(6,11)
                //middle spike platforms
                .platform()
                    .at(W/2-3,JUMPER_PLATFORM_HEIGHT)
                    .size(5,3)
                    .pattern("#^^^#"+
                             "<ppp>"+
                             "#vvv#")
                .platform()
                    .at(19, JUMPER_PLATFORM_HEIGHT-3)
                    .size(3,2)
                    .pattern("ppp"+
                             "vvv")
                //jumper platform
                .platform()
                    .at(19,JUMPER_PLATFORM_HEIGHT)
                    .size(10,2)
                .player()
                    .at(28,2)
                    .orient(Direction.UP,false)
                .jumper()
                    .at(24,JUMPER_PLATFORM_HEIGHT+2)
                    .orient(Direction.UP)
                .build();

        level.build(root,context);
    }

    public static void movementTutorial(GameObject root, Context context) {
        Level level = new LevelBuilder("Movement Tutorial")
                .json(getJSON(context,"1"))
                .text(context.getString(R.string.tap_to_jump))
                    .at(16,5)
                .text(context.getString(R.string.swipe_to_dash))
                    .at(16,3)
                .build();

        level.build(root,context);
    }

    public static void dashJumpTutorial(GameObject root, Context context) {
        final int WIDTH = 12;
        final int HEIGHT = 5;
        final int SPIKE_HEIGHT = 4;
        Level level = new LevelBuilder("Dash-Jump Tutorial")
                .platform()
                    .at(0,3)
                    .size(WIDTH,HEIGHT)
                .copy()
                    .at(W-WIDTH,3)
                .platform()
                    .at(W/2-1,SPIKE_HEIGHT)
                    .sprite(ResourceSystem.SpriteEnum.platformHugePlate)
                .player()
                    .at(1,3+HEIGHT)
                    .orient(Direction.UP,true)
                .copter()
                    .at(W/2,10)
                //top spikes
                .spikes()
                    .at(W/2,SPIKE_HEIGHT+3)
                    .dir(Direction.UP)
                .copy().x(W/2-1)
                .copy().x(W/2+1)
                //bottom spikes
                .spikes()
                    .at(W/2,SPIKE_HEIGHT-1)
                    .dir(Direction.DOWN)
                .copy().x(W/2-1)
                .copy().x(W/2+1)
                //left spikes
                .spikes()
                    .at(W/2-2,SPIKE_HEIGHT)
                    .dir(Direction.LEFT)
                .copy().y(SPIKE_HEIGHT+1)
                .copy().y(SPIKE_HEIGHT+2)
                //right spikes
                .spikes()
                    .at(W/2+2,SPIKE_HEIGHT)
                    .dir(Direction.RIGHT)
                .copy().y(SPIKE_HEIGHT+1)
                .copy().y(SPIKE_HEIGHT+2)
                .text(context.getString(R.string.dash_and_jump))
                    .at(W/2,H-4)
                .build();

        level.build(root,context);
    }

    public static void createLevel1(GameObject root, Context context) {

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

    public static void createLevel2(GameObject root, Context context) {
        Level level = new LevelBuilder("2")
                .outerWall()
                .thickness(2)
                .holeX(6, 7, 24, 25)
                .holeY(8, 9)
                .horizontalPattern("G#")
                .verticalPattern("P###")
                .platform()
                .at(10, 11)
                .size(8, 3)
                .pattern("+CIOG#II" +
                         "G#+O##O+" +
                         "##CO+IO+")
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

        level.build(root,context);
    }

    public static void fivePlatforms(GameObject root, Context context) {
        final int D = 3;//corner platform dist
        Level level = new LevelBuilder("Five platforms")
                .outerWall()
                .platform()
                .at(D, D)
                .size(6, 3)
                .pattern("CIO+G#" +
                         "G#IO##" +
                         "##+ICC")
                .copy()
                .at(32 - D - 6, 18 - D - 3)
                .platform()
                .at(32 - D - 6, D)
                .size(6, 3)
                .pattern("P#+IO+" +
                         "##CIP#" +
                         "OO+C##")
                .copy()
                .at(D, 18 - D - 3)
                .platform()
                .at(8, 8)
                .size(16, 2)
                .pattern("+OICO")
                .player()
                .at(5, 6)
                .orient(Direction.UP, true)
                .blocker()
                .at(15, 7)
                .orient(Direction.DOWN, false)
                .blocker()
                .at(32 - 5, 6)
                .orient(Direction.UP, true)
                .dynamic(false)
                .copy()
                .at(4, 15)
                .blocker()
                .at(22, 4)
                .orient(Direction.LEFT, false)
                .dynamic(false)
                .blocker()
                .at(9, 18 - 6)
                .orient(Direction.RIGHT, false)
                .dynamic(false)
                .jumper()
                .at(18, 10)
                .orient(Direction.UP)
                .jumper()
                .at(12, 18 - 2)
                .orient(Direction.DOWN)
                //.copter().at(20,2)
                .spikes()
                .at(14, 1)
                .dir(Direction.UP)
                .copy().at(15, 1)
                .build();

        level.build(root,context);
    }

    public static void tightSlalom(GameObject root, Context context) {
        Level level = new LevelBuilder("Tight Slalom")
                .outerWall()
                .thickness(2)
                .horizontalPattern("P#")
                .verticalPattern("P###")
                .platform()
                .at(6, 2)
                .size(32 - 8, 2)
                .pattern("P#")
                .copy().at(6, 4)
                .copy().at(6, 6)
                .copy()
                .at(2, 14)
                .size(32 - 4, 2)
                .copy().at(2, 12)
                .copy().at(2, 10)
                .platform()
                .at(3, 4)
                .size(2, 2)
                .pattern("G")
                //.platform()
                //    .at(2,6)
                //    .size(32-4,1)
                //    .pattern("P")
                //.copy().at(2,11)
                .player()
                .at(3, 3)
                .orient(Direction.DOWN, false)
                .blocker()
                .at(15, 8)
                .orient(Direction.UP, false)
                .dynamic(false)
                .copy().at(25, 8)
                .blocker()
                .at(10, 9)
                .orient(Direction.DOWN, true)
                .dynamic(false)
                .copy().at(20, 9)
                .build();

        level.build(root,context);
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

            //deserialize level from json and load
            try {
                JSONObject json = getJSON(context, name);
                Level level = Level.fromJSON(json);
                level.build(root,context);
                return true;
            } catch (JSONException e) {
                //Log.e(TAG, "Unable to load level json", e);
                return false;
            }
        }

        levelsByName.get(name).create(root, context);
        return true;
    }

    public static JSONObject getJSON(Context context, String levelName) {
        String fileContent = "{}";
        try {
            //loading text from asset: https://stackoverflow.com/a/16110044
            InputStream is = context.getAssets().open(String.format("levels/%s.json", levelName));
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();

            fileContent = sb.toString();
        } catch (IOException e) {
            //Log.e(TAG, String.format("Exception while opening level %s", levelName), e);
        }

        try {
            return (JSONObject) new JSONTokener(fileContent).nextValue();
        } catch (JSONException e) {
            //Log.e(TAG, "Unable to load level json", e);
            return new JSONObject();
        }
    }
}
