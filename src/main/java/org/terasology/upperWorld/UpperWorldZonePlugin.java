package org.terasology.upperWorld;

import org.terasology.world.generator.plugin.RegisterPlugin;
import org.terasology.world.zones.LayeredZoneRegionFunction;
import org.terasology.world.zones.MinMaxLayerThickness;
import org.terasology.world.zones.ZonePlugin;

@RegisterPlugin
public class UpperWorldZonePlugin extends ZonePlugin {

    public UpperWorldZonePlugin() {

        super("Plugin zone", new LayeredZoneRegionFunction(new MinMaxLayerThickness(1, 300), LayeredZoneRegionFunction.LayeredZoneOrdering.LOW_SKY));

        // Inferno
        addProvider(new SurfaceProvider());
        addRasterizer(new UpperWorldRasterizer());
    }
}