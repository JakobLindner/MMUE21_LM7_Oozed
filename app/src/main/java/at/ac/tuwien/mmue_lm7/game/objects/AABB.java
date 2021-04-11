package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.utils.Subject;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Bounding box
 */
public class AABB extends GameObject {
    private static final float DEBUG_RECT_STROKE_WIDTH = 4;
    private static final int DEBUG_RECT_COLOR = Color.GREEN;

    /**
     * Subject for collision events involving this aabb, the passed contact is in the view of this aabb
     */
    public final Subject<PhysicsSystem.Contact> onCollide = new Subject<PhysicsSystem.Contact>();
    /**
     * half Size of the bounding box, components must be >=0
     */
    private Vec2 halfSize = new Vec2();
    /**
     * Defines the layer this AABB is scanning collisions for
     */
    private short collisionMask = 0;
    private short collisionLayer = CollisionLayers.NONE;

    /**
     * Copies the values of the vector
     */
    public AABB(float halfWidth, float halfHeight) {
        this.halfSize.set(halfWidth, halfHeight);
    }

    public Vec2 getHalfSize() {
        return halfSize;
    }

    public short getCollisionMask() {
        return collisionMask;
    }

    public void setCollisionMask(short collisionMask) {
        this.collisionMask = collisionMask;

        Game.get().getPhysicsSystem().updateAABB(this);
    }

    public short getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(short collisionLayer) {
        this.collisionLayer = collisionLayer;

        Game.get().getPhysicsSystem().updateAABB(this);
    }

    /**
     * Changes collision mask and layer at once, should be used if both values need to be changed
     *
     * @param collisionLayer
     * @param collisionMask
     */
    public void setCollisionLayerAndMask(short collisionLayer, short collisionMask) {
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;

        Game.get().getPhysicsSystem().updateAABB(this);
    }

    @Override
    public void init() {
        Game.get().getPhysicsSystem().addAABB(this);
    }

    @Override
    protected void onDestroy() {
        Game.get().getPhysicsSystem().removeAABB(this);
    }

    @Override
    public void debugRender(RenderSystem render) {
        render.drawRect()
                .at(position)
                .halfSize(halfSize)
                .color(DEBUG_RECT_COLOR)
                .style(Paint.Style.STROKE)
                .strokeWidth(DEBUG_RECT_STROKE_WIDTH);
    }
}
