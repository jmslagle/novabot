package core;

class ReporterChannel
{
    private final Region region;
    final String discordName;

    public ReporterChannel(final Region region, final String discordName) {
        this.region = region;
        this.discordName = discordName;
    }
}
