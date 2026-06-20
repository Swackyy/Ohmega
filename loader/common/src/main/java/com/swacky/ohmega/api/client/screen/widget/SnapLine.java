package com.swacky.ohmega.api.client.screen.widget;

public record SnapLine(boolean vertical, int value) {
    public int test(int testValue, int delta) {
        int startDistance = Math.abs(testValue - value);
        int centreDistance = Math.abs(testValue + delta / 2 - value);
        int endDistance = Math.abs(testValue + delta - value);
        int closestDistance = Integer.MAX_VALUE;
        int returnValue = -1;

        if (startDistance < 6) {
            closestDistance = startDistance;
            returnValue = value;
        }

        if (centreDistance < 6 && centreDistance < closestDistance) {
            closestDistance = centreDistance;
            returnValue = value - delta / 2;
        }

        if (endDistance < 6 && endDistance < closestDistance) {
            returnValue = value - delta;
        }

        return returnValue;
    }
}
