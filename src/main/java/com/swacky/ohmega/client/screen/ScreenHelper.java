package com.swacky.ohmega.client.screen;

class ScreenHelper {
    /**
     * Runs if mouse is between points (rect)
     * @param x0 left x
     * @param x1 right x
     * @param y0 top y
     * @param y1 bottom y
     * @param mx mouse x
     * @param my mouse y
     * @param runnable the action to perform if the mouse is inside the rect
     */
    public static void runIfBetween(int x0, int x1, int y0, int y1, int mx, int my, Runnable runnable) {
        if(mx > x0 - 1 && mx < x1 + 1 && my > y0 - 1 && my < y1 + 1) {
            runnable.run();
        }
    }
}
