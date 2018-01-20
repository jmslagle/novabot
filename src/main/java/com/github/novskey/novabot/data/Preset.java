package com.github.novskey.novabot.data;

import com.github.novskey.novabot.core.Location;

/**
 * Created by Paris on 17/01/2018.
 */
public class Preset {

    public String presetName;
    public Location location;

    public Preset(String preset, Location location) {
        this.presetName = preset;
        this.location = location;
    }

    @Override
    public int hashCode() {
        return presetName.hashCode() *
                (location == null ? 1 : location.toDbString().toLowerCase().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())) return false;
        Preset preset = (Preset) obj;
        return preset.presetName.equals(this.presetName) && preset.location.equals(this.location);
    }
}
