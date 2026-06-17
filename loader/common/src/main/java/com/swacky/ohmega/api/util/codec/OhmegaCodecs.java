package com.swacky.ohmega.api.util.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.HexFormat;

/**
 * Contains some general use {@link Codec}s that Ohmega may use internally,
 * but is placed in the {@code api} package in case they may be otherwise useful
 */
public class OhmegaCodecs {
    /**
     * Codec for more broad integer colour parsing, encoding as a string but decoding as an integer
     * This supports both integer literals and formats detailed in {@link Integer#decode(String)}
     */
    public static final Codec<Integer> COLOUR_INT = Codec.either(
            Codec.INT,
            Codec.STRING.comapFlatMap(
                    string -> {
                        try {
                            return DataResult.success(Integer.decode(string));
                        } catch (NumberFormatException e) {
                            return DataResult.error(() -> "Could not decode colour from string '" + string + "', detail: " + e.getMessage());
                        }
                    }, integer -> "0x" + HexFormat.of().toHexDigits(integer).substring(2))
    ).xmap(either -> either.map(
            value -> value,
            value -> value
    ), Either::right);
}
