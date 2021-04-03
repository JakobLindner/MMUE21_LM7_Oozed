package at.ac.tuwien.mmue_lm7.game;

import android.graphics.Canvas;

import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
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

    //TODO optimization: have object pools for all types of game objects, free in GameObject::destroy

    /**
     * Initializes the game world
     * Allocates ressources
     */
    public void init() {
        //initialize singleton
        singleton = this;

        //initialize root, so that every object added to scene tree gets automatically initialized
        root.init();

        //TODO load assets
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
}
