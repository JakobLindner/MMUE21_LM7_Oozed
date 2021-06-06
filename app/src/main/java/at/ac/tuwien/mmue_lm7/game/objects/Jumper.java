package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
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

    public static final short MOVEMENT_MASK = CollisionLayers.ENEMY | CollisionLayers.PLATFORM;
    //sprite size
    public static final float HALF_SIZE = 0.5f;
    public static final float HALF_WIDTH = 0.5f;
    public static final float HALF_HEIGHT = 0.5f - GameConstants.UNITS_PER_PIXEL;

    private enum JumperState {
        JUMPING,
        STANDING
    }
    private JumperState state = JumperState.STANDING;

    private AABB box;
    private Direction upDir;

    private Jump jump = new Jump();
    private int timeTilljump = STANDING_TIME;

    //helper vector to prevent allocations
    private Vec2 move = new Vec2();

    public Jumper(AABB box, Direction upDir) {
        this.box = box;
        this.upDir = upDir;

        jump.setJump(JUMP_DURATION,JUMP_HEIGHT);

        updateOrientation();
    }

    @Override
    public void init() {
        super.init();
        box.onCollide.addListener(this,this::onCollide);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        box.onCollide.removeListener(this);
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
            PhysicsSystem.Sweep movement = Game.get().getPhysicsSystem().move(box, move, MOVEMENT_MASK);

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
            --timeTilljump;

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
        updateBox();
    }

    private void updateBox() {
        box.position.set(Game.get().tmpVec().set(upDir.dir).inv().scl(HALF_SIZE - HALF_HEIGHT));

        float width = upDir.isVertical() ? HALF_WIDTH : HALF_HEIGHT;
        float height = upDir.isVertical() ? HALF_HEIGHT : HALF_WIDTH;
        box.halfSize.set(width, height);
    }

    /**
     * changes move
     * @param boxPos
     */
    private void updatePos(Vec2 boxPos) {
        setGlobalPosition(move.set(boxPos).sub(box.position));
    }

    /**
     * Aligns sprite bounds with given edge of bounding box
     * changes move
     * @param edge
     */
    private void alignSpriteWithEdge(Direction edge) {
        float currentSize = edge.isVertical() == upDir.isVertical() ? HALF_HEIGHT : HALF_WIDTH;
        float diff = (currentSize-HALF_SIZE)+ (edge.dir.x+edge.dir.y)*(edge.isHorizontal()?box.position.x:box.position.y);

        position.add(move.set(edge.dir).scl(diff));
    }

    private void changeState(JumperState to) {
        if(state==to)
            return;

        if(to==JumperState.JUMPING) {
            jump.setPositioningAndMirroring(upDir.rotateCW(),upDir,position,0);

            //TODO play sound?
        } else if(to==JumperState.STANDING) {
            timeTilljump = STANDING_TIME;
        }

        state = to;
    }

    private boolean onCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
            //check side of collision
            if (contact.getNormal().approxEquals(upDir.opposite().dir))
                kill();//get killed by player if player touches from above
            else
                contact.getOther().kill();//kill player

        }
        return false;
    }

    private Vec2 hs = new Vec2(HALF_SIZE,HALF_SIZE);
    @Override
    public void render(RenderSystem render) {
        render.drawRect()
                .at(getGlobalPosition())
                .halfSize(hs)
                .color((state==JumperState.JUMPING || timeTilljump>ANTICIPATION_TIME)?Color.GREEN: Color.RED);
    }
}
