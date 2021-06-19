package io.itch.jaknak72.oozed.game.physics;

import java.util.ArrayList;

import io.itch.jaknak72.oozed.game.Game;
import io.itch.jaknak72.oozed.game.objects.AABB;
import io.itch.jaknak72.oozed.utils.BitUtils;
import io.itch.jaknak72.oozed.utils.ObjectPool;
import io.itch.jaknak72.oozed.utils.Subject;
import io.itch.jaknak72.oozed.utils.Utils;
import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Manages all Bounding boxes, checks collisions between them
 * offers methods for collision checks, raycasts and sweeps
 * @author simon
 */
public class PhysicsSystem {

    private static final String TAG = "PhysicsSystem";

    //events
    public final Subject<Contact> onGlobalCollision = new Subject<>();

    //Possible optimization: unordered array list
    private final ArrayList<AABB>[] aabbByLayer = new ArrayList[16];//16 because collision layer is a short

    //Contact management, to reduce frequent allocations and prevent unnecessary garbage collecting
    private final ObjectPool<Contact> contactPool = new ObjectPool<>(Contact::new);
    /**
     * Contacts that are generated during collision events and used outside the physics system,
     * these get cleared at the beginning of each physics step
     */
    private final ArrayList<Contact> usedContacts = new ArrayList<Contact>(64);//TODO find good initial capacity
    //sweep object management, same as contacts
    private final ObjectPool<Sweep> sweepPool = new ObjectPool<>(Sweep::new);
    /**
     * Sweeps that are returned when calling a public sweep function, these get cleared at the beginning of each physics step
     */
    private final ArrayList<Sweep> usedSweeps = new ArrayList<>(16);//TODO find good initial capacity

    /**
     * Called once by game
     */
    public void init() {
        //instantiate array lists of aabbs
        for (int i = 0; i < aabbByLayer.length; ++i) {
            aabbByLayer[i] = new ArrayList<AABB>(32);//TODO find good initial capacity
        }
    }

    /**
     * Adds given box to the physics system
     *
     * @param box, !=null
     */
    public void addAABB(AABB box) {
        if(box.getCollisionLayer()==0) {
            //Log.w(TAG,String.format("No layer set for aabb %s, it will be ignored",box.toString()));
            return;
        }
        aabbByLayer[BitUtils.getRightmostSetBit(box.getCollisionLayer())-1].add(box);
    }

    /**
     * removes given box from the physics system
     *
     * @param box, !=null
     */
    public void removeAABB(AABB box) {
        if(box.getCollisionLayer()==0)
            return;

        aabbByLayer[BitUtils.getRightmostSetBit(box.getCollisionLayer())-1].remove(box);
    }

    /**
     * Should be called when the collision mask or layer changes
     *
     * @param box, !=null
     */
    public void updateAABB(AABB box) {
        removeAABB(box);
        addAABB(box);
    }

    public void update() {
        //reset and clear all contact and sweep objects
        contactPool.freeAll(usedContacts);
        usedContacts.clear();
        sweepPool.freeAll(usedSweeps);
        usedSweeps.clear();

        //calculate collisions
        for (short layer = 1, li = 0; li < CollisionLayers.MAX_LAYERS; ++li, layer = (short) (1 << li)) {

            ArrayList<AABB> aabbs = aabbByLayer[li];
            for (int i = 0; i < aabbs.size(); ++i) {
                AABB a = aabbs.get(i);

                //check which layers should collisions be tested
                for (short alayer = 1, ali = 0; ali < CollisionLayers.MAX_LAYERS; ++ali, alayer = (short) (1 << ali)) {
                    if ((alayer & a.getCollisionMask()) == 0)
                        continue;

                    //check collisions against other aabbs
                    ArrayList<AABB> others = aabbByLayer[ali];
                    for (int j = 0; j < others.size(); ++j) {
                        //prevent self check
                        if (layer == alayer && i == j)
                            continue;

                        AABB b = others.get(j);
                        //prevent checks that have already be done
                        if ((alayer < layer || (alayer == layer && j < i)) && (b.getCollisionMask() & a.getCollisionLayer()) != 0)
                            continue;

                        //test collision
                        Contact contact = testCollision(a, b);
                        if (contact != null) {
                            //generate inverted contact
                            Contact inverted = contactPool.obtain();
                            contact.makeInvertContact(inverted);

                            //register contacts as used before broadcasting out of the system
                            usedContacts.add(contact);
                            usedContacts.add(inverted);

                            //emit collision events
                            a.onCollide.notify(contact);
                            b.onCollide.notify(inverted);
                            onGlobalCollision.notify(contact);
                        }
                    }
                }
            }
        }
    }


    /**
     * Tests whether or not the two given aabbs collide with each other and generates a contact object in that case
     * inspired by:
     * https://noonat.github.io/intersect/
     * https://gamedev.docrobs.co.uk/first-steps-in-pico-8-hitting-things
     *
     * @param a, !=null
     * @param b, !=null
     * @return null, if there is no collision, a contact object obtained from the contact pool from the view of a
     */
    private Contact testCollision(AABB a, AABB b) {
        Vec2 globalA = a.getGlobalPosition();
        Vec2 globalB = b.getGlobalPosition();

        //check if distance between midpoints is smaller than sum of half widths
        float dx = globalA.x - globalB.x;
        float px = a.getHalfSize().x + b.getHalfSize().x - Math.abs(dx);
        if (px <= 0)
            return null;

        //check if vertical distance between midpoints is smaller than sum of half heights
        float dy = globalA.y - globalB.y;
        float py = a.getHalfSize().y + b.getHalfSize().y - Math.abs(dy);
        if (py <= 0)
            return null;

        //generate contact information
        //contact is assumed to be in reset state
        Contact contact = contactPool.obtain();
        contact.box = a;
        contact.other = b;
        //check which axis has the least overlap
        if (px < py) {
            int sx = (int) Math.signum(dx);
            contact.overlap.x = px * sx;
            contact.normal.x = sx;
            contact.position.x = globalA.x + a.getHalfSize().x * sx;
            contact.position.y = (globalA.y + globalB.y) * 0.5f;
        } else {
            int sy = (int) Math.signum(dy);
            contact.overlap.y = py * sy;
            contact.normal.y = sy;
            //halfway between positions is used as estimate
            contact.position.x = (globalA.x + globalB.x) * 0.5f;
            contact.position.y = globalA.y + a.getHalfSize().y * sy;
        }

        return contact;
    }

    /**
     * Performs a raycast against all objects specified by given mask and returns the first collision
     *
     * @param position origin of ray
     * @param ray      describes length and direction of ray
     * @param mask
     * @return the first contact or null if there was no contact
     */
    public Contact raycast(Vec2 position, Vec2 ray, short mask) {

        Contact closest = null;

        //iterate over interesting layers
        for (short layer = 1, li = 0; li < CollisionLayers.MAX_LAYERS; ++li, layer = (short) (1 << li)) {
            if ((layer & mask) == 0)
                continue;

            //iterate over all aabbs in layer
            ArrayList<AABB> others = aabbByLayer[li];
            for (int i = 0; i < others.size(); ++i) {
                AABB other = others.get(i);

                //perform raycast and take contact, if it is closer than current closest contact
                Contact raycast = raycast(position, ray, other, 0,0);
                if (raycast!=null && (closest==null || raycast.time<closest.time)) {
                    if(closest!=null)
                        contactPool.free(closest);
                    closest = raycast;
                }
            }
        }

        //register returned contact as used
        if(closest!=null)
            usedContacts.add(closest);

        return closest;
    }


    /**
     * Performs a raycast against given aabb with given padding
     *
     * @param position, origin of ray
     * @param ray,      describes length and direction of ray
     * @param aabb,     !=null
     * @param paddingX
     * @param paddingY
     * @return null if there is no collision, otherwise a contact object with contact.box set to null, contact.other=aabb, the contact is not marked as used
     */
    private Contact raycast(Vec2 position, Vec2 ray, AABB aabb, float paddingX, float paddingY) {
        Vec2 globalPos = aabb.getGlobalPosition();
        //check 0 cases
        if (ray.x == 0) {
            if (position.x <= (globalPos.x - aabb.getHalfSize().x - paddingX) ||
                    position.x >= (globalPos.x + aabb.getHalfSize().x + paddingX))
                return null;
            //reduce to 1d
            float time = raycast1D(position.y, ray.y, globalPos.y, aabb.getHalfSize().y, paddingY);
            if (time >= 1)
                return null;

            //generate contact
            Contact contact = contactPool.obtain();
            contact.other = aabb;
            contact.time = time;
            contact.normal.set(0, -Math.signum(ray.y));
            contact.overlap.set(ray).scl(-(1 - contact.time));
            contact.position.set(ray).scl(contact.time).add(position);
            return contact;
        } else if (ray.y == 0) {
            if (position.y <= (globalPos.y - aabb.getHalfSize().y - paddingY) ||
                    position.y >= (globalPos.y + aabb.getHalfSize().y + paddingY))
                return null;
            //reduce to 1d
            float time = raycast1D(position.x, ray.x, globalPos.x, aabb.getHalfSize().x, paddingX);
            if (time >= 1)
                return null;

            //generate contact
            Contact contact = contactPool.obtain();
            contact.other = aabb;
            contact.time = time;
            contact.normal.set(-Math.signum(ray.x), 0);
            contact.overlap.set(ray).scl(-(1 - contact.time));
            contact.position.set(ray).scl(contact.time).add(position);
            return contact;
        }

        //calculate intersections with the infinitely long axes of the edges and ray
        Vec2 scale = Game.get().tmpVec().set(1 / ray.x, 1 / ray.y);
        Vec2 sign = Game.get().tmpVec().set(scale).sign();
        Vec2 edgePos = Game.get().tmpVec().set(aabb.getHalfSize()).add(paddingX, paddingY).scl(sign);
        //if ray.x>0 then left edge of aabb is near right is far
        //if ray.x<0 then right edge=near, left=far
        //vice versa for vertical
        Vec2 near = Game.get().tmpVec().set(globalPos).sub(edgePos).sub(position).scl(scale);
        Vec2 far = Game.get().tmpVec().set(globalPos).add(edgePos).sub(position).scl(scale);

        //if intersection with near edge if further than intersection with opposite far edge -> no collision
        if (near.x > far.y || near.y > far.x)
            return null;

        //only bigger near and smaller far is relevant
        float nearMax = Math.max(near.x, near.y);
        float farMin = Math.min(far.x, far.y);

        //no collision if ray ends before colliding or starts after box
        if (nearMax >= 1 || farMin <= 0)
            return null;

        //there is a collision
        Contact contact = contactPool.obtain();
        contact.other = aabb;
        contact.time = Utils.clamp(nearMax, 0, 1);

        if (near.x > near.y)
            contact.normal.set(-sign.x, 0);
        else
            contact.normal.set(0, -sign.y);
        contact.overlap.set(ray).scl(-(1 - contact.time));
        contact.position.set(ray).scl(contact.time).add(position);

        return contact;
    }

    /**
     * @return >1 for no collision, otherwise 0-1 to describe the time of the collision
     */
    private float raycast1D(float pos, float ray, float aabbPos, float aabbHalfSize, float padding) {
        float scale = 1 / ray;
        float sign = Math.signum(scale);
        float edgeOffset = sign * (aabbHalfSize + padding);
        float near = (aabbPos - edgeOffset - pos) * scale;
        float far = (aabbPos + edgeOffset - pos) * scale;

        if (near >= 1 || far <= 0)
            return 2;
        else
            return Utils.clamp(near, 0, 1);
    }

    /**
     * Performs a sweep against all other aabbs, that are in the collision mask of the given aabb
     */
    public Sweep move(AABB aabb, Vec2 move) {
        return move(aabb, move, aabb.getCollisionMask());
    }

    /**
     * Performs a sweep check against all objects of given layers (mask)
     *
     * @return sweep, registered as used by the physics system
     */
    public Sweep move(AABB aabb, Vec2 move, short mask) {
        Vec2 aabbPos = aabb.getGlobalPosition();

        Sweep closest = sweepPool.obtain();
        closest.time = 1;
        closest.position.set(aabbPos).add(move);

        //iterate over interesting layers
        for (short layer = 1, li = 0; li < CollisionLayers.MAX_LAYERS; ++li, layer = (short) (1 << li)) {
            if ((layer & mask) == 0)
                continue;

            //iterate over all aabbs in layer
            ArrayList<AABB> others = aabbByLayer[li];
            for (int i = 0; i < others.size(); ++i) {
                AABB other = others.get(i);
                //prevent self check
                if (aabb == other)
                    continue;

                Sweep sweep = move(aabb, other, move);
                if (sweep.time < closest.time) {
                    sweepPool.free(closest);
                    closest = sweep;
                }
            }
        }

        //register returned sweep as used
        usedSweeps.add(closest);

        return closest;
    }

    /**
     * Performs a sweep of aabb against other
     *
     * @param move, describes move of aabb, !=null
     * @return sweep, not registered as used
     */
    private Sweep move(AABB aabb, AABB other, Vec2 move) {
        Vec2 aabbPos = aabb.getGlobalPosition();
        Vec2 otherPos = other.getGlobalPosition();

        Sweep sweep = sweepPool.obtain();
        //check if it simply does not move
        if (move.len2() == 0) {
            //perform simple intersection
            sweep.position.set(aabbPos);
            sweep.contact = testCollision(aabb, other);
            sweep.time = sweep.contact != null ? (sweep.contact.time = 0) : 1;
            return sweep;
        }

        //blow up other box by size of aabb and perform a raycast
        sweep.contact = raycast(aabbPos, move, other, aabb.getHalfSize().x, aabb.getHalfSize().y);
        if (sweep.contact != null) {
            sweep.contact.box = aabb;
            sweep.time = Utils.clamp(sweep.contact.time - Utils.EPSILON, 0, 1);
            sweep.position.set(move).scl(sweep.time).add(aabbPos);

            Vec2 dir = Game.get().tmpVec().set(move).norm();
            //undo the blow up
            sweep.contact.position.x = Utils.clamp(sweep.contact.position.x + dir.x * aabb.getHalfSize().x,
                    otherPos.x - other.getHalfSize().x, otherPos.x + other.getHalfSize().x);
            sweep.contact.position.y = Utils.clamp(sweep.contact.position.y + dir.y * aabb.getHalfSize().y,
                    otherPos.y - other.getHalfSize().y, otherPos.y + other.getHalfSize().y);
        } else {
            sweep.position.set(aabbPos).add(move);
            sweep.time = 1;
        }
        return sweep;
    }

    /**
     * Contact object describing a collision between two objects
     * Contact objects are only valid for 1 frame, since they get reused
     */
    public static class Contact implements ObjectPool.Poolable {
        private AABB box;
        private AABB other;
        private final Vec2 position = new Vec2(0, 0);
        /**
         * this i always seen from the surface of other
         */
        private final Vec2 normal = new Vec2(0, 0);
        private final Vec2 overlap = new Vec2(0, 0);
        private float time = 1;

        @Override
        public void reset() {
            box = null;
            other = null;
            position.set(0, 0);
            normal.set(0, 0);
            overlap.set(0, 0);
            time = 1;
        }

        /**
         * sets the fields of the given contact to reflect the same contact from the view of the other aabb
         *
         * @param inverted, !=null
         */
        public void makeInvertContact(Contact inverted) {
            inverted.box = other;
            inverted.other = box;
            inverted.position.set(position);
            inverted.normal.set(normal).inv();
            inverted.overlap.set(overlap).inv();
        }

        /**
         * may be null if this contact was generated during a raycast or other collision test that does not involve 2 aabbs
         */
        public AABB getBox() {
            return box;
        }

        public AABB getOther() {
            return other;
        }

        /**
         * @return closest position of contact
         */
        public Vec2 getPosition() {
            return position;
        }

        public Vec2 getNormal() {
            return normal;
        }

        /**
         * @return the overlap of the contact, can be used  on box.pos to resolve the collision
         */
        public Vec2 getOverlap() {
            return overlap;
        }

        /**
         * set between 0 and 1 for raycasts and sweeps, 1 otherwise
         */
        public float getTime() {
            return time;
        }
    }

    /**
     * Represents a sweep result from a sweep test
     * Sweep objects are only valid for 1 frame since they get reused
     */
    public static class Sweep implements ObjectPool.Poolable {
        /**
         * If the object has been colliding with something during the sweep, this field !=null
         */
        private Contact contact;
        /**
         * Furthest position until colliding with an object
         */
        private Vec2 position = new Vec2(0, 0);
        /**
         * Describes when on the sweep has the contact happened (in %)
         * 1 if no contact has happened
         */
        private float time = 1;

        @Override
        public void reset() {
            contact = null;
            position.set(0, 0);
            time = 1;
        }

        public Contact getContact() {
            return contact;
        }

        public Vec2 getPosition() {
            return position;
        }

        public float getTime() {
            return time;
        }
    }
}
