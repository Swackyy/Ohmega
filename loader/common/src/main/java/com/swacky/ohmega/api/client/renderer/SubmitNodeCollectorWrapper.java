package com.swacky.ohmega.api.client.renderer;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;

/**
 * A simple wrapper for {@link SubmitNodeCollector} to automatically order rendering draws.
 * This is done to enforce accessories equipped with a higher index to have higher rendering priority and to avoid Z-fighting
 */
public final class SubmitNodeCollectorWrapper {
    private final SubmitNodeCollector collector;
    private int order = 1; // Begin at 1 instead of 0 to avoid vanilla rendering

    public SubmitNodeCollectorWrapper(SubmitNodeCollector collector) {
        this.collector = collector;
    }

    /**
     * Certain functions may only allow for passing in an unordered {@link SubmitNodeCollector}
     * If you do not have good reason for doing this, please avoid it and use {@link #next()} instead
     * @return the held {@link SubmitNodeCollector} this class is wrapping
     */
    public SubmitNodeCollector unwrap() {
        return collector;
    }

    /**
     * Use this to get an {@link OrderedSubmitNodeCollector} to submit draw calls to
     * @return a new {@link SubmitNodeCollector} with the next order
     */
    public OrderedSubmitNodeCollector next() {
        return collector.order(order++);
    }

    /**
     * An unwrapped version for if manual intervention is needed.
     * If you do not have good reason for doing this, please avoid it and use {@link #next()} instead
     * @param order order to manually use
     * @return a new {@link SubmitNodeCollector} with the specified order
     */
    public OrderedSubmitNodeCollector order(int order) {
        return collector.order(order);
    }
}
