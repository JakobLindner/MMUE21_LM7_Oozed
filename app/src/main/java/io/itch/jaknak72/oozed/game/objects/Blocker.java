package io.itch.jaknak72.oozed.game.objects;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.GameConstants;
import io.itch.jaknak72.oozed.game.physics.CollisionLayers;
import io.itch.jaknak72.oozed.game.physics.PhysicsSystem;
import io.itch.jaknak72.oozed.game.rendering.Layers;
import io.itch.jaknak72.oozed.game.resources.ResourceSystem;
import io.itch.jaknak72.oozed.game.resources.SpriteInfo;
import io.itch.jaknak72.oozed.utils.Direction;
import io.itch.jaknak72.oozed.utils.Utils;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Blocker enemy, that cannot be touched from front
 * can be configured to walk back and forth on platform or stand still
 * @author simon
 */
public class Blocker extends Enemy {
    private static final String TAG = "Blocker";

    private static final float WALK_SPEED = 0.04f;
    /**
     * Delay before initiating turn
     */
    private static final int TURN_DELAY = 30;
    //private static final int TURN_DURATION = 30;
    /**
     * Delay before walking after turn
     */
    private static final int TURN_POST_DELAY = 60;

    private static final short MOVEMENT_MASK = CollisionLayers.ENEMY | CollisionLayers.PLATFORM | CollisionLayers.DEADLY;
    private static final float WALKING_RAY_CAST_LENGTH = 1;
    /**
     * Offset of raycast origin from center
     */
    private static final float WALKING_RAY_CAST_OFFSET = 0.5f - GameConstants.UNITS_PER_PIXEL;
    /**
     * Mask used for checking whether end of platform has been reached
     */
    private static final short RAY_CAST_MASK = CollisionLayers.PLATFORM;
    private static final float HALF_WIDTH = 0.5f;
    private static final float HALF_HEIGHT = 0.5f - GameConstants.UNITS_PER_PIXEL;
    private static final float BLOCKER_HALF_SIZE = 0.5f;

    private AABB box;
    private AnimatedSprite sprite;

    private Direction dir;
    private Direction upDir;

    ///////////////////////////////////////////////////////////////////////////
    // STATE
    ///////////////////////////////////////////////////////////////////////////
    private enum BlockerState {
        WALKING,
        STANDING,//used before and after turning
        TURNING
    }

    private BlockerState state = BlockerState.WALKING;

    //vector instance reused for movement
    //2 vectors are used almost every frame, so we keep them here as members for optimization
    private Vec2 move = new Vec2();
    private Vec2 ray = new Vec2();

    public Blocker(AABB box, AnimatedSprite sprite, Direction upDir, boolean runningCW, boolean dynamic) {
        this.box = box;
        this.upDir = upDir;
        this.sprite = sprite;
        if (runningCW)
            this.dir = upDir.rotateCW();
        else
            this.dir = upDir.rotateCCW();

        setWrappable(dynamic);

        state = dynamic ? BlockerState.WALKING : BlockerState.STANDING;
    }

    @Override
    public void init() {
        super.init();
        box.onCollide.addListener(this, this::onCollide);

        updateOrientation();
        updateSprite();
    }

    @Override
    public void update() {
        if (state == BlockerState.WALKING) {
            //perform move
            PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(box,
                    move.set(dir.dir).scl(WALK_SPEED),
                    MOVEMENT_MASK);
            setGlobalPosition(move.set(movement.getPosition()).sub(box.position));

            //check if there is a collision = obstacle in front of
            if (movement.getContact() != null || endOfPlatformReached(movement.getPosition())) {
                Game.get().getTimingSystem().addDelayedAction(this::initTurning, TURN_DELAY);
                changeState(BlockerState.STANDING);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        box.onCollide.removeListener(this);
    }

    private boolean onCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
            //check side of collision
            if (contact.getNormal().approxEquals(dir.opposite().dir))
                contact.getOther().kill();//kill player
            else
                kill();//get killed by player
        }
        return false;
    }

    /**
     * Updates rotation and mirroring based on dir and updir
     */
    private void updateOrientation() {
        //TODO implement via vector angle
        rotation = dir.getRotation() + 180;
        rotation %= 360;

        mirrored = !upDir.dir.isCCW(dir.dir);
        if (mirrored && dir.isHorizontal()) {
            rotation += 180;
            rotation %= 360;
        }

        //update bounding box
        float width = upDir.isVertical() ? HALF_WIDTH : HALF_HEIGHT;
        float height = upDir.isVertical() ? HALF_HEIGHT : HALF_WIDTH;
        box.halfSize.set(width, height);
        box.position.set(upDir.dir).inv().scl(BLOCKER_HALF_SIZE - HALF_HEIGHT);
    }

    /**
     * Performs raycasts to check if the end of the current platform has been reached
     * @return true is end of platform has been reached
     */
    private boolean endOfPlatformReached(Vec2 globalPos) {
        //setup ray
        ray.set(upDir.dir).inv().scl(WALKING_RAY_CAST_LENGTH);
        //setup ray origin
        move.set(dir.dir).scl(WALKING_RAY_CAST_OFFSET).add(globalPos);

        //perform raycast, redo with a slight offset if nothing has been hit, since it might have been performed exactly between two blocks
        PhysicsSystem.Contact frontRay = Game.get().getPhysicsSystem().raycast(move, ray, RAY_CAST_MASK);
        if (frontRay == null) {
            move.set(dir.dir).scl(WALKING_RAY_CAST_OFFSET - Utils.EPSILON).add(globalPos);
            frontRay = Game.get().getPhysicsSystem().raycast(move, ray, RAY_CAST_MASK);
        }

        return frontRay == null;
    }

    private void initTurning() {
        if (isDestroyed())
            return;

        //TODO animation
        changeState(BlockerState.TURNING);

        //TODO do this at end of turning animation
        changeState(BlockerState.STANDING);
        dir = dir.opposite();
        updateOrientation();
        Game.get().getTimingSystem().addDelayedAction(this::switchToWalking, TURN_POST_DELAY);
    }

    private void changeState(BlockerState to) {
        if(state == to)
            return;

        state = to;

        updateSprite();
    }

    private void updateSprite() {
        ResourceSystem.SpriteEnum nextSprite;
        if(state == BlockerState.WALKING)
            nextSprite = ResourceSystem.SpriteEnum.blockerRun;
        else
            nextSprite = ResourceSystem.SpriteEnum.blockerIdle;

        SpriteInfo si = ResourceSystem.spriteInfo(nextSprite);
        if(sprite.getSpriteInfo()!=si)
            sprite.setSpriteInfo(si);
    }

    private void switchToWalking() {
        if (isDestroyed())
            return;

        changeState(BlockerState.WALKING);
    }

}
