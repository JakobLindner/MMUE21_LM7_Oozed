package at.ac.tuwien.mmue_lm7.game.rendering;

import at.ac.tuwien.mmue_lm7.game.physics.CollisionLayers;

/**
 * Static constants for render and gameplay layers
 * @author simon
 */
public class Layers {
    //TODO do we need separate gameplay and collision layers?
    public static final short DEFAULT = CollisionLayers.NONE;
    public static final short PLAYER = CollisionLayers.PLAYER;
    public static final short ENEMY = CollisionLayers.ENEMY;

    //prevent instantiation
    private Layers() {

    }
}
