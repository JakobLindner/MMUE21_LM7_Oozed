package at.ac.tuwien.mmue_lm7.game.objects;

import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.ObjectFactories;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.Layers;
import at.ac.tuwien.mmue_lm7.utils.Direction;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class Blocker extends Enemy {
    private static final String TAG = "Blocker";

    private AABB box;

    private Direction dir;
    private Direction upDir;

    public Blocker(AABB box, Direction startingDir, boolean runningCW) {
        this.box = box;
        this.dir = startingDir;
        if (runningCW)
            upDir = startingDir.rotateCW();
        else
            upDir = startingDir.rotateCCW();
    }

    @Override
    public void init() {
        super.init();
        box.onCollide.addListener(this::onCollide);

        updateOrientation();
    }

    @Override
    public void kill() {
        super.kill();

        //create disappear effect
        Vec2 pos = getGlobalPosition();
        GameObject effect = ObjectFactories.makeKilledEffect(pos.x, pos.y);
        Game.get().getRoot().addChild(effect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        box.onCollide.removeListener(this::onCollide);
    }

    private boolean onCollide(PhysicsSystem.Contact contact) {
        if (contact.getOther().layer == Layers.PLAYER) {
            //check side of collision
            if(contact.getNormal().approxEquals(dir.opposite().dir))
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
        rotation = dir.getRotation()+180;
        rotation%=360;

        mirrored = upDir.dir.isCCW(dir.dir);
        if (upDir.dir.isCCW(dir.dir) && dir.isHorizontal()) {
            rotation += 180;
            rotation %= 360;
        }
    }

}
