package org.terasology.upperWorld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

//for showing surface of world
@Produces(UpperWorldFacet.class)
public class UpperWorldSurfaceProvider implements FacetProvider {
    public Noise surfaceNoise;
    private static Logger logger = LoggerFactory.getLogger(UpperWorldSurfaceProvider.class);

    //for getting noise
    @Override
    public void setSeed(long seed) {
        surfaceNoise = new SubSampledNoise(new SimplexNoise(seed), new Vector2f(0.01f, 0.01f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(UpperWorldFacet.class);
        UpperWorldFacet facet = new UpperWorldFacet(region.getRegion(), border);
        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position:processRegion.contents()) {
            facet.setWorld(position, surfaceNoise.noise(position.x(),position.y())*20+10000);
        }

        region.setRegionFacet(UpperWorldFacet.class, facet);
    }
}