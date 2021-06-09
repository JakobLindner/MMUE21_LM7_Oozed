package at.ac.tuwien.mmue_lm7.game;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import at.ac.tuwien.mmue_lm7.R;
import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.Blocker;
import at.ac.tuwien.mmue_lm7.game.objects.Button;
import at.ac.tuwien.mmue_lm7.game.objects.Copter;
import at.ac.tuwien.mmue_lm7.game.objects.DeadlyAABB;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Jumper;
import at.ac.tuwien.mmue_lm7.game.objects.Lifetime;
import at.ac.tuwien.mmue_lm7.game.objects.MuteButton;
import at.ac.tuwien.mmue_lm7.game.objects.Platform;
import at.ac.tuwien.mmue_lm7.game.objects.Player;
import at.ac.tuwien.mmue_lm7.game.objects.Rect;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.objects.TouchEventFilter;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SpriteInfo;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * creates all game objects for easy instanciation from the LevelFactories
 * all x and y parameters are the lower left corner of the object
 *
 * @author jakob
 */
public class ObjectFactories {
    public static final float U = GameConstants.UNITS_PER_PIXEL;

    public static Player makeOoze(int x, int y, Direction upDir, boolean runningCW) {
        AABB box = new AABB(1,1,Player.PLAYER_MASK,CollisionLayers.PLAYER);

        Player ooze = new Player(upDir, runningCW, box);//TODO horizontally flip this or ooze sprites
        ooze.position.set(x + 0.5f, y + 0.5f);

        ooze.addChild(box);

        AnimatedSprite runningSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.oozeRun);
        runningSprite.position.set(0, 0);
        ooze.addChild(runningSprite);

        ooze.setLayerRecursive(Layers.PLAYER);
        return ooze;
    }

    public static DeadlyAABB makeSpikes(int x, int y, Direction direction) {
        final float SPIKE_HALF_WIDTH = 0.5f - U;
        final float SPIKE_HALF_HEIGHT = 0.25f - U;
        AABB box = new AABB(direction.isVertical() ? SPIKE_HALF_WIDTH : SPIKE_HALF_HEIGHT,
                direction.isHorizontal() ? SPIKE_HALF_WIDTH : SPIKE_HALF_HEIGHT,
                CollisionLayers.PLAYER,
                CollisionLayers.DEADLY);
        //position bounding box
        box.position.add(direction.dir).scl(SPIKE_HALF_HEIGHT + 2 * U).inv();

        DeadlyAABB spikes = new DeadlyAABB(box);
        spikes.position.set(x + 0.5f, y + 0.5f);
        spikes.rotation = (direction.getRotation() + 90) % 360;

        spikes.addChild(box);
        spikes.addChild(new Sprite(ResourceSystem.SpriteEnum.spikes));

        spikes.setLayerRecursive(Layers.OBSTACLES);
        return spikes;
    }

    public static GameObject makeBlocker(int x, int y, Direction upDir, boolean runningCW, boolean dynamic) {
        AABB box = new AABB(0.5f, 0.5f, CollisionLayers.PLAYER, CollisionLayers.ENEMY);
        AnimatedSprite sprite = new AnimatedSprite(ResourceSystem.SpriteEnum.blockerRun);

        Blocker blocker = new Blocker(box, sprite, upDir, runningCW, dynamic);
        blocker.position.set(x + 0.5f, y + 0.5f);

        blocker.addChild(box);

        //TODO different sprite for static blocker
        sprite.position.set(0, 0);
        blocker.addChild(sprite);

        blocker.setLayerRecursive(Layers.ENEMY);
        return blocker;
    }

    public static GameObject makeJumper(int x, int y, Direction upDir) {
        AABB box = new AABB(0.5f, 0.5f, CollisionLayers.PLAYER, CollisionLayers.ENEMY);

        AnimatedSprite sprite = new AnimatedSprite(ResourceSystem.SpriteEnum.jumperIdle);

        Jumper jumper = new Jumper(box, sprite, upDir);
        jumper.position.set(x + 0.5f, y + 0.5f);

        jumper.addChild(box);

        sprite.position.set(0, 0);
        jumper.addChild(sprite);

        jumper.setLayerRecursive(Layers.ENEMY);
        return jumper;
    }

    public static GameObject makeCopter(int x, int y, Direction upDir, boolean noHover) {

        AABB copterBox = new AABB(0.5f, 0.5f, CollisionLayers.PLAYER, CollisionLayers.ENEMY);
        AABB bodyBox = new AABB(0.5f, 0.5f, CollisionLayers.PLAYER, CollisionLayers.ENEMY);


        Copter copter = new Copter(copterBox, bodyBox, upDir, noHover);
        copter.position.set(x + 0.5f, y + 0.5f);

        copter.addChild(copterBox);

        copter.addChild(bodyBox);

        AnimatedSprite idleSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.flyerIdle);
        idleSprite.position.set(0, 0);
        copter.addChild(idleSprite);

        copter.setLayerRecursive(Layers.ENEMY);
        return copter;
    }

    public static GameObject makePlatform(int x, int y) {
        Platform platform = new Platform();
        platform.position.set(x, y);

        platform.setLayerRecursive(Layers.TILES);
        return platform;
    }

    /**
     * @return platform with width and height = 1
     */
    public static GameObject makePlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y, 1, 1, 0, false, sprite);
    }

    public static GameObject makePlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y, 1, 1, rot, mir, sprite);
    }

    /**
     * @return platform with width = height = 2
     */
    public static GameObject makeBigPlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y, 2, 2, 0, false, sprite);
    }

    public static GameObject makeBigPlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y, 2, 2, rot, mir, sprite);
    }

    public static GameObject makePlatformTile(int x, int y, int width, int height, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        GameObject tile = new GameObject();
        tile.position.set(x + halfWidth, y + halfHeight);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        AABB aabb = new AABB(halfWidth, halfHeight, (short) 0, CollisionLayers.PLATFORM);
        tile.addChild(aabb);

        tile.setLayerRecursive(Layers.TILES);
        return tile;
    }

    public static GameObject makeBackground() {
        Sprite sprite = new Sprite(ResourceSystem.SpriteEnum.background);
        sprite.position.set(16, 9);
        return sprite;
    }

    /**
     * @param x center of text! not left bound
     */
    public static GameObject makeText(int x, int y, String text) {
        Text t = new Text(text);
        t.position.set(x, y);

        t.setLayerRecursive(Layers.UI);
        return t;
    }

    public static GameObject makeTextButton(float x, float y, String text, Button.Action action) {
        final float HALF_WIDTH = 3;
        final float HALF_HEIGHT = 1;
        final float TEXT_SIZE = 18;
        final float TEXT_OFFSET = -0.25f;

        GameObject container = new GameObject();
        container.position.set(x, y);

        Button button = new Button(new Vec2(HALF_WIDTH, HALF_HEIGHT), action);
        container.addChild(button);

        Text t = new Text(text, Color.BLACK, TEXT_SIZE);
        t.position.set(0, TEXT_OFFSET);
        container.addChild(t);

        Rect rect = new Rect(new Vec2(HALF_WIDTH, HALF_HEIGHT), Color.WHITE, Paint.Style.FILL);
        container.addChild(rect);

        container.setLayerRecursive(Layers.UI);
        return container;
    }

    public static GameObject makeImageButton(float x, float y, float halfWidth, float halfHeight, ResourceSystem.SpriteEnum sprite, Button.Action action) {
        GameObject container = new GameObject();
        container.position.set(x, y);

        Button button = new Button(new Vec2(halfWidth, halfHeight), action);
        container.addChild(button);

        Sprite s = new Sprite(sprite);
        container.addChild(s);

        container.setLayerRecursive(Layers.UI);
        return container;
    }

    public static GameObject makeMuteButton(float x, float y, float halfWidth, float halfHeight) {
        SpriteInfo muted = ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.muted);
        SpriteInfo notMuted = ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.notMuted);

        Sprite sprite = new Sprite(ResourceSystem.SpriteEnum.muted);

        MuteButton button = new MuteButton(new Vec2(halfWidth, halfHeight), sprite, muted, notMuted);
        button.position.set(x, y);

        button.addChild(sprite);

        button.setLayerRecursive(Layers.UI);
        return button;
    }

    public static GameObject makePauseScreen(String titleString) {
        /**
         * While paused, a black rectangle with this alpha value is rendered on top of the scene
         * This is to increase contrast between the game scene and the pause screen
         */
        final int PAUSE_SCREEN_OVERLAY_ALPHA = 180;
        final float TITLE_SIZE = 36;

        GameObject pauseScreen = new GameObject();
        pauseScreen.position.set(GameConstants.HALF_GAME_WIDTH, GameConstants.HALF_GAME_HEIGHT);

        //render text
        Text title = new Text(titleString, Color.WHITE, TITLE_SIZE);
        pauseScreen.addChild(title);

        //mute button
        GameObject muteButton = makeMuteButton(0, -2, 1, 1);
        pauseScreen.addChild(muteButton);

        //resume button
        GameObject resumeButton = makeImageButton(4, -2, 1, 1,
                ResourceSystem.SpriteEnum.resume,
                button -> {
                    Game.get().getResourceSystem().playSound(ResourceSystem.Sound.BUTTON);
                    Game.get().resumeGame();
                });
        pauseScreen.addChild(resumeButton);

        GameObject mainMenuButton = makeImageButton(-4, -2, 1, 1,
                ResourceSystem.SpriteEnum.house,
                button -> {
                    Game.get().getResourceSystem().playSound(ResourceSystem.Sound.BUTTON);
                    Game.get().quitGame();
                });
        pauseScreen.addChild(mainMenuButton);

        //main menu button
        //GameObject mainMenuButton = makeTextButton(0,-3.5f,"Main Menu", button -> {
        //TODO do we even need this?
        //});
        //pauseScreen.addChild(mainMenuButton);
        //renderSystem.drawText()
        //        .text("Tap top right corner to resume")
        //       .at(tmpVec().set(GameConstants.HALF_GAME_WIDTH, GameConstants.HALF_GAME_HEIGHT - 1.5f))//TODO remove offset magic number
        //      .typeFace(Typeface.DEFAULT)
        //     .align(Paint.Align.CENTER)
        //     .color(Color.WHITE)
        //     .size(16);

        //draw overlay rect to darken game rendering
        Rect background = new Rect(new Vec2(GameConstants.HALF_GAME_WIDTH, GameConstants.HALF_GAME_HEIGHT),
                Color.argb(PAUSE_SCREEN_OVERLAY_ALPHA, 0, 0, 0),
                Paint.Style.FILL);
        pauseScreen.addChild(background);

        //filter touch inputs
        TouchEventFilter filter = new TouchEventFilter();
        filter.layer = Layers.UI - 1;
        pauseScreen.addChild(filter);

        return pauseScreen;
    }

    public static GameObject makeIngameUI() {
        GameObject ui = new GameObject();

        //pausebutton in top right corner
        GameObject pauseButton = makeImageButton(GameConstants.GAME_WIDTH - 1, GameConstants.GAME_HEIGHT - 1, 1, 1,
                ResourceSystem.SpriteEnum.pause,
                button -> {
                    Game.get().getResourceSystem().playSound(ResourceSystem.Sound.BUTTON);
                    Game.get().pauseGame();
                });
        ui.addChild(pauseButton);

        ui.setLayerRecursive(Layers.UI);
        return ui;
    }

    public static GameObject makeLifeScreen(String level, int lives) {
        final float TITLE_SIZE = 36;
        final float TEXT_SIZE = 18;

        GameObject lifeScreen = new GameObject();
        lifeScreen.position.set(GameConstants.HALF_GAME_WIDTH, GameConstants.HALF_GAME_HEIGHT);

        //render level text
        Text title = new Text(Game.get().getContext().getString(R.string.level, level),
                Color.WHITE,
                TITLE_SIZE);
        title.position.set(0, 1);
        lifeScreen.addChild(title);

        //show lives
        Sprite heart = new Sprite(ResourceSystem.SpriteEnum.heart);
        heart.position.set(-1, 0);
        lifeScreen.addChild(heart);

        Text livesText = new Text(Game.get().getContext().getString(R.string.lives, lives),
                Color.WHITE,
                TEXT_SIZE);
        livesText.position.set(0.85f, -0.4f);
        lifeScreen.addChild(livesText);

        Text instruction = new Text(Game.get().getContext().getString(R.string.tap_to_start),
                        Color.WHITE,
                        TEXT_SIZE);
        instruction.position.set(0, -2);
        lifeScreen.addChild(instruction);

        //draw overlay rect to darken game rendering
        Rect background = new Rect(new Vec2(GameConstants.HALF_GAME_WIDTH, GameConstants.HALF_GAME_HEIGHT),
                Color.BLACK,
                Paint.Style.FILL);
        lifeScreen.addChild(background);

        //full screen button to resume game
        Button button = new Button(new Vec2(GameConstants.HALF_GAME_WIDTH,GameConstants.HALF_GAME_HEIGHT),button1 -> {
            Game.get().resumeGame();
        });
        button.layer = Layers.UI;
        lifeScreen.addChild(button);

        return lifeScreen;
    }

    public static GameObject makeKilledEffect(float x, float y) {
        final int EFFECT_DURATION = 30;//in frames
        Lifetime object = new Lifetime(EFFECT_DURATION);
        object.position.set(x, y);

        object.addChild(new Sprite(ResourceSystem.SpriteEnum.disappearEffect));

        object.setLayerRecursive(Layers.EFFECTS);
        return object;
    }
}
