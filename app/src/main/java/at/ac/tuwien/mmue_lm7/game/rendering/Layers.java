package at.ac.tuwien.mmue_lm7.game.rendering;

import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;

/**
 * Static constants for render and gameplay layers
 * @author simon
 */
public class Layers {
    public static final short DEFAULT = 0;
    public static final short TILES = 5;
    public static final short PLAYER = 10;
    public static final short ENEMY = 20;
    public static final short OBSTACLES = 25;
    public static final short EFFECTS = 50;
    public static final short UI = 100;

    //prevent instantiation
    private Layers() {

    }
}
