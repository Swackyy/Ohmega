package com.swacky.ohmega.common.command;

import com.swacky.ohmega.common.Ohmega;

import java.util.ArrayList;
import java.util.List;

public final class MessageHelper {
    public static final String CONTEXT_HOVER_KEY = context("hover");

    private static Builder builder() {
        Builder builder = new Builder();

        builder.add(Ohmega.MODID);
        builder.add("command");
        return builder;
    }

    public static String context(String suffix) {
        return builder().add("context").add(suffix).toString();
    }

    public static String exception(String suffix) {
        return builder().add("exception").add(suffix).toString();
    }

    public static Builder command(String command) {
        return builder().add(command);
    }

    public static final class Builder {
        private final List<String> components = new ArrayList<>(2);

        public Builder add(String component) {
            components.add(component);
            return this;
        }

        public String feedback(String suffix) {
            return add("feedback").add(suffix).toString();
        }

        public String feedback() {
            return add("feedback").toString();
        }

        public String exception(String suffix) {
            return add("exception").add(suffix).toString();
        }

        public String exception() {
            return add("exception").toString();
        }

        @Override
        public String toString() {
            return String.join(".", components);
        }
    }
}