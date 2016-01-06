package net.minecraft.client.render;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.block.Block;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBubbleFX;
import net.minecraft.entity.EntityExplodeFX;
import net.minecraft.entity.EntityFlameFX;
import net.minecraft.entity.EntityFootStepFX;
import net.minecraft.entity.EntityHeartFX;
import net.minecraft.entity.EntityLavaFX;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityNoteFX;
import net.minecraft.entity.EntityPlayer;
import net.minecraft.entity.EntityPortalFX;
import net.minecraft.entity.EntityReddustFX;
import net.minecraft.entity.EntitySlimeFX;
import net.minecraft.entity.EntitySmokeFX;
import net.minecraft.entity.EntitySnowShovelFX;
import net.minecraft.entity.EntitySorter;
import net.minecraft.entity.EntitySplashFX;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.ICamera;
import net.minecraft.src.IWorldAccess;
import net.minecraft.src.ImageBufferDownload;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Tessellator;
import net.minecraft.world.tiles.TileEntity;
import net.minecraft.world.tiles.TileEntityRenderer;
import net.minecraft.src.Vec3D;
import net.minecraft.world.World;
import net.minecraft.world.WorldRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

public class RenderGlobal implements IWorldAccess {

    private long lastMovedTime = System.currentTimeMillis();
    public List tileEntities = new ArrayList();
    private World worldObj;
    private RenderEngine renderEngine;
    private List worldRenderersToUpdate = new ArrayList();
    private WorldRenderer[] sortedWorldRenderers;
    private WorldRenderer[] worldRenderers;
    private int renderChunksWide;
    private int renderChunksTall;
    private int renderChunksDeep;
    private int glRenderListBase;
    private Minecraft mc;
    private RenderBlocks globalRenderBlocks;
    private IntBuffer glOcclusionQueryBase;
    private boolean occlusionEnabled = false;
    private int cloudOffsetX = 0;
    private int starGLCallList;
    private int glSkyList;
    private int glSkyList2;
    private int minBlockX;
    private int minBlockY;
    private int minBlockZ;
    private int maxBlockX;
    private int maxBlockY;
    private int maxBlockZ;
    private int renderDistance = -1;
    private int renderEntitiesStartupCounter = 2;
    private int countEntitiesTotal;
    private int countEntitiesRendered;
    private int countEntitiesHidden;
    int[] dummyBuf50k = new int['썐'];
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
    private int renderersLoaded;
    private int renderersBeingClipped;
    private int renderersBeingOccluded;
    private int renderersBeingRendered;
    private int renderersSkippingRenderPass;
    private int worldRenderersCheckIndex;
    private IntBuffer field_22019_aY = BufferUtils.createIntBuffer(65536);
    int dummyInt0 = 0;
    int glDummyList = GLAllocation.generateDisplayLists(1);
    double prevSortX = -9999.0D;
    double prevSortY = -9999.0D;
    double prevSortZ = -9999.0D;
    public float damagePartialTime;
    int frustrumCheckOffset = 0;
    double prevReposX;
    double prevReposY;
    double prevReposZ;

    public RenderGlobal(Minecraft minecraft, RenderEngine renderengine) {
        this.mc = minecraft;
        this.renderEngine = renderengine;
        byte maxChunkDim = 64;

        this.glRenderListBase = GLAllocation.generateDisplayLists(maxChunkDim * maxChunkDim * maxChunkDim * 3);
        this.occlusionEnabled = minecraft.getOpenGlCapsChecker().checkARBOcclusion();
        if (this.occlusionEnabled) {
            this.occlusionResult.clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(maxChunkDim * maxChunkDim * maxChunkDim);
            this.glOcclusionQueryBase.clear();
            this.glOcclusionQueryBase.position(0);
            this.glOcclusionQueryBase.limit(maxChunkDim * maxChunkDim * maxChunkDim);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, 4864);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator tessellator = Tessellator.instance;

        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, 4864);
        byte byte1 = 64;
        int i = 256 / byte1 + 2;
        float f = 16.0F;

        int k;
        int i1;

        for (k = -byte1 * i; k <= byte1 * i; k += byte1) {
            for (i1 = -byte1 * i; i1 <= byte1 * i; i1 += byte1) {
                tessellator.startDrawingQuads();
                tessellator.addVertex((double) (k + 0), (double) f, (double) (i1 + 0));
                tessellator.addVertex((double) (k + byte1), (double) f, (double) (i1 + 0));
                tessellator.addVertex((double) (k + byte1), (double) f, (double) (i1 + byte1));
                tessellator.addVertex((double) (k + 0), (double) f, (double) (i1 + byte1));
                tessellator.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, 4864);
        f = -16.0F;
        tessellator.startDrawingQuads();

        for (k = -byte1 * i; k <= byte1 * i; k += byte1) {
            for (i1 = -byte1 * i; i1 <= byte1 * i; i1 += byte1) {
                tessellator.addVertex((double) (k + byte1), (double) f, (double) (i1 + 0));
                tessellator.addVertex((double) (k + 0), (double) f, (double) (i1 + 0));
                tessellator.addVertex((double) (k + 0), (double) f, (double) (i1 + byte1));
                tessellator.addVertex((double) (k + byte1), (double) f, (double) (i1 + byte1));
            }
        }

        tessellator.draw();
        GL11.glEndList();
    }

    private void renderStars() {
        Random random = new Random(10842L);
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();

        for (int i = 0; i < 1500; ++i) {
            double d = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double) (random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double) (0.25F + random.nextFloat() * 0.25F);
            double d4 = d * d + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D) {
                d4 = 1.0D / Math.sqrt(d4);
                d *= d4;
                d1 *= d4;
                d2 *= d4;
                double d5 = d * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d * d + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * 3.141592653589793D * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j) {
                    double d17 = 0.0D;
                    double d18 = (double) ((j & 2) - 1) * d3;
                    double d19 = (double) ((j + 1 & 2) - 1) * d3;
                    double d20 = d18 * d16 - d19 * d15;
                    double d21 = d19 * d16 + d18 * d15;
                    double d22 = d20 * d12 + d17 * d13;
                    double d23 = d17 * d12 - d20 * d13;
                    double d24 = d23 * d9 - d21 * d10;
                    double d25 = d21 * d9 + d23 * d10;

                    tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
                }
            }
        }

        tessellator.draw();
    }

    public void changeWorld(World world) {
        if (this.worldObj != null) {
            this.worldObj.removeWorldAccess(this);
        }

        this.prevSortX = -9999.0D;
        this.prevSortY = -9999.0D;
        this.prevSortZ = -9999.0D;
        RenderManager.instance.set(world);
        this.worldObj = world;
        this.globalRenderBlocks = new RenderBlocks(world);
        if (world != null) {
            world.addWorldAccess(this);
            this.loadRenderers();
        }

    }

    public void loadRenderers() {
        Block.leaves.setGraphicsLevel(Config.isTreesFancy());
        this.renderDistance = this.mc.gameSettings.renderDistance;
        int numBlocks;

        if (this.worldRenderers != null) {
            for (numBlocks = 0; numBlocks < this.worldRenderers.length; ++numBlocks) {
                this.worldRenderers[numBlocks].func_1204_c();
            }
        }

        numBlocks = 64 << 3 - this.renderDistance;
        if (Config.isLoadChunksFar()) {
            numBlocks = 512;
        }

        if (Config.isFarView()) {
            if (numBlocks < 512) {
                numBlocks *= 3;
            } else {
                numBlocks *= 2;
            }
        }

        numBlocks += Config.getPreloadedChunks() * 2 * 16;
        if (!Config.isFarView() && numBlocks > 400) {
            numBlocks = 400;
        }

        this.prevReposX = -9999.0D;
        this.prevReposY = -9999.0D;
        this.prevReposZ = -9999.0D;
        this.renderChunksWide = numBlocks / 16 + 1;
        this.renderChunksTall = 8;
        this.renderChunksDeep = numBlocks / 16 + 1;
        this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
        this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
        int k = 0;
        int l = 0;

        this.minBlockX = 0;
        this.minBlockY = 0;
        this.minBlockZ = 0;
        this.maxBlockX = this.renderChunksWide;
        this.maxBlockY = this.renderChunksTall;
        this.maxBlockZ = this.renderChunksDeep;

        int entityliving;

        for (entityliving = 0; entityliving < this.worldRenderersToUpdate.size(); ++entityliving) {
            WorldRenderer k1 = (WorldRenderer) this.worldRenderersToUpdate.get(entityliving);

            if (k1 != null) {
                k1.needsUpdate = false;
            }
        }

        this.worldRenderersToUpdate.clear();
        this.tileEntities.clear();

        for (entityliving = 0; entityliving < this.renderChunksWide; ++entityliving) {
            for (int i = 0; i < this.renderChunksTall; ++i) {
                for (int l1 = 0; l1 < this.renderChunksDeep; ++l1) {
                    int wri = (l1 * this.renderChunksTall + i) * this.renderChunksWide + entityliving;

                    this.worldRenderers[wri] = new WorldRenderer(this.worldObj, this.tileEntities, entityliving * 16, i * 16, l1 * 16, 16, this.glRenderListBase + k);
                    if (this.occlusionEnabled) {
                        this.worldRenderers[wri].glOcclusionQuery = this.glOcclusionQueryBase.get(l);
                    }

                    this.worldRenderers[wri].isWaitingOnOcclusionQuery = false;
                    this.worldRenderers[wri].isVisible = true;
                    this.worldRenderers[wri].isInFrustum = false;
                    this.worldRenderers[wri].chunkIndex = l++;
                    this.worldRenderers[wri].markDirty();
                    this.sortedWorldRenderers[wri] = this.worldRenderers[wri];
                    this.worldRenderersToUpdate.add(this.worldRenderers[wri]);
                    k += 3;
                }
            }
        }

        if (this.worldObj != null) {
            Entity object = this.mc.renderViewEntity;

            if (object == null) {
                object = this.mc.thePlayer;
            }

            if (object != null) {
                this.markRenderersForNewPosition(MathHelper.floor_double(object.posX), MathHelper.floor_double(((Entity) object).posY), MathHelper.floor_double(((Entity) object).posZ));
                Arrays.sort(this.sortedWorldRenderers, new EntitySorter((Entity) object));
            }
        }

        this.renderEntitiesStartupCounter = 2;
    }

    public void renderEntities(Vec3D vec3d, ICamera icamera, float f) {
        if (this.renderEntitiesStartupCounter > 0) {
            --this.renderEntitiesStartupCounter;
        } else {
            TileEntityRenderer.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.thePlayer, f);
            RenderManager.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.thePlayer, this.mc.gameSettings, f);
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            EntityLiving entityliving = this.mc.thePlayer;

            RenderManager.renderPosX = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double) f;
            RenderManager.renderPosY = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double) f;
            RenderManager.renderPosZ = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double) f;
            TileEntityRenderer.staticPlayerX = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double) f;
            TileEntityRenderer.staticPlayerY = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double) f;
            TileEntityRenderer.staticPlayerZ = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double) f;
            List list = this.worldObj.getLoadedEntityList();

            this.countEntitiesTotal = list.size();

            int k;
            Entity entity1;

            for (k = 0; k < this.worldObj.weatherEffects.size(); ++k) {
                entity1 = (Entity) this.worldObj.weatherEffects.get(k);
                ++this.countEntitiesRendered;
                if (entity1.isInRangeToRenderVec3D(vec3d)) {
                    RenderManager.instance.renderEntity(entity1, f);
                }
            }

            for (k = 0; k < list.size(); ++k) {
                entity1 = (Entity) list.get(k);
                if (entity1.isInRangeToRenderVec3D(vec3d) && (entity1.ignoreFrustumCheck || icamera.isBoundingBoxInFrustum(entity1.boundingBox)) && (entity1 != this.mc.thePlayer || this.mc.gameSettings.thirdPersonView || this.mc.thePlayer.isPlayerSleeping())) {
                    int l = MathHelper.floor_double(entity1.posY);

                    if (l < 0) {
                        l = 0;
                    }

                    if (l >= 128) {
                        l = 127;
                    }

                    if (this.worldObj.blockExists(MathHelper.floor_double(entity1.posX), l, MathHelper.floor_double(entity1.posZ))) {
                        ++this.countEntitiesRendered;
                        RenderManager.instance.renderEntity(entity1, f);
                    }
                }
            }

            for (k = 0; k < this.tileEntities.size(); ++k) {
                TileEntityRenderer.instance.renderTileEntity((TileEntity) this.tileEntities.get(k), f);
            }

        }
    }

    public String getDebugInfoRenders() {
        return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
    }

    public String getDebugInfoEntities() {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
    }

    private void markRenderersForNewPosition(int x, int y, int z) {
        x -= 8;
        y -= 8;
        z -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int blocksWide = this.renderChunksWide * 16;
        int blocksWide2 = blocksWide / 2;

        for (int ix = 0; ix < this.renderChunksWide; ++ix) {
            int blockX = ix * 16;
            int blockXAbs = blockX + blocksWide2 - x;

            if (blockXAbs < 0) {
                blockXAbs -= blocksWide - 1;
            }

            blockXAbs /= blocksWide;
            blockX -= blockXAbs * blocksWide;
            if (blockX < this.minBlockX) {
                this.minBlockX = blockX;
            }

            if (blockX > this.maxBlockX) {
                this.maxBlockX = blockX;
            }

            for (int iz = 0; iz < this.renderChunksDeep; ++iz) {
                int blockZ = iz * 16;
                int blockZAbs = blockZ + blocksWide2 - z;

                if (blockZAbs < 0) {
                    blockZAbs -= blocksWide - 1;
                }

                blockZAbs /= blocksWide;
                blockZ -= blockZAbs * blocksWide;
                if (blockZ < this.minBlockZ) {
                    this.minBlockZ = blockZ;
                }

                if (blockZ > this.maxBlockZ) {
                    this.maxBlockZ = blockZ;
                }

                for (int iy = 0; iy < this.renderChunksTall; ++iy) {
                    int blockY = iy * 16;

                    if (blockY < this.minBlockY) {
                        this.minBlockY = blockY;
                    }

                    if (blockY > this.maxBlockY) {
                        this.maxBlockY = blockY;
                    }

                    WorldRenderer worldrenderer = this.worldRenderers[(iz * this.renderChunksTall + iy) * this.renderChunksWide + ix];
                    boolean wasNeedingUpdate = worldrenderer.needsUpdate;

                    worldrenderer.setPosition(blockX, blockY, blockZ);
                    if (!wasNeedingUpdate && worldrenderer.needsUpdate) {
                        this.worldRenderersToUpdate.add(worldrenderer);
                    }
                }
            }
        }

    }

    public int sortAndRender(EntityLiving player, int renderPass, double partialTicks) {
        if (this.worldRenderersToUpdate.size() < 10) {
            byte partialX = 10;

            for (int i = 0; i < partialX; ++i) {
                this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
                WorldRenderer partialY = this.worldRenderers[this.worldRenderersCheckIndex];

                if (partialY.needsUpdate && !this.worldRenderersToUpdate.contains(partialY)) {
                    this.worldRenderersToUpdate.add(partialY);
                }
            }
        }

        if (this.mc.gameSettings.renderDistance != this.renderDistance && !Config.isLoadChunksFar()) {
            this.loadRenderers();
        }

        if (renderPass == 0) {
            this.renderersLoaded = 0;
            this.renderersBeingClipped = 0;
            this.renderersBeingOccluded = 0;
            this.renderersBeingRendered = 0;
            this.renderersSkippingRenderPass = 0;
        }

        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double partialZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        double dSortX = player.posX - this.prevSortX;
        double dSortY = player.posY - this.prevSortY;
        double dSortZ = player.posZ - this.prevSortZ;
        double distSqSort = dSortX * dSortX + dSortY * dSortY + dSortZ * dSortZ;
        int num;

        if (distSqSort > 16.0D) {
            this.prevSortX = player.posX;
            this.prevSortY = player.posY;
            this.prevSortZ = player.posZ;
            num = Config.getPreloadedChunks() * 16;
            double ocReq = player.posX - this.prevReposX;
            double lastIndex = player.posY - this.prevReposY;
            double stepNum = player.posZ - this.prevReposZ;
            double switchStep = ocReq * ocReq + lastIndex * lastIndex + stepNum * stepNum;

            if (switchStep > (double) (num * num) + 16.0D) {
                this.prevReposX = player.posX;
                this.prevReposY = player.posY;
                this.prevReposZ = player.posZ;
                this.markRenderersForNewPosition(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
            }

            Arrays.sort(this.sortedWorldRenderers, new EntitySorter(player));
        }

        if (this.mc.gameSettings.ofSmoothFps && renderPass == 0) {
            GL11.glFinish();
        }

        if (this.mc.gameSettings.ofSmoothInput && renderPass == 0) {
            Config.sleep(1L);
        }

        byte b0 = 0;
        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && renderPass == 0) {
            byte firstIndex = 0;
            byte b1 = 20;

            this.checkOcclusionQueryResult(firstIndex, b1, player.posX, player.posY, player.posZ);

            int endIndex;

            for (endIndex = firstIndex; endIndex < b1; ++endIndex) {
                this.sortedWorldRenderers[endIndex].isVisible = true;
            }

            num = b0 + this.renderSortedRenderers(firstIndex, b1, renderPass, partialTicks);
            endIndex = b1;
            int j = 0;
            byte step = 30;

            int startIndex;

            for (int k = this.renderChunksWide / 2; endIndex < this.sortedWorldRenderers.length; num += this.renderSortedRenderers(startIndex, endIndex, renderPass, partialTicks)) {
                startIndex = endIndex;
                if (j < k) {
                    ++j;
                } else {
                    --j;
                }

                endIndex += j * step;
                if (endIndex <= startIndex) {
                    endIndex = startIndex + 10;
                }

                if (endIndex > this.sortedWorldRenderers.length) {
                    endIndex = this.sortedWorldRenderers.length;
                }

                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(3008);
                GL11.glDisable(2912);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                this.checkOcclusionQueryResult(startIndex, endIndex, player.posX, player.posY, player.posZ);
                GL11.glPushMatrix();
                float sumTX = 0.0F;
                float sumTY = 0.0F;
                float sumTZ = 0.0F;

                for (int kk = startIndex; kk < endIndex; ++kk) {
                    WorldRenderer wr = this.sortedWorldRenderers[kk];

                    if (wr.skipAllRenderPasses()) {
                        wr.isInFrustum = false;
                    } else if (wr.isInFrustum) {
                        if (Config.isOcclusionFancy() && !wr.isInFrustrumFully) {
                            wr.isVisible = true;
                        } else if (wr.isInFrustum && !wr.isWaitingOnOcclusionQuery) {
                            float bbX;
                            float bbY;
                            float bbZ;
                            float tX;

                            if (wr.isVisibleFromPosition) {
                                bbX = Math.abs((float) (wr.visibleFromX - player.posX));
                                bbY = Math.abs((float) (wr.visibleFromY - player.posY));
                                bbZ = Math.abs((float) (wr.visibleFromZ - player.posZ));
                                tX = bbX + bbY + bbZ;
                                if ((double) tX < 10.0D + (double) kk / 1000.0D) {
                                    wr.isVisible = true;
                                    continue;
                                }

                                wr.isVisibleFromPosition = false;
                            }

                            bbX = (float) ((double) wr.posXMinus - d0);
                            bbY = (float) ((double) wr.posYMinus - d1);
                            bbZ = (float) ((double) wr.posZMinus - partialZ);
                            tX = bbX - sumTX;
                            float tY = bbY - sumTY;
                            float tZ = bbZ - sumTZ;

                            if (tX != 0.0F || tY != 0.0F || tZ != 0.0F) {
                                GL11.glTranslatef(tX, tY, tZ);
                                sumTX += tX;
                                sumTY += tY;
                                sumTZ += tZ;
                            }

                            ARBOcclusionQuery.glBeginQueryARB('褔', wr.glOcclusionQuery);
                            wr.callOcclusionQueryList();
                            ARBOcclusionQuery.glEndQueryARB('褔');
                            wr.isWaitingOnOcclusionQuery = true;
                        }
                    }
                }

                GL11.glPopMatrix();
                GL11.glColorMask(true, true, true, true);
                GL11.glDepthMask(true);
                GL11.glEnable(3553);
                GL11.glEnable(3008);
                GL11.glEnable(2912);
            }
        } else {
            num = b0 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, renderPass, partialTicks);
        }

        return num;
    }

    private void checkOcclusionQueryResult(int startIndex, int endIndex, double px, double py, double pz) {
        for (int k = startIndex; k < endIndex; ++k) {
            WorldRenderer wr = this.sortedWorldRenderers[k];

            if (wr.isWaitingOnOcclusionQuery) {
                this.occlusionResult.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(wr.glOcclusionQuery, '衧', this.occlusionResult);
                if (this.occlusionResult.get(0) != 0) {
                    wr.isWaitingOnOcclusionQuery = false;
                    this.occlusionResult.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(wr.glOcclusionQuery, '衦', this.occlusionResult);
                    boolean wasVisible = wr.isVisible;

                    wr.isVisible = this.occlusionResult.get(0) > 0;
                    if (wasVisible && wr.isVisible) {
                        wr.isVisibleFromPosition = true;
                        wr.visibleFromX = px;
                        wr.visibleFromY = py;
                        wr.visibleFromZ = pz;
                    }
                }
            }
        }

    }

    private int renderSortedRenderers(int startIndex, int endIndex, int renderPass, double partialTicks) {
        this.field_22019_aY.clear();
        int l = 0;

        for (int entityliving = startIndex; entityliving < endIndex; ++entityliving) {
            if (renderPass == 0) {
                ++this.renderersLoaded;
                if (this.sortedWorldRenderers[entityliving].skipRenderPass[renderPass]) {
                    ++this.renderersSkippingRenderPass;
                } else if (!this.sortedWorldRenderers[entityliving].isInFrustum) {
                    ++this.renderersBeingClipped;
                } else if (this.occlusionEnabled && !this.sortedWorldRenderers[entityliving].isVisible) {
                    ++this.renderersBeingOccluded;
                } else {
                    ++this.renderersBeingRendered;
                }
            }

            if (!this.sortedWorldRenderers[entityliving].skipRenderPass[renderPass] && this.sortedWorldRenderers[entityliving].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[entityliving].isVisible)) {
                int partialX = this.sortedWorldRenderers[entityliving].getGLCallListForPass(renderPass);

                if (partialX >= 0) {
                    this.field_22019_aY.put(partialX);
                    ++l;
                }
            }
        }

        this.field_22019_aY.flip();
        EntityLiving entityliving = this.mc.thePlayer;
        double d0 = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * partialTicks;
        double partialY = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * partialTicks;
        double partialZ = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * partialTicks;

        GL11.glTranslatef((float) (-d0), (float) (-partialY), (float) (-partialZ));
        GL11.glCallLists(this.field_22019_aY);
        GL11.glTranslatef((float) d0, (float) partialY, (float) partialZ);
        return l;
    }

    public void renderAllRenderLists(int renderPass, double partialTicks) {
    }

    public void updateClouds() {
        ++this.cloudOffsetX;
    }

    public void renderSky(float f) {
        if (!this.mc.theWorld.worldProvider.isNether) {
            GL11.glDisable(3553);
            Vec3D vec3d = this.worldObj.getSkyColor(this.mc.thePlayer, f);
            float f1 = (float) vec3d.xCoord;
            float f2 = (float) vec3d.yCoord;
            float f3 = (float) vec3d.zCoord;
            float f6;

            if (this.mc.gameSettings.anaglyph) {
                float tessellator = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                float af = (f1 * 30.0F + f2 * 70.0F) / 100.0F;

                f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
                f1 = tessellator;
                f2 = af;
                f3 = f6;
            }

            GL11.glColor3f(f1, f2, f3);
            Tessellator tessellator = Tessellator.instance;

            GL11.glDepthMask(false);
            GL11.glEnable(2912);
            GL11.glColor3f(f1, f2, f3);
            if (Config.isSkyEnabled()) {
                GL11.glCallList(this.glSkyList);
            }

            GL11.glDisable(2912);
            GL11.glDisable(3008);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            RenderHelper.disableStandardItemLighting();
            float[] afloat = this.worldObj.worldProvider.calcSunriseSunsetColors(this.worldObj.getCelestialAngle(f), f);
            float f9;
            float f11;
            float f13;
            float f15;
            float f17;

            if (afloat != null && Config.isSkyEnabled()) {
                GL11.glDisable(3553);
                GL11.glShadeModel(7425);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                f6 = this.worldObj.getCelestialAngle(f);
                GL11.glRotatef(f6 <= 0.5F ? 0.0F : 180.0F, 0.0F, 0.0F, 1.0F);
                f9 = afloat[0];
                f11 = afloat[1];
                f13 = afloat[2];
                float f20;

                if (this.mc.gameSettings.anaglyph) {
                    f15 = (f9 * 30.0F + f11 * 59.0F + f13 * 11.0F) / 100.0F;
                    f17 = (f9 * 30.0F + f11 * 70.0F) / 100.0F;
                    f20 = (f9 * 30.0F + f13 * 70.0F) / 100.0F;
                    f9 = f15;
                    f11 = f17;
                    f13 = f20;
                }

                tessellator.startDrawing(6);
                tessellator.setColorRGBA_F(f9, f11, f13, afloat[3]);
                tessellator.addVertex(0.0D, 100.0D, 0.0D);
                byte b0 = 16;

                tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);

                for (int i = 0; i <= b0; ++i) {
                    f20 = (float) i * 3.141593F * 2.0F / (float) b0;
                    float f21 = MathHelper.sin(f20);
                    float f22 = MathHelper.cos(f20);

                    tessellator.addVertex((double) (f21 * 120.0F), (double) (f22 * 120.0F), (double) (-f22 * 40.0F * afloat[3]));
                }

                tessellator.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(7424);
            }

            GL11.glEnable(3553);
            GL11.glBlendFunc(770, 1);
            GL11.glPushMatrix();
            f6 = 1.0F - this.worldObj.getCelestialAngle(f);//.func_27162_g(f);
            f9 = 0.0F;
            f11 = 0.0F;
            f13 = 0.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f6);
            GL11.glTranslatef(f9, f11, f13);
            GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(this.worldObj.getCelestialAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F);
            f15 = 30.0F;
            GL11.glBindTexture(3553, this.renderEngine.getTexture("/assets/terrain/sun.png"));
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double) (-f15), 100.0D, (double) (-f15), 0.0D, 0.0D);
            tessellator.addVertexWithUV((double) f15, 100.0D, (double) (-f15), 1.0D, 0.0D);
            tessellator.addVertexWithUV((double) f15, 100.0D, (double) f15, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double) (-f15), 100.0D, (double) f15, 0.0D, 1.0D);
            tessellator.draw();
            f15 = 20.0F;
            GL11.glBindTexture(3553, this.renderEngine.getTexture("/assets/terrain/moon.png"));
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double) (-f15), -100.0D, (double) f15, 1.0D, 1.0D);
            tessellator.addVertexWithUV((double) f15, -100.0D, (double) f15, 0.0D, 1.0D);
            tessellator.addVertexWithUV((double) f15, -100.0D, (double) (-f15), 0.0D, 0.0D);
            tessellator.addVertexWithUV((double) (-f15), -100.0D, (double) (-f15), 1.0D, 0.0D);
            tessellator.draw();
            GL11.glDisable(3553);
            f17 = this.worldObj.getStarBrightness(f) * f6;
            if (f17 > 0.0F) {
                GL11.glColor4f(f17, f17, f17, f17);
                if (Config.isStarsEnabled()) {
                    GL11.glCallList(this.starGLCallList);
                }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(3008);
            GL11.glEnable(2912);
            GL11.glPopMatrix();
            if (this.worldObj.worldProvider.func_28112_c()) {
                GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
            } else {
                GL11.glColor3f(f1, f2, f3);
            }

            GL11.glDisable(3553);
            if (Config.isSkyEnabled()) {
                GL11.glCallList(this.glSkyList2);
            }

            GL11.glEnable(3553);
            GL11.glDepthMask(true);
        }
    }

    public void renderClouds(float f) {
        if (!this.mc.theWorld.worldProvider.isNether) {
            if (this.mc.gameSettings.ofClouds != 3) {
                if (Config.isCloudsFancy()) {
                    this.renderCloudsFancy(f);
                } else {
                    GL11.glDisable(2884);
                    float f1 = (float) (this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double) f);
                    byte byte0 = 32;
                    int i = 256 / byte0;
                    Tessellator tessellator = Tessellator.instance;

                    GL11.glBindTexture(3553, this.renderEngine.getTexture("/assets/environment/clouds.png"));
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    Vec3D vec3d = this.worldObj.drawClouds(f);//.func_628_d(f);
                    float f2 = (float) vec3d.xCoord;
                    float f3 = (float) vec3d.yCoord;
                    float f4 = (float) vec3d.zCoord;
                    float f6;

                    if (this.mc.gameSettings.anaglyph) {
                        f6 = (f2 * 30.0F + f3 * 59.0F + f4 * 11.0F) / 100.0F;
                        float d = (f2 * 30.0F + f3 * 70.0F) / 100.0F;
                        float f8 = (f2 * 30.0F + f4 * 70.0F) / 100.0F;

                        f2 = f6;
                        f3 = d;
                        f4 = f8;
                    }

                    f6 = 4.882813E-4F;
                    double d1 = this.mc.thePlayer.prevPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX) * (double) f + (double) (((float) this.cloudOffsetX + f) * 0.03F);
                    double d2 = this.mc.thePlayer.prevPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ) * (double) f;
                    int j = MathHelper.floor_double(d1 / 2048.0D);
                    int k = MathHelper.floor_double(d2 / 2048.0D);

                    d1 -= (double) (j * 2048);
                    d1 -= (double) (k * 2048);
                    float f9 = this.worldObj.worldProvider.getCloudHeight() - f1 + 0.33F;

                    f9 += this.mc.gameSettings.ofCloudsHeight * 25.0F;
                    float f10 = (float) (d1 * (double) f6);
                    float f11 = (float) (d2 * (double) f6);

                    tessellator.startDrawingQuads();
                    tessellator.setColorRGBA_F(f2, f3, f4, 0.8F);

                    for (int l = -byte0 * i; l < byte0 * i; l += byte0) {
                        for (int i1 = -byte0 * i; i1 < byte0 * i; i1 += byte0) {
                            tessellator.addVertexWithUV((double) (l + 0), (double) f9, (double) (i1 + byte0), (double) ((float) (l + 0) * f6 + f10), (double) ((float) (i1 + byte0) * f6 + f11));
                            tessellator.addVertexWithUV((double) (l + byte0), (double) f9, (double) (i1 + byte0), (double) ((float) (l + byte0) * f6 + f10), (double) ((float) (i1 + byte0) * f6 + f11));
                            tessellator.addVertexWithUV((double) (l + byte0), (double) f9, (double) (i1 + 0), (double) ((float) (l + byte0) * f6 + f10), (double) ((float) (i1 + 0) * f6 + f11));
                            tessellator.addVertexWithUV((double) (l + 0), (double) f9, (double) (i1 + 0), (double) ((float) (l + 0) * f6 + f10), (double) ((float) (i1 + 0) * f6 + f11));
                        }
                    }

                    tessellator.draw();
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3042);
                    GL11.glEnable(2884);
                }
            }
        }
    }

    public boolean func_27307_a(double d, double d1, double d2, float f) {
        return false;
    }

    public void renderCloudsFancy(float f) {
        GL11.glDisable(2884);
        float f1 = (float) (this.mc.thePlayer.lastTickPosY + (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * (double) f);
        Tessellator tessellator = Tessellator.instance;
        float f2 = 12.0F;
        float f3 = 4.0F;
        double d = (this.mc.thePlayer.prevPosX + (this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX) * (double) f + (double) (((float) this.cloudOffsetX + f) * 0.03F)) / (double) f2;
        double d1 = (this.mc.thePlayer.prevPosZ + (this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ) * (double) f) / (double) f2 + 0.33000001311302185D;
        float f4 = this.worldObj.worldProvider.getCloudHeight() - f1 + 0.33F;

        f4 += this.mc.gameSettings.ofCloudsHeight * 25.0F;
        int i = MathHelper.floor_double(d / 2048.0D);
        int j = MathHelper.floor_double(d1 / 2048.0D);

        d -= (double) (i * 2048);
        d1 -= (double) (j * 2048);
        GL11.glBindTexture(3553, this.renderEngine.getTexture("/assets/environment/clouds.png"));
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Vec3D vec3d = this.worldObj.drawClouds(f);//.func_628_d(f);
        float f5 = (float) vec3d.xCoord;
        float f6 = (float) vec3d.yCoord;
        float f7 = (float) vec3d.zCoord;
        float f9;
        float f11;
        float f13;

        if (this.mc.gameSettings.anaglyph) {
            f9 = (f5 * 30.0F + f6 * 59.0F + f7 * 11.0F) / 100.0F;
            f11 = (f5 * 30.0F + f6 * 70.0F) / 100.0F;
            f13 = (f5 * 30.0F + f7 * 70.0F) / 100.0F;
            f5 = f9;
            f6 = f11;
            f7 = f13;
        }

        f9 = (float) (d * 0.0D);
        f11 = (float) (d1 * 0.0D);
        f13 = 0.00390625F;
        f9 = (float) MathHelper.floor_double(d) * f13;
        f11 = (float) MathHelper.floor_double(d1) * f13;
        float f14 = (float) (d - (double) MathHelper.floor_double(d));
        float f15 = (float) (d1 - (double) MathHelper.floor_double(d1));
        byte k = 8;
        byte byte0 = 3;
        float f16 = 9.765625E-4F;

        GL11.glScalef(f2, 1.0F, f2);

        for (int l = 0; l < 2; ++l) {
            if (l == 0) {
                GL11.glColorMask(false, false, false, false);
            } else if (this.mc.gameSettings.anaglyph) {
                if (EntityRenderer.anaglyphField == 0) {
                    GL11.glColorMask(false, true, true, true);
                } else {
                    GL11.glColorMask(true, false, false, true);
                }
            } else {
                GL11.glColorMask(true, true, true, true);
            }

            double dd = 0.02D;

            for (int i1 = -byte0 + 1; i1 <= byte0; ++i1) {
                for (int j1 = -byte0 + 1; j1 <= byte0; ++j1) {
                    tessellator.startDrawingQuads();
                    float f17 = (float) (i1 * k);
                    float f18 = (float) (j1 * k);
                    float f19 = f17 - f14;
                    float f20 = f18 - f15;

                    tessellator.setColorRGBA_F(f5 * 0.9F, f6 * 0.9F, f7 * 0.9F, 0.8F);
                    int j2;

                    if (i1 > -1) {
                        tessellator.setNormal(-1.0F, 0.0F, 0.0F);

                        for (j2 = 0; j2 < k; ++j2) {
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 0.0F), (double) (f4 + 0.0F) + dd, (double) (f20 + (float) k), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 0.0F), (double) (f4 + f3) - dd, (double) (f20 + (float) k), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 0.0F), (double) (f4 + f3) - dd, (double) (f20 + 0.0F), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 0.0F), (double) (f4 + 0.0F) + dd, (double) (f20 + 0.0F), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                        }
                    }

                    if (i1 <= 1) {
                        tessellator.setNormal(1.0F, 0.0F, 0.0F);

                        for (j2 = 0; j2 < k; ++j2) {
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 1.0F - f16), (double) (f4 + 0.0F) + dd, (double) (f20 + (float) k), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 1.0F - f16), (double) (f4 + f3) - dd, (double) (f20 + (float) k), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 1.0F - f16), (double) (f4 + f3) - dd, (double) (f20 + 0.0F), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) j2 + 1.0F - f16), (double) (f4 + 0.0F) + dd, (double) (f20 + 0.0F), (double) ((f17 + (float) j2 + 0.5F) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                        }
                    }

                    tessellator.setColorRGBA_F(f5 * 0.8F, f6 * 0.8F, f7 * 0.8F, 0.8F);
                    if (j1 > -1) {
                        tessellator.setNormal(0.0F, 0.0F, -1.0F);

                        for (j2 = 0; j2 < k; ++j2) {
                            tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + f3) - dd, (double) (f20 + (float) j2 + 0.0F), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + f3) - dd, (double) (f20 + (float) j2 + 0.0F), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + 0.0F) + dd, (double) (f20 + (float) j2 + 0.0F), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + 0.0F) + dd, (double) (f20 + (float) j2 + 0.0F), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                        }
                    }

                    if (j1 <= 1) {
                        tessellator.setNormal(0.0F, 0.0F, 1.0F);

                        for (j2 = 0; j2 < k; ++j2) {
                            tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + f3) - dd, (double) (f20 + (float) j2 + 1.0F - f16), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + f3) - dd, (double) (f20 + (float) j2 + 1.0F - f16), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + 0.0F) + dd, (double) (f20 + (float) j2 + 1.0F - f16), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                            tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + 0.0F) + dd, (double) (f20 + (float) j2 + 1.0F - f16), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + (float) j2 + 0.5F) * f13 + f11));
                        }
                    }

                    if (f4 > -f3 - 1.0F) {
                        tessellator.setColorRGBA_F(f5 * 0.7F, f6 * 0.7F, f7 * 0.7F, 0.8F);
                        tessellator.setNormal(0.0F, -1.0F, 0.0F);
                        tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + 0.0F), (double) (f20 + (float) k), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                        tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + 0.0F), (double) (f20 + (float) k), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                        tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + 0.0F), (double) (f20 + 0.0F), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                        tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + 0.0F), (double) (f20 + 0.0F), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                    }

                    if (f4 <= f3 + 1.0F) {
                        tessellator.setColorRGBA_F(f5, f6, f7, 0.8F);
                        tessellator.setNormal(0.0F, 1.0F, 0.0F);
                        tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + f3 - f16), (double) (f20 + (float) k), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                        tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + f3 - f16), (double) (f20 + (float) k), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + (float) k) * f13 + f11));
                        tessellator.addVertexWithUV((double) (f19 + (float) k), (double) (f4 + f3 - f16), (double) (f20 + 0.0F), (double) ((f17 + (float) k) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                        tessellator.addVertexWithUV((double) (f19 + 0.0F), (double) (f4 + f3 - f16), (double) (f20 + 0.0F), (double) ((f17 + 0.0F) * f13 + f9), (double) ((f18 + 0.0F) * f13 + f11));
                    }

                    tessellator.draw();
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
    }

    public boolean updateRenderers(EntityLiving entityliving, boolean flag) {
        if (this.worldRenderersToUpdate.size() <= 0) {
            return false;
        } else {
            int num = 0;
            int maxNum = Config.getUpdatesPerFrame();

            if (Config.isDynamicUpdates() && !this.isMoving(entityliving)) {
                maxNum *= 3;
            }

            byte NOT_IN_FRUSTRUM_MUL = 4;
            int numValid = 0;
            WorldRenderer wrBest = null;
            float distSqBest = Float.MAX_VALUE;
            int indexBest = -1;

            int dstIndex;

            for (dstIndex = 0; dstIndex < this.worldRenderersToUpdate.size(); ++dstIndex) {
                WorldRenderer i = (WorldRenderer) this.worldRenderersToUpdate.get(dstIndex);

                if (i != null) {
                    ++numValid;
                    if (!i.needsUpdate) {
                        this.worldRenderersToUpdate.set(dstIndex, (Object) null);
                    } else {
                        float wr = i.distanceToEntitySquared(entityliving);

                        if (wr <= 256.0F && this.isActingNow()) {
                            i.updateRenderer();
                            i.needsUpdate = false;
                            this.worldRenderersToUpdate.set(dstIndex, (Object) null);
                            ++num;
                        } else {
                            if (wr > 256.0F && num >= maxNum) {
                                break;
                            }

                            if (!i.isInFrustum) {
                                wr *= (float) NOT_IN_FRUSTRUM_MUL;
                            }

                            if (wrBest == null) {
                                wrBest = i;
                                distSqBest = wr;
                                indexBest = dstIndex;
                            } else if (wr < distSqBest) {
                                wrBest = i;
                                distSqBest = wr;
                                indexBest = dstIndex;
                            }
                        }
                    }
                }
            }

            int i;

            if (wrBest != null) {
                wrBest.updateRenderer();
                wrBest.needsUpdate = false;
                this.worldRenderersToUpdate.set(indexBest, (Object) null);
                ++num;
                float f = distSqBest / 5.0F;

                for (i = 0; i < this.worldRenderersToUpdate.size() && num < maxNum; ++i) {
                    WorldRenderer worldrenderer = (WorldRenderer) this.worldRenderersToUpdate.get(i);

                    if (worldrenderer != null) {
                        float distSq = worldrenderer.distanceToEntitySquared(entityliving);

                        if (!worldrenderer.isInFrustum) {
                            distSq *= (float) NOT_IN_FRUSTRUM_MUL;
                        }

                        float diffDistSq = Math.abs(distSq - distSqBest);

                        if (diffDistSq < f) {
                            worldrenderer.updateRenderer();
                            worldrenderer.needsUpdate = false;
                            this.worldRenderersToUpdate.set(i, (Object) null);
                            ++num;
                        }
                    }
                }
            }

            if (numValid == 0) {
                this.worldRenderersToUpdate.clear();
            }

            if (this.worldRenderersToUpdate.size() > 100 && numValid < this.worldRenderersToUpdate.size() * 4 / 5) {
                dstIndex = 0;

                for (i = 0; i < this.worldRenderersToUpdate.size(); ++i) {
                    Object object = this.worldRenderersToUpdate.get(i);

                    if (object != null && i != dstIndex) {
                        this.worldRenderersToUpdate.set(dstIndex, object);
                        ++dstIndex;
                    }
                }

                for (i = this.worldRenderersToUpdate.size() - 1; i >= dstIndex; --i) {
                    this.worldRenderersToUpdate.remove(i);
                }
            }

            return true;
        }
    }

    private boolean isMoving(EntityLiving entityliving) {
        boolean moving = this.isMovingNow(entityliving);

        if (moving) {
            this.lastMovedTime = System.currentTimeMillis();
            return true;
        } else {
            return System.currentTimeMillis() - this.lastMovedTime < 2000L;
        }
    }

    private boolean isMovingNow(EntityLiving entityliving) {
        double maxDiff = 0.001D;

        return entityliving.isJumping ? true : 
                (entityliving.isSneaking() ? true : 
                ((double) entityliving.prevSwingProgress > maxDiff ? true : 
                (this.mc.mouseHelper.deltaX != 0 ? true : 
                (this.mc.mouseHelper.deltaY != 0 ? true : 
                (Math.abs(entityliving.posX - entityliving.prevPosX) > maxDiff ? true : 
                (Math.abs(entityliving.posY - entityliving.prevPosY) > maxDiff ? true : 
                Math.abs(entityliving.posZ - entityliving.prevPosZ) > maxDiff))))));
    }

    private boolean isActingNow() {
        return Mouse.isButtonDown(0) ? true : Mouse.isButtonDown(1);
    }

    public void drawBlockBreaking(EntityPlayer entityplayer, MovingObjectPosition movingobjectposition, int i, ItemStack itemstack, float f) {
        Tessellator tessellator = Tessellator.instance;

        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float) System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
        int l;

        if (i == 0) {
            if (this.damagePartialTime > 0.0F) {
                GL11.glBlendFunc(774, 768);
                int f1 = this.renderEngine.getTexture("/assets/terrain.png");

                GL11.glBindTexture(3553, f1);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                GL11.glPushMatrix();
                l = this.worldObj.getBlockId(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
                Block i1 = l <= 0 ? null : Block.blocksList[l];

                GL11.glDisable(3008);
                GL11.glPolygonOffset(-3.0F, -3.0F);
                GL11.glEnable('耷');
                double j1 = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double) f;
                double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double) f;
                double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double) f;

                if (i1 == null) {
                    i1 = Block.stone;
                }

                GL11.glEnable(3008);
                tessellator.startDrawingQuads();
                tessellator.setTranslationD(-j1, -d1, -d2);
                tessellator.disableColor();
                this.globalRenderBlocks.renderBlockUsingTexture(i1, movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ, 240 + (int) (this.damagePartialTime * 10.0F));
                tessellator.draw();
                tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
                GL11.glDisable(3008);
                GL11.glPolygonOffset(0.0F, 0.0F);
                GL11.glDisable('耷');
                GL11.glEnable(3008);
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
            }
        } else if (itemstack != null) {
            GL11.glBlendFunc(770, 771);
            float ff = MathHelper.sin((float) System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;

            GL11.glColor4f(ff, ff, ff, MathHelper.sin((float) System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
            l = this.renderEngine.getTexture("/assets/terrain.png");
            GL11.glBindTexture(3553, l);
            int i1 = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k1 = movingobjectposition.blockZ;

            if (movingobjectposition.sideHit == 0) {
                --j;
            }

            if (movingobjectposition.sideHit == 1) {
                ++j;
            }

            if (movingobjectposition.sideHit == 2) {
                --k1;
            }

            if (movingobjectposition.sideHit == 3) {
                ++k1;
            }

            if (movingobjectposition.sideHit == 4) {
                --i1;
            }

            if (movingobjectposition.sideHit == 5) {
                ++i1;
            }
        }

        GL11.glDisable(3042);
        GL11.glDisable(3008);
    }

    public void drawSelectionBox(EntityPlayer entityplayer, MovingObjectPosition movingobjectposition, int i, ItemStack itemstack, float f) {
        if (i == 0 && movingobjectposition.typeOfHit == EnumMovingObjectType.TILE) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDepthMask(false);
            float f1 = 0.002F;
            int j = this.worldObj.getBlockId(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);

            if (j > 0) {
                Block.blocksList[j].setBlockBoundsBasedOnState(this.worldObj, movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
                double d = entityplayer.lastTickPosX + (entityplayer.posX - entityplayer.lastTickPosX) * (double) f;
                double d1 = entityplayer.lastTickPosY + (entityplayer.posY - entityplayer.lastTickPosY) * (double) f;
                double d2 = entityplayer.lastTickPosZ + (entityplayer.posZ - entityplayer.lastTickPosZ) * (double) f;

                this.drawOutlinedBoundingBox(Block.blocksList[j].getSelectedBoundingBoxFromPool(this.worldObj, movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ).expand((double) f1, (double) f1, (double) f1).getOffsetBoundingBox(-d, -d1, -d2));
            }

            GL11.glDepthMask(true);
            GL11.glEnable(3553);
            GL11.glDisable(3042);
        }

    }

    private void drawOutlinedBoundingBox(AxisAlignedBB axisalignedbb) {
        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawing(3);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        tessellator.draw();
        tessellator.startDrawing(3);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        tessellator.draw();
        tessellator.startDrawing(1);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
        tessellator.addVertex(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
        tessellator.draw();
    }

    public void func_949_a(int i, int j, int k, int l, int i1, int j1) {
        int k1 = MathHelper.bucketInt(i, 16);
        int l1 = MathHelper.bucketInt(j, 16);
        int i2 = MathHelper.bucketInt(k, 16);
        int j2 = MathHelper.bucketInt(l, 16);
        int k2 = MathHelper.bucketInt(i1, 16);
        int l2 = MathHelper.bucketInt(j1, 16);

        for (int i3 = k1; i3 <= j2; ++i3) {
            int j3 = i3 % this.renderChunksWide;

            if (j3 < 0) {
                j3 += this.renderChunksWide;
            }

            for (int k3 = l1; k3 <= k2; ++k3) {
                int l3 = k3 % this.renderChunksTall;

                if (l3 < 0) {
                    l3 += this.renderChunksTall;
                }

                for (int i4 = i2; i4 <= l2; ++i4) {
                    int j4 = i4 % this.renderChunksDeep;

                    if (j4 < 0) {
                        j4 += this.renderChunksDeep;
                    }

                    int k4 = (j4 * this.renderChunksTall + l3) * this.renderChunksWide + j3;
                    WorldRenderer worldrenderer = this.worldRenderers[k4];

                    if (!worldrenderer.needsUpdate) {
                        this.worldRenderersToUpdate.add(worldrenderer);
                        worldrenderer.markDirty();
                    }
                }
            }
        }

    }

    public void markBlockAndNeighborsNeedsUpdate(int i, int j, int k) {
        this.func_949_a(i - 1, j - 1, k - 1, i + 1, j + 1, k + 1);
    }

    public void markBlockRangeNeedsUpdate(int i, int j, int k, int l, int i1, int j1) {
        this.func_949_a(i - 1, j - 1, k - 1, l + 1, i1 + 1, j1 + 1);
    }

    public void clipRenderersByFrustrum(ICamera icamera, float f) {
        for (int i = 0; i < this.worldRenderers.length; ++i) {
            if (!this.worldRenderers[i].skipAllRenderPasses()) {
                this.worldRenderers[i].updateInFrustrum(icamera);
            }
        }

        ++this.frustrumCheckOffset;
    }

    public void playRecord(String s, int i, int j, int k) {
        if (s != null) {
            this.mc.ingameGUI.setRecordPlayingMessage("C418 - " + s);
        }

        this.mc.sndManager.playStreaming(s, (float) i, (float) j, (float) k, 1.0F, 1.0F);
    }

    public void playSound(String s, double d, double d1, double d2, float f, float f1) {
        float f2 = 16.0F;

        if (f > 1.0F) {
            f2 *= f;
        }

        if (this.mc.thePlayer.getDistanceSq(d, d1, d2) < (double) (f2 * f2)) {
            this.mc.sndManager.playSound(s, (float) d, (float) d1, (float) d2, f, f1);
        }

    }

    @Override
    public void spawnParticle(String s, double d, double d1, double d2, double d3, double d4, double d5) {
        if (this.mc != null && this.mc.thePlayer != null && this.mc.effectRenderer != null) {
            double d6 = this.mc.thePlayer.posX - d;
            double d7 = this.mc.thePlayer.posY - d1;
            double d8 = this.mc.thePlayer.posZ - d2;
            double d9 = 16.0D;

            if (d6 * d6 + d7 * d7 + d8 * d8 <= d9 * d9) {
                switch (s) {
                    case "bubble":
                        this.mc.effectRenderer.addEffect(new EntityBubbleFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        break;
                    case "smoke":
                        if (Config.isAnimatedSmoke()) {
                            this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        }   break;
                    case "note":
                        this.mc.effectRenderer.addEffect(new EntityNoteFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        break;
                    case "portal":
                        this.mc.effectRenderer.addEffect(new EntityPortalFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        break;
                    case "explode":
                        if (Config.isAnimatedExplosion()) {
                            this.mc.effectRenderer.addEffect(new EntityExplodeFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        }   break;
                    case "flame":
                        if (Config.isAnimatedFlame()) {
                            this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        }   break;
                    case "lava":
                        this.mc.effectRenderer.addEffect(new EntityLavaFX(this.worldObj, d, d1, d2));
                        break;
                    case "footstep":
                        this.mc.effectRenderer.addEffect(new EntityFootStepFX(this.renderEngine, this.worldObj, d, d1, d2));
                        break;
                    case "splash":
                        this.mc.effectRenderer.addEffect(new EntitySplashFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        break;
                    case "largesmoke":
                        if (Config.isAnimatedSmoke()) {
                            this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, d, d1, d2, d3, d4, d5, 2.5F));
                    }   break;
                    case "reddust":
                        if (Config.isAnimatedRedstone()) {
                            this.mc.effectRenderer.addEffect(new EntityReddustFX(this.worldObj, d, d1, d2, (float) d3, (float) d4, (float) d5));
                    }   break;
                    case "snowballpoof":
                        this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, d, d1, d2, Item.snowball));
                        break;
                    case "snowshovel":
                        this.mc.effectRenderer.addEffect(new EntitySnowShovelFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        break;
                    case "slime":
                        this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, d, d1, d2, Item.slimeBall));
                        break;
                    case "heart":
                        this.mc.effectRenderer.addEffect(new EntityHeartFX(this.worldObj, d, d1, d2, d3, d4, d5));
                        break;
                }

            }
        }
    }

    public void obtainEntitySkin(Entity entity) {
        entity.updateCloak();
        if (entity.skinUrl != null) {
            this.renderEngine.obtainImageData(entity.skinUrl, new ImageBufferDownload());
        }

        if (entity.cloakUrl != null) {
            this.renderEngine.obtainImageData(entity.cloakUrl, new ImageBufferDownload());
        }

    }

    public void releaseEntitySkin(Entity entity) {
        if (entity.skinUrl != null) {
            this.renderEngine.releaseImageData(entity.skinUrl);
        }

        if (entity.cloakUrl != null) {
            this.renderEngine.releaseImageData(entity.cloakUrl);
        }

    }

    public void updateAllRenderers() {
        if (this.worldRenderers != null) {
            for (int i = 0; i < this.worldRenderers.length; ++i) {
                if (this.worldRenderers[i].isChunkLit && !this.worldRenderers[i].needsUpdate) {
                    this.worldRenderersToUpdate.add(this.worldRenderers[i]);
                    this.worldRenderers[i].markDirty();
                }
            }

        }
    }

    public void setAllRenderesVisible() {
        if (this.worldRenderers != null) {
            for (int i = 0; i < this.worldRenderers.length; ++i) {
                this.worldRenderers[i].isVisible = true;
            }

        }
    }

    public void doNothingWithTileEntity(int i, int j, int k, TileEntity tileentity) {
    }

    public void func_28137_f() {
        GLAllocation.func_28194_b(this.glRenderListBase);
    }

    @Override
    public void playAuxSFX(EntityPlayer entityplayer, int i, int j, int k, int l, int i1) {
        Random random = this.worldObj.rand;
        int i2;

        switch (i) {
            case 1000:
                this.worldObj.playSoundEffect((double) j, (double) k, (double) l, "random.click", 1.0F, 1.0F);
                break;

            case 1001:
                this.worldObj.playSoundEffect((double) j, (double) k, (double) l, "random.click", 1.0F, 1.2F);
                break;

            case 1002:
                this.worldObj.playSoundEffect((double) j, (double) k, (double) l, "random.bow", 1.0F, 1.2F);
                break;

            case 1003:
                if (Math.random() < 0.5D) {
                    this.worldObj.playSoundEffect((double) j + 0.5D, (double) k + 0.5D, (double) l + 0.5D, "random.door_open", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                } else {
                    this.worldObj.playSoundEffect((double) j + 0.5D, (double) k + 0.5D, (double) l + 0.5D, "random.door_close", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }
                break;

            case 1004:
                this.worldObj.playSoundEffect((double) ((float) j + 0.5F), (double) ((float) k + 0.5F), (double) ((float) l + 0.5F), "random.fizz", 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
                break;

            case 1005:
                if (Item.itemsList[i1] instanceof ItemRecord) {
                    this.worldObj.playRecord(((ItemRecord) Item.itemsList[i1]).recordName, j, k, l);
                } else {
                    this.worldObj.playRecord((String) null, j, k, l);
                }
                break;

            case 2000:
                int j1 = i1 % 3 - 1;
                int k1 = i1 / 3 % 3 - 1;
                double d = (double) j + (double) j1 * 0.6D + 0.5D;
                double d1 = (double) k + 0.5D;
                double d2 = (double) l + (double) k1 * 0.6D + 0.5D;

                for (i2 = 0; i2 < 10; ++i2) {
                    double d0 = random.nextDouble() * 0.2D + 0.01D;
                    double d4 = d + (double) j1 * 0.01D + (random.nextDouble() - 0.5D) * (double) k1 * 0.5D;
                    double d5 = d1 + (random.nextDouble() - 0.5D) * 0.5D;
                    double d6 = d2 + (double) k1 * 0.01D + (random.nextDouble() - 0.5D) * (double) j1 * 0.5D;
                    double d7 = (double) j1 * d0 + random.nextGaussian() * 0.01D;
                    double d8 = -0.03D + random.nextGaussian() * 0.01D;
                    double d9 = (double) k1 * d0 + random.nextGaussian() * 0.01D;

                    this.spawnParticle("smoke", d4, d5, d6, d7, d8, d9);
                }

                return;

            case 2001:
                i2 = i1 & 255;
                if (i2 > 0) {
                    Block block = Block.blocksList[i2];

                    this.mc.sndManager.playSound(block.stepSound.stepSoundDir(), (float) j + 0.5F, (float) k + 0.5F, (float) l + 0.5F, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(j, k, l, i1 & 255, i1 >> 8 & 255);
        }

    }

    public int renderAllSortedRenderers(int renderPass, double partialTicks) {
        return this.renderSortedRenderers(0, this.sortedWorldRenderers.length, renderPass, partialTicks);
    }
}
