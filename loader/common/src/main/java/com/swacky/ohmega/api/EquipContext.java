package com.swacky.ohmega.api;

/**
 * Context for when an accessory is equipped, provided for posted AccessoryEquip events/callbacks
 */
public enum EquipContext {
    GENERIC,
    SLOT_PLACE,
    RIGHT_CLICK_HELD_ITEM
}
