package io.itch.jaknak72.oozed.game.objects;

import androidx.annotation.CallSuper;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.rendering.Layers;
import io.itch.jaknak72.oozed.game.rendering.RenderSystem;
import io.itch.jaknak72.oozed.utils.Subject;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Base class for every type of object in the game
 *
 * @author simon & jakob
 */
public class GameObject {
    /**
     * local position of object
     */
    public Vec2 position = new Vec2();
    /**
     * local rotation in degrees, clockwise
     */
    public float rotation = 0;
    /**
     * local x axis mirroring of the object
     */
    public boolean mirrored = false;

    //references to objects in scene tree
    private GameObject parent = null;
    private GameObject firstChild = null;
    private GameObject nextSibling = null;
    private GameObject prevSibling = null;

    /**
     * whether or not this game object has been initialized
     */
    private boolean initialized = false;

    /**
     * If this is set to true then this gameobject is registered at the wraparoundsystem
     */
    private boolean wrappable = false;

    /**
     * true when destroyed has been called
     */
    private boolean destroyed = false;

    /**
     * The gameplay and rendering layer this object belongs to
     */
    public short layer = Layers.DEFAULT;

    ///////////////////////////////////////////////////////////////////////////
    // EVENTS
    ///////////////////////////////////////////////////////////////////////////
    //passes this when kill() is called
    protected final Subject<GameObject> onKilled = new Subject<>();

    //##################################
    //###   METHODS FOR OVERWRITING  ###
    //##################################

    /**
     * Called when first added to scene tree, can be overwritten by subclasses
     * can be used to set up game object, register callback functions, ...
     * super.init() should be called
     */
    @CallSuper
    public void init() {
        if (!initialized && wrappable) {
            Game.get().getWraparoundSystem().registerWrappable(this);
        }

        //used for root game object
        initialized = true;
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
     * renders debug information if enabled, can be overwritten by subclass
     */
    public void debugRender(RenderSystem render) {

    }

    /**
     * called by the destroy method
     * super.onDestroy() should be called
     */
    @CallSuper
    protected void onDestroy() {
        if (wrappable) {
            Game.get().getWraparoundSystem().removeWrappable(this);
        }
    }

    /**
     * called by the wraparound system whenever this go has been wrapped around screen
     */
    @CallSuper
    public void onWrap(Vec2 translation) {

    }

    /**
     * used for gameplay purposes, can be overwritten to make effects or custom behaviour
     * calls destroy()
     */
    @CallSuper
    public void kill() {
        onKilled.notify(this);
        destroy();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    //###############################
    //#####  OTHER METHODS  #########
    //###############################

    /**
     * Removes the game object from the scene tree
     * calls destroy on all child objects
     */
    //TODO mark as destroyed here, destroyed = true, register at game, game must cleanup marked entities on end of every update
    public final void destroy() {
        destroyed = true;
        destroyChildren();
        onDestroy();

        Game.get().markForRemoval(this);
    }

    /**
     * Recursively initializes children in depth first order
     */
    private void initChildren() {
        for (GameObject child = firstChild; child != null; child = child.nextSibling) {
            if (!child.initialized) {
                child.init();
                child.initChildren();
            }
        }
    }

    /**
     * calls update on children in depth first order
     */
    public final void updateChildren() {
        for (GameObject child = firstChild; child != null; child = child.nextSibling) {
            child.update();
            child.updateChildren();
        }
    }

    /**
     * calls render() on children in depth first order
     */
    public final void renderChildren(RenderSystem render) {
        for (GameObject child = firstChild; child != null; child = child.nextSibling) {
            child.render(render);
            child.renderChildren(render);
        }
    }

    /**
     * calls debugRender() on children in depth first order
     *
     * @param render
     */
    public final void debugRenderChildren(RenderSystem render) {
        for (GameObject child = firstChild; child != null; child = child.nextSibling) {
            child.debugRender(render);
            child.debugRenderChildren(render);
        }
    }

    /**
     * calls destroy on all children, order is depth first, but i guess it wont matter here
     */
    public final void destroyChildren() {
        for (GameObject child = firstChild; child != null; child = child.nextSibling)
            child.destroy();
    }

    /**
     * adds given child at the beginning of the child list
     *
     * @param child, not null
     * @return this for chaining
     */
    public final GameObject addChild(GameObject child) {
        //initialize child, if this game object is already part of the scene tree
        if (initialized && !child.initialized) {
            child.init();
            child.initChildren();
        }

        //clear current parent of child
        child.detachFromParent();

        //set parent
        child.parent = this;

        //update references
        child.nextSibling = firstChild;
        if (firstChild != null)
            firstChild.prevSibling = child;
        firstChild = child;

        return this;
    }

    /**
     * removes the game object from the scene tree
     */
    public void detachFromParent() {
        if (parent == null)
            return;

        //update references
        if (prevSibling != null)
            prevSibling.nextSibling = nextSibling;
        if (nextSibling != null)
            nextSibling.prevSibling = prevSibling;
        if (parent.firstChild == this)
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
     *
     * @return global position as a temporary vector!!
     */
    //TODO optimization: if this is used often, then this should be stored in the Gameobject and marked dirty for recalculation if a parent changes position
    public final Vec2 getGlobalPosition() {
        Vec2 global = Game.get().tmpVec();
        getGlobalPosition(global);
        return global;
    }

    /**
     * Sets given vector to global position
     *
     * @param vec
     */
    public final void getGlobalPosition(Vec2 vec) {
        vec.set(position);

        GameObject go = parent;
        while (go != null) {
            vec.add(go.position);
            go = go.parent;
        }
    }

    /**
     * Changes the local position to match given global pos
     *
     * @param globalPos !=null is unchanged
     */
    public final void setGlobalPosition(Vec2 globalPos) {
        Vec2 diff = getGlobalPosition().sub(globalPos).inv();//TODO pool vectors
        position.add(diff);
    }

    /**
     * @return global rotation of this object
     */
    //TODO optimization: if this is used often, then this should be stored in the Gameobject and marked dirty for recalculation if a parent changes rotation
    public final float getGlobalRotation() {
        float global = rotation;

        GameObject go = parent;
        while (go != null) {
            global += go.rotation;
            go = go.parent;
        }

        return global;
    }

    /**
     * @return global mirroring of this object
     */
    //TODO optimization: if this is used often, then this should be stored in the Gameobject and marked dirty for recalculation if a parent changes position
    public final boolean getGlobalMirroring() {
        boolean global = mirrored;

        GameObject go = parent;
        while (go != null) {
            global = go.mirrored != global;
            go = go.parent;
        }

        return global;
    }

    /**
     * Changes whether or not the game object should wrap around screen
     *
     * @param wrappable
     */
    public void setWrappable(boolean wrappable) {
        if (initialized) {
            if (!this.wrappable && wrappable)
                Game.get().getWraparoundSystem().registerWrappable(this);
            if (this.wrappable && !wrappable)
                Game.get().getWraparoundSystem().removeWrappable(this);
        }

        this.wrappable = wrappable;
    }

    /**
     * sets the layer for this subtree in a recursive manner
     *
     * @param layer
     */
    public void setLayerRecursive(short layer) {
        this.layer = layer;
        for (GameObject child = firstChild; child != null; child = child.nextSibling) {
            child.setLayerRecursive(layer);
        }
    }

    //maybe setParent method?
    //maybe removeChild method?
}
