package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.CollisionLayers;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Bounding box
 */
public class AABB extends GameObject{
    //TODO collision events
    /**
     * half Size of the bounding box
     */
    private Vec2 halfSize;
    private short collisionMask = 0;
    private short collisionLayer = CollisionLayers.NONE;

    //TODO constructor

    public Vec2 getHalfSize() {
        return halfSize;
    }

    public short getCollisionMask() {
        return collisionMask;
    }

    public void setCollisionMask(short collisionMask) {
        this.collisionMask = collisionMask;
    }

    public short getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(short collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    //TODO register on init to physics system

    //TODO deregister on destroy to physics system
}
