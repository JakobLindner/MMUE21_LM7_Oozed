package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SpriteInfo;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

public class AnimatedSprite extends GameObject{
    private static final float DEBUG_RECT_STROKE_WIDTH = 1;
    private static final int DEBUG_RECT_COLOR = Color.RED;

    private int frame = 0;
    private int updateCount = 0;
    private SpriteInfo spriteInfo = new SpriteInfo();

    public void setSpriteInfo(SpriteInfo spriteInfo) {
        this.spriteInfo = spriteInfo;
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
        render.drawSprite()
                .at(position)
                .rotated(rotation)
                .mirrored(mirrored)
                .spriteInfo(spriteInfo)
                .frame(frame);
    }

    @Override
    public void debugRender(RenderSystem render) {
        render.drawRect()
                .at(position)
                .halfSize(new Vec2(0.5f, 0.5f))
                .color(DEBUG_RECT_COLOR)
                .style(Paint.Style.STROKE)
                .strokeWidth(DEBUG_RECT_STROKE_WIDTH);
    }
}
