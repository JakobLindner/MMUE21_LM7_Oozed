package at.ac.tuwien.mmue_lm7.utils;

/**
 * Event listener interface
 */
@FunctionalInterface
public interface Listener<Event> {
    /**
     * Handles given event
     * @param event
     * @return true if the event is considered "handled" or filtered, then subsequent listeners are not notified
     */
    boolean handle(Event event);
}
