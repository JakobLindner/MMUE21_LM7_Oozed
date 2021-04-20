package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;

import at.ac.tuwien.mmue_lm7.R;
import at.ac.tuwien.mmue_lm7.game.GameConstants;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.game.resources.ResourceSystem;
import at.ac.tuwien.mmue_lm7.game.resources.SpriteInfo;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

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
        render.drawSprite()
                .at(position)
                .rotated(rotation)
                .mirrored(mirrored)
                .spriteInfo(spriteInfo)
                .frame(0);
    }

    @Override
    public void debugRender(RenderSystem render) {
        render.drawRect()
                .at(position)
                .halfSize(new Vec2(0.5f * spriteInfo.size / GameConstants.PIXELS_PER_UNIT, 0.5f * spriteInfo.size / GameConstants.PIXELS_PER_UNIT))
                .color(DEBUG_RECT_COLOR)
                .style(Paint.Style.STROKE)
                .strokeWidth(DEBUG_RECT_STROKE_WIDTH);
    }
}
