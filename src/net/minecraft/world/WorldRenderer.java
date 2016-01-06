package net.minecraft.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.block.Block;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.entity.Entity;
import net.minecraft.src.ICamera;
import net.minecraft.src.MathHelper;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderItem;
import net.minecraft.src.Config;
import net.minecraft.src.Tessellator;
import net.minecraft.world.tiles.TileEntity;
import net.minecraft.world.tiles.TileEntityRenderer;
import org.lwjgl.opengl.GL11;

public class WorldRenderer {

   public World worldObj;
   private int glRenderList = -1;
   private static final Tessellator tessellator = Tessellator.instance;
   public static int chunksUpdated = 0;
   public int posX;
   public int posY;
   public int posZ;
   public int sizeWidth;
   public int sizeHeight;
   public int sizeDepth;
   public int posXMinus;
   public int posYMinus;
   public int posZMinus;
   public int posXClip;
   public int posYClip;
   public int posZClip;
   public boolean isInFrustum = false;
   public boolean[] skipRenderPass = new boolean[2];
   public int posXPlus;
   public int posYPlus;
   public int posZPlus;
   public float rendererRadius;
   public boolean needsUpdate;
   public AxisAlignedBB rendererBoundingBox;
   public int chunkIndex;
   public boolean isVisible = true;
   public boolean isWaitingOnOcclusionQuery;
   public int glOcclusionQuery;
   public boolean isChunkLit;
   private boolean isInitialized = false;
   public List tileEntityRenderers = new ArrayList();
   private List tileEntities;
    private boolean needsBoxUpdate = false;
    public boolean isInFrustrumFully = false;
    public boolean isVisibleFromPosition = false;
    public double visibleFromX;
    public double visibleFromY;
    public double visibleFromZ;

    public WorldRenderer(World world, List list, int i, int j, int k, int l, int i1) {
        this.worldObj = world;
        this.tileEntities = list;
        this.sizeWidth = this.sizeHeight = this.sizeDepth = l;
        this.rendererRadius = MathHelper.sqrt_float((float) (this.sizeWidth * this.sizeWidth + this.sizeHeight * this.sizeHeight + this.sizeDepth * this.sizeDepth)) / 2.0F;
        this.glRenderList = i1;
        this.posX = -999;
        this.setPosition(i, j, k);
        this.needsUpdate = false;
    }

    public void setPosition(int px, int py, int pz) {
        if (px != this.posX || py != this.posY || pz != this.posZ) {
            this.setDontDraw();
            this.posX = px;
            this.posY = py;
            this.posZ = pz;
            this.posXPlus = px + this.sizeWidth / 2;
            this.posYPlus = py + this.sizeHeight / 2;
            this.posZPlus = pz + this.sizeDepth / 2;
            this.posXClip = px & 1023;
            this.posYClip = py;
            this.posZClip = pz & 1023;
            this.posXMinus = px - this.posXClip;
            this.posYMinus = py - this.posYClip;
            this.posZMinus = pz - this.posZClip;
            float f = 0.0F;

            this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double) ((float) px - f), (double) ((float) py - f), (double) ((float) pz - f), (double) ((float) (px + this.sizeWidth) + f), (double) ((float) (py + this.sizeHeight) + f), (double) ((float) (pz + this.sizeDepth) + f));
            this.needsBoxUpdate = true;
            this.markDirty();
            this.isVisibleFromPosition = false;
        }
    }

    private void setupGLTranslation() {
        GL11.glTranslatef((float) this.posXClip, (float) this.posYClip, (float) this.posZClip);
    }

    public void updateRenderer() {
        if (this.needsUpdate) {
            ++WorldRenderer.chunksUpdated;
            if (this.needsBoxUpdate) {
                float xMin = 0.0F;

                GL11.glNewList(this.glRenderList + 2, 4864);
                RenderItem.renderAABB(AxisAlignedBB.getBoundingBoxFromPool((double) ((float) this.posXClip - xMin), (double) ((float) this.posYClip - xMin), (double) ((float) this.posZClip - xMin), (double) ((float) (this.posXClip + this.sizeWidth) + xMin), (double) ((float) (this.posYClip + this.sizeHeight) + xMin), (double) ((float) (this.posZClip + this.sizeDepth) + xMin)));
                GL11.glEndList();
                this.needsBoxUpdate = false;
            }

            this.isVisible = true;
            this.isVisibleFromPosition = false;
            int i = this.posX;
            int yMin = this.posY;
            int zMin = this.posZ;
            int xMax = this.posX + this.sizeWidth;
            int yMax = this.posY + this.sizeHeight;
            int zMax = this.posZ + this.sizeDepth;

            for (int lightCache = 0; lightCache < 2; ++lightCache) {
                this.skipRenderPass[lightCache] = true;
            }

            Chunk.isLit = false;
            HashSet hashset = new HashSet();

            hashset.addAll(this.tileEntityRenderers);
            this.tileEntityRenderers.clear();
            byte one = 1;
            ChunkCache chunkcache = new ChunkCache(this.worldObj, i - one, yMin - one, zMin - one, xMax + one, yMax + one, zMax + one);
            RenderBlocks renderblocks = new RenderBlocks(chunkcache);

            for (int renderPass = 0; renderPass < 2; ++renderPass) {
                boolean hashset1 = false;
                boolean hasRenderedBlocks = false;
                boolean hasGlList = false;

                for (int y = yMin; y < yMax; ++y) {
                    for (int z = zMin; z < zMax; ++z) {
                        for (int x = i; x < xMax; ++x) {
                            int i3 = chunkcache.getBlockId(x, y, z);

                            if (i3 > 0) {
                                if (!hasGlList) {
                                    hasGlList = true;
                                    GL11.glNewList(this.glRenderList + renderPass, 4864);
                                    WorldRenderer.tessellator.setRenderingChunk(true);
                                    WorldRenderer.tessellator.startDrawingQuads();
                                }

                                if (renderPass == 0 && Block.isBlockContainer[i3]) {
                                    TileEntity block = chunkcache.getBlockTileEntity(x, y, z);

                                    if (TileEntityRenderer.instance.hasSpecialRenderer(block)) {
                                        this.tileEntityRenderers.add(block);
                                    }
                                }

                                Block block = Block.blocksList[i3];
                                int blockPass = block.getRenderBlockPass();

                                if (blockPass != renderPass) {
                                    hashset1 = true;
                                } else if (blockPass == renderPass) {
                                    hasRenderedBlocks |= renderblocks.renderBlockByRenderType(block, x, y, z);
                                }
                            }
                        }
                    }
                }

                if (hasGlList) {
                    WorldRenderer.tessellator.draw();
                    GL11.glEndList();
                    WorldRenderer.tessellator.setRenderingChunk(false);
                } else {
                    hasRenderedBlocks = false;
                }

                if (hasRenderedBlocks) {
                    this.skipRenderPass[renderPass] = false;
                }

                if (!hashset1) {
                    break;
                }
            }

            HashSet hashseta = new HashSet();

            hashseta.addAll(this.tileEntityRenderers);
            hashseta.removeAll(hashseta);
            this.tileEntities.addAll(hashseta);
            hashseta.removeAll(this.tileEntityRenderers);
            this.tileEntities.removeAll(hashseta);
            this.isChunkLit = Chunk.isLit;
            this.isInitialized = true;
        }
    }

    public float distanceToEntitySquared(Entity entity) {
        float f = (float) (entity.posX - (double) this.posXPlus);
        float f1 = (float) (entity.posY - (double) this.posYPlus);
        float f2 = (float) (entity.posZ - (double) this.posZPlus);

        return f * f + f1 * f1 + f2 * f2;
    }

    public void setDontDraw() {
        for (int i = 0; i < 2; ++i) {
            this.skipRenderPass[i] = true;
        }

        this.isInFrustum = false;
        this.isInitialized = false;
    }

    public void func_1204_c() {
        this.setDontDraw();
        this.worldObj = null;
    }

    public int getGLCallListForPass(int i) {
        return !this.isInFrustum ? -1 : (!this.skipRenderPass[i] ? this.glRenderList + i : -1);
    }

    public void updateInFrustrum(ICamera icamera) {
        this.isInFrustum = icamera.isBoundingBoxInFrustum(this.rendererBoundingBox);
        if (this.isInFrustum && Config.isOcclusionEnabled() && Config.isOcclusionFancy()) {
            this.isInFrustrumFully = icamera.isBoundingBoxInFrustumFully(this.rendererBoundingBox);
        } else {
            this.isInFrustrumFully = false;
        }

    }

    public void callOcclusionQueryList() {
        GL11.glCallList(this.glRenderList + 2);
    }

    public boolean skipAllRenderPasses() {
        return !this.isInitialized ? false : this.skipRenderPass[0] && this.skipRenderPass[1];
    }

    public void markDirty() {
        this.needsUpdate = true;
    }
}
