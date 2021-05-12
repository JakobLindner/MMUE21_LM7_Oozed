package at.ac.tuwien.mmue_lm7.game.objects;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class Blocker extends Enemy{
    private AABB box;

    public Blocker(AABB box) {
        this.box = box;
    }

    @Override
    public void init() {
        super.init();
        box.onCollide.addListener(this::onCollide);
    }

    @Override
    public void kill() {
        super.kill();

        //create disappear effect
        Vec2 pos = getGlobalPosition();
        GameObject effect = ObjectFactories.makeKilledEffect(pos.x,pos.y);
        Game.get().getRoot().addChild(effect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        box.onCollide.removeListener(this::onCollide);
    }

    private boolean onCollide(PhysicsSystem.Contact contact) {
        if(contact.getOther().layer== Layers.PLAYER)
            kill();
        return false;
    }

}
