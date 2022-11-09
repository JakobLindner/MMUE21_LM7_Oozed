package io.itch.jaknak72.oozed.game.objects;

import android.graphics.Color;
import android.graphics.Paint;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.physics.CollisionLayers;
import io.itch.jaknak72.oozed.game.physics.PhysicsSystem;
import io.itch.jaknak72.oozed.game.rendering.RenderSystem;
import io.itch.jaknak72.oozed.utils.Subject;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Bounding box, registers itself to the physics system
 * @author simon
 */
public class AABB extends GameObject {
    private static final float DEBUG_RECT_STROKE_WIDTH = 1;
    private static final int DEBUG_RECT_COLOR = Color.GREEN;

    /**
     * Subject for collision events involving this aabb, the passed contact is in the view of this aabb
     */
    public final Subject<PhysicsSystem.Contact> onCollide = new Subject<PhysicsSystem.Contact>();
    /**
     * half Size of the bounding box, components must be >=0
     */
    public Vec2 halfSize = new Vec2();
    /**
     * Defines the layer this AABB is scanning collisions for
     */
    private short collisionMask = 0;
    private short collisionLayer = CollisionLayers.NONE;

    public AABB(float halfWidth, float halfHeight, short mask, short layer) {
        this.halfSize.set(halfWidth, halfHeight);
        this.collisionMask = mask;
        this.collisionLayer = layer;
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
        super.init();
        Game.get().getPhysicsSystem().addAABB(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Game.get().getPhysicsSystem().removeAABB(this);
    }

    @Override
    public void debugRender(RenderSystem render) {
        render.drawRect()
                .at(getGlobalPosition())
                .halfSize(halfSize)
                .color(DEBUG_RECT_COLOR)
                .style(Paint.Style.STROKE)
                .strokeWidth(DEBUG_RECT_STROKE_WIDTH);
    }
}
