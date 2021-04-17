package at.ac.tuwien.mmue_lm7.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Represents the main game
 */
public class Game {

    private static Game singleton = null;
    public static Game get() {
        return singleton;
    }

    /**
     * Root object of the scene tree
     */
    private GameObject root = new GameObject();

    private RenderSystem renderSystem = new RenderSystem();
    private PhysicsSystem physicsSystem = new PhysicsSystem();
    private ResourceSystem resourceSystem;

    //TODO optimization: have object pools for all types of game objects, free in GameObject::destroy

    public Game(Context context) {
        resourceSystem = new ResourceSystem(context);
    }

    /**
     * Initializes the game world
     * Allocates ressources
     */
    public void init() {
        Log.i("Game","Initialize Game");
        //initialize singleton
        singleton = this;

        //initialize root, so that every object added to scene tree gets automatically initialized
        root.init();

        //initialize systems
        physicsSystem.init();
        renderSystem.init();

        //TODO load assets

        //TODO plz remove following
        AABB aabb = new AABB(1,1);
        aabb.position.set(15,8);
        root.addChild(aabb);
        Text text = new Text("There is a green rectangle.");
        text.position.set(15,4);
        root.addChild(text);
        Sprite sprite = new Sprite();
        sprite.position.set(3,6);
        sprite.rotation = 90;
        sprite.setSpriteInfo(resourceSystem.spriteInfo(ResourceSystem.SpriteEnum.tileCenter));
        root.addChild(sprite);
        AnimatedSprite animatedSprite = new AnimatedSprite();
        animatedSprite.position.set(5,6);
        animatedSprite.mirrored = true;
        animatedSprite.setSpriteInfo(resourceSystem.spriteInfo(ResourceSystem.SpriteEnum.oozeRun));
        root.addChild(animatedSprite);
    }

    /**
     * Cleans up all resources
     */
    public void cleanup() {

    }

    /**
     * Updates all game entities
     */
    public void update() {
        //advance physics, calculate collisions, emit collision events
        physicsSystem.update();
        //update game world: players, entities, ...
        root.updateChildren();
        //check loose win condition(s),
        //are there still enemies left
        //does the player have lives left?
        //TODO maybe in own game object??
    }

    /**
     * renders all game objects
     * @param canvas, not null
     */
    public void render(Canvas canvas) {
        //render game objects
        root.renderChildren(renderSystem);

        //perform batched render commands
        renderSystem.render(canvas);

        //DEBUG RENDER
        //TODO only if enabled
        root.debugRenderChildren(renderSystem);
        renderSystem.render(canvas);
    }

    public void tap(Vec2 position) {
        //TODO emit event for all listening game objects
    }

    public void swipe(Vec2 position, Vec2 direction) {
        //TODO emit event for all listening game objects
    }

    /**
     * @return root of scene tree
     */
    public GameObject getRoot() {
        return root;
    }

    public RenderSystem getRenderSystem() {
        return renderSystem;
    }

    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    public ResourceSystem getResourceSystem() {
        return resourceSystem;
    }
}
