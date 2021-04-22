package at.ac.tuwien.mmue_lm7.game.physics;

/**
 * Static class with collision layer constants
 * layer is a short, so there are 16 layers possible
 * e.g. used for collisions
 * @author simon
 */
public class CollisionLayers {
    public static final short MAX_LAYERS = 16;

    public static final short NONE = 1<<0;
    public static final short PLAYER = 1<<1;
    public static final short ENEMY = 1<<2;
    public static final short PLATFORM = 1<<3;
    //...

    //prevent instantiation
    private CollisionLayers() {}
}
