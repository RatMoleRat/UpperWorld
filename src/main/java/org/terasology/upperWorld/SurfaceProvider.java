package org.terasology.upperWorld;

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
import org.terasology.world.generation.facets.SurfaceHeightFacet;

//for showing surface of world
@Produces(SurfaceHeightFacet.class)
public class SurfaceProvider implements FacetProvider {
    public Noise surfaceNoise;
    public SurfaceHeightFacet facet;

    //for getting noise
    @Override
    public void setSeed(long seed) {
        surfaceNoise = new SubSampledNoise(new SimplexNoise(seed), new Vector2f(0.01f, 0.01f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        border.extendBy(5, 5, 5);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);

        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position:processRegion.contents()) {
            facet.setWorld(position, surfaceNoise.noise(position.x(),position.y())*20);
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);
        facet = region.getRegionFacet(SurfaceHeightFacet.class);
    }
}