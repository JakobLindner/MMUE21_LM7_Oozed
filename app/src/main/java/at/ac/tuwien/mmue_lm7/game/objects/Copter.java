package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Utils;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class Copter extends Enemy {
    public static final float U = GameConstants.UNITS_PER_PIXEL;

    //public static final short MOVEMENT_MASK = CollisionLayers.ENEMY | CollisionLayers.PLATFORM;
    //sprite size
    //public static final float HALF_SIZE = 0.5f;

    public static final float HOVER_DIST = GameConstants.UNITS_PER_PIXEL*8;
    public static final float HOVER_DURATION = 120;
    public static final float MOVE_SPEED = 0.03f;

    public static final float HALF_SIZE = 0.5f;
    public static final float COPTER_WIDTH = 0.5f-2*U;
    public static final float COPTER_HEIGHT = U;
    public static final float COPTER_OFFSET = 6*U;
    public static final float BODY_WIDTH = 0.5f;
    public static final float BODY_HEIGHT = 4*U;
    public static final float BODY_OFFSET = -2*U;

    private AABB copterBox;
    private AABB bodyBox;
    private Vec2 initialPos = new Vec2();
    private int hover = 0;
    private int hoverInc = 1;

    /**
     * If true, the copter flies up all the time
     */
    private boolean noHover;
    private Direction upDir;

    public Copter(AABB copterBox, AABB bodyBox, Direction upDir, boolean noHover) {
        this.copterBox = copterBox;
        this.bodyBox = bodyBox;
        this.upDir = upDir;

        this.noHover = noHover;
        if(noHover)
            setWrappable(true);

        updateOrientation();
    }

    @Override
    public void init() {
        super.init();
        copterBox.onCollide.addListener(this,this::onCopterCollide);
        bodyBox.onCollide.addListener(this,this::onBodyCollide);

        initialPos.set(position);
    }

    @Override
    public void update() {
        if(noHover) {
            position.add(MOVE_SPEED*upDir.dir.x,MOVE_SPEED*upDir.dir.y);
        }
        else {
            hover += hoverInc;

            float t = (float) hover / (float) HOVER_DURATION;
            t = Utils.easeInOutSine(t);
            t -= 0.5f;

            position.set(initialPos.x+(HOVER_DIST*t*upDir.dir.x),initialPos.y+(HOVER_DIST*t*upDir.dir.y));

            if (hover == 0)
                hoverInc = 1;
            else if (hover == HOVER_DURATION)
                hoverInc = -1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        copterBox.onCollide.removeListener(this);
        bodyBox.onCollide.removeListener(this);
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
        updateBox(copterBox,COPTER_WIDTH,COPTER_HEIGHT,COPTER_OFFSET);
        updateBox(bodyBox,BODY_WIDTH,BODY_HEIGHT,BODY_OFFSET);
    }

    private void updateBox(AABB box, float halfWidth, float halfHeight, float offset) {
        box.position.set(Game.get().tmpVec().set(upDir.dir).scl(offset));

        float width = upDir.isVertical() ? halfWidth : halfHeight;
        float height = upDir.isVertical() ? halfHeight : halfWidth;
        box.halfSize.set(width, height);
    }

    private boolean onCopterCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
                contact.getOther().kill();//kill player
        }
        return false;
    }

    private boolean onBodyCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
            kill();//get killed
        }
        return false;
    }
}
