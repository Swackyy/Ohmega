package com.swacky.ohmega.datagen.client;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

/**
 * Why not have some fun, eh?
 * <p>
 * Adds custom splashes but merges the output with the vanilla one to prevent overriding it completely
 */
public class OhmegaSplashProvider implements DataProvider {
    private final FabricPackOutput output;
    private final Set<String> data = new TreeSet<>();

    public OhmegaSplashProvider(FabricPackOutput output) {
        this.output = output;
    }

    private void add(String string) {
        data.add(string);
    }

    private void addSplashes() {
        add("All hail " + ChatFormatting.BOLD + "Zero Two");
        add("All hail " + ChatFormatting.BOLD + "Mai Sakurajima");
        add("All hail " + ChatFormatting.BOLD + "Mashiro Shiina");
        add("Ohmega alone is the " + ChatFormatting.GOLD + "honoured one");
        add("Ohmega NOT vibecoded");
        add("Same brainlength fr");
        add("gingle");
        add("Multi-loader shenanigans");
        add("A proud creation of Duk Developments");
        add("if (bugs) fixBugs();");
        add("Java has the best lambda syntax");
        add("Jump up, kick back, whip around and spin");
    }

    @Override
    public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
        addSplashes();

        if (!data.isEmpty()) {
            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(Identifier.DEFAULT_NAMESPACE);
            Path path = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                    .resolve(Identifier.DEFAULT_NAMESPACE)
                    .resolve("texts")
                    .resolve("splashes.txt");

            return CompletableFuture.runAsync(() -> {
                if (container.isPresent()) {
                    Optional<Path> vanilla = container.get().findPath("assets/" + Identifier.DEFAULT_NAMESPACE + "/texts/splashes.txt");

                    if (vanilla.isPresent()) {
                        try {
                            BufferedReader reader = Files.newBufferedReader(vanilla.get());

                            while (reader.ready()) {
                                data.add(reader.readLine());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Could not merge vanilla splashes.txt file", e);
                        }
                    }
                }

                StringBuilder builder = new StringBuilder();

                data.forEach(entry -> builder.append(entry).append('\n'));

                try {
                    Files.createDirectories(path.getParent());
                    Files.writeString(path, builder.toString());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to write splashes.txt to file for namespace '" + output.getModId() + '\'', e);
                }
            });
        }

        return CompletableFuture.allOf();
    }

    @Override
    public @NonNull String getName() {
        return "Splashes for namespace '" + output.getModId() + '\'';
    }
}