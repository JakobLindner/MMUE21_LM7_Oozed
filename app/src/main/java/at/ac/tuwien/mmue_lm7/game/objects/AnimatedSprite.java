package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SpriteInfo;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Animated sprite object that gets drawn on the canvas
 * @author jakob
 */
public class AnimatedSprite extends GameObject{
    private static final float DEBUG_RECT_STROKE_WIDTH = 1;
    private static final int DEBUG_RECT_COLOR = Color.RED;

    private int frame = 0;
    private int updateCount = 0;
    private SpriteInfo spriteInfo = new SpriteInfo();

    public AnimatedSprite(ResourceSystem.SpriteEnum sprite) {
        this.spriteInfo = ResourceSystem.spriteInfo(sprite);
    }

    public SpriteInfo getSpriteInfo() {
        return spriteInfo;
    }

    public void setSpriteInfo(SpriteInfo spriteInfo) {
        this.spriteInfo = spriteInfo;
        //reset animation state
        updateCount = 0;
        frame = 0;
    }

    @Override
    public void update() { // one FIXED_DELTA_MS passed
        if (++updateCount > spriteInfo.frameDuration) {
            frame++;
            frame %= spriteInfo.animationLength;
            updateCount = 0;
        }
    }

    @Override
    public void render(RenderSystem render) {
        render.drawSprite(layer)
                .at(getGlobalPosition())
                .rotated(getGlobalRotation())
                .mirrored(getGlobalMirroring())
                .spriteInfo(spriteInfo)
                .frame(frame);
    }

    @Override
    public void debugRender(RenderSystem render) {
        render.drawRect()
                .at(getGlobalPosition())
                .halfSize(new Vec2(0.5f * spriteInfo.size / GameConstants.PIXELS_PER_UNIT, 0.5f * spriteInfo.size / GameConstants.PIXELS_PER_UNIT))
                .color(DEBUG_RECT_COLOR)
                .style(Paint.Style.STROKE)
                .strokeWidth(DEBUG_RECT_STROKE_WIDTH);
    }
}
