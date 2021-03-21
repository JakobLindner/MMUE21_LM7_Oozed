package at.ac.tuwien.mmue_lm7.game.rendering.commands;

import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Command object that contains all info to draw a text
 */
//TODO make comparable by layer
public class DrawText {
    public Vec2 position;
    public short layer;
    public String text;
}
