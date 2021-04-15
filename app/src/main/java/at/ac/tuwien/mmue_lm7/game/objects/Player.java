package at.ac.tuwien.mmue_lm7.game.objects;

import android.util.Log;

import at.ac.tuwien.mmue_lm7.Constants;
import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.SwipeEvent;
import at.ac.tuwien.mmue_lm7.game.TapEvent;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Player object
 */
public class Player extends AABB{
    private static final String TAG = "Player";

    ///////////////////////////////////////////////////////////////////////////
    // Player constants
    ///////////////////////////////////////////////////////////////////////////
    /**
     * per frame
     */
    public static final float PLAYER_SPEED = 0.05f;
    public static final float PLAYER_DASH_SPEED = 0.2f;
    /**
     * in frames
     */
    public static final float PLAYER_DASH_DURATION = 60;
    public static final float PLAYER_HALF_SIZE = 0.5f;
    public static final short PLAYER_MASK = CollisionLayers.ENEMY;
    public static final short PLAYER_MOVEMENT_MASK = CollisionLayers.PLATFORM;

    public enum PlayerState {
        RUNNING,
        DASHING,
        JUMPING
    }
    ///////////////////////////////////////////////////////////////////////////
    // STATE variables
    ///////////////////////////////////////////////////////////////////////////
    private PlayerState state = PlayerState.RUNNING;
    /**
     * set to true if screen is tapped
     */
    private boolean wantJump = false;
    /**
     * set to true if swipe detected
     */
    private boolean wantDash = false;
    //RUNNING
    private enum Direction {
        UP(new Vec2(0,1)),
        DOWN(new Vec2(0,-1)),
        LEFT(new Vec2(-1,0)),
        RIGHT(new Vec2(1,0));

        public final Vec2 dir;

        public Direction rotateCCW() {
            switch(this) {
                case UP:
                    return LEFT;
                case DOWN:
                    return RIGHT;
                case LEFT:
                    return DOWN;
                case RIGHT:
                    return UP;
                default:
                    Log.e(TAG,"Unknown Direction in rotateCCW");
                    return UP;
            }
        }

        public Direction rotateCW() {
            switch(this) {
                case UP:
                    return RIGHT;
                case DOWN:
                    return LEFT;
                case LEFT:
                    return UP;
                case RIGHT:
                    return DOWN;
                default:
                    Log.e(TAG,"Unknown Direction in rotateCW");
                    return UP;
            }
        }

        private Direction(Vec2 dir) {
            this.dir = dir;
        }
    }

    /**
     * Direction of movement
     */
    private Direction dir;
    /**
     * local up direction
     */
    private Direction upDir;

    /**
     * Vector reused for movement operations
     */
    private Vec2 move = new Vec2();

    //DASHING
    private float duration;

    public Player() {
        super(PLAYER_HALF_SIZE,
                PLAYER_HALF_SIZE,
                CollisionLayers.PLAYER,
                PLAYER_MASK);
    }

    @Override
    public void init() {
        super.init();

        //register to events
        onCollide.addListener(this::onCollide);
        Game.get().onTap.addListener(this::onTap);
        Game.get().onSwipe.addListener(this::onSwipe);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //deregister from events
        onCollide.removeListener(this::onCollide);
        Game.get().onTap.removeListener(this::onTap);
        Game.get().onSwipe.removeListener(this::onSwipe);
    }

    @Override
    public void update() {

        //handle inputs
        if(wantJump && (state==PlayerState.DASHING || state==PlayerState.RUNNING)) {
            state = PlayerState.JUMPING;
            //TODO calculate jump parabola
        }
        if(wantDash && state==PlayerState.RUNNING) {
            state = PlayerState.JUMPING;
        }

        //handle current state
        switch (state) {
            case RUNNING:
            case DASHING:
            {
                //perform movement
                PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(this,
                        move.set(dir.dir).scl(state==PlayerState.DASHING?PLAYER_DASH_SPEED:PLAYER_SPEED),
                        PLAYER_MOVEMENT_MASK);

                //check if there is a collision
                if(movement.getContact()!=null) {
                    PhysicsSystem.Contact contact = movement.getContact();

                    //resolve collision
                    position = movement.getPosition();

                    //rotate player
                    if(dir.dir.isCCW(upDir.dir)) {
                        dir = dir.rotateCCW();
                        upDir = upDir.rotateCCW();
                    }
                    else {
                        dir = dir.rotateCW();
                        upDir = upDir.rotateCW();
                    }

                    //TODO should player move the remaining time 1-movement.time
                    //TODO should player need time for rotating (e.g playing animation?
                }
                else {
                    //cast a raycast "down" to check if the end of the platform has been reached
                    //TODO
                }

                //count down dash duration, switch to running if dash is over
                if(state==PlayerState.DASHING) {
                    duration-= Constants.FIXED_DELTA;
                    if(duration<=0)
                        state = PlayerState.RUNNING;
                }
                break;
            }
            case JUMPING:
            {
                //TODO
                break;
            }
            default:
            {
                Log.e(TAG,"Unhandled/Unknown player state");
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handler
    ///////////////////////////////////////////////////////////////////////////

    private boolean onCollide(PhysicsSystem.Contact contact) {
        //TODO handle
        return false;
    }

    private boolean onTap(TapEvent tap) {
        jump();
        return false;
    }

    private boolean onSwipe(SwipeEvent swipe) {
        dash();
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * may be called by different input methods to perform a jump
     */
    protected void jump() {
        wantJump = true;
    }

    /**
     * may be called by different input methods to perform a dash
     */
    protected void dash() {
        wantDash = true;
    }
}
