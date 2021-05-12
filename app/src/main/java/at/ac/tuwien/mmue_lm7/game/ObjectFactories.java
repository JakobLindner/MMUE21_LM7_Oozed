package at.ac.tuwien.mmue_lm7.game;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.Blocker;
import at.ac.tuwien.mmue_lm7.game.objects.DeadlyAABB;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Lifetime;
import at.ac.tuwien.mmue_lm7.game.objects.Platform;
import at.ac.tuwien.mmue_lm7.game.objects.Player;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;

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

    public static GameObject makePlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makePlatformTile(x, y, 0, false, sprite);
    }

    public static GameObject makePlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        GameObject tile = new GameObject();
        tile.position.set(x + 0.5f, y + 0.5f);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        AABB aabb = new AABB(0.5f, 0.5f,(short) 0, CollisionLayers.PLATFORM);
        tile.addChild(aabb);

        tile.setLayerRecursive(Layers.TILES);
        return tile;
    }

    public static GameObject makeBigPlatformTile(int x, int y, ResourceSystem.SpriteEnum sprite) {
        return makeBigPlatformTile(x, y, 0, false, sprite);
    }

    public static GameObject makeBigPlatformTile(int x, int y, float rot, boolean mir, ResourceSystem.SpriteEnum sprite) {
        GameObject tile = new GameObject();
        tile.position.set(x + 1.0f, y + 1.0f);
        tile.rotation = rot;
        tile.mirrored = mir;

        tile.addChild(new Sprite(sprite));

        AABB aabb = new AABB(1.0f, 1.0f,(short) 0, CollisionLayers.PLATFORM);
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

    public static GameObject makeKilledEffect(float x, float y) {
        final int EFFECT_DURATION = 30;//in frames
        Lifetime object = new Lifetime(EFFECT_DURATION);
        object.position.set(x,y);

        object.addChild(new Sprite(ResourceSystem.SpriteEnum.disappearEffect));

        object.setLayerRecursive(Layers.EFFECTS);
        return object;
    }
}
