package nests;

import core.*;

public class NestSearch
{
    private final Pokemon[] pokemon;
    private final NestStatus[] statuses;

    public NestSearch(final Pokemon[] pokemon, final NestStatus[] statuses) {
        this.pokemon = pokemon;
        this.statuses = statuses;
    }

    public Pokemon[] getPokemon() {
        return this.pokemon;
    }

    public NestStatus[] getStatuses() {
        return this.statuses;
    }
}
