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
import at.ac.tuwien.mmue_lm7.game.objects.DeadlyAABB;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Lifetime;
import at.ac.tuwien.mmue_lm7.game.objects.Platform;
import at.ac.tuwien.mmue_lm7.game.objects.Player;
import at.ac.tuwien.mmue_lm7.game.objects.Rect;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * creates all game objects for easy instanciation from the LevelFactories
 * all x and y parameters are the lower left corner of the object
 * @author jakob
 */
public class ObjectFactories {

    public static Player makeOoze(int x, int y, Direction direction, boolean runningCW) {
        Player ooze = new Player(direction, runningCW);//TODO horizontally flip this or ooze sprites
        ooze.position.set(x + 0.5f, y + 0.5f);

        AnimatedSprite runningSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.oozeRun);
        runningSprite.position.set(0, 0);
        ooze.addChild(runningSprite);

        ooze.setLayerRecursive(Layers.PLAYER);
        return ooze;
    }

    public static DeadlyAABB makeSpikes(int x, int y, Direction direction) {
        final float SPIKE_HALF_WIDTH = 0.5f-GameConstants.UNITS_PER_PIXEL;
        final float SPIKE_HALF_HEIGHT = 0.25f-GameConstants.UNITS_PER_PIXEL;
        AABB box = new AABB(direction.isVertical()?SPIKE_HALF_WIDTH:SPIKE_HALF_HEIGHT,
                                            direction.isHorizontal()?SPIKE_HALF_WIDTH:SPIKE_HALF_HEIGHT,
                                            CollisionLayers.PLAYER,
                                            CollisionLayers.DEADLY);
        //position bounding box
        box.position.add(direction.dir).scl(SPIKE_HALF_HEIGHT+2*GameConstants.UNITS_PER_PIXEL).inv();

        DeadlyAABB spikes = new DeadlyAABB(box);
        spikes.position.set(x + 0.5f,y + 0.5f);
        spikes.rotation = (direction.getRotation()+90)%360;

        spikes.addChild(box);
        spikes.addChild(new Sprite(ResourceSystem.SpriteEnum.spikes));

        spikes.setLayerRecursive(Layers.OBSTACLES);
        return spikes;
    }

    public static GameObject makeBlocker(int x, int y, Direction lookDir, boolean runningCW) {
        AABB box = new AABB(0.5f,0.5f,CollisionLayers.PLAYER, CollisionLayers.ENEMY);

        Blocker blocker = new Blocker(box, lookDir, runningCW);
        blocker.position.set(x + 0.5f, y + 0.5f);

        blocker.addChild(box);

        AnimatedSprite idleSprite = new AnimatedSprite(ResourceSystem.SpriteEnum.blockerIdle);
        idleSprite.position.set(0, 0);
        blocker.addChild(idleSprite);

        blocker.setLayerRecursive(Layers.ENEMY);
        return blocker;
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
        return makePlatformTile(x, y, 1,1,0, false, sprite);
    }

    public static GameObject makePlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x,y,1,1,rot,mir,sprite);
    }

    /**
     * @return platform with width = height = 2
     */
    public static GameObject makeBigPlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y,2,2, 0, false, sprite);
    }

    public static GameObject makeBigPlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x,y,2,2,rot,mir,sprite);
    }

    public static GameObject makePlatformTile(int x, int y, int width, int height, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        float halfWidth = width*0.5f;
        float halfHeight = height*0.5f;

        GameObject tile = new GameObject();
        tile.position.set(x + halfWidth, y + halfHeight);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        AABB aabb = new AABB(halfWidth, halfHeight,(short) 0, CollisionLayers.PLATFORM);
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
        final float HALF_WIDTH = 4;
        final float HALF_HEIGHT = 2;
        final float TEXT_SIZE = 16;

        GameObject container = new GameObject();
        container.position.set(x,y);

        Button button = new Button(new Vec2(HALF_WIDTH, HALF_HEIGHT),action);
        container.addChild(button);

        Rect rect = new Rect(new Vec2(HALF_WIDTH, HALF_HEIGHT), Color.WHITE, Paint.Style.FILL);
        container.addChild(rect);

        Text t = new Text(text,Color.BLACK,TEXT_SIZE);
        container.addChild(t);

        t.setLayerRecursive(Layers.UI);
        return container;
    }

    public static GameObject makePauseScreen(String titleString) {
        /**
         * While paused, a black rectangle with this alpha value is rendered on top of the scene
         * This is to increase contrast between the game scene and the pause screen
         */
        final int PAUSE_SCREEN_OVERLAY_ALPHA = 180;
        final float TITLE_SIZE = 32;

        GameObject pauseScreen = new GameObject();
        pauseScreen.position.set(GameConstants.HALF_GAME_WIDTH,GameConstants.HALF_GAME_HEIGHT);

        //render text
        Text title = new Text(titleString,Color.WHITE,TITLE_SIZE);
        pauseScreen.addChild(title);

        //renderSystem.drawText()
        //        .text("Tap top right corner to resume")
         //       .at(tmpVec().set(GameConstants.HALF_GAME_WIDTH, GameConstants.HALF_GAME_HEIGHT - 1.5f))//TODO remove offset magic number
          //      .typeFace(Typeface.DEFAULT)
           //     .align(Paint.Align.CENTER)
           //     .color(Color.WHITE)
           //     .size(16);

        //draw overlay rect to darken game rendering
        Rect background = new Rect(new Vec2(GameConstants.HALF_GAME_WIDTH,GameConstants.HALF_GAME_HEIGHT),
                Color.argb(PAUSE_SCREEN_OVERLAY_ALPHA, 0, 0, 0),
                Paint.Style.FILL);
        pauseScreen.addChild(background);

        return pauseScreen;
    }

    public static GameObject makeKilledEffect(float x, float y) {
        final int EFFECT_DURATION = 30;//in frames
        Lifetime object = new Lifetime(EFFECT_DURATION);
        object.position.set(x,y);

        object.addChild(new Sprite(ResourceSystem.SpriteEnum.disappearEffect));

        object.setLayerRecursive(Layers.EFFECTS);
        return object;
    }
}
