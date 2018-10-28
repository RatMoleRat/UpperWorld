package org.terasology.upperWorld;
//TODO: figure out how to check if a block is in the inventory

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
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.World;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generator.WorldGenerator;

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
    @In
    InventoryManager inventoryManager;
    @In
    private WorldRasterizer worldRasterizer;

    //some code adapted from https://github.com/Terasology/Inferno/blob/master/src/main/java/org/terasology/inferno/world/InfernoClientSystem.java used as a basis
    @ReceiveEvent(priority = EventPriority.PRIORITY_CRITICAL)
    public void onDeath(BeforeDestroyEvent event, EntityRef entity, CharacterComponent characterComponent, LocationComponent locationComponent) {
        Random rand = new Random();

        boolean[] items = new boolean[]{false, false};
        EntityRef character = localPlayer.getCharacterEntity();
        Block leaf = CoreRegistry.get(BlockManager.class).getBlock("Core:GreenLeaf");
        logger.info("leaf id: "+leaf);
        for (int i = 0; i < InventoryUtils.getSlotCount(character); i++) {
            logger.info("i: "+i);
            EntityRef slot = InventoryUtils.getItemAt(character, i);
            if (slot.hasComponent(BlockItemComponent.class)) {
                logger.info("has a block item component");
                if (slot.getComponent(BlockItemComponent.class).blockFamily.getURI().toString().equals(((Block) leaf).getURI().toString())) {
                    logger.info("is equal");
                    World world = worldGenerator.getWorld();
                    Object[] providers = world.getAllFacets().toArray();
                    Vector3f newPos = findPosition(locationComponent, world);
                    if (newPos != null) {
                        event.consume();
                        character.send(new DoHealEvent(100000));
                        character.send(new CharacterTeleportEvent(newPos));
                    }
                }
            }
        }
    }
    public Vector3f findPosition(LocationComponent loc, World wor) {
        Vector3i search = new Vector3i(100, 11000, 100);
        Vector3f center = loc.getWorldPosition();
        Region3i area = Region3i.createFromCenterExtents(new Vector3i(center.x, center.y+200, center.z), search);
        Region worldRegion = wor.getWorldData(area);

        logger.info(worldRegion.toString());
        for (int i=0; i<wor.getAllFacets().toArray().length; i++) {
            logger.info(wor.getAllFacets().toArray()[i].toString());
        }
        UpperWorldFacet facet = worldRegion.getFacet(UpperWorldFacet.class);
        if (facet != null) {
            logger.info("not null!");
            for (BaseVector2i pos : facet.getWorldRegion().contents()) {
                float surfaceHeight = facet.getWorld(pos);
                logger.info("surfaceheight: " + surfaceHeight);
                if (surfaceHeight > 10000) {
                    return new Vector3f(pos.x(), surfaceHeight + 20, pos.y());
                }
            }
        }
        return null;
    }
}
