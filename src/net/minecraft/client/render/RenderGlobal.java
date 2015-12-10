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
import net.minecraft.entity.EntityRenderer;
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
import net.minecraft.client.item.Item;
import net.minecraft.client.item.ItemRecord;
import net.minecraft.client.item.ItemStack;
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
    IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
    private int renderersLoaded;
    private int renderersBeingClipped;
    private int renderersBeingOccluded;
    private int renderersBeingRendered;
    private int renderersSkippingRenderPass;
    private int worldRenderersCheckIndex;
    private IntBuffer glRenderLists = BufferUtils.createIntBuffer(65536);
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

    public RenderGlobal(Minecraft var1, RenderEngine var2) {
        this.mc = var1;
        this.renderEngine = var2;
        byte var3 = 64;
        this.glRenderListBase = GLAllocation.generateDisplayLists(var3 * var3 * var3 * 3);
        this.occlusionEnabled = var1.getOpenGlCapsChecker().checkARBOcclusion();
        if (this.occlusionEnabled) {
            this.occlusionResult.clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(var3 * var3 * var3);
            this.glOcclusionQueryBase.clear();
            this.glOcclusionQueryBase.position(0);
            this.glOcclusionQueryBase.limit(var3 * var3 * var3);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, 4864 /*GL_COMPILE*/);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator var4 = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, 4864 /*GL_COMPILE*/);
        byte var6 = 64;
        int var7 = 256 / var6 + 2;
        float var5 = 16.0F;

        int var8;
        int var9;
        for (var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
            for (var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
                var4.startDrawingQuads();
                var4.addVertex((double) (var8 + 0), (double) var5, (double) (var9 + 0));
                var4.addVertex((double) (var8 + var6), (double) var5, (double) (var9 + 0));
                var4.addVertex((double) (var8 + var6), (double) var5, (double) (var9 + var6));
                var4.addVertex((double) (var8 + 0), (double) var5, (double) (var9 + var6));
                var4.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, 4864 /*GL_COMPILE*/);
        var5 = -16.0F;
        var4.startDrawingQuads();

        for (var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
            for (var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
                var4.addVertex((double) (var8 + var6), (double) var5, (double) (var9 + 0));
                var4.addVertex((double) (var8 + 0), (double) var5, (double) (var9 + 0));
                var4.addVertex((double) (var8 + 0), (double) var5, (double) (var9 + var6));
                var4.addVertex((double) (var8 + var6), (double) var5, (double) (var9 + var6));
            }
        }

        var4.draw();
        GL11.glEndList();
    }

    private void renderStars() {
        Random var1 = new Random(10842L);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();

        for (int var3 = 0; var3 < 1500; ++var3) {
            double var4 = (double) (var1.nextFloat() * 2.0F - 1.0F);
            double var6 = (double) (var1.nextFloat() * 2.0F - 1.0F);
            double var8 = (double) (var1.nextFloat() * 2.0F - 1.0F);
            double var10 = (double) (0.25F + var1.nextFloat() * 0.25F);
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;
            if (var12 < 1.0D && var12 > 0.01D) {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                double var14 = var4 * 100.0D;
                double var16 = var6 * 100.0D;
                double var18 = var8 * 100.0D;
                double var20 = Math.atan2(var4, var8);
                double var22 = Math.sin(var20);
                double var24 = Math.cos(var20);
                double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                double var28 = Math.sin(var26);
                double var30 = Math.cos(var26);
                double var32 = var1.nextDouble() * 3.141592653589793D * 2.0D;
                double var34 = Math.sin(var32);
                double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38) {
                    double var39 = 0.0D;
                    double var41 = (double) ((var38 & 2) - 1) * var10;
                    double var43 = (double) ((var38 + 1 & 2) - 1) * var10;
                    double var47 = var41 * var36 - var43 * var34;
                    double var49 = var43 * var36 + var41 * var34;
                    double var53 = var47 * var28 + var39 * var30;
                    double var55 = var39 * var28 - var47 * var30;
                    double var57 = var55 * var22 - var49 * var24;
                    double var61 = var49 * var22 + var55 * var24;
                    var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
                }
            }
        }

        var2.draw();
    }

    public void changeWorld(World var1) {
        if (this.worldObj != null) {
            this.worldObj.removeWorldAccess(this);
        }

        this.prevSortX = -9999.0D;
        this.prevSortY = -9999.0D;
        this.prevSortZ = -9999.0D;
        RenderManager.instance.set(var1);
        this.worldObj = var1;
        this.globalRenderBlocks = new RenderBlocks(var1);
        if (var1 != null) {
            var1.addWorldAccess(this);
            this.loadRenderers();
        }

    }

    public void setAllRenderesVisible() {
        if (this.worldRenderers != null) {
            for (int i = 0; i < this.worldRenderers.length; ++i) {
                this.worldRenderers[i].isVisible = true;
            }
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
        int var2 = 0;
        int var3 = 0;
        this.minBlockX = 0;
        this.minBlockY = 0;
        this.minBlockZ = 0;
        this.maxBlockX = this.renderChunksWide;
        this.maxBlockY = this.renderChunksTall;
        this.maxBlockZ = this.renderChunksDeep;

        int var4;
        for (var4 = 0; var4 < this.worldRenderersToUpdate.size(); ++var4) {
            WorldRenderer k1 = (WorldRenderer) this.worldRenderersToUpdate.get(var4);

            if (k1 != null) {
                k1.needsUpdate = false;
            }
        }

        this.worldRenderersToUpdate.clear();
        this.tileEntities.clear();

        for (var4 = 0; var4 < this.renderChunksWide; ++var4) {
            for (int var5 = 0; var5 < this.renderChunksTall; ++var5) {
                for (int var6 = 0; var6 < this.renderChunksDeep; ++var6) {
                    int wri = (var6 * this.renderChunksTall + var5) * this.renderChunksWide + var4;

                    this.worldRenderers[wri] = new WorldRenderer(this.worldObj, this.tileEntities, var4 * 16, var5 * 16, var6 * 16, 16, this.glRenderListBase + var2);
                    if (this.occlusionEnabled) {
                        this.worldRenderers[wri].glOcclusionQuery = this.glOcclusionQueryBase.get(var3);
                    }

                    this.worldRenderers[wri].isWaitingOnOcclusionQuery = false;
                    this.worldRenderers[wri].isVisible = true;
                    this.worldRenderers[wri].isInFrustum = false;
                    this.worldRenderers[wri].chunkIndex = var3++;
                    this.worldRenderers[wri].markDirty();
                    this.sortedWorldRenderers[wri] = this.worldRenderers[wri];
                    this.worldRenderersToUpdate.add(this.worldRenderers[wri]);
                    var2 += 3;
                }
            }
        }

        if (this.worldObj != null) {
            EntityLiving var7 = this.mc.renderViewEntity;

            if (var7 == null) {
                var7 = this.mc.thePlayer;
            }

            if (var7 != null) {
                this.markRenderersForNewPosition(MathHelper.floor_double(var7.posX), MathHelper.floor_double(var7.posY), MathHelper.floor_double(var7.posZ));
                Arrays.sort(this.sortedWorldRenderers, new EntitySorter(var7));
            }
        }

        this.renderEntitiesStartupCounter = 2;
    }

    public void renderEntities(Vec3D var1, ICamera var2, float var3) {
        if (this.renderEntitiesStartupCounter > 0) {
            --this.renderEntitiesStartupCounter;
        } else {
            TileEntityRenderer.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, var3);
            RenderManager.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.gameSettings, var3);
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            EntityLiving var4 = this.mc.renderViewEntity;
            RenderManager.renderPosX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double) var3;
            RenderManager.renderPosY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double) var3;
            RenderManager.renderPosZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double) var3;
            TileEntityRenderer.staticPlayerX = var4.lastTickPosX + (var4.posX - var4.lastTickPosX) * (double) var3;
            TileEntityRenderer.staticPlayerY = var4.lastTickPosY + (var4.posY - var4.lastTickPosY) * (double) var3;
            TileEntityRenderer.staticPlayerZ = var4.lastTickPosZ + (var4.posZ - var4.lastTickPosZ) * (double) var3;
            List var5 = this.worldObj.getLoadedEntityList();
            this.countEntitiesTotal = var5.size();

            int var6;
            Entity var7;
            for (var6 = 0; var6 < this.worldObj.weatherEffects.size(); ++var6) {
                var7 = (Entity) this.worldObj.weatherEffects.get(var6);
                ++this.countEntitiesRendered;
                if (var7.isInRangeToRenderVec3D(var1)) {
                    RenderManager.instance.renderEntity(var7, var3);
                }
            }

            for (var6 = 0; var6 < var5.size(); ++var6) {
                var7 = (Entity) var5.get(var6);
                if (var7.isInRangeToRenderVec3D(var1) && (var7.ignoreFrustumCheck || var2.isBoundingBoxInFrustum(var7.boundingBox)) && (var7 != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView || this.mc.renderViewEntity.isPlayerSleeping())) {
                    int var8 = MathHelper.floor_double(var7.posY);
                    if (var8 < 0) {
                        var8 = 0;
                    }

                    if (var8 >= 128) {
                        var8 = 127;
                    }

                    if (this.worldObj.blockExists(MathHelper.floor_double(var7.posX), var8, MathHelper.floor_double(var7.posZ))) {
                        ++this.countEntitiesRendered;
                        RenderManager.instance.renderEntity(var7, var3);
                    }
                }
            }

            for (var6 = 0; var6 < this.tileEntities.size(); ++var6) {
                TileEntityRenderer.instance.renderTileEntity((TileEntity) this.tileEntities.get(var6), var3);
            }

        }
    }

    public String getDebugInfoRenders() {
        return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
    }

    public String getDebugInfoEntities() {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
    }

    private void markRenderersForNewPosition(int var1, int var2, int var3) {
        var1 -= 8;
        var2 -= 8;
        var3 -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int var4 = this.renderChunksWide * 16;
        int var5 = var4 / 2;

        for (int var6 = 0; var6 < this.renderChunksWide; ++var6) {
            int var7 = var6 * 16;
            int var8 = var7 + var5 - var1;
            if (var8 < 0) {
                var8 -= var4 - 1;
            }

            var8 /= var4;
            var7 -= var8 * var4;
            if (var7 < this.minBlockX) {
                this.minBlockX = var7;
            }

            if (var7 > this.maxBlockX) {
                this.maxBlockX = var7;
            }

            for (int var9 = 0; var9 < this.renderChunksDeep; ++var9) {
                int var10 = var9 * 16;
                int var11 = var10 + var5 - var3;
                if (var11 < 0) {
                    var11 -= var4 - 1;
                }

                var11 /= var4;
                var10 -= var11 * var4;
                if (var10 < this.minBlockZ) {
                    this.minBlockZ = var10;
                }

                if (var10 > this.maxBlockZ) {
                    this.maxBlockZ = var10;
                }

                for (int var12 = 0; var12 < this.renderChunksTall; ++var12) {
                    int var13 = var12 * 16;
                    if (var13 < this.minBlockY) {
                        this.minBlockY = var13;
                    }

                    if (var13 > this.maxBlockY) {
                        this.maxBlockY = var13;
                    }

                    WorldRenderer var14 = this.worldRenderers[(var9 * this.renderChunksTall + var12) * this.renderChunksWide + var6];
                    boolean var15 = var14.needsUpdate;
                    var14.setPosition(var7, var13, var10);
                    if (!var15 && var14.needsUpdate) {
                        this.worldRenderersToUpdate.add(var14);
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

        double partialX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double partialY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double partialZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        double dSortX = player.posX - this.prevSortX;
        double dSortY = player.posY - this.prevSortY;
        double dSortZ = player.posZ - this.prevSortZ;
        int num;
        if (dSortX * dSortX + dSortY * dSortY + dSortZ * dSortZ > 16.0D) {
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
        int i = 0;
        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && renderPass == 0) {
            byte firstIndex = 0;
            int b1 = 20;
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

                            bbX = (float) ((double) wr.posXMinus - partialX);
                            bbY = (float) ((double) wr.posYMinus - partialY);
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
                            ++i;
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
        this.glRenderLists.clear();
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
                    this.glRenderLists.put(partialX);
                    ++l;
                }
            }
        }

        this.glRenderLists.flip();
        EntityLiving entityliving = this.mc.renderViewEntity;
        double d0 = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * partialTicks;
        double partialY = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * partialTicks;
        double partialZ = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * partialTicks;

        GL11.glTranslatef((float) (-d0), (float) (-partialY), (float) (-partialZ));
        GL11.glCallLists(this.glRenderLists);
        GL11.glTranslatef((float) d0, (float) partialY, (float) partialZ);
        return l;
    }

    public void renderAllRenderLists(int var1, double var2) {
    }

    public void updateClouds() {
        ++this.cloudOffsetX;
    }

    public void renderSky(float f) {
        if (!this.mc.theWorld.worldProvider.isNether) {
            GL11.glDisable(3553);
            Vec3D vec3d = this.worldObj.getSkyColor(this.mc.renderViewEntity, f);
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
            f6 = 1.0F - this.worldObj.getRainStrength(f);
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
                    float f1 = (float) (this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double) f);
                    byte byte0 = 32;
                    int i = 256 / byte0;
                    Tessellator tessellator = Tessellator.instance;

                    GL11.glBindTexture(3553, this.renderEngine.getTexture("/assets/environment/clouds.png"));
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    Vec3D vec3d = this.worldObj.drawClouds(f);
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
                    double d1 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double) f + (double) (((float) this.cloudOffsetX + f) * 0.03F);
                    double d2 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double) f;
                    int j = MathHelper.floor_double(d1 / 2048.0D);
                    int k = MathHelper.floor_double(d2 / 2048.0D);

                    d1 -= (double) (j * 2048);
                    d2 -= (double) (k * 2048);
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

    public boolean func_27307_a(double var1, double var3, double var5, float var7) {
        return false;
    }

    public void renderCloudsFancy(float f) {
        GL11.glDisable(2884);
        float f1 = (float) (this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double) f);
        Tessellator tessellator = Tessellator.instance;
        float f2 = 12.0F;
        float f3 = 4.0F;
        double d = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double) f + (double) (((float) this.cloudOffsetX + f) * 0.03F)) / (double) f2;
        double d1 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double) f) / (double) f2 + 0.33000001311302185D;
        float f4 = this.worldObj.worldProvider.getCloudHeight() - f1 + 0.33F;

        f4 += this.mc.gameSettings.ofCloudsHeight * 25.0F;
        int i = MathHelper.floor_double(d / 2048.0D);
        int j = MathHelper.floor_double(d1 / 2048.0D);

        d -= (double) (i * 2048);
        d1 -= (double) (j * 2048);
        GL11.glBindTexture(3553, this.renderEngine.getTexture("/assets/environment/clouds.png"));
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Vec3D vec3d = this.worldObj.drawClouds(f);
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

        return entityliving.isJumping ? true
                : (entityliving.isSneaking() ? true
                        : ((double) entityliving.prevSwingProgress > maxDiff ? true
                                : (this.mc.mouseHelper.deltaX != 0 ? true
                                        : (this.mc.mouseHelper.deltaY != 0 ? true
                                                : (Math.abs(entityliving.posX - entityliving.prevPosX) > maxDiff ? true
                                                        : (Math.abs(entityliving.posY - entityliving.prevPosY) > maxDiff ? true
                                                                : Math.abs(entityliving.posZ - entityliving.prevPosZ) > maxDiff))))));
    }

    private boolean isActingNow() {
        return Mouse.isButtonDown(0) ? true : Mouse.isButtonDown(1);
    }

    public void drawBlockBreaking(EntityPlayer var1, MovingObjectPosition var2, int var3, ItemStack var4, float var5) {
        Tessellator var6 = Tessellator.instance;
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float) System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
        int var8;
        if (var3 == 0) {
            if (this.damagePartialTime > 0.0F) {
                GL11.glBlendFunc(774, 768);
                int var7 = this.renderEngine.getTexture(Minecraft.TERRAIN_TEXTURE);
                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var7);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                GL11.glPushMatrix();
                var8 = this.worldObj.getBlockId(var2.blockX, var2.blockY, var2.blockZ);
                Block var9 = var8 > 0 ? Block.blocksList[var8] : null;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                GL11.glPolygonOffset(-3.0F, -3.0F);
                GL11.glEnable('\u8037');
                double var10 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double) var5;
                double var12 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double) var5;
                double var14 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double) var5;
                if (var9 == null) {
                    var9 = Block.stone;
                }

                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                var6.startDrawingQuads();
                var6.setTranslationD(-var10, -var12, -var14);
                var6.disableColor();
                this.globalRenderBlocks.renderBlockUsingTexture(var9, var2.blockX, var2.blockY, var2.blockZ, 240 + (int) (this.damagePartialTime * 10.0F));
                var6.draw();
                var6.setTranslationD(0.0D, 0.0D, 0.0D);
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                GL11.glPolygonOffset(0.0F, 0.0F);
                GL11.glDisable('\u8037');
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                GL11.glDepthMask(true);
                GL11.glPopMatrix();
            }
        } else if (var4 != null) {
            GL11.glBlendFunc(770, 771);
            float var16 = MathHelper.sin((float) System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;
            GL11.glColor4f(var16, var16, var16, MathHelper.sin((float) System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
            var8 = this.renderEngine.getTexture(Minecraft.TERRAIN_TEXTURE);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, var8);
        }
        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
    }

    public void drawSelectionBox(EntityPlayer var1, MovingObjectPosition var2, int var3, ItemStack var4, float var5) {
        if (var3 == 0 && var2.typeOfHit == EnumMovingObjectType.TILE) {
            GL11.glEnable(3042 /*GL_BLEND*/);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
            GL11.glDepthMask(false);
            float var6 = 0.002F;
            int var7 = this.worldObj.getBlockId(var2.blockX, var2.blockY, var2.blockZ);
            if (var7 > 0) {
                Block.blocksList[var7].setBlockBoundsBasedOnState(this.worldObj, var2.blockX, var2.blockY, var2.blockZ);
                double var8 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double) var5;
                double var10 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double) var5;
                double var12 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double) var5;
                this.drawOutlinedBoundingBox(Block.blocksList[var7].getSelectedBoundingBoxFromPool(this.worldObj, var2.blockX, var2.blockY, var2.blockZ).expand((double) var6, (double) var6, (double) var6).getOffsetBoundingBox(-var8, -var10, -var12));
            }

            GL11.glDepthMask(true);
            GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
            GL11.glDisable(3042 /*GL_BLEND*/);
        }

    }

    private void drawOutlinedBoundingBox(AxisAlignedBB var1) {
        Tessellator var2 = Tessellator.instance;
        var2.startDrawing(3);
        var2.addVertex(var1.minX, var1.minY, var1.minZ);
        var2.addVertex(var1.maxX, var1.minY, var1.minZ);
        var2.addVertex(var1.maxX, var1.minY, var1.maxZ);
        var2.addVertex(var1.minX, var1.minY, var1.maxZ);
        var2.addVertex(var1.minX, var1.minY, var1.minZ);
        var2.draw();
        var2.startDrawing(3);
        var2.addVertex(var1.minX, var1.maxY, var1.minZ);
        var2.addVertex(var1.maxX, var1.maxY, var1.minZ);
        var2.addVertex(var1.maxX, var1.maxY, var1.maxZ);
        var2.addVertex(var1.minX, var1.maxY, var1.maxZ);
        var2.addVertex(var1.minX, var1.maxY, var1.minZ);
        var2.draw();
        var2.startDrawing(1);
        var2.addVertex(var1.minX, var1.minY, var1.minZ);
        var2.addVertex(var1.minX, var1.maxY, var1.minZ);
        var2.addVertex(var1.maxX, var1.minY, var1.minZ);
        var2.addVertex(var1.maxX, var1.maxY, var1.minZ);
        var2.addVertex(var1.maxX, var1.minY, var1.maxZ);
        var2.addVertex(var1.maxX, var1.maxY, var1.maxZ);
        var2.addVertex(var1.minX, var1.minY, var1.maxZ);
        var2.addVertex(var1.minX, var1.maxY, var1.maxZ);
        var2.draw();
    }

    public void markBlocksForUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
        int var7 = MathHelper.bucketInt(var1, 16);
        int var8 = MathHelper.bucketInt(var2, 16);
        int var9 = MathHelper.bucketInt(var3, 16);
        int var10 = MathHelper.bucketInt(var4, 16);
        int var11 = MathHelper.bucketInt(var5, 16);
        int var12 = MathHelper.bucketInt(var6, 16);

        for (int var13 = var7; var13 <= var10; ++var13) {
            int var14 = var13 % this.renderChunksWide;
            if (var14 < 0) {
                var14 += this.renderChunksWide;
            }

            for (int var15 = var8; var15 <= var11; ++var15) {
                int var16 = var15 % this.renderChunksTall;
                if (var16 < 0) {
                    var16 += this.renderChunksTall;
                }

                for (int var17 = var9; var17 <= var12; ++var17) {
                    int var18 = var17 % this.renderChunksDeep;
                    if (var18 < 0) {
                        var18 += this.renderChunksDeep;
                    }

                    int var19 = (var18 * this.renderChunksTall + var16) * this.renderChunksWide + var14;
                    WorldRenderer var20 = this.worldRenderers[var19];
                    if (!var20.needsUpdate) {
                        this.worldRenderersToUpdate.add(var20);
                        var20.markDirty();
                    }
                }
            }
        }

    }

    public void markBlockAndNeighborsNeedsUpdate(int var1, int var2, int var3) {
        this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
    }

    public void markBlockRangeNeedsUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
        this.markBlocksForUpdate(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1);
    }

    public void clipRenderersByFrustrum(ICamera var1, float var2) {
        for (int var3 = 0; var3 < this.worldRenderers.length; ++var3) {
            if (!this.worldRenderers[var3].skipAllRenderPasses() && (!this.worldRenderers[var3].isInFrustum || (var3 + this.frustrumCheckOffset & 15) == 0)) {
                this.worldRenderers[var3].updateInFrustrum(var1);
            }
        }

        ++this.frustrumCheckOffset;
    }

    public void playRecord(String var1, int var2, int var3, int var4) {
        if (var1 != null) {
            this.mc.ingameGUI.setRecordPlayingMessage("C418 - " + var1);
        }

        this.mc.sndManager.playStreaming(var1, (float) var2, (float) var3, (float) var4, 1.0F, 1.0F);
    }

    public void playSound(String var1, double var2, double var4, double var6, float var8, float var9) {
        float var10 = 16.0F;
        if (var8 > 1.0F) {
            var10 *= var8;
        }

        if (this.mc.renderViewEntity.getDistanceSq(var2, var4, var6) < (double) (var10 * var10)) {
            this.mc.sndManager.playSound(var1, (float) var2, (float) var4, (float) var6, var8, var9);
        }

    }

    public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
        if (this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null) {
            double var14 = this.mc.renderViewEntity.posX - var2;
            double var16 = this.mc.renderViewEntity.posY - var4;
            double var18 = this.mc.renderViewEntity.posZ - var6;
            double var20 = 16.0D;
            if (var14 * var14 + var16 * var16 + var18 * var18 <= var20 * var20) {
                if (var1.equals("bubble")) {
                    this.mc.effectRenderer.addEffect(new EntityBubbleFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                } else if (var1.equals("smoke")) {
                    this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                } else if (var1.equals("note")) {
                    if (Config.isAnimatedSmoke()) {
                        this.mc.effectRenderer.addEffect(new EntityNoteFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                    }
                } else if (var1.equals("portal")) {
                    this.mc.effectRenderer.addEffect(new EntityPortalFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                } else if (var1.equals("explode")) {
                    if (Config.isAnimatedExplosion()) {
                        this.mc.effectRenderer.addEffect(new EntityExplodeFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                    }
                } else if (var1.equals("flame")) {
                    if (Config.isAnimatedFlame()) {
                        this.mc.effectRenderer.addEffect(new EntityFlameFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                    }
                } else if (var1.equals("lava")) {
                    this.mc.effectRenderer.addEffect(new EntityLavaFX(this.worldObj, var2, var4, var6));
                } else if (var1.equals("footstep")) {
                    this.mc.effectRenderer.addEffect(new EntityFootStepFX(this.renderEngine, this.worldObj, var2, var4, var6));
                } else if (var1.equals("splash")) {
                    this.mc.effectRenderer.addEffect(new EntitySplashFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                } else if (var1.equals("largesmoke")) {
                    if (Config.isAnimatedSmoke()) {
                        this.mc.effectRenderer.addEffect(new EntitySmokeFX(this.worldObj, var2, var4, var6, var8, var10, var12, 2.5F));
                    }
                } else if (var1.equals("reddust")) {
                    if (Config.isAnimatedRedstone()) {
                        this.mc.effectRenderer.addEffect(new EntityReddustFX(this.worldObj, var2, var4, var6, (float) var8, (float) var10, (float) var12));
                    }
                } else if (var1.equals("snowballpoof")) {
                    this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, var2, var4, var6, Item.snowball));
                } else if (var1.equals("snowshovel")) {
                    this.mc.effectRenderer.addEffect(new EntitySnowShovelFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                } else if (var1.equals("slime")) {
                    this.mc.effectRenderer.addEffect(new EntitySlimeFX(this.worldObj, var2, var4, var6, Item.slimeBall));
                } else if (var1.equals("heart")) {
                    this.mc.effectRenderer.addEffect(new EntityHeartFX(this.worldObj, var2, var4, var6, var8, var10, var12));
                }

            }
        }
    }

    public void obtainEntitySkin(Entity var1) {
        var1.updateCloak();
        if (var1.skinUrl != null) {
            this.renderEngine.obtainImageData(var1.skinUrl, new ImageBufferDownload());
        }

        if (var1.cloakUrl != null) {
            this.renderEngine.obtainImageData(var1.cloakUrl, new ImageBufferDownload());
        }

    }

    public void releaseEntitySkin(Entity var1) {
        if (var1.skinUrl != null) {
            this.renderEngine.releaseImageData(var1.skinUrl);
        }

        if (var1.cloakUrl != null) {
            this.renderEngine.releaseImageData(var1.cloakUrl);
        }

    }

    public void updateAllRenderers() {
        for (int var1 = 0; var1 < this.worldRenderers.length; ++var1) {
            if (this.worldRenderers[var1].isChunkLit && !this.worldRenderers[var1].needsUpdate) {
                this.worldRenderersToUpdate.add(this.worldRenderers[var1]);
                this.worldRenderers[var1].markDirty();
            }
        }

    }

    public void doNothingWithTileEntity(int var1, int var2, int var3, TileEntity var4) {
    }

    public void func_28137_f() {
        GLAllocation.func_28194_b(this.glRenderListBase);
    }

    public void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
        Random var7 = this.worldObj.rand;
        int var16;
        switch (var2) {
            case 1000:
                this.worldObj.playSoundEffect((double) var3, (double) var4, (double) var5, "random.click", 1.0F, 1.0F);
                break;
            case 1001:
                this.worldObj.playSoundEffect((double) var3, (double) var4, (double) var5, "random.click", 1.0F, 1.2F);
                break;
            case 1002:
                this.worldObj.playSoundEffect((double) var3, (double) var4, (double) var5, "random.bow", 1.0F, 1.2F);
                break;
            case 1003:
                if (Math.random() < 0.5D) {
                    this.worldObj.playSoundEffect((double) var3 + 0.5D, (double) var4 + 0.5D, (double) var5 + 0.5D, "random.door_open", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                } else {
                    this.worldObj.playSoundEffect((double) var3 + 0.5D, (double) var4 + 0.5D, (double) var5 + 0.5D, "random.door_close", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
                }
                break;
            case 1004:
                this.worldObj.playSoundEffect((double) ((float) var3 + 0.5F), (double) ((float) var4 + 0.5F), (double) ((float) var5 + 0.5F), "random.fizz", 0.5F, 2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F);
                break;
            case 1005:
                if (Item.itemsList[var6] instanceof ItemRecord) {
                    this.worldObj.playRecord(((ItemRecord) Item.itemsList[var6]).recordName, var3, var4, var5);
                } else {
                    this.worldObj.playRecord((String) null, var3, var4, var5);
                }
                break;
            case 2000:
                int var8 = var6 % 3 - 1;
                int var9 = var6 / 3 % 3 - 1;
                double var10 = (double) var3 + (double) var8 * 0.6D + 0.5D;
                double var12 = (double) var4 + 0.5D;
                double var14 = (double) var5 + (double) var9 * 0.6D + 0.5D;

                for (var16 = 0; var16 < 10; ++var16) {
                    double var31 = var7.nextDouble() * 0.2D + 0.01D;
                    double var19 = var10 + (double) var8 * 0.01D + (var7.nextDouble() - 0.5D) * (double) var9 * 0.5D;
                    double var21 = var12 + (var7.nextDouble() - 0.5D) * 0.5D;
                    double var23 = var14 + (double) var9 * 0.01D + (var7.nextDouble() - 0.5D) * (double) var8 * 0.5D;
                    double var25 = (double) var8 * var31 + var7.nextGaussian() * 0.01D;
                    double var27 = -0.03D + var7.nextGaussian() * 0.01D;
                    double var29 = (double) var9 * var31 + var7.nextGaussian() * 0.01D;
                    this.spawnParticle("smoke", var19, var21, var23, var25, var27, var29);
                }

                return;
            case 2001:
                var16 = var6 & 255;
                if (var16 > 0) {
                    Block var17 = Block.blocksList[var16];
                    this.mc.sndManager.playSound(var17.stepSound.stepSoundDir(), (float) var3 + 0.5F, (float) var4 + 0.5F, (float) var5 + 0.5F, (var17.stepSound.getVolume() + 1.0F) / 2.0F, var17.stepSound.getPitch() * 0.8F);
                }

                this.mc.effectRenderer.addBlockDestroyEffects(var3, var4, var5, var6 & 255, var6 >> 8 & 255);
        }

    }
}
