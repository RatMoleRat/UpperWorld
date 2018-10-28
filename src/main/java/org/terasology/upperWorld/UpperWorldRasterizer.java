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
        BaseVector3i heightInWorld = ChunkMath.calcBlockPos(new Vector3i(0, 10000, 0));
        for (Vector3i position : chunkRegion.getRegion()) {
            position = new Vector3i(position.x, position.y+10000, position.z);
            float surfaceHeight = facet.getWorld(position.x, position.z);
            surfaceHeight = ChunkMath.calcBlockPosY((int)surfaceHeight+10000);
            AABB boundsOfObj = chunk.getBlock(ChunkMath.calcBlockPos(position)).getBounds(position);
            if (ChunkMath.calcBlockPosY(position.y) < surfaceHeight) {
                /*
                if (surfaceHeight<10000){
                    chunk.setBlock(ChunkMath.calcBlockPos(position), leaves);
                    //adds water where it should
                    for (float i=ChunkMath.calcBlockPosY(position.y); i<surfaceHeight; i+= (boundsOfObj.maxY()-boundsOfObj.minY())) {
                        BaseVector3i positionOfNew = position;
                        ((Vector3i) positionOfNew).y+=i*((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        positionOfNew = ChunkMath.calcBlockPos((Vector3i)positionOfNew);
                        chunkRegion.getRegion().expandToContain(positionOfNew);

                        if (positionOfNew.y() < heightInWorld.y()) {
                            chunk.setBlock(positionOfNew,water);
                        }
                    }
                }
                else{*/
                    chunk.setBlock(ChunkMath.calcBlockPos(position), glass);
                    //possibly adds flowers
                    if (rand.nextInt(9)>7) {
                        BaseVector3i positionOfNew = position;
                        ((Vector3i) positionOfNew).y+=((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        positionOfNew = ChunkMath.calcBlockPos((Vector3i)positionOfNew);
                        chunkRegion.getRegion().expandToContain(positionOfNew);
                        if (positionOfNew.y() < 64) {
                            chunk.setBlock(positionOfNew,glowbellBloom);
                        }
                    }
                    //random chance for leaves
                    if (rand.nextInt(200)>198) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), leaves);
                    }
                //}

            }
        }
    }
}
