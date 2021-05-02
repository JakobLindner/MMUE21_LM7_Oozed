package at.ac.tuwien.mmue_lm7.game.objects;

import android.util.Log;
import android.view.KeyEvent;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.SwipeEvent;
import at.ac.tuwien.mmue_lm7.game.TapEvent;
import at.ac.tuwien.mmue_lm7.game.WraparoundSystem;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Jump;
import at.ac.tuwien.mmue_lm7.utils.Utils;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Player object
 * ASSUMPTION: the player never runs faster than its own size in a single frame, otherwise edge turns cannot be performed correctly
 * since every frame two rays are performed down at the front and back of the player to
 * detect the start of running off the edge
 * Dashing can only end while running, so the player won't suddenly become slower in midair
 * @author simon
 */
public class Player extends AABB {
    private static final String TAG = "Player";

    ///////////////////////////////////////////////////////////////////////////
    // Player constants
    ///////////////////////////////////////////////////////////////////////////
    /**
     * per frame
     */
    public static final float PLAYER_SPEED = 0.075f;
    public static final float PLAYER_DASH_SPEED = 0.2f;
    /**
     * in frames
     */
    public static final float PLAYER_DASH_DURATION = 60;
    public static final float PLAYER_HALF_SIZE = 0.5f;
    public static final short PLAYER_MASK = CollisionLayers.ENEMY | CollisionLayers.PLATFORM;
    public static final short PLAYER_MOVEMENT_MASK = CollisionLayers.PLATFORM;
    /**
     * Jump distance for normal jump
     */
    public static final float NORMAL_JUMP_DISTANCE = 4;
    /**
     * Jump height for
     */
    public static final float JUMP_HEIGHT = 2;
    //assuming platforms are at least 1 in width & height
    public static final float RUNNING_RAY_CAST_LENGTH = 1;
    /**
     * How much of its own size can the player
     */
    public static final float OVERHANG_PERCENT = 0.5f;
    /**
     * Outer turn animation duration is measured in distance, so the turn is automatically performed faster when dashing
     */
    public static final float OUTER_TURN_DIST = 0.25f;

    //input
    public static final int JUMP_KEY = KeyEvent.KEYCODE_W;
    public static final int DASH_KEY = KeyEvent.KEYCODE_D;

    public enum PlayerState {
        RUNNING,
        JUMPING,
        //TODO OUTER_TURN custom hitbox and turn animation
        OUTER_TURN,
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
    //RUNNING & DASHING
    /**
     * Direction of movement
     */
    private Direction dir;
    /**
     * local up direction
     */
    private Direction upDir;

    /**
     * Vectors reused for movement operations
     */
    private Vec2 move = new Vec2();
    private Vec2 ray = new Vec2();

    /**
     * This is set when the player is starting to run off the edge, when this is 0 => state = OUTER_TURN
     */
    private float distUntilTurn = PLAYER_DASH_SPEED * 2;

    /**
     * is the player currently dashing?
     */
    private boolean dashing = false;
    /**
     * Remaining duration of dash
     */
    private float dashDuration;

    //JUMPING
    //these jump objects are valid when state==JUMPING
    private Jump jump = new Jump();
    private Jump dashJump = new Jump();

    //OUTER_TURN
    //set by previous RUNNING/DASHING state
    private Vec2 cornerPos = new Vec2();
    private float coveredTurnDist;

    public Player(Direction startingDir, boolean runningCW) {
        super(PLAYER_HALF_SIZE,
                PLAYER_HALF_SIZE,
                CollisionLayers.PLAYER,
                PLAYER_MASK);

        this.dir = startingDir;
        if (runningCW)
            this.upDir = this.dir.rotateCCW();
        else
            this.upDir = this.dir.rotateCW();

        updateOrientation();

        setWrappable(true);
    }

    @Override
    public void init() {
        super.init();

        //register to events and systems
        onCollide.addListener(this::onCollide);
        Game.get().onTap.addListener(this::onTap);
        Game.get().onSwipe.addListener(this::onSwipe);
        Game.get().onKeyDown.addListener(this::onKeyDown);

        //initialize jumps
        jump.setJump(NORMAL_JUMP_DISTANCE, JUMP_HEIGHT);
        dashJump.setJump(PLAYER_DASH_SPEED*NORMAL_JUMP_DISTANCE/PLAYER_SPEED, JUMP_HEIGHT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //deregister from events and systems
        onCollide.removeListener(this::onCollide);
        Game.get().onTap.removeListener(this::onTap);
        Game.get().onSwipe.removeListener(this::onSwipe);
        Game.get().onKeyDown.removeListener(this::onKeyDown);
    }

    @Override
    public void update() {

        //handle inputs
        if (wantJump && state == PlayerState.RUNNING) {
            changeState(PlayerState.JUMPING);
        }
        if (wantDash && state == PlayerState.RUNNING) {
            dashDuration = PLAYER_DASH_DURATION;
            dashing = true;
        }

        //handle current state
        switch (state) {
            case RUNNING: {
                //should turning be initiated?
                if (distUntilTurn < getCurrentSpeed()) {
                    //set how much of the turn is already covered
                    coveredTurnDist = getCurrentSpeed() - distUntilTurn;
                    changeState(PlayerState.OUTER_TURN);
                    break;
                }

                //see if dash has ended
                if (dashing && dashDuration <= 0) {
                    dashing = false;
                }

                //perform movement
                PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(this,
                        move.set(dir.dir).scl(getCurrentSpeed()),
                        PLAYER_MOVEMENT_MASK);

                //check if there is a collision
                if (movement.getContact() != null) {
                    PhysicsSystem.Contact contact = movement.getContact();

                    //check if player is just too close to platform
                    if (Game.get().tmpVec().set(contact.getNormal()).scl(dir.dir).len2() == 0) {
                        //lift player up, just a tiny bit
                        position.add(upDir.dir.x * Utils.EPSILON, upDir.dir.y * Utils.EPSILON);
                    } else {
                        //resolve collision
                        setGlobalPosition(movement.getPosition());

                        //rotate player
                        if (dir.dir.isCCW(upDir.dir)) {
                            dir = dir.rotateCCW();
                            upDir = upDir.rotateCCW();
                        } else {
                            dir = dir.rotateCW();
                            upDir = upDir.rotateCW();
                        }
                        updateOrientation();
                    }

                    //TODO should player move the remaining time 1-movement.time
                    //TODO should player need time for rotating (e.g playing animation?
                } else {
                    //perform move
                    setGlobalPosition(movement.getPosition());

                    //cast two raycast down(=inverse to upDir) to check if the end of the platform has been reached
                    //rays are redone with a slight offset since there is a tiny chance that the rays slip exactly between two boxes
                    ray.set(upDir.dir).inv().scl(RUNNING_RAY_CAST_LENGTH);
                    move.set(dir.dir).scl(PLAYER_HALF_SIZE).add(movement.getPosition());
                    PhysicsSystem.Contact frontRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
                    if (frontRay == null) {
                        move.set(dir.dir).scl(PLAYER_HALF_SIZE - Utils.EPSILON).add(movement.getPosition());
                        frontRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
                    }

                    move.set(dir.dir).inv().scl(PLAYER_HALF_SIZE).add(movement.getPosition());
                    PhysicsSystem.Contact backRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
                    if (backRay == null) {
                        move.set(dir.dir).inv().scl(PLAYER_HALF_SIZE - Utils.EPSILON).add(movement.getPosition());
                        backRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
                    }

                    if (backRay != null) {
                        //check if player is already partly over edge
                        if (frontRay == null) {
                            //determine how far the player is already over the edge

                            //determine corner position
                            AABB platform = backRay.getOther();
                            cornerPos.set(dir.dir).add(upDir.dir).scl(platform.getHalfSize()).add(platform.getGlobalPosition());

                            //determine how far player is peeking off the platform edge with the front edge
                            //ray = overhang
                            ray.set(getGlobalPosition())
                                    .sub(cornerPos)
                                    .scl(dir.dir);//take only relevant component
                            float overhang = ray.x + ray.y + PLAYER_HALF_SIZE;

                            //set remaining overhang, such that a state change can be triggered
                            distUntilTurn = OVERHANG_PERCENT * 2 * PLAYER_HALF_SIZE - overhang;
                        }
                    } else {
                        if (frontRay == null) {
                            Log.e(TAG, "No collision for any ray detected");
                            //TODO should player simply fall down?
                        }
                    }
                }

                break;
            }
            case JUMPING: {
                //move along jump parabola
                Jump currentJump = getCurrentJump();
                currentJump.advance(getCurrentSpeed());
                currentJump.getPosition(move);
                move.sub(position);

                //perform sweep
                PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(this, move, PLAYER_MOVEMENT_MASK);
                //updatePosition
                setGlobalPosition(movement.getPosition());

                //check if there was a collision
                if (movement.getContact() != null) {
                    PhysicsSystem.Contact contact = movement.getContact();

                    //recalculate direction and up vector
                    //set up vector to most similar inverse cardinal direction to normal
                    upDir = Direction.getClosest(contact.getNormal());

                    //dir = most similar direction to move, which is perpendicular to up
                    if (upDir.dir.isCCW(move))
                        dir = upDir.rotateCCW();
                    else
                        dir = upDir.rotateCW();

                    updateOrientation();

                    //switch to running
                    changeState(PlayerState.RUNNING);
                }

                break;
            }
            case OUTER_TURN: {
                //TODO play animation, dependent on coveredTurnDist

                //advance turn
                coveredTurnDist += getCurrentSpeed();

                //if turn animation performed, switch to previously running/dashing
                if (coveredTurnDist >= OUTER_TURN_DIST) {
                    //rotate player
                    if (dir.dir.isCCW(upDir.dir)) {
                        dir = dir.rotateCW();
                        upDir = upDir.rotateCW();
                    } else {
                        dir = dir.rotateCCW();
                        upDir = upDir.rotateCCW();
                    }
                    updateOrientation();

                    //position player
                    Vec2 newPos = Game.get().tmpVec().set(upDir.dir).scl(PLAYER_HALF_SIZE).add(cornerPos);
                    setGlobalPosition(newPos);

                    //change to running
                    changeState(PlayerState.RUNNING);
                }
                break;
            }
            default: {
                Log.e(TAG, "Unhandled/Unknown player state");
            }
        }

        if (dashing) {
            --dashDuration;
        }

        //reset inputs
        wantJump = wantDash = false;

        //TODO based on state, set hitbox and sprite
    }

    @Override
    public void onWrap(Vec2 translation) {
        super.onWrap(translation);

        //wrap jump parabola start positions
        jump.translateStartingPosition(translation);
        dashJump.translateStartingPosition(translation);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handler
    ///////////////////////////////////////////////////////////////////////////

    private boolean onCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().getCollisionLayer() == CollisionLayers.PLATFORM) {
            //resolve collision
            position.add(Game.get().tmpVec().set(contact.getNormal()).scl(Utils.EPSILON));
        }
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

    private boolean onKeyDown(KeyEvent event) {
        if(event.getKeyCode()==JUMP_KEY)
            wantJump = true;
        if(event.getKeyCode()==DASH_KEY)
            wantDash = true;
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private void changeState(PlayerState to) {
        if (state == to)
            return;

        switch (state) {
            case OUTER_TURN: {
                //reset remaining over edge distance
                distUntilTurn = PLAYER_DASH_SPEED * 2;
            }
        }
        switch (to) {
            case JUMPING: {
                //position jump parabolas
                jump.setPositioningAndMirroring(dir, upDir, position, 0);
                dashJump.setPositioningAndMirroring(dir, upDir, position, 0);
                break;
            }
        }

        //TODO make effects (e.g. RUNNING->DASHING)
        //TODO play sounds (e.g. ->JUMPING)

        state = to;
    }

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

    private Jump getCurrentJump() {
        if (dashing)
            return dashJump;
        return jump;
    }

    /**
     * @return current running speed
     */
    private float getCurrentSpeed() {
        return dashing ? PLAYER_DASH_SPEED : PLAYER_SPEED;
    }

    /**
     * Updates rotation and mirroring based on dir and updir
     */
    private void updateOrientation() {
        //TODO implement via vector angle
        rotation = dir.getRotation();

        mirrored = upDir.dir.isCCW(dir.dir);
        if (upDir.dir.isCCW(dir.dir) && dir.isHorizontal()) {
            rotation += 180;
            rotation %= 360;
        }
    }
}
