package at.ac.tuwien.mmue_lm7.game.objects;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;

/**
 * Draws a text
 * @author simon
 */
public class Text extends GameObject {
    private static final float DEFAULT_TEXT_SIZE = 32;

    public String text;
    public Paint.Align align = Paint.Align.CENTER;
    public int color = Color.WHITE;
    private float textSize;

    public Text() {
        this("");
    }

    public Text(String text) {
        this(text,Color.WHITE,DEFAULT_TEXT_SIZE);
    }

    public Text(String text, int color, float textSize) {
        this.text = text;
        this.color = color;
        this.textSize = textSize;
    }

    @Override
    public void render(RenderSystem render) {
        render.drawText()
                .at(getGlobalPosition())
                .text(text)
                .align(align)
                .typeFace(Typeface.DEFAULT)
                .color(color)
                .size(textSize);
    }
}
