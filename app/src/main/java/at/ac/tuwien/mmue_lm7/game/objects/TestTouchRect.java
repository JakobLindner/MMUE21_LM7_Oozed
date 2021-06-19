package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.TapEvent;
import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;
import at.ac.tuwien.mmue_lm7.game.physics.PhysicsSystem;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Test object used for testing basic collision
 * @author simon
 */
public class TestTouchRect extends AABB {

    private static final float SPEED = 0.05f;
    private static final String TAG = "TestRect";

    private Vec2 targetPos = new Vec2();
    private Vec2 move = new Vec2();

    private boolean isColliding = false;

    public TestTouchRect() {
        super(0.5f, 0.5f, CollisionLayers.PLATFORM, CollisionLayers.PLAYER);
    }

    @Override
    public void init() {
        super.init();
        getGlobalPosition(targetPos);

        Game.get().onTap.addListener(this,this::onTap);
        super.onCollide.addListener(this,this::onCollide);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Game.get().onTap.removeListener(this);
        super.onCollide.removeListener(this);
    }

    @Override
    public void update() {
        move.set(targetPos).sub(getGlobalPosition());
        if (move.len2() > SPEED * SPEED) {
            PhysicsSystem.Sweep sweep = Game.get().getPhysicsSystem().move(this, move.norm().scl(SPEED));

            setGlobalPosition(sweep.getPosition());

            if(sweep.getContact()!=null)
                isColliding = true;
        }
    }

    @Override
    public void debugRender(RenderSystem render) {
        render.drawRect()
                .at(getGlobalPosition())
                .strokeWidth(1)
                .halfSize(getHalfSize())
                .color(isColliding ? Color.RED : Color.GREEN)
                .style(Paint.Style.STROKE);

        isColliding = false;
    }


    private boolean onTap(TapEvent e) {
        //Log.i(TAG, String.format("New target position: %s", e.getPosition()));

        targetPos.set(e.getPosition());
        return false;
    }

    private boolean onCollide(PhysicsSystem.Contact contact) {
        //Log.i(TAG, "Collision");
        isColliding = true;

        return false;
    }
}
