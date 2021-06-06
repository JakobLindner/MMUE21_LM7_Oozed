package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.utils.Utils;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class Copter extends Enemy {

    //public static final short MOVEMENT_MASK = CollisionLayers.ENEMY | CollisionLayers.PLATFORM;
    //sprite size
    //public static final float HALF_SIZE = 0.5f;

    public static final float HOVER_DIST = GameConstants.UNITS_PER_PIXEL*8;
    public static final float HOVER_DURATION = 120;

    private AABB copterBox;
    private AABB bodyBox;
    private Vec2 initialPos = new Vec2();
    private int hover = 0;
    private int hoverInc = 1;

    public Copter(AABB copterBox, AABB bodyBox) {
        this.copterBox = copterBox;
        this.bodyBox = bodyBox;
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
        hover += hoverInc;

        float t = (float)hover/(float)HOVER_DURATION;
        t = Utils.easeInOutSine(t);
        t-=0.5f;

        position.y = initialPos.y+HOVER_DIST*t;

        if(hover==0)
            hoverInc = 1;
        else if(hover==HOVER_DURATION)
            hoverInc = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        copterBox.onCollide.removeListener(this);
        bodyBox.onCollide.removeListener(this);
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
