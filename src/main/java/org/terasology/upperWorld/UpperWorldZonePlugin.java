package org.terasology.upperWorld;

import org.terasology.world.generator.plugin.RegisterPlugin;
import org.terasology.world.zones.LayeredZoneRegionFunction;
import org.terasology.world.zones.MinMaxLayerThickness;
import org.terasology.world.zones.ZonePlugin;

@RegisterPlugin
public class UpperWorldZonePlugin extends ZonePlugin {

    public UpperWorldZonePlugin() {

        super("Plugin", new LayeredZoneRegionFunction(new MinMaxLayerThickness(1, 1000), LayeredZoneRegionFunction.LayeredZoneOrdering.HIGH_SKY));

        addProvider(new SurfaceProvider());
        addRasterizer(new UpperWorldRasterizer());
    }
}