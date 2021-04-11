package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;

/**
 * Draws a text
 */
public class Text extends GameObject {
    public String text;
    public Paint.Align align = Paint.Align.CENTER;
    public int color = Color.WHITE;

    public Text() {
    }

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void render(RenderSystem render) {
        render.drawText()
                .at(getGlobalPosition())
                .text(text)
                .align(align)
                .typeFace(Typeface.DEFAULT)
                .color(color)
                .size(64);//TODO make size configurable
    }
}
