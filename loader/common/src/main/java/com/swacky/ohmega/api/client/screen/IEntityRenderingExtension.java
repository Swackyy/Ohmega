package com.swacky.ohmega.api.client.screen;

/**
 * Implement this on your {@link AccessoryScreenExtension} if you want to be able to render a flippable entity in it.
 * Define a {@code boolean} in your class to hold the state of being flipped or not and reference it in the defined methods below
 * @apiNote This will only be relevant when the active screen implements {@link IEntityRenderingScreen}
 */
public interface IEntityRenderingExtension {
    /**
     * Check if the entity should be flipped on the x-axis
     * @return {@code true} if the entity rendered should be flipped, {@code false} otherwise
     */
    boolean isEntityFlipped();

    /**
     * Set the entity to be flipped or not
     * @param value {@code true} to flip the entity, {@code false} otherwise
     */
    void setFlipEntity(boolean value);

    /**
     * A helper method to toggle the entity flipping to the opposite of the current state
     */
    default void toggleFlipEntity() {
        setFlipEntity(!isEntityFlipped());
    }
}
