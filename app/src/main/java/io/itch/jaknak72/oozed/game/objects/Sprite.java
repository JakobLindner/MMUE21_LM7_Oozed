package io.itch.jaknak72.oozed.game.objects;

import android.graphics.Color;
import android.graphics.Paint;

import io.itch.jaknak72.oozed.game.GameConstants;
import io.itch.jaknak72.oozed.game.rendering.RenderSystem;
import io.itch.jaknak72.oozed.game.resources.ResourceSystem;
import io.itch.jaknak72.oozed.game.resources.SpriteInfo;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Static sprite object that gets drawn on the canvas
 * For animated sprites use AnimatedSprite instead
 * @author jakob
 */
public class Sprite extends GameObject {
    private static final float DEBUG_RECT_STROKE_WIDTH = 1;
    private static final int DEBUG_RECT_COLOR = Color.YELLOW;

    private SpriteInfo spriteInfo;

    public Sprite(ResourceSystem.SpriteEnum sprite) {
        this.spriteInfo = ResourceSystem.spriteInfo(sprite);
    }

    public void setSpriteInfo(SpriteInfo spriteInfo) {
        this.spriteInfo = spriteInfo;
    }

    @Override
    public void render(RenderSystem render) {
        render.drawSprite(layer)
                .at(getGlobalPosition())
                .rotated(getGlobalRotation())
                .mirrored(getGlobalMirroring())
                .spriteInfo(spriteInfo)
                .frame(0);
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
