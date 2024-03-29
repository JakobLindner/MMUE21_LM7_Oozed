package io.itch.jaknak72.oozed.game.objects;

import android.view.KeyEvent;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.GameConstants;
import io.itch.jaknak72.oozed.game.ObjectFactories;
import io.itch.jaknak72.oozed.game.SwipeEvent;
import io.itch.jaknak72.oozed.game.TapEvent;
import io.itch.jaknak72.oozed.game.physics.CollisionLayers;
import io.itch.jaknak72.oozed.game.physics.PhysicsSystem;
import io.itch.jaknak72.oozed.game.resources.ResourceSystem;
import io.itch.jaknak72.oozed.utils.Direction;
import io.itch.jaknak72.oozed.utils.Jump;
import io.itch.jaknak72.oozed.utils.Utils;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Player object
 * ASSUMPTION: the player never runs faster than its own size in a single frame, otherwise edge turns cannot be performed correctly
 * since every frame two rays are performed down at the front and back of the player to
 * detect the start of running off the edge
 * Dashing can only end while running, so the player won't suddenly become slower in midair
 *
 * @author simon
 */
public class Player extends GameObject {
    private static final String TAG = "Player";

    ///////////////////////////////////////////////////////////////////////////
    // Player constants
    ///////////////////////////////////////////////////////////////////////////
    /**
     * per frame
     */
    public static final float PLAYER_SPEED = 0.1f;
    public static final float PLAYER_DASH_SPEED = 0.25f;
    /**
     * in frames
     */
    public static final float PLAYER_DASH_DURATION = 30;
    /**
     * Jump distance for normal jump
     */
    public static final float NORMAL_JUMP_DISTANCE = 4;
    public static final float JUMP_HEIGHT = 2.5f;
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

    public static final float PLAYER_HALF_SIZE = 0.5f;
    //Bounding box
    public static final float HALF_WIDTH = 0.5f - 2 * GameConstants.UNITS_PER_PIXEL;
    public static final float HALF_HEIGHT = 0.25f;
    public static final short PLAYER_MASK = CollisionLayers.ENEMY | CollisionLayers.PLATFORM;
    public static final short PLAYER_MOVEMENT_MASK = CollisionLayers.PLATFORM;

    //input
    public static final int JUMP_KEY = KeyEvent.KEYCODE_W;
    public static final int DASH_KEY = KeyEvent.KEYCODE_D;

    public static final int RESPAWN_DELAY = 60;

    private AABB box;

    public enum PlayerState {
        RUNNING,
        JUMPING,
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

    /*
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

    public Player(Direction upDir, boolean runningCW, AABB box) {
        this.upDir = upDir;
        if (runningCW)
            this.dir = upDir.rotateCW();
        else
            this.dir = upDir.rotateCCW();
        this.box = box;

        updateOrientation();

        setWrappable(true);
    }

    @Override
    public void init() {
        super.init();

        //register to events and systems
        box.onCollide.addListener(this, this::onCollide);
        box.onKilled.addListener(this, this::onBoxKilled);
        Game.get().onTap.addListener(this, this::onTap);
        Game.get().onSwipe.addListener(this, this::onSwipe);
        Game.get().onKeyDown.addListener(this, this::onKeyDown);

        //initialize jumps
        jump.setJump(NORMAL_JUMP_DISTANCE, JUMP_HEIGHT);
        dashJump.setJump(PLAYER_DASH_SPEED * NORMAL_JUMP_DISTANCE / PLAYER_SPEED, JUMP_HEIGHT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //deregister from events and systems
        box.onCollide.removeListener(this);
        box.onKilled.removeListener(this);
        Game.get().onTap.removeListener(this);
        Game.get().onSwipe.removeListener(this);
        Game.get().onKeyDown.removeListener(this);
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

            //play dash sound
            Game.get().getResourceSystem().playSound(ResourceSystem.Sound.PLAYER_DASH);
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
                PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(box,
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
                        updatePos(movement.getPosition());

                        //align player sprite with edge
                        alignPlayerWithEdge(dir);

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
                    updatePos(movement.getPosition());

                    performRaycasts(movement.getPosition());
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
                PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(box, move, PLAYER_MOVEMENT_MASK);

                //updatePosition
                updatePos(movement.getPosition());

                //check if there was a collision
                if (movement.getContact() != null) {
                    PhysicsSystem.Contact contact = movement.getContact();

                    //recalculate direction and up vector
                    //set up vector to most similar inverse cardinal direction to normal
                    Direction normalDir = Direction.getClosest(contact.getNormal());
                    //align player sprite with edge before rotating
                    alignPlayerWithEdge(normalDir.opposite());
                    upDir = normalDir;

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
                //Log.e(TAG, "Unhandled/Unknown player state");
            }
        }

        if (dashing) {
            --dashDuration;
        }

        //reset inputs
        wantJump = wantDash = false;

        //debug tools
        /*if(lastPos.sub(position).len2()>PLAYER_DASH_SPEED*PLAYER_DASH_SPEED*4) {
           lastPos.add(position);
           //Log.w(TAG,String.format("Pos reset, %s -> %s | %s -> %s | Corner: %s",lastPos.toString(),position.toString(),lastState.toString(),state.toString(),cornerPos.toString()));
        }

        lastPos.set(position);
        lastState = state;*/
    }

    @Override
    public void kill() {
        //create disappear effect
        Vec2 pos = getGlobalPosition();
        GameObject effect = ObjectFactories.makeKilledEffect(pos.x, pos.y);
        Game.get().getRoot().addChild(effect);

        //play sound
        Game.get().getResourceSystem().playSound(ResourceSystem.Sound.PLAYER_DEATH);

        //call respawn function delayed
        if(!Game.get().isLevelCleared())
            Game.get().getTimingSystem().addDelayedAction(Game.get()::respawnPlayer, RESPAWN_DELAY);

        super.kill();
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
            //position.add(Game.get().tmpVec().set(contact.getNormal()).scl(Utils.EPSILON));
            position.add(contact.getOverlap());
        }
        return false;
    }

    private boolean onBoxKilled(GameObject box) {
        kill();
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
        if (event.getKeyCode() == JUMP_KEY)
            wantJump = true;
        if (event.getKeyCode() == DASH_KEY)
            wantDash = true;
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private void changeState(PlayerState to) {
        if (state == to)
            return;

        ////Log.d(TAG, String.format("Player State %-10s -> %s",state.toString(),to.toString()));

        switch (state) {

        }
        switch (to) {
            case JUMPING: {
                //position jump parabolas
                jump.setPositioningAndMirroring(dir, upDir, position, 0);
                dashJump.setPositioningAndMirroring(dir, upDir, position, 0);

                //play sound
                Game.get().getResourceSystem().playSound(ResourceSystem.Sound.OOZE_JUMP);
                break;
            }
            case RUNNING: {
                //reset remaining over edge distance
                distUntilTurn = PLAYER_DASH_SPEED * 2;
                //check platform state before doing further moves
                if (state == PlayerState.JUMPING)
                    performRaycasts();
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
     * Aligns player sprite bounds with given edge of bounding box
     * @param edge
     */
    private void alignPlayerWithEdge(Direction edge) {
        float currentSize = edge.isVertical() == upDir.isVertical() ? HALF_HEIGHT : HALF_WIDTH;
        float diff = (currentSize-PLAYER_HALF_SIZE)+ (edge.dir.x+edge.dir.y)*(edge.isHorizontal()?box.position.x:box.position.y);

        position.add(posUpdate.set(edge.dir).scl(diff));
    }

    /**
     * Updates rotation and mirroring based on dir and updir
     * updates bounding box to reflect current rotation
     */
    private void updateOrientation() {
        //TODO implement via vector angle
        rotation = dir.getRotation();

        mirrored = upDir.dir.isCCW(dir.dir);
        if (upDir.dir.isCCW(dir.dir) && dir.isHorizontal()) {
            rotation += 180;
            rotation %= 360;
        }

        //update box position and size
        updateBox();
    }

    private void updateBox() {
        box.position.set(Game.get().tmpVec().set(upDir.dir).inv().scl(PLAYER_HALF_SIZE - HALF_HEIGHT));

        float width = upDir.isVertical() ? HALF_WIDTH : HALF_HEIGHT;
        float height = upDir.isVertical() ? HALF_HEIGHT : HALF_WIDTH;
        box.halfSize.set(width, height);
    }

    /**
     * Updates player position such that the bounding box has given global position
     *
     * @param boxPos is unchanged
     */
    //helper vector
    private Vec2 posUpdate = new Vec2();

    private void updatePos(Vec2 boxPos) {
        setGlobalPosition(posUpdate.set(boxPos).sub(box.position));
    }

    private void performRaycasts() {
        performRaycasts(getGlobalPosition());
    }

    /**
     * cast two raycast down(=inverse to upDir) to check if the end of the platform has been reached
     * rays are redone with a slight offset since there is a tiny chance that the rays slip exactly between two boxes
     *
     * @param globalPos is unchanged
     */
    private void performRaycasts(Vec2 globalPos) {
        //setup ray
        ray.set(upDir.dir).inv().scl(RUNNING_RAY_CAST_LENGTH);

        //setup ray origin
        move.set(dir.dir).scl(HALF_WIDTH).add(globalPos);
        //perform front ray, possibly 2 times with a slight offset
        PhysicsSystem.Contact frontRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
        if (frontRay == null) {
            move.set(dir.dir).scl(HALF_WIDTH - Utils.EPSILON).add(globalPos);
            frontRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
        }

        //perform back ray, possibly 2 times with a slight offset
        //adjust ray origin
        move.set(dir.dir).inv().scl(HALF_WIDTH).add(globalPos);
        PhysicsSystem.Contact backRay = Game.get().getPhysicsSystem().raycast(move, ray, PLAYER_MOVEMENT_MASK);
        if (backRay == null) {
            move.set(dir.dir).inv().scl(HALF_WIDTH - Utils.EPSILON).add(globalPos);
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
                float overhang = ray.x + ray.y + HALF_WIDTH;

                //set remaining overhang, such that a state change can be triggered
                distUntilTurn = OVERHANG_PERCENT * 2 * HALF_WIDTH - overhang;
            }
        } else {
            if (frontRay == null) {
                //Log.e(TAG, "No collision for any ray detected");
                //TODO should player simply fall down?
            }
        }
    }
}
