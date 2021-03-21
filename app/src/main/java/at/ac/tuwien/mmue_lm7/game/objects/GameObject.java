package at.ac.tuwien.mmue_lm7.game.objects;

import java.util.ArrayList;

import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Initialization may happen in constructor
 */
public class GameObject {
    public Vec2 position = new Vec2();
    /**
     * rotation in degrees
     */
    public float rotation = 0;
    public ArrayList<GameObject> children = new ArrayList<GameObject>();
    public GameObject parent = null;

    /**
     * Updates the game object, can be overwritten by subclasses
     */
    public void update() {

    }

    /**
     * renders the game object, can be overwritten by subclasses
     */
    public void render(RenderSystem render) {
    }

    /**
     * Removes the game object from the scene tree
     */
    public final void destroy() {
        //TODO
    }

    /**
     * adds given child
     * @param child
     * @return this for chaining
     */
    public GameObject addChild(GameObject child) {
        //TODO add child
        return this;
    }

    /**
     * removes given child
     * @param child
     * @return this for chaining
     */
    public GameObject removeChild(GameObject child) {
        //TODO remove child
        return this;
    }

    public void onTap(Vec2 position) {
        //TODO player jump
    }
}
