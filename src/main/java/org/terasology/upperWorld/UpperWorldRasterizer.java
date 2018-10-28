package org.terasology.upperWorld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.AABB;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.Random;

public class UpperWorldRasterizer implements WorldRasterizer {
    private static final Logger logger = LoggerFactory.getLogger(UpperWorldRasterizer.class);
    private Block water;
    private Block leaves;
    private Block glass;
    private Block glowbellBloom;

    @Override
    public void initialize() {
        water = CoreRegistry.get(BlockManager.class).getBlock("Core:Water");
        leaves = CoreRegistry.get(BlockManager.class).getBlock("Core:GreenLeaf");
        glass = CoreRegistry.get(BlockManager.class).getBlock("UpperWorld:BlueTintGlass");
        glowbellBloom = CoreRegistry.get(BlockManager.class).getBlock("Core:GlowbellBloom");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        Random rand = new Random();
        UpperWorldFacet facet = chunkRegion.getFacet(UpperWorldFacet.class);
        if (chunkRegion.getRegion().maxY() > 10000 && chunkRegion.getRegion().minY()<10000) {
            BaseVector3i heightInWorld = ChunkMath.calcBlockPos(new Vector3i(0, 10000, 0));
            for (Vector3i position : chunkRegion.getRegion()) {
                position = new Vector3i(position.x, position.y, position.z);
                float surfaceHeight = facet.getWorld(position.x, position.z);
                surfaceHeight = ChunkMath.calcBlockPosY((int) surfaceHeight);
                position.y = (int)surfaceHeight;
                AABB boundsOfObj = chunk.getBlock(ChunkMath.calcBlockPos(position)).getBounds(position);
                chunk.setBlock(ChunkMath.calcBlockPos(position), glass);
                //possibly adds flowers
                if (rand.nextInt(50) > 48) {
                    BaseVector3i positionOfNew = position;
                    ((Vector3i) positionOfNew).y += ((int) boundsOfObj.maxY() - (int) boundsOfObj.minY());
                    positionOfNew = ChunkMath.calcBlockPos((Vector3i) positionOfNew);
                    chunkRegion.getRegion().expandToContain(positionOfNew);
                    if (positionOfNew.y() < 64) {
                        chunk.setBlock(positionOfNew, glowbellBloom);
                    }
                }
            }
        }
    }
}