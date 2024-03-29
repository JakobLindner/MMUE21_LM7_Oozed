package io.itch.jaknak72.oozed.utils;

/**
 * Event listener interface that can be registered at matching Subject classes
 * @author simon
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
