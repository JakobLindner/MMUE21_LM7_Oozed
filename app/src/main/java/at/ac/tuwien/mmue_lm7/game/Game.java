package at.ac.tuwien.mmue_lm7.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import at.ac.tuwien.mmue_lm7.R;
import at.ac.tuwien.mmue_lm7.game.level.Level;
import at.ac.tuwien.mmue_lm7.game.objects.GameObject;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SoundSystem;
import at.ac.tuwien.mmue_lm7.utils.ObjectPool;
import at.ac.tuwien.mmue_lm7.utils.Subject;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Represents the main game, accessible globally via singleton pattern
 *
 * @author simon
 */
public class Game {
    private static final String TAG = "Game";
    private static final int DEBUG_TOGGLE_KEY = KeyEvent.KEYCODE_0;
    private static final int PAUSE_TOGGLE_KEY = KeyEvent.KEYCODE_P;
    private static final int WIN_TRIGGER_KEY = KeyEvent.KEYCODE_O;
    //delays level load after clearing a level
    private static final int CLEAR_DELAY = 60;

    private static Game singleton = null;

    public static Game get() {
        return singleton;
    }

    private int playerLives = GameConstants.PLAYER_LIVES;
    private String currentLevel = "1";
    private int lastMainLevel = 1;
    /**
     * The time in frames the game has been running
     * this is stored in addition to the highscore, since when you clear all levels you can still try
     * clearing the game faster
     */
    private int time = 0;

    /**
     * Root object of the scene tree
     */
    private GameObject root = new GameObject();
    private GameObject pauseRoot = new GameObject();
    /**
     * Destroyed entities are added to this list and are removed from the scene tree at the end of the update
     */
    private ArrayList<GameObject> markedForRemoval = new ArrayList<GameObject>(16);

    private final Context context;
    private final RenderSystem renderSystem = new RenderSystem();
    private final PhysicsSystem physicsSystem = new PhysicsSystem();
    private final ResourceSystem resourceSystem;
    private final WraparoundSystem wraparoundSystem = new WraparoundSystem();
    private final TimingSystem timingSystem = new TimingSystem();
    //always active, also during pause
    private final TimingSystem pauselessTimingSystem = new TimingSystem();
    private final LevelStatusSystem levelStatusSystem = new LevelStatusSystem();
    private final LevelLoader levelLoader;

    /**
     * if true, debugRender is called on all objects
     */
    private boolean renderDebug = false;
    /**
     * true if gameplay is paused, no gameobjects are updated and a pause screen is shown
     */
    private boolean paused = false;

    //TODO optimization: have object pools for all types of game objects, free in GameObject::destroy

    ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    ///////////////////////////////////////////////////////////////////////////
    public final Subject<TapEvent> onTap = new Subject<>();
    public final Subject<SwipeEvent> onSwipe = new Subject<>();
    public final Subject<KeyEvent> onKeyDown = new Subject<>();
    public final Subject<KeyEvent> onKeyUp = new Subject<>();
    public final Subject<LevelEvent> onLevelLoaded = new Subject<>();
    public final Subject<LevelEvent> onLevelCleared = new Subject<>();
    public final Subject<Score> onGameOver = new Subject<>();
    public final Subject<QuitEvent> onQuit = new Subject<QuitEvent>();

    private BlockingQueue<TapEvent> tapQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<SwipeEvent> swipeQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<KeyEvent> keyDownQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<KeyEvent> keyUpQueue = new LinkedBlockingQueue<>();

    ///////////////////////////////////////////////////////////////////////////
    // POOLS
    ///////////////////////////////////////////////////////////////////////////
    private final static int VECTOR_POOL_SIZE = 128;
    /**
     * Pool for reusing vector objects, which can be used in temporary calculations, that are valid for a single update/render
     */
    private final ObjectPool<Vec2> vectorPool = new ObjectPool<Vec2>(Vec2::new, VECTOR_POOL_SIZE);
    private final ArrayList<Vec2> usedVectors = new ArrayList<Vec2>(VECTOR_POOL_SIZE);

    public Game(Context context, String startLevel) {
        this.context = context;

        resourceSystem = new ResourceSystem(context);
        levelLoader = new LevelLoader(context);

        currentLevel = startLevel;
        try {
            lastMainLevel = Integer.parseInt(startLevel);
        } catch(NumberFormatException e) {
            lastMainLevel = 0;
        }
    }


    /**
     * Initializes the game world
     * Allocates ressources
     */
    public void init() {
        Log.i(TAG, "Initialize Game");
        //initialize singleton
        singleton = this;

        //initialize systems
        physicsSystem.init();
        renderSystem.init();

        resourceSystem.loadResources();

        loadLevel(currentLevel);
    }

    /**
     * Cleans up all resources
     */
    public void cleanup() {
        Log.i(TAG, "Cleanup Game");
        resourceSystem.releaseResources();
        SoundSystem.get().stopMusic();
    }

    /**
     * Called when activity is paused
     * releases all resources
     * pauses game
     */
    public void pause() {
        Log.i(TAG, "Pause Game");
        resourceSystem.releaseResources();

        SoundSystem.get().pauseMusic();
    }

    /**
     * Called when activity is resumed
     * loads all resources which are released in pause
     */
    public void resume() {
        Log.i(TAG, "Resume Game");
        resourceSystem.loadResources();

        SoundSystem.get().playMusic(R.raw.retro_platformer_5);
    }

    /**
     * Updates all game entities
     */
    public void update() {
        //process inputs
        processInputs();

        if (!paused) {
            //advance physics, calculate collisions, emit collision events
            physicsSystem.update();

            //update timers
            timingSystem.update();

            //update game world: players, entities, ...
            root.updateChildren();

            //perform screen wrapping
            wraparoundSystem.update();

            //remove all game objects, which have been destroyed this frame
            for (GameObject gameObject : markedForRemoval)
                gameObject.detachFromParent();
            markedForRemoval.clear();

            //advance time
            ++time;
        }

        pauselessTimingSystem.update();

        freeAllTmpVec();
    }

    /**
     * renders all game objects
     *
     * @param canvas, not null
     */
    public void render(Canvas canvas) {
        //render game objects
        root.renderChildren(renderSystem);

        //perform batched render commands
        renderSystem.render(canvas);

        //DEBUG RENDER
        if (renderDebug) {
            root.debugRenderChildren(renderSystem);
            renderSystem.render(canvas);
        }

        if (paused) {
            pauseRoot.renderChildren(renderSystem);

            //render batched commands for pause screen
            renderSystem.render(canvas);
        }

        freeAllTmpVec();
    }

    /**
     * Processes all inputs that have been queued up
     */
    private void processInputs() {
        //taps
        for (TapEvent tap = tapQueue.poll(); tap != null; tap = tapQueue.poll())
            handleTap(tap);

        //swipes
        for (SwipeEvent swipe = swipeQueue.poll(); swipe != null; swipe = swipeQueue.poll())
            handleSwipe(swipe);

        //keyDown
        for (KeyEvent keyDown = keyDownQueue.poll(); keyDown != null; keyDown = keyDownQueue.poll())
            handleKeyDown(keyDown);

        //keyup
        for (KeyEvent keyUp = keyUpQueue.poll(); keyUp != null; keyUp = keyUpQueue.poll())
            handleKeyUp(keyUp);
    }

    /**
     * @param position in game units
     */
    public void tap(Vec2 position) {
        Log.d(TAG, String.format("Tap at: %s", position.toString()));

        //enqueue event for later processing
        tapQueue.offer(new TapEvent(position));
    }

    private void handleTap(TapEvent event) {
        onTap.notify(event);
    }

    /**
     * @param position  where the swipe started in game units
     * @param direction should be normalized
     */
    public void swipe(Vec2 position, Vec2 direction) {
        Log.d(TAG, String.format("Swipe starting at: %s, Direction: %s", position.toString(), direction.toString()));

        //enqueue swipe event
        swipeQueue.offer(new SwipeEvent(position, direction));
    }

    private void handleSwipe(SwipeEvent event) {
        onSwipe.notify(event);
    }

    /**
     * Called by GameSurfaceView whenever a key has been pressed
     */
    public void keyDown(KeyEvent event) {
        Log.d(TAG, String.format("Key Down: %s", KeyEvent.keyCodeToString(event.getKeyCode())));

        //enqueue event for later processing
        keyDownQueue.offer(event);
    }

    private void handleKeyDown(KeyEvent event) {
        onKeyDown.notify(event);

        if (event.getKeyCode() == DEBUG_TOGGLE_KEY)
            toggleDebugRender();
        else if (event.getKeyCode() == PAUSE_TOGGLE_KEY)
            togglePause();
        else if(event.getKeyCode() == WIN_TRIGGER_KEY){
            onGameOver.notify(new Score(lastMainLevel-1,time,true));
        }
    }

    /**
     * Called by GameSurfaceView whenever a key has been pressed
     */
    public void keyUp(KeyEvent event) {
        Log.d(TAG, String.format("Key Up: %s", KeyEvent.keyCodeToString(event.getKeyCode())));

        //enqueue event for later processing
        keyDownQueue.offer(event);
    }

    private void handleKeyUp(KeyEvent event) {
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

    public TimingSystem getTimingSystem() {
        return timingSystem;
    }

    public LevelStatusSystem getLevelStatusSystem() {
        return levelStatusSystem;
    }

    public int getPlayerLives() {
        return playerLives;
    }

    public Context getContext() {
        return context;
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

    /**
     * Pauses gameplay and shows pause screen
     * Does nothing if game is already paused
     */
    public void pauseGame() {
        if (!paused) {
            pauseRoot.init();
            pauseRoot.addChild(ObjectFactories.makePauseScreen(context.getResources().getString(R.string.pause_screen_title)));
            //TODO play sound, ...
        }
        paused = true;
    }

    /**
     * Resumes gameplay and removes pause screen
     * Does nothing if game is not paused
     */
    public void resumeGame() {
        if (paused) {
            pauseRoot.destroy();
            pauseRoot = new GameObject();
            //TODO play sound, ...
        }
        paused = false;
    }

    /**
     * emits event, that game activity should be cleared
     */
    public void quitGame() {
        onQuit.notify(new QuitEvent());
    }

    /**
     * Toggles the pause state
     */
    public void togglePause() {
        if (paused)
            resumeGame();
        else
            pauseGame();
    }

    /**
     * Respawns player if there are lives left
     * if lives==0, then the game is lost
     */
    public void respawnPlayer() {
        Log.d(TAG, "Respawn player");

        //decrease lives
        --playerLives;

        if (playerLives == 0) {
            Log.i(TAG, "No lives left, show lost screen");
            onGameOver.notify(new Score(lastMainLevel-1, time, false));
        } else {
            //restart level
            loadLevel();
        }
    }

    /**
     * Marks given gameobject for removal at the end of the frame, called by GameObject::destroy
     *
     * @param gameObject !=null
     */
    public void markForRemoval(GameObject gameObject) {
        markedForRemoval.add(gameObject);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Level/scene management
    ///////////////////////////////////////////////////////////////////////////

    private void advanceLevel() {
        ++lastMainLevel;
        currentLevel = Integer.toString(lastMainLevel);
    }

    /**
     * loads currentLevel
     */
    private void loadLevel() {
        loadLevel(currentLevel);
    }

    private void loadLevel(String level) {
        //clear scene tree
        if(root!=null)
            root.destroy();
        //TODO delete marked entities
        //come and eat GC!!
        root = new GameObject();
        root.init();

        levelStatusSystem.clearLevelStatus();

        if (!levelLoader.loadLevel(root, level)) {
            Log.i(TAG, "All levels completed, show win screen");
            onGameOver.notify(new Score(lastMainLevel-1, time, true));
        } else {
            //add ingame ui
            root.addChild(ObjectFactories.makeIngameUI());

            Log.i(TAG, String.format("Loaded level '%s'", level));
            onLevelLoaded.notify(new LevelEvent(level));

            //pause game and show lives screen
            paused = true;
            pauseRoot.init();
            pauseRoot.addChild(ObjectFactories.makeLifeScreen(level,playerLives));
        }
    }

    /**
     * called when all objectives have been fulfilled
     */
    public void clearLevel() {
        Log.i(TAG, "Level cleared!");
        onLevelCleared.notify(new LevelEvent(currentLevel));

        //play sound
        resourceSystem.playSound(ResourceSystem.Sound.LEVEL_CLEAR);

        advanceLevel();
        paused = true;
        pauselessTimingSystem.addDelayedAction(() -> {
            resumeGame();
            loadLevel();
        },CLEAR_DELAY);

    }
}
