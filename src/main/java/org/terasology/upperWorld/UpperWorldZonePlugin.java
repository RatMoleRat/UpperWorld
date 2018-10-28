package org.terasology.upperWorld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.core.world.generator.facetProviders.*;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.registry.In;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id="upperworld", displayName = "Including Upper World")
public class UpperWorldZonePlugin extends BaseFacetedWorldGenerator {

    @In
    WorldGeneratorPluginLibrary worldGenLib;

    private static Logger logger = LoggerFactory.getLogger(UpperWorldZonePlugin.class);
    public UpperWorldZonePlugin(SimpleUri uri) {
        super(uri);
    }

    @Override
    protected WorldBuilder createWorld() {
        ImmutableVector2i spawnPos = new ImmutableVector2i(0, 0);
        // for the basic world
        return new WorldBuilder(worldGenLib)
                .setSeaLevel(32)
                .addProvider(new UpperWorldSurfaceProvider())     //for the Upper World
                .addRasterizer(new UpperWorldRasterizer()) //for the Upper World
                .addProvider(new SeaLevelProvider(0))
                .addProvider(new PerlinHumidityProvider())
                .addProvider(new PerlinSurfaceTemperatureProvider())
                .addProvider(new PerlinBaseSurfaceProvider())
                .addProvider(new PerlinRiverProvider())
                .addProvider(new PerlinOceanProvider())
                .addProvider(new PerlinHillsAndMountainsProvider())
                .addProvider(new BiomeProvider())
                .addProvider(new SurfaceToDensityProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new DefaultTreeProvider())
                .addProvider(new PlateauProvider(spawnPos, 4, 5, 20))
                .addRasterizer(new SolidRasterizer())
                .addRasterizer(new FloraRasterizer());

    }
}