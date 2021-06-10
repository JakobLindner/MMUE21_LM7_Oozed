package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Jump;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class Jumper extends Enemy {

    /**
     * in frames
     */
    public static final float JUMP_DURATION = 60;
    public static final float JUMP_HEIGHT = 3f;

    //time until next jump when standing
    public static final int STANDING_TIME = 120;
    //time before jump, where jump can be anticipated
    public static final int ANTICIPATION_TIME = 60;

    public static final short MOVEMENT_MASK = CollisionLayers.PLATFORM;
    //sprite size
    public static final float HALF_SIZE = 0.5f;
    public static final float BODY_WIDTH = 0.5f;
    public static final float BODY_HEIGHT = 0.5f-3*GameConstants.UNITS_PER_PIXEL;
    public static final float BODY_OFFSET = BODY_HEIGHT-HALF_SIZE;//-2*GameConstants.UNITS_PER_PIXEL;
    public static final float WEAK_WIDTH = BODY_WIDTH-2*GameConstants.UNITS_PER_PIXEL;
    public static final float WEAK_HEIGHT = 2*GameConstants.UNITS_PER_PIXEL;
    public static final float WEAK_OFFSET = 4*GameConstants.UNITS_PER_PIXEL;
    public static final float MOVE_WIDTH = 0.5f;
    public static final float MOVE_HEIGHT = HALF_SIZE-GameConstants.UNITS_PER_PIXEL;
    public static final float MOVE_OFFSET = MOVE_HEIGHT-HALF_SIZE;

    private enum JumperState {
        JUMPING,
        STANDING
    }
    private JumperState state = JumperState.STANDING;

    private AABB bodyBox;
    private AABB weakBox;
    /**
     * Bounding box used for sweep operations, layer=NONE
     */
    private AABB moveBox;
    private AnimatedSprite sprite;
    private Direction upDir;

    private Jump jump = new Jump();
    private int timeTilljump = STANDING_TIME;

    //helper vector to prevent allocations
    private Vec2 move = new Vec2();

    public Jumper(AABB bodyBox, AABB weakBox, AABB moveBox, AnimatedSprite sprite, Direction upDir) {
        this.bodyBox = bodyBox;
        this.weakBox = weakBox;
        this.moveBox = moveBox;
        this.upDir = upDir;
        this.sprite = sprite;

        jump.setJump(JUMP_DURATION,JUMP_HEIGHT);

        updateOrientation();
    }

    @Override
    public void init() {
        super.init();
        bodyBox.onCollide.addListener(this,this::onBodyCollide);
        weakBox.onCollide.addListener(this, this::onWeakCollide);
        moveBox.onCollide.addListener(this,this::onMoveCollide);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bodyBox.onCollide.removeListener(this);
        weakBox.onCollide.removeListener(this);
        moveBox.onCollide.removeListener(this);
    }

    @Override
    public void update() {
        super.update();
        if(state==JumperState.JUMPING) {
            jump.advance(1);
            jump.getPosition(move);
            move.sub(position);
            //cancel horizontal movement
            move.scl(upDir.dir.x*upDir.dir.x,upDir.dir.y*upDir.dir.y);

            //perform sweep
            PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(moveBox, move, MOVEMENT_MASK);

            //updatePosition
            updatePos(movement.getPosition());

            //check if there was a collision
            if (movement.getContact() != null) {
                PhysicsSystem.Contact contact = movement.getContact();

                Direction normalDir = Direction.getClosest(contact.getNormal());

                //if collision on top side then flip
                if(normalDir.opposite()==upDir) {
                    //align sprite with edge
                    alignSpriteWithEdge(upDir);
                    //inverse orientation
                    upDir = upDir.opposite();

                    updateOrientation();
                }

                //switch to running
                changeState(JumperState.STANDING);
            }
        }
        else if(state == JumperState.STANDING) {
            boolean anticipating = isAnticipating();
            --timeTilljump;

            //switch animation if it is anticipating now
            if(!anticipating && isAnticipating()) {
                sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.jumperJump));
            }

            if(timeTilljump<=0) {
                changeState(JumperState.JUMPING);
            }
        }
    }

    /**
     * Updates rotation and mirroring based on dir and updir
     * updates bounding box to reflect current rotation
     */
    private void updateOrientation() {
        rotation = upDir.rotateCW().getRotation();

        //update box position and size
        updateBoxes();
    }

    private void updateBoxes() {
        updateBox(weakBox,WEAK_WIDTH,WEAK_HEIGHT,WEAK_OFFSET);
        updateBox(bodyBox,BODY_WIDTH,BODY_HEIGHT,BODY_OFFSET);
        updateBox(moveBox,MOVE_WIDTH, MOVE_HEIGHT, MOVE_OFFSET);
    }

    private void updateBox(AABB box, float halfWidth, float halfHeight, float offset) {
        box.position.set(Game.get().tmpVec().set(upDir.dir).scl(offset));

        float width = upDir.isVertical() ? halfWidth : halfHeight;
        float height = upDir.isVertical() ? halfHeight : halfWidth;
        box.halfSize.set(width, height);
    }

    /**
     * changes move
     * @param boxPos
     */
    private void updatePos(Vec2 boxPos) {
        setGlobalPosition(move.set(boxPos).sub(moveBox.position));
    }

    /**
     * Aligns sprite bounds with given edge of bounding box
     * changes move
     * @param edge
     */
    private void alignSpriteWithEdge(Direction edge) {
        float currentSize = edge.isVertical() == upDir.isVertical() ? MOVE_HEIGHT : MOVE_WIDTH;
        float diff = (currentSize-HALF_SIZE)+ (edge.dir.x+edge.dir.y)*(edge.isHorizontal()?moveBox.position.x:moveBox.position.y);

        position.add(move.set(edge.dir).scl(diff));
    }

    private boolean isAnticipating() {
        return state == JumperState.STANDING && timeTilljump<=ANTICIPATION_TIME;
    }

    private void changeState(JumperState to) {
        if(state==to)
            return;

        if(to==JumperState.JUMPING) {
            jump.setPositioningAndMirroring(upDir.rotateCW(),upDir,position,0);

            //change sprite
            sprite.setSpriteInfo(ResourceSystem.spriteInfo(ResourceSystem.SpriteEnum.jumperIdle));

            //TODO play sound?
        } else if(to==JumperState.STANDING) {
            timeTilljump = STANDING_TIME;
        }

        state = to;
    }

    private boolean onWeakCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
            kill();//get killed by player
        }

        return false;
    }

    private boolean onBodyCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
            //check side of collision
            if (contact.getNormal().approxEquals(upDir.opposite().dir))
                kill();//get killed by player if player touches from above
            else
                contact.getOther().kill();//kill player

        }
        return false;
    }

    private boolean onMoveCollide(PhysicsSystem.Contact contact) {
        return false;
    }
}
