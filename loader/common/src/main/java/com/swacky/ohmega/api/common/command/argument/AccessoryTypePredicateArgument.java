package com.swacky.ohmega.api.common.command.argument;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.swacky.ohmega.api.common.accessorytype.AccessoryType;
import com.swacky.ohmega.api.common.accessorytype.AccessoryTypeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.IdentifierParseRule;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Simple command argument for {@code Predicate<AccessoryType>}, similar to {@link ItemPredicateArgument} but for {@link AccessoryType}s.
 * Supports parsing for syntax elements:
 * <ul>
 *     <li>Wildcard ({@code '*'})</li>
 *     <li>Element negation ({@code '!'})</li>
 *     <li>{@link AccessoryType} lookup via their {@link Identifier} keys</li>
 * </ul>
 */
public class AccessoryTypePredicateArgument extends ParserBasedArgument<AccessoryTypePredicateArgument.Result> {
    public static final @NonNull String KEY = "accessory_type_predicate";
    public static final @NonNull BooleanArgumentSerialiser<AccessoryTypePredicateArgument> SERIALISER = new BooleanArgumentSerialiser<>(
            AccessoryTypePredicateArgument::new, inst -> inst.referenceableOnly);
    private static final Grammar<Result> ANY_GRAMMAR = createGrammar(false);
    private static final Grammar<Result> REFERENCEABLE_GRAMMAR = createGrammar(true);
    private static final @NonNull List<String> EXAMPLES = List.of(
            "*",
            AccessoryType.NORMAL_ID.toString(),
            AccessoryType.UTILITY_ID.toString());

    private final boolean referenceableOnly;

    /**
     * Internal constructor, use {@link #any()} and {@link #referenceable()}
     * @param referenceableOnly controls whether we should allow parsing and validation for types marked with {@link AccessoryType#allowReference()}
     */
    private AccessoryTypePredicateArgument(boolean referenceableOnly) {
        Grammar<Result> grammar;

        if (referenceableOnly) {
            grammar = ANY_GRAMMAR;
        } else {
            grammar = REFERENCEABLE_GRAMMAR;
        }

        super(grammar.mapResult(predicates -> Util.allOf(predicates)::test));

        this.referenceableOnly = referenceableOnly;
    }

    /**
     * Creates the {@link Grammar} parsing instances, used internally to create {@code static} fields {@link #ANY_GRAMMAR} and {@link #REFERENCEABLE_GRAMMAR}
     * @param referenceableOnly controls whether we should allow parsing and validation for types marked with {@link AccessoryType#allowReference()}
     * @return grammar rules and parsing schematic for this instance
     */
    private static Grammar<Result> createGrammar(boolean referenceableOnly) {
        Dictionary<StringReader> rules = new Dictionary<>();

        Atom<Unit> wildcardAtom = Atom.of("wildcard");
        Atom<Result> negationAtom = Atom.of("negation");
        Atom<Result> elementAtom = Atom.of("element");

        NamedRule<StringReader, Identifier> idRule = rules.put(Atom.of("id"), IdentifierParseRule.INSTANCE);

        rules.put(elementAtom, new ResourceLookupRule<>(idRule, Unit.INSTANCE) {
            @Override
            public @Nullable Result parse(@NonNull ParseState<StringReader> state) {
                StringReader reader = state.input();

                if (reader.canRead() && Character.isWhitespace(reader.peek())) {
                    return null;
                }

                return super.parse(state);
            }

            @Override
            protected @NonNull Result validateElement(@NonNull ImmutableStringReader reader, @NonNull Identifier id) throws Exception {
                if (AccessoryTypeManager.exists(id)) {
                    AccessoryType type = AccessoryTypeManager.get(id);

                    if (!referenceableOnly || type.allowReference()) {
                        return type::equals;
                    }

                    throw AccessoryTypeArgument.EXCEPTION_UNSPECIFIABLE_TYPE.create(id);
                } else {
                    throw AccessoryTypeArgument.EXCEPTION_UNKNOWN_TYPE.create(id);
                }
            }

            @Override
            public @NonNull Stream<Identifier> possibleResources() {
                return AccessoryTypeManager.getTypeIdentifiers(referenceableOnly).stream();
            }
        });
        rules.put(wildcardAtom, StringReaderTerms.character('*'), _ -> Unit.INSTANCE);
        rules.put(negationAtom, Term.sequence(StringReaderTerms.character('!'), rules.named(elementAtom)), scope -> {
            Result type = scope.getOrThrow(elementAtom);

            return other -> !type.test(other);
        });

        return new Grammar<>(rules, rules.put(Atom.of("top"), Term.alternative(rules.named(wildcardAtom), rules.named(negationAtom), rules.named(elementAtom)), scope -> {
            if (scope.get(wildcardAtom) != null) {
                return _ -> true;
            }

            if (scope.get(negationAtom) != null) {
                return scope.getOrThrow(negationAtom);
            }

            return scope.getOrThrow(elementAtom);
        }));
    }

    /**
     * Creates a predicate argument for any {@link AccessoryType}s
     * @return a non-referenceable-asserting predicate argument instance
     */
    public static AccessoryTypePredicateArgument any() {
        return new AccessoryTypePredicateArgument(false);
    }

    /**
     * Creates a predicate argument for referenceable {@link AccessoryType}s
     * @return a referenceable-asserting predicate argument instance
     */
    public static AccessoryTypePredicateArgument referenceable() {
        return new AccessoryTypePredicateArgument(true);
    }

    /**
     * Retrieves the {@code Predicate<AccessoryType>} from the command via named lookup
     * @param context provided with {@link ArgumentBuilder#executes(Command)}
     * @param name key for the type predicate in the command
     * @return the type predicate present in the command, {@link NonNull} as it will simply throw if an instance with the given {@code name} is not found
     */
    public static @NonNull Predicate<AccessoryType> getTypePredicate(@NonNull CommandContext<CommandSourceStack> context, @NonNull String name) throws IllegalArgumentException {
        return context.getArgument(name, Result.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    /**
     * Wrapper for {@code Predicate<AccessoryType>} as to avoid polymorphism and allow it to have a unique {@code .class} instance
     */
    public interface Result extends Predicate<AccessoryType> {}
}
