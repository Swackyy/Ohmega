package com.swacky.ohmega.api.client.renderer;

import org.jspecify.annotations.NonNull;

/**
 * The base class for rendering accessories on entities, you shouldn't be using this directly, instead you'll want to use either:
 * <ul>
 *     <li>{@link IHumanoidAccessoryRenderer} for humanoid entities</li>
 *     <li>{@link ILivingAccessoryRenderer} for other generic living entities</li>
 * </ul>
 */
public interface IAccessoryRenderer<T extends AccessoryRenderContext<?, ?>> {
    void submit(@NonNull T context);
}
