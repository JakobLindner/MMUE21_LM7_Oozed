package at.ac.tuwien.mmue_lm7.game.objects;

import java.util.ArrayList;

import at.ac.tuwien.mmue_lm7.game.Game;
import at.ac.tuwien.mmue_lm7.game.rendering.RenderSystem;
import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * Initialization may happen in constructor
 */
public class GameObject {
    /**
     * local position of object
     */
    public Vec2 position = new Vec2();
    /**
     * local rotation in degrees
     */
    public float rotation = 0;

    //references to objects in scene tree
    private GameObject parent = null;
    private GameObject firstChild = null;
    private GameObject nextSibling = null;
    private GameObject prevSibling = null;

    public GameObject getParent() {
        return parent;
    }

    public GameObject getFirstChild() {
        return firstChild;
    }

    public GameObject getNextSibling() {
        return nextSibling;
    }

    public GameObject getPrevSibling() {
        return prevSibling;
    }

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
     * calls destroy on all child objects
     */
    public final void destroy() {
        destroyChildren();

        onDestroy();
        detachFromParent();
    }

    /**
     * calls update on children in breadth first order
     */
    public final void updateChildren() {
        //update children
        for(GameObject child = firstChild; child!=null; child = firstChild.nextSibling)
            child.update();
        //update grandchildren
        for(GameObject child = firstChild; child!=null; child = firstChild.nextSibling)
            child.updateChildren();
    }

    /**
     * calls render() on children in breadth first order
     */
    public final void renderChildren(RenderSystem render) {
        //render children
        for(GameObject child = firstChild; child!=null; child = firstChild.nextSibling)
            child.render(render);
        //render grandchildren
        for(GameObject child = firstChild; child!=null; child = firstChild.nextSibling)
            child.renderChildren(render);
    }

    /**
     * called by the destroy method when
     */
    protected void onDestroy() { }

    /**
     * calls destroy on all children, order is depth first, but i guess it wont matter here
     */
    public final void destroyChildren() {
        for(GameObject child = firstChild; child!=null; child = firstChild.nextSibling)
            child.destroy();
    }

    /**
     * adds given child at the beginning of the child list
     * @param child, not null
     * @return this for chaining
     */
    public final GameObject addChild(GameObject child) {
        //clear current parent of child
        child.detachFromParent();

        //set parent
        child.parent = this;

        //update references
        child.nextSibling = firstChild;
        if(firstChild!=null)
            firstChild.prevSibling = child;
        firstChild = child;

        return this;
    }

    /**
     * removes the game object from the scene tree
     */
    private void detachFromParent() {
        if(parent == null)
            return;

        //update references
        if(prevSibling!=null)
            prevSibling.nextSibling = nextSibling;
        if(nextSibling!=null)
            nextSibling.prevSibling = prevSibling;
        if(parent.firstChild==this)
            parent.firstChild = nextSibling;
        parent = nextSibling = prevSibling = null;
    }

    /**
     * removes the game object from the scene tree and attaches it to the root
     */
    public final void clearParent() {
        //detach from scene tree
        detachFromParent();

        //add to root
        Game.get().getRoot().addChild(this);
    }

    /**
     * Calculate global position by accumulating all translations up to the root game object
     * @return global position
     */
    //TODO optimization: if this is used often, then this should be stored in the Gameobject and marked dirty for recalculation if a parent changes position
    public final Vec2 getGlobalPosition() {
        Vec2 global = position.copy();

        GameObject go = parent;
        while(parent!=null) {
            global.add(parent.position);
            go = go.parent;
        }

        return global;
    }

    /**
     * @return global rotation of this object
     */
    //TODO optimization: if this is used often, then this should be stored in the Gameobject and marked dirty for recalculation if a parent changes rotation
    public final float getGlobalRotation() {
        float global = rotation;

        GameObject go = parent;
        while(parent!=null) {
            global += parent.rotation;
            go = go.parent;
        }

        return global;
    }

    //maybe setParent method?
    //maybe removeChild method?
}
