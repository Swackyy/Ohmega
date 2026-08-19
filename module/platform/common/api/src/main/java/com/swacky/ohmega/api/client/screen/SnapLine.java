package com.swacky.ohmega.api.client.screen;

import com.swacky.ohmega.api.config.OhmegaConfig;

/**
 * Represents a line where magnetics should try to snap onto
 * @param vertical {@code true} if this is a vertical line, {@code false} if horizontal
 * @param value the {@code x} or {@code y} value (depends on {@code vertical}) for where the line is
 */
public record SnapLine(boolean vertical, int value) {
    /**
     * Test this line for snapping
     * @param testValue the position of the test
     * @param delta the delta for the test, typically the width or height (depends on {@link #vertical})
     * @return the new position for the value passed, or {@code -1} if the snapping failed for this line
     */
    public int test(int testValue, int delta) {
        int startDistance = Math.abs(testValue - value);
        int maximumDistance = OhmegaConfig.Client.getData().magneticsStrength().get();
        int closestDistance = Integer.MAX_VALUE;
        int returnValue = -1;
        int centreDistance = Math.abs(testValue + delta / 2 - value);
        int endDistance = Math.abs(testValue + delta - value);

        if (startDistance <= maximumDistance) {
            closestDistance = startDistance;
            returnValue = value + 1;
        }

        if (centreDistance <= maximumDistance && centreDistance < closestDistance) {
            closestDistance = centreDistance;
            returnValue = value - delta / 2;
        }

        if (endDistance <= maximumDistance && endDistance < closestDistance) {
            returnValue = value - delta;
        }

        return returnValue;
    }
}
