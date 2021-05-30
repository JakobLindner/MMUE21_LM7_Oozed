package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Paint;

import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Draws a rectangle
 */
public class Rect extends GameObject{

    private Vec2 halfSize;
    private int color;
    private Paint.Style style;

    public Rect(Vec2 halfSize, int color, Paint.Style style) {
        this.halfSize = halfSize;
        this.color = color;
        this.style = style;
    }

    @Override
    public void render(RenderSystem render) {
        render.drawRect()
                .at(getGlobalPosition())
                .halfSize(halfSize)
                .color(color)
                .style(style);

    }
}
