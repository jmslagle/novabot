package com.github.novskey.novabot.parser;

import java.util.*;

public class Command
{
    int minArgs;
    int maxArgs;
    HashSet<ArgType> validArgTypes;
    private HashSet<ArgType> requiredArgTypes;
    final boolean allowDuplicateArgs;
    private Argument[] arguments;

    public Command() {
        this.validArgTypes = new HashSet<>();
        this.requiredArgTypes = new HashSet<>();
        this.allowDuplicateArgs = false;
    }

    public Command setValidArgTypes(final HashSet<ArgType> validArgTypes) {
        this.validArgTypes = validArgTypes;
        return this;
    }

    public Command setRequiredArgTypes(final HashSet<ArgType> requiredArgTypes) {
        this.requiredArgTypes = requiredArgTypes;
        return this;
    }

    public HashSet<ArgType> getValidArgTypes() {
        return this.validArgTypes;
    }

    public Command setArgRange(final int min, final int max) {
        this.minArgs = min;
        this.maxArgs = max;
        return this;
    }

    public HashSet<ArgType> getRequiredArgTypes() {
        return this.requiredArgTypes;
    }

    public boolean meetsRequirements(final Argument[] args) {
        final boolean meetsRequirements = true;
        for (final ArgType argType : this.requiredArgTypes) {
            boolean containsThisArg = false;
            for (final Argument arg : args) {
                if (arg.getType() == argType) {
                    containsThisArg = true;
                }
            }
            if (!containsThisArg) {
                return false;
            }
        }
        return true;
    }

    public int getMinArgs() {
        return this.minArgs;
    }

    public int getMaxArgs() {
        return this.maxArgs;
    }
}
