package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;

/**
 * Adds deadly behaviour to bounding box, that kills things by calling GameObject::kill()
 * it kills everything it can collide with
 */
public class DeadlyAABB extends GameObject{

    private AABB aabb;

    public DeadlyAABB(AABB aabb) {
        this.aabb = aabb;
    }

    @Override
    public void init() {
        super.init();
        aabb.onCollide.addListener(this,this::onCollide);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        aabb.onCollide.removeListener(this);
    }

    private boolean onCollide(PhysicsSystem.Contact contact) {
        contact.getOther().kill();
        return false;
    }
}
