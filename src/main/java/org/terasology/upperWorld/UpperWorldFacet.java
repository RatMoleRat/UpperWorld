package org.terasology.upperWorld;

import org.terasology.math.Region3i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFieldFacet2D;

public class UpperWorldFacet extends BaseFieldFacet2D {
    private int baseSurfaceHeight;
    public UpperWorldFacet(Region3i region, Border3D border) {
        super(region, border);
    }
    public int getBaseSurfaceHeight() {
        return baseSurfaceHeight;
    }
    public void setBaseSurfaceHeight(int toSet) {
        baseSurfaceHeight = toSet;
    }
}
