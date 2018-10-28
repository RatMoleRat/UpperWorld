package org.terasology.upperWorld;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.characters.CharacterTeleportEvent;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.logic.health.DoHealEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.World;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generator.WorldGenerator;
import org.terasology.world.zones.Zone;

import java.util.Random;

@RegisterSystem(RegisterMode.CLIENT)
public class ResurrectAndStuff extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ResurrectAndStuff.class);
    @In
    private LocalPlayer localPlayer;
    @In
    private WorldGenerator worldGenerator;
    @In
    private WorldProvider worldProvider;
    private Zone zone;
    @In
    private WorldRasterizer worldRasterizer;

    //some code adapted from https://github.com/Terasology/Inferno/blob/master/src/main/java/org/terasology/inferno/world/InfernoClientSystem.java used as a basis
    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void onDeath(BeforeDestroyEvent event, EntityRef entity, CharacterComponent characterComponent, LocationComponent locationComponent) {

        logger.info("dead...");
        Random rand = new Random();

        EntityRef character = localPlayer.getCharacterEntity();
        character.send(new DoHealEvent(100000));

        World world = worldGenerator.getWorld();
        Vector3i search = new Vector3i(100, 1000, 100);
        Vector3f center = locationComponent.getWorldPosition();
        Region3i area = Region3i.createFromCenterExtents(new Vector3i(center.x, center.y+1000, center.z), search);
        Region worldRegion = world.getWorldData(area);

        Object[] providers = world.getAllFacets().toArray();

        Vector3f newPos = findPosition(locationComponent, world);

        if (newPos != null) {
            event.consume();
            character.send(new CharacterTeleportEvent(newPos));
        }
        logger.info("teleported.");
    }
    public Vector3f findPosition(LocationComponent loc, World wor) {
        Vector3i search = new Vector3i(100, 1000, 100);
        Vector3f center = loc.getWorldPosition();
        Region3i area = Region3i.createFromCenterExtents(new Vector3i(center.x, center.y+1000, center.z), search);
        Region worldRegion = wor.getWorldData(area);

        UpperWorldFacet facet;
        try {
            facet = worldRegion.getFacet(UpperWorldFacet.class);
        }
        catch (NullPointerException n) {
            facet = new UpperWorldFacet(area, new Border3D(28, 28, 28));
            wor.getAllFacets().add(facet.getClass());
            worldRegion = wor.getWorldData(area);
        }
        for (BaseVector2i pos : facet.getWorldRegion().contents()) {
            float surfaceHeight = facet.getWorld(pos);
            if (surfaceHeight > 1000) {
                return new Vector3f(pos.x(), surfaceHeight + 1, pos.y());
            }
        }
        return null;
    }
}
