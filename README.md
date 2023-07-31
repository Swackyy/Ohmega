# Ohmega
 A modernized and lightweight accessories mod for Minecraft

---
## For modders
To use this mod, add it as a dependency to your project through a curse maven. [Documentation can be found on how to use this here](https://www.cursemaven.com).

To use the mod's API to create an accessory item, simply let your item class implement `IAccessory` and finish the `getType()` method with the desired type of accessory. With this, you can override the default methods (blank) based on the desired outcome.

A lot of useful methods can be found in the `AccessoryHelper` class allowing for maximum control over what your items do and how they behave, read the Javadocs for each to gain understanding on how to utilise the functions within the utility class. 

### Events

Events may also be used to trigger an update, for example an accessory allowing the user to fly will be triggered upon wearing the accessory, but when switching between game-modes it may become disabled.

As a workaround, you can listen to the `PlayerEvent.PlayerChangeGameModeEvent` and when the player changes to a non-flying mode, you can run code to re-apply the flying effect using this line of code: `AccessoryHelper.updateIfPresent(player, (Item & IAccessory) ModItems.FLY_ACCESSORY.get())`.
This will run the `onUpdate()` method, also overridable in your accessory's class. This casting is only required to avoid crashes and misuse of the function.

You can of course do this without an event and have it be more versatile, such as the accessory still allowing for survival flight even after another cause for disabling flying, however this is an option.

---
## Other info
This mod does not add any accessories in itself (aside from the example), just the accessory functionality, capabilities to add and create them with ease and the additional segments of the inventory of which accessories are to be equipped.

Post issues [here](https://github.com/Swackyy/Ohmega/issues) and submit pull requests to the main branch if you feel as if something is needed.