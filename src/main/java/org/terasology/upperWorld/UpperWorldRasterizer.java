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
import org.terasology.world.generation.facets.SurfaceHeightFacet;

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
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        BaseVector3i heightInWorld = ChunkMath.calcBlockPos(new Vector3i(0, 1000, 0));
        for (Vector3i position : chunkRegion.getRegion()) {
            float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
            position = new Vector3i(position.x, position.y+1000, position.z);
            surfaceHeight = ChunkMath.calcBlockPosY((int)surfaceHeight+1000);
            AABB boundsOfObj = chunk.getBlock(ChunkMath.calcBlockPos(position)).getBounds(position);
            if (position.y < surfaceHeight) {
                if (surfaceHeight<1000){
                    chunk.setBlock(ChunkMath.calcBlockPos(position), leaves);
                    //adds water where it should
                    for (float i=position.y; i<surfaceHeight; i+= (boundsOfObj.maxY()-boundsOfObj.minY())) {
                        BaseVector3i positionOfNew0 = ChunkMath.calcBlockPos(position);
                        BaseVector3i positionOfNew1 = positionOfNew0;
                        ((Vector3i) positionOfNew1).y+=i*((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        positionOfNew1 = ChunkMath.calcBlockPos((Vector3i)positionOfNew1);
                        ((Vector3i) positionOfNew1).setX(positionOfNew0.x());
                        ((Vector3i) positionOfNew1).setZ(positionOfNew0.z());
                        chunkRegion.getRegion().expandToContain(positionOfNew1);
                        if (positionOfNew1.y() < heightInWorld.y()) {
                            chunk.setBlock(positionOfNew1,water);
                        }
                    }
                }
                else{
                    chunk.setBlock(ChunkMath.calcBlockPos(position), glass);
                    //possibly adds grass
                    if (rand.nextInt(9)>7) {
                        BaseVector3i positionOfNew0 = ChunkMath.calcBlockPos(position);
                        BaseVector3i positionOfNew1 = positionOfNew0;
                        ((Vector3i) positionOfNew1).y+=((int)boundsOfObj.maxY()-(int)boundsOfObj.minY());
                        positionOfNew1 = ChunkMath.calcBlockPos((Vector3i)positionOfNew1);
                        ((Vector3i) positionOfNew1).setX(positionOfNew0.x());
                        ((Vector3i) positionOfNew1).setZ(positionOfNew0.z());
                        chunkRegion.getRegion().expandToContain(positionOfNew1);
                        if (positionOfNew1.y() < 64) {
                            chunk.setBlock(positionOfNew1,glowbellBloom);
                        }
                    }
                    //random chance for dirt
                    if (rand.nextInt(50)>48) {
                        chunk.setBlock(ChunkMath.calcBlockPos(position), leaves);
                    }
                }

            }
        }
    }
}
