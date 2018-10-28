package org.terasology.upperWorld;

import org.terasology.math.Region3i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFieldFacet2D;

public class UpperWorldFacet extends BaseFieldFacet2D {
    public UpperWorldFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
