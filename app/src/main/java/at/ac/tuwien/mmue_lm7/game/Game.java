package at.ac.tuwien.mmue_lm7.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;

import at.ac.tuwien.mmue_lm7.game.objects.AABB;
import at.ac.tuwien.mmue_lm7.game.objects.AnimatedSprite;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.objects.Sprite;
import at.ac.tuwien.mmue_lm7.game.objects.TestTouchRect;
import at.ac.tuwien.mmue_lm7.game.objects.Text;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.ObjectPool;
import at.ac.tuwien.mmue_lm7.utils.Subject;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * @author simon
 * Represents the main game, accessible globally via singleton pattern
 */
public class Game {
    private static final String TAG = "Game";
    private static final int DEBUG_TOGGLE_KEY = KeyEvent.KEYCODE_0;

    private static Game singleton = null;
    public static Game get() {
        return singleton;
    }

    /**
     * Root object of the scene tree
     */
    private GameObject root = new GameObject();

    private final RenderSystem renderSystem = new RenderSystem();
    private final PhysicsSystem physicsSystem = new PhysicsSystem();
    private final ResourceSystem resourceSystem;
    private final WraparoundSystem wraparoundSystem = new WraparoundSystem();

    /**
     * if true, debugRender is called on all objects
     */
    private boolean renderDebug = false;

    //TODO optimization: have object pools for all types of game objects, free in GameObject::destroy

    ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    ///////////////////////////////////////////////////////////////////////////
    public final Subject<TapEvent> onTap = new Subject<>();
    public final Subject<SwipeEvent> onSwipe = new Subject<>();
    public final Subject<KeyEvent> onKeyDown = new Subject<>();
    public final Subject<KeyEvent> onKeyUp = new Subject<>();

    ///////////////////////////////////////////////////////////////////////////
    // POOLS
    ///////////////////////////////////////////////////////////////////////////
    private final static int VECTOR_POOL_SIZE = 128;
    /**
     * Pool for reusing vector objects, which can be used in temporary calculations, that are valid for a single update/render
     */
    private final ObjectPool<Vec2> vectorPool = new ObjectPool<Vec2>(Vec2::new,VECTOR_POOL_SIZE);
    private final ArrayList<Vec2> usedVectors = new ArrayList<Vec2>(VECTOR_POOL_SIZE);

    public Game(Context context) {
        resourceSystem = new ResourceSystem(context);
    }


    /**
     * Initializes the game world
     * Allocates ressources
     */
    public void init() {
        Log.i(TAG,"Initialize Game");
        //initialize singleton
        singleton = this;

        //initialize root, so that every object added to scene tree gets automatically initialized
        root.init();

        //initialize systems
        physicsSystem.init();
        renderSystem.init();

        resourceSystem.loadResources();

        LevelFactories.loadLevel(root, 1);

        //TestTouchRect testRect = new TestTouchRect();
        //testRect.position.set(1,1);
        //root.addChild(testRect);
    }

    /**
     * Cleans up all resources
     */
    public void cleanup() {
        Log.i(TAG,"Cleanup Game");
        resourceSystem.releaseResources();
    }

    /**
     * Called when activity is paused
     * releases all resources
     */
    public void pause() {
        Log.i(TAG,"Pause Game");
        resourceSystem.releaseResources();
    }

    /**
     * Called when activity is resumed
     * loads all resources which are released in pause
     */
    public void resume() {
        Log.i(TAG,"Resume Game");
        resourceSystem.loadResources();
    }

    /**
     * Updates all game entities
     */
    public void update() {
        //advance physics, calculate collisions, emit collision events
        physicsSystem.update();
        //update game world: players, entities, ...
        root.updateChildren();

        //perform screen wrapping
        wraparoundSystem.update();
        //check loose win condition(s),
        //are there still enemies left
        //does the player have lives left?
        //TODO maybe in own game object??

        freeAllTmpVec();
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
        if(renderDebug) {
            root.debugRenderChildren(renderSystem);
            renderSystem.render(canvas);
        }

        freeAllTmpVec();
    }

    /**
     * @param position in game units
     */
    public void tap(Vec2 position) {
        Log.d(TAG, String.format("Tap at: %s",position.toString()));
        onTap.notify(new TapEvent(position));
    }

    /**
     * @param position where the swipe started in game units
     * @param direction should be normalized
     */
    public void swipe(Vec2 position, Vec2 direction) {
        Log.d(TAG, String.format("Swipe starting at: %s, Direction: %s",position.toString(),direction.toString()));
        onSwipe.notify(new SwipeEvent(position,direction));
    }

    /**
     * Called by GameSurfaceView whenever a key has been pressed
     */
    public void keyDown(KeyEvent event) {
        Log.d(TAG, String.format("Key Down: %s",KeyEvent.keyCodeToString(event.getKeyCode())));
        onKeyDown.notify(event);

        if(event.getKeyCode()==DEBUG_TOGGLE_KEY)
            toggleDebugRender();
    }

    /**
     * Called by GameSurfaceView whenever a key has been pressed
     */
    public void keyUp(KeyEvent event) {
        Log.d(TAG, String.format("Key Up: %s",KeyEvent.keyCodeToString(event.getKeyCode())));
        onKeyUp.notify(event);
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

    public WraparoundSystem getWraparoundSystem() {
        return wraparoundSystem;
    }

    /**
     * @return a temporary vector that may only be used in the same update or render, x=y=0
     */
    public Vec2 tmpVec() {
        Vec2 tmpVec = vectorPool.obtain();
        usedVectors.add(tmpVec);
        return tmpVec;
    }

    private void freeAllTmpVec() {
        vectorPool.freeAll(usedVectors);
        usedVectors.clear();
    }

    public void disableDebugRender() {
        renderDebug = false;
    }

    public void enableDebugRender() {
        renderDebug = true;
    }

    public void toggleDebugRender() {
        renderDebug = !renderDebug;
    }
}
