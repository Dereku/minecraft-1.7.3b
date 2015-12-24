package net.minecraft.client.render;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.block.Block;
import net.minecraft.item.ItemRenderer;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.RenderHelper;
import net.minecraft.client.render.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityPlayer;
import net.minecraft.entity.EntityRainFX;
import net.minecraft.entity.EntitySmokeFX;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ClippingHelperImpl;
import net.minecraft.src.ClippingHelperImpl;
import net.minecraft.src.Config;
import net.minecraft.src.Config;
import net.minecraft.client.render.EffectRenderer;
import net.minecraft.src.Frustrum;
import net.minecraft.src.Frustrum;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.IChunkProvider;
import net.minecraft.client.render.ItemRendererHD;
import net.minecraft.src.Material;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MouseFilter;
import net.minecraft.src.MouseFilter;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.PlayerControllerTest;
import net.minecraft.src.PlayerControllerTest;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Vec3D;
import net.minecraft.src.Vec3D;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.ChunkProviderLoadOrGenerate;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

public class EntityRenderer {

    public static boolean field_28135_a = false;
    public static int anaglyphField;
    private Minecraft mc;
    private float farPlaneDistance = 0.0F;
    public ItemRenderer itemRenderer;
    private int rendererUpdateCount;
    private Entity pointedEntity = null;
    private MouseFilter mouseFilterXAxis = new MouseFilter();
    private MouseFilter mouseFilterYAxis = new MouseFilter();
    private float field_22228_r = 4.0F;
    private float field_22227_s = 4.0F;
    private float field_22226_t = 0.0F;
    private float field_22225_u = 0.0F;
    private float field_22224_v = 0.0F;
    private float field_22223_w = 0.0F;
    private float field_22222_x = 0.0F;
    private float field_22221_y = 0.0F;
    private float field_22220_z = 0.0F;
    private float field_22230_A = 0.0F;
    private boolean cloudFog = false;
    private double cameraZoom = 1.0D;
    private double cameraYaw = 0.0D;
    private double cameraPitch = 0.0D;
    private long prevFrameTime = System.currentTimeMillis();
    private long renderEndNanoTime = 0L;
    private Random random = new Random();
    private int rainSoundCounter = 0;
    volatile int field_1394_b = 0;
    volatile int field_1393_c = 0;
    FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    float fogColorRed;
    float fogColorGreen;
    float fogColorBlue;
    private float fogColor2;
    private float fogColor1;
    private WorldProvider updatedWorldProvider = null;
    private boolean showDebugInfo = false;
    private boolean zoomMode = false;

    public EntityRenderer(Minecraft minecraft) {
        this.mc = minecraft;
        this.itemRenderer = new ItemRenderer(minecraft);
    }

    public void updateRenderer() {
        this.fogColor2 = this.fogColor1;
        this.field_22227_s = this.field_22228_r;
        this.field_22225_u = this.field_22226_t;
        this.field_22223_w = this.field_22224_v;
        this.field_22221_y = this.field_22222_x;
        this.field_22230_A = this.field_22220_z;
        if (this.mc.renderViewEntity == null) {
            this.mc.renderViewEntity = this.mc.thePlayer;
        }

        float f = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.mc.renderViewEntity.posX), MathHelper.floor_double(this.mc.renderViewEntity.posY), MathHelper.floor_double(this.mc.renderViewEntity.posZ));
        float f1 = (float) (3 - this.mc.gameSettings.renderDistance) / 3.0F;
        float f2 = f * (1.0F - f1) + f1;

        this.fogColor1 += (f2 - this.fogColor1) * 0.1F;
        ++this.rendererUpdateCount;
        this.itemRenderer.updateEquippedItem();
        this.addRainParticles();
    }

    public void getMouseOver(float f) {
        if (this.mc.renderViewEntity != null) {
            if (this.mc.theWorld != null) {
                double d0 = (double) this.mc.playerController.getBlockReachDistance();

                this.mc.objectMouseOver = this.mc.renderViewEntity.rayTrace(d0, f);
                double d1 = d0;
                Vec3D vec3d = this.mc.renderViewEntity.getPosition(f);

                if (this.mc.objectMouseOver != null) {
                    d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3d);
                }

                if (this.mc.playerController instanceof PlayerControllerTest) {
                    d0 = 32.0D;
                    d1 = 32.0D;
                } else {
                    if (d1 > 3.0D) {
                        d1 = 3.0D;
                    }

                    d0 = d1;
                }

                Vec3D vec3d1 = this.mc.renderViewEntity.getLook(f);
                Vec3D vec3d2 = vec3d.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);

                this.pointedEntity = null;
                float f1 = 1.0F;
                List list = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.renderViewEntity, this.mc.renderViewEntity.boundingBox.addCoord(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0).expand((double) f1, (double) f1, (double) f1));
                double d2 = 0.0D;

                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (entity.canBeCollidedWith()) {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double) f2, (double) f2, (double) f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.func_1169_a(vec3d, vec3d2);

                        if (axisalignedbb.isVecInside(vec3d)) {
                            if (0.0D < d2 || d2 == 0.0D) {
                                this.pointedEntity = entity;
                                d2 = 0.0D;
                            }
                        } else if (movingobjectposition != null) {
                            double d3 = vec3d.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D) {
                                this.pointedEntity = entity;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && !(this.mc.playerController instanceof PlayerControllerTest)) {
                    this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity);
                }

            }
        }
    }

    private float getFOVModifier(float f) {
        EntityLiving entityliving = this.mc.renderViewEntity;
        float f1 = 70.0F;

        if (entityliving.isInsideOfMaterial(Material.water)) {
            f1 = 60.0F;
        }

        if (Keyboard.isKeyDown(this.mc.gameSettings.ofKeyBindZoom.keyCode)) {
            if (!this.zoomMode) {
                this.zoomMode = true;
                this.mc.gameSettings.smoothCamera = true;
            }

            if (this.zoomMode) {
                f1 /= 4.0F;
            }
        } else if (this.zoomMode) {
            this.zoomMode = false;
            this.mc.gameSettings.smoothCamera = false;
        }

        if (entityliving.health <= 0) {
            float f2 = (float) entityliving.deathTime + f;

            f1 /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
        }

        return f1 + this.field_22221_y + (this.field_22222_x - this.field_22221_y) * f;
    }

    private void hurtCameraEffect(float f) {
        EntityLiving entityliving = this.mc.renderViewEntity;
        float f1 = (float) entityliving.hurtTime - f;
        float f2;

        if (entityliving.health <= 0) {
            f2 = (float) entityliving.deathTime + f;
            GL11.glRotatef(40.0F - 8000.0F / (f2 + 200.0F), 0.0F, 0.0F, 1.0F);
        }

        if (f1 >= 0.0F) {
            f1 /= (float) entityliving.maxHurtTime;
            f1 = MathHelper.sin(f1 * f1 * f1 * f1 * 3.141593F);
            f2 = entityliving.attackedAtYaw;
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-f1 * 14.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
        }
    }

    private void setupViewBobbing(float f) {
        if (this.mc.renderViewEntity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) this.mc.renderViewEntity;
            float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
            float f2 = -(entityplayer.distanceWalkedModified + f1 * f);
            float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * f;
            float f4 = entityplayer.cameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * f;

            GL11.glTranslatef(MathHelper.sin(f2 * 3.141593F) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * 3.141593F) * f3), 0.0F);
            GL11.glRotatef(MathHelper.sin(f2 * 3.141593F) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos(f2 * 3.141593F - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
        }
    }

    private void orientCamera(float f) {
        EntityLiving entityliving = this.mc.renderViewEntity;
        float f1 = entityliving.yOffset - 1.62F;
        double d0 = entityliving.prevPosX + (entityliving.posX - entityliving.prevPosX) * (double) f;
        double d1 = entityliving.prevPosY + (entityliving.posY - entityliving.prevPosY) * (double) f - (double) f1;
        double d2 = entityliving.prevPosZ + (entityliving.posZ - entityliving.prevPosZ) * (double) f;

        GL11.glRotatef(this.field_22230_A + (this.field_22220_z - this.field_22230_A) * f, 0.0F, 0.0F, 1.0F);
        if (entityliving.isPlayerSleeping()) {
            f1 = (float) ((double) f1 + 1.0D);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.field_22273_E) {
                int i = this.mc.theWorld.getBlockId(MathHelper.floor_double(entityliving.posX), MathHelper.floor_double(entityliving.posY), MathHelper.floor_double(entityliving.posZ));

                if (i == Block.bed.blockID) {
                    int j = this.mc.theWorld.getBlockMetadata(MathHelper.floor_double(entityliving.posX), MathHelper.floor_double(entityliving.posY), MathHelper.floor_double(entityliving.posZ));
                    int k = j & 3;

                    GL11.glRotatef((float) (k * 90), 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * f + 180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glRotatef(entityliving.prevRotationPitch + (entityliving.rotationPitch - entityliving.prevRotationPitch) * f, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.gameSettings.thirdPersonView) {
            double d3 = (double) (this.field_22227_s + (this.field_22228_r - this.field_22227_s) * f);
            float f2;
            float f3;

            if (this.mc.gameSettings.field_22273_E) {
                f3 = this.field_22225_u + (this.field_22226_t - this.field_22225_u) * f;
                f2 = this.field_22223_w + (this.field_22224_v - this.field_22223_w) * f;
                GL11.glTranslatef(0.0F, 0.0F, (float) (-d3));
                GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            } else {
                f3 = entityliving.rotationYaw;
                f2 = entityliving.rotationPitch;
                double d4 = (double) (-MathHelper.sin(f3 / 180.0F * 3.141593F) * MathHelper.cos(f2 / 180.0F * 3.141593F)) * d3;
                double d5 = (double) (MathHelper.cos(f3 / 180.0F * 3.141593F) * MathHelper.cos(f2 / 180.0F * 3.141593F)) * d3;
                double d6 = (double) (-MathHelper.sin(f2 / 180.0F * 3.141593F)) * d3;

                for (int l = 0; l < 8; ++l) {
                    float f4 = (float) ((l & 1) * 2 - 1);
                    float f5 = (float) ((l >> 1 & 1) * 2 - 1);
                    float f6 = (float) ((l >> 2 & 1) * 2 - 1);

                    f4 *= 0.1F;
                    f5 *= 0.1F;
                    f6 *= 0.1F;
                    MovingObjectPosition movingobjectposition = this.mc.theWorld.rayTraceBlocks(Vec3D.createVector(d0 + (double) f4, d1 + (double) f5, d2 + (double) f6), Vec3D.createVector(d0 - d4 + (double) f4 + (double) f6, d1 - d6 + (double) f5, d2 - d5 + (double) f6));

                    if (movingobjectposition != null) {
                        double d7 = movingobjectposition.hitVec.distanceTo(Vec3D.createVector(d0, d1, d2));

                        if (d7 < d3) {
                            d3 = d7;
                        }
                    }
                }

                GL11.glRotatef(entityliving.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(entityliving.rotationYaw - f3, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float) (-d3));
                GL11.glRotatef(f3 - entityliving.rotationYaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(f2 - entityliving.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.gameSettings.field_22273_E) {
            GL11.glRotatef(entityliving.prevRotationPitch + (entityliving.rotationPitch - entityliving.prevRotationPitch) * f, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * f + 180.0F, 0.0F, 1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, f1, 0.0F);
        d0 = entityliving.prevPosX + (entityliving.posX - entityliving.prevPosX) * (double) f;
        d1 = entityliving.prevPosY + (entityliving.posY - entityliving.prevPosY) * (double) f - (double) f1;
        d2 = entityliving.prevPosZ + (entityliving.posZ - entityliving.prevPosZ) * (double) f;
        this.cloudFog = this.mc.renderGlobal.func_27307_a(d0, d1, d2, f);
    }

    private void setupCameraTransform(float f, int i) {
        this.farPlaneDistance = (float) (32 << 3 - this.mc.gameSettings.renderDistance);
        if (Config.isFarView()) {
            if (this.farPlaneDistance < 256.0F) {
                this.farPlaneDistance *= 3.0F;
            } else {
                this.farPlaneDistance *= 2.0F;
            }
        }

        if (Config.isFogFancy()) {
            this.farPlaneDistance *= 0.95F;
        } else {
            this.farPlaneDistance *= 0.83F;
        }

        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        float f1 = 0.07F;

        if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float) (-(i * 2 - 1)) * f1, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D) {
            GL11.glTranslatef((float) this.cameraYaw, (float) (-this.cameraPitch), 0.0F);
            GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
            GLU.gluPerspective(this.getFOVModifier(f), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        } else {
            GLU.gluPerspective(this.getFOVModifier(f), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        }

        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float) (i * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(f);
        if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(f);
        }

        float f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * f;

        if (f2 > 0.0F) {
            float f3 = 5.0F / (f2 * f2 + 5.0F) - f2 * 0.04F;

            f3 *= f3;
            GL11.glRotatef(((float) this.rendererUpdateCount + f) * 20.0F, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / f3, 1.0F, 1.0F);
            GL11.glRotatef(-((float) this.rendererUpdateCount + f) * 20.0F, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(f);
    }

    private void func_4135_b(float f, int i) {
        GL11.glLoadIdentity();
        if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((float) (i * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        GL11.glPushMatrix();
        this.hurtCameraEffect(f);
        if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(f);
        }

        if (!this.mc.gameSettings.thirdPersonView && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI) {
            this.itemRenderer.renderItemInFirstPerson(f);
        }

        GL11.glPopMatrix();
        if (!this.mc.gameSettings.thirdPersonView && !this.mc.renderViewEntity.isPlayerSleeping()) {
            this.itemRenderer.renderOverlays(f);
            this.hurtCameraEffect(f);
        }

        if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(f);
        }

    }

    public void updateCameraAndRender(float f) {
        World world = this.mc.theWorld;

        if (world != null && world.worldProvider != null && this.updatedWorldProvider != world.worldProvider) {
            this.updateWorldLightLevels();
            this.updatedWorldProvider = this.mc.theWorld.worldProvider;
        }

        Minecraft.hasPaidCheckTime = 0L;
        RenderBlocks.fancyGrass = Config.isGrassFancy();
        if (Config.isBetterGrassFancy()) {
            RenderBlocks.fancyGrass = true;
        }

        Block.leaves.setGraphicsLevel(Config.isTreesFancy());
        Config.setMinecraft(this.mc);
        if (Config.getIconWidthTerrain() > 16 && !(this.itemRenderer instanceof ItemRendererHD)) {
            this.itemRenderer = new ItemRendererHD(this.mc);
            RenderManager.instance.itemRenderer = this.itemRenderer;
        }

        if (world != null) {
            world.autosavePeriod = this.mc.gameSettings.ofAutoSaveTicks;
        }

        if (!Config.isWeatherEnabled() && world != null && world.worldInfo != null) {
            world.worldInfo.setIsRaining(false);
        }

        if (world != null) {
            long i = world.getWorldTime();
            long j = i % 24000L;

            if (Config.isTimeDayOnly()) {
                if (j <= 1000L) {
                    world.setWorldTime(i - j + 1001L);
                }

                if (j >= 11000L) {
                    world.setWorldTime(i - j + 24001L);
                }
            }

            if (Config.isTimeNightOnly()) {
                if (j <= 14000L) {
                    world.setWorldTime(i - j + 14001L);
                }

                if (j >= 22000L) {
                    world.setWorldTime(i - j + 24000L + 14001L);
                }
            }
        }

        if (!Display.isActive()) {
            if (System.currentTimeMillis() - this.prevFrameTime > 500L) {
                this.mc.displayInGameMenu();
            }
        } else {
            this.prevFrameTime = System.currentTimeMillis();
        }

        if (this.mc.inGameHasFocus) {
            this.mc.mouseHelper.mouseXYChange();
            float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f1 * f1 * f1 * 8.0F;
            float f3 = (float) this.mc.mouseHelper.deltaX * f2;
            float f4 = (float) this.mc.mouseHelper.deltaY * f2;
            byte b0 = 1;

            if (this.mc.gameSettings.invertMouse) {
                b0 = -1;
            }

            if (this.mc.gameSettings.smoothCamera) {
                f3 = this.mouseFilterXAxis.func_22386_a(f3, 0.05F * f2);
                f4 = this.mouseFilterYAxis.func_22386_a(f4, 0.05F * f2);
            }

            this.mc.thePlayer.setAngles(f3, f4 * (float) b0);
        }

        if (!this.mc.skipRenderWorld) {
            EntityRenderer.field_28135_a = this.mc.gameSettings.anaglyph;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            int i1 = Mouse.getX() * k / this.mc.displayWidth;
            int j1 = l - Mouse.getY() * l / this.mc.displayHeight - 1;
            short short0 = 200;

            if (this.mc.gameSettings.limitFramerate == 1) {
                short0 = 120;
            }

            if (this.mc.gameSettings.limitFramerate == 2) {
                short0 = 40;
            }

            long k1;

            if (this.mc.theWorld != null) {
                if (this.mc.gameSettings.limitFramerate == 0) {
                    this.renderWorld(f, 0L);
                } else {
                    this.renderWorld(f, this.renderEndNanoTime + (long) (1.0E9D / (double) short0));
                }

                if (this.mc.gameSettings.limitFramerate == 2) {
                    k1 = (this.renderEndNanoTime + (long) (1.0E9D / (double) short0) - System.nanoTime()) / 1000000L;
                    if (k1 > 0L && k1 < 500L) {
                        try {
                            Thread.sleep(k1);
                        } catch (InterruptedException interruptedexception) {
                            interruptedexception.printStackTrace();
                        }
                    }
                }

                this.renderEndNanoTime = System.nanoTime();
                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
                    if (this.mc.gameSettings.ofFastDebugInfo) {
                        Minecraft minecraft = this.mc;

                        if (Minecraft.isDebugInfoEnabled()) {
                            this.showDebugInfo = !this.showDebugInfo;
                        }

                        if (this.showDebugInfo) {
                            this.mc.gameSettings.showDebugInfo = true;
                        }
                    }

                    this.mc.ingameGUI.renderGameOverlay(f, this.mc.currentScreen != null, i1, j1);
                    if (this.mc.gameSettings.ofFastDebugInfo) {
                        this.mc.gameSettings.showDebugInfo = false;
                    }
                }
            } else {
                GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
                GL11.glLoadIdentity();
                this.func_905_b();
                if (this.mc.gameSettings.limitFramerate == 2) {
                    k1 = (this.renderEndNanoTime + (long) (1.0E9D / (double) short0) - System.nanoTime()) / 1000000L;
                    if (k1 < 0L) {
                        k1 += 10L;
                    }

                    if (k1 > 0L && k1 < 500L) {
                        try {
                            Thread.sleep(k1);
                        } catch (InterruptedException interruptedexception1) {
                            interruptedexception1.printStackTrace();
                        }
                    }
                }

                this.renderEndNanoTime = System.nanoTime();
            }

            if (this.mc.currentScreen != null) {
                GL11.glClear(256);
                this.mc.currentScreen.drawScreen(i1, j1, f);
                if (this.mc.currentScreen != null && this.mc.currentScreen.guiParticles != null) {
                    this.mc.currentScreen.guiParticles.draw(f);
                }
            }

        }
    }

    public void updateWorldLightLevels() {
        if (this.mc != null) {
            if (this.mc.theWorld != null) {
                if (this.mc.theWorld.worldProvider != null) {
                    float f = this.mc.gameSettings.ofBrightness;
                    float[] afloat = this.mc.theWorld.worldProvider.lightBrightnessTable;
                    float f1 = 0.05F;

                    if (this.mc.theWorld.worldProvider != null && this.mc.theWorld.worldProvider.isNether) {
                        f1 = 0.1F + f * 0.15F;
                    }

                    float f2 = 3.0F * (1.0F - f);

                    for (int i = 0; i <= 15; ++i) {
                        float f3 = 1.0F - (float) i / 15.0F;

                        afloat[i] = (1.0F - f3) / (f3 * f2 + 1.0F) * (1.0F - f1) + f1;
                    }

                    Config.setLightLevels(afloat);
                }
            }
        }
    }

    public void renderWorld(float f, long i) {
        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        if (this.mc.renderViewEntity == null) {
            this.mc.renderViewEntity = this.mc.thePlayer;
        }

        this.getMouseOver(f);
        EntityLiving entityliving = this.mc.renderViewEntity;
        RenderGlobal renderglobal = this.mc.renderGlobal;
        EffectRenderer effectrenderer = this.mc.effectRenderer;
        double d0 = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double) f;
        double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double) f;
        double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double) f;
        IChunkProvider ichunkprovider = this.mc.theWorld.getIChunkProvider();

        if (ichunkprovider instanceof ChunkProviderLoadOrGenerate) {
            ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate) ichunkprovider;
            int j = MathHelper.floor_float((float) ((int) d0)) >> 4;
            int k = MathHelper.floor_float((float) ((int) d2)) >> 4;

            chunkproviderloadorgenerate.setCurrentChunkOver(j, k);
        }

        for (int l = 0; l < 2; ++l) {
            if (this.mc.gameSettings.anaglyph) {
                EntityRenderer.anaglyphField = l;
                if (EntityRenderer.anaglyphField == 0) {
                    GL11.glColorMask(false, true, true, false);
                } else {
                    GL11.glColorMask(true, false, false, false);
                }
            }

            GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            this.updateFogColor(f);
            GL11.glClear(16640);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            this.setupCameraTransform(f, l);
            ClippingHelperImpl.getInstance();
            if (this.mc.gameSettings.renderDistance < 2 || Config.isFarView()) {
                this.setupFog(-1, f);
                renderglobal.renderSky(f);
            }

            GL11.glEnable(2912 /*GL_FOG*/);
            this.setupFog(1, f);
            if (this.mc.gameSettings.ambientOcclusion) {
                GL11.glShadeModel(7425 /*GL_SMOOTH*/);
            }

            Frustrum frustrum = new Frustrum();

            frustrum.setPosition(d0, d1, d2);
            this.mc.renderGlobal.clipRenderersByFrustrum(frustrum, f);
            if (l == 0) {
                while (!this.mc.renderGlobal.updateRenderers(entityliving, false) && i != 0L) {
                    long i1 = i - System.nanoTime();

                    if (i1 < 0L || (double) i1 > 1.0E9D) {
                        break;
                    }
                }
            }

            this.setupFog(0, f);
            GL11.glEnable(2912 /*GL_FOG*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/assets/terrain.png"));
            RenderHelper.disableStandardItemLighting();
            if (Config.isUseAlphaFunc()) {
                GL11.glAlphaFunc(516, Config.getAlphaFuncLevel());
            }

            renderglobal.sortAndRender(entityliving, 0, (double) f);
            GL11.glShadeModel(7424 /*GL_FLAT*/);
            RenderHelper.enableStandardItemLighting();
            renderglobal.renderEntities(entityliving.getPosition(f), frustrum, f);
            effectrenderer.func_1187_b(entityliving, f);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, f);
            effectrenderer.renderParticles(entityliving, f);
            EntityPlayer entityplayer;

            if (this.mc.objectMouseOver != null && entityliving.isInsideOfMaterial(Material.water) && entityliving instanceof EntityPlayer) {
                entityplayer = (EntityPlayer) entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                renderglobal.drawBlockBreaking(entityplayer, this.mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }

            GL11.glBlendFunc(770, 771);
            this.setupFog(0, f);
            GL11.glEnable(3042 /*GL_BLEND*/);
            GL11.glDisable(2884 /*GL_CULL_FACE*/);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/assets/terrain.png"));
            if (Config.isWaterFancy()) {
                if (this.mc.gameSettings.ambientOcclusion) {
                    GL11.glShadeModel(7425 /*GL_SMOOTH*/);
                }

                GL11.glColorMask(false, false, false, false);
                int j1 = renderglobal.renderAllSortedRenderers(1, (double) f);

                if (this.mc.gameSettings.anaglyph) {
                    if (EntityRenderer.anaglyphField == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }

                if (j1 > 0) {
                    renderglobal.renderAllSortedRenderers(1, (double) f);
                }

                GL11.glShadeModel(7424 /*GL_FLAT*/);
            } else {
                renderglobal.sortAndRender(entityliving, 1, (double) f);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(2884 /*GL_CULL_FACE*/);
            GL11.glDisable(3042 /*GL_BLEND*/);
            if (this.cameraZoom == 1.0D && entityliving instanceof EntityPlayer && this.mc.objectMouseOver != null && !entityliving.isInsideOfMaterial(Material.water)) {
                entityplayer = (EntityPlayer) entityliving;
                GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
                renderglobal.drawBlockBreaking(entityplayer, this.mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, entityplayer.inventory.getCurrentItem(), f);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            }

            this.renderRainSnow(f);
            GL11.glDisable(2912 /*GL_FOG*/);
            if (this.pointedEntity == null) {
                ;
            }

            this.setupFog(0, f);
            GL11.glEnable(2912 /*GL_FOG*/);
            renderglobal.renderClouds(f);
            GL11.glDisable(2912 /*GL_FOG*/);
            this.setupFog(1, f);
            if (this.cameraZoom == 1.0D) {
                GL11.glClear(256);
                this.func_4135_b(f, l);
            }

            if (!this.mc.gameSettings.anaglyph) {
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
    }

    private void addRainParticles() {
        float f = this.mc.theWorld.getRainStrength(1.0F);

        if (!Config.isRainFancy()) {
            f /= 2.0F;
        }

        if (f != 0.0F) {
            this.random.setSeed((long) this.rendererUpdateCount * 312987231L);
            EntityLiving entityliving = this.mc.renderViewEntity;
            World world = this.mc.theWorld;
            int i = MathHelper.floor_double(entityliving.posX);
            int j = MathHelper.floor_double(entityliving.posY);
            int k = MathHelper.floor_double(entityliving.posZ);
            byte b0 = 10;
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            int l = 0;

            for (int i1 = 0; i1 < (int) (100.0F * f * f); ++i1) {
                int j1 = i + this.random.nextInt(b0) - this.random.nextInt(b0);
                int k1 = k + this.random.nextInt(b0) - this.random.nextInt(b0);
                int l1 = world.getTopSolidOrLiquidBlock(j1, k1);
                int i2 = world.getBlockId(j1, l1 - 1, k1);

                if (l1 <= j + b0 && l1 >= j - b0 && world.getWorldChunkManager().getBiomeGenAt(j1, k1).canSpawnLightningBolt()) {
                    float f1 = this.random.nextFloat();
                    float f2 = this.random.nextFloat();

                    if (i2 > 0) {
                        if (Block.blocksList[i2].blockMaterial == Material.lava) {
                            this.mc.effectRenderer.addEffect(new EntitySmokeFX(world, (double) ((float) j1 + f1), (double) ((float) l1 + 0.1F) - Block.blocksList[i2].minY, (double) ((float) k1 + f2), 0.0D, 0.0D, 0.0D));
                        } else {
                            ++l;
                            if (this.random.nextInt(l) == 0) {
                                d0 = (double) ((float) j1 + f1);
                                d1 = (double) ((float) l1 + 0.1F) - Block.blocksList[i2].minY;
                                d2 = (double) ((float) k1 + f2);
                            }

                            this.mc.effectRenderer.addEffect(new EntityRainFX(world, (double) ((float) j1 + f1), (double) ((float) l1 + 0.1F) - Block.blocksList[i2].minY, (double) ((float) k1 + f2)));
                        }
                    }
                }
            }

            if (l > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
                this.rainSoundCounter = 0;
                if (d1 > entityliving.posY + 1.0D && world.getTopSolidOrLiquidBlock(MathHelper.floor_double(entityliving.posX), MathHelper.floor_double(entityliving.posZ)) > MathHelper.floor_double(entityliving.posY)) {
                    this.mc.theWorld.playSoundEffect(d0, d1, d2, "ambient.weather.rain", 0.1F, 0.5F);
                } else {
                    this.mc.theWorld.playSoundEffect(d0, d1, d2, "ambient.weather.rain", 0.2F, 1.0F);
                }
            }

        }
    }

    protected void renderRainSnow(float f) {
        float f1 = this.mc.theWorld.getRainStrength(f);

        if (f1 > 0.0F) {
            if (!Config.isRainOff()) {
                EntityLiving entityliving = this.mc.renderViewEntity;
                World world = this.mc.theWorld;
                int i = MathHelper.floor_double(entityliving.posX);
                int j = MathHelper.floor_double(entityliving.posY);
                int k = MathHelper.floor_double(entityliving.posZ);
                Tessellator tessellator = Tessellator.instance;

                GL11.glDisable(2884 /*GL_CULL_FACE*/);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
                GL11.glAlphaFunc(516, 0.01F);
                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/assets/environment/snow.png"));
                double d0 = entityliving.lastTickPosX + (entityliving.posX - entityliving.lastTickPosX) * (double) f;
                double d1 = entityliving.lastTickPosY + (entityliving.posY - entityliving.lastTickPosY) * (double) f;
                double d2 = entityliving.lastTickPosZ + (entityliving.posZ - entityliving.lastTickPosZ) * (double) f;
                int l = MathHelper.floor_double(d1);
                byte b0 = 5;

                if (Config.isRainFancy()) {
                    b0 = 10;
                }

                BiomeGenBase[] abiomegenbase = world.getWorldChunkManager().func_4069_a(i - b0, k - b0, b0 * 2 + 1, b0 * 2 + 1);
                int i1 = 0;

                int j1;
                int k1;
                BiomeGenBase biomegenbase;
                int l1;
                int i2;
                int j2;
                float f2;

                for (j1 = i - b0; j1 <= i + b0; ++j1) {
                    for (k1 = k - b0; k1 <= k + b0; ++k1) {
                        biomegenbase = abiomegenbase[i1++];
                        if (biomegenbase.getEnableSnow()) {
                            l1 = world.getTopSolidOrLiquidBlock(j1, k1);
                            if (l1 < 0) {
                                l1 = 0;
                            }

                            i2 = l1;
                            if (l1 < l) {
                                i2 = l;
                            }

                            j2 = j - b0;
                            int k2 = j + b0;

                            if (j2 < l1) {
                                j2 = l1;
                            }

                            if (k2 < l1) {
                                k2 = l1;
                            }

                            f2 = 1.0F;
                            if (j2 != k2) {
                                this.random.setSeed((long) (j1 * j1 * 3121 /*GL_RGBA_MODE*/ + j1 * 45238971 + k1 * k1 * 418711 + k1 * 13761));
                                float f3 = (float) this.rendererUpdateCount + f;
                                float f4 = ((float) (this.rendererUpdateCount & 511) + f) / 512.0F;
                                float f5 = this.random.nextFloat() + f3 * 0.01F * (float) this.random.nextGaussian();
                                float f6 = this.random.nextFloat() + f3 * (float) this.random.nextGaussian() * 0.001F;
                                double d3 = (double) ((float) j1 + 0.5F) - entityliving.posX;
                                double d4 = (double) ((float) k1 + 0.5F) - entityliving.posZ;
                                float f7 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / (float) b0;

                                tessellator.startDrawingQuads();
                                float f8 = world.getLightBrightness(j1, i2, k1);

                                GL11.glColor4f(f8, f8, f8, ((1.0F - f7 * f7) * 0.3F + 0.5F) * f1);
                                tessellator.setTranslationD(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                                tessellator.addVertexWithUV((double) (j1 + 0), (double) j2, (double) k1 + 0.5D, (double) (0.0F * f2 + f5), (double) ((float) j2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) (j1 + 1), (double) j2, (double) k1 + 0.5D, (double) (1.0F * f2 + f5), (double) ((float) j2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) (j1 + 1), (double) k2, (double) k1 + 0.5D, (double) (1.0F * f2 + f5), (double) ((float) k2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) (j1 + 0), (double) k2, (double) k1 + 0.5D, (double) (0.0F * f2 + f5), (double) ((float) k2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) j2, (double) (k1 + 0), (double) (0.0F * f2 + f5), (double) ((float) j2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) j2, (double) (k1 + 1), (double) (1.0F * f2 + f5), (double) ((float) j2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) k2, (double) (k1 + 1), (double) (1.0F * f2 + f5), (double) ((float) k2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) k2, (double) (k1 + 0), (double) (0.0F * f2 + f5), (double) ((float) k2 * f2 / 4.0F + f4 * f2 + f6));
                                tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
                                tessellator.draw();
                            }
                        }
                    }
                }

                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/assets/environment/rain.png"));
                if (Config.isRainFancy()) {
                    b0 = 10;
                }

                i1 = 0;

                for (j1 = i - b0; j1 <= i + b0; ++j1) {
                    for (k1 = k - b0; k1 <= k + b0; ++k1) {
                        biomegenbase = abiomegenbase[i1++];
                        if (biomegenbase.canSpawnLightningBolt()) {
                            l1 = world.getTopSolidOrLiquidBlock(j1, k1);
                            i2 = j - b0;
                            j2 = j + b0;
                            if (i2 < l1) {
                                i2 = l1;
                            }

                            if (j2 < l1) {
                                j2 = l1;
                            }

                            float f9 = 1.0F;

                            if (i2 != j2) {
                                this.random.setSeed((long) (j1 * j1 * 3121 /*GL_RGBA_MODE*/ + j1 * 45238971 + k1 * k1 * 418711 + k1 * 13761));
                                f2 = ((float) (this.rendererUpdateCount + j1 * j1 * 3121 /*GL_RGBA_MODE*/ + j1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31) + f) / 32.0F * (3.0F + this.random.nextFloat());
                                double d5 = (double) ((float) j1 + 0.5F) - entityliving.posX;
                                double d6 = (double) ((float) k1 + 0.5F) - entityliving.posZ;
                                float f10 = MathHelper.sqrt_double(d5 * d5 + d6 * d6) / (float) b0;

                                tessellator.startDrawingQuads();
                                float f11 = world.getLightBrightness(j1, 128, k1) * 0.85F + 0.15F;

                                GL11.glColor4f(f11, f11, f11, ((1.0F - f10 * f10) * 0.5F + 0.5F) * f1);
                                tessellator.setTranslationD(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                                tessellator.addVertexWithUV((double) (j1 + 0), (double) i2, (double) k1 + 0.5D, (double) (0.0F * f9), (double) ((float) i2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) (j1 + 1), (double) i2, (double) k1 + 0.5D, (double) (1.0F * f9), (double) ((float) i2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) (j1 + 1), (double) j2, (double) k1 + 0.5D, (double) (1.0F * f9), (double) ((float) j2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) (j1 + 0), (double) j2, (double) k1 + 0.5D, (double) (0.0F * f9), (double) ((float) j2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) i2, (double) (k1 + 0), (double) (0.0F * f9), (double) ((float) i2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) i2, (double) (k1 + 1), (double) (1.0F * f9), (double) ((float) i2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) j2, (double) (k1 + 1), (double) (1.0F * f9), (double) ((float) j2 * f9 / 4.0F + f2 * f9));
                                tessellator.addVertexWithUV((double) j1 + 0.5D, (double) j2, (double) (k1 + 0), (double) (0.0F * f9), (double) ((float) j2 * f9 / 4.0F + f2 * f9));
                                tessellator.setTranslationD(0.0D, 0.0D, 0.0D);
                                tessellator.draw();
                            }
                        }
                    }
                }

                GL11.glEnable(2884 /*GL_CULL_FACE*/);
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glAlphaFunc(516, 0.1F);
            }
        }
    }

    public void func_905_b() {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);

        GL11.glClear(256);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, scaledresolution.scaledWidthD, scaledresolution.scaledHeightD, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    private void updateFogColor(float f) {
        World world = this.mc.theWorld;
        EntityLiving entityliving = this.mc.renderViewEntity;
        float f1 = 1.0F / (float) (4 - this.mc.gameSettings.renderDistance);

        f1 = 1.0F - (float) Math.pow((double) f1, 0.25D);
        Vec3D vec3d = world.getSkyColor(this.mc.renderViewEntity, f);
        float f2 = (float) vec3d.xCoord;
        float f3 = (float) vec3d.yCoord;
        float f4 = (float) vec3d.zCoord;
        Vec3D vec3d1 = world.getFogColor(f);

        this.fogColorRed = (float) vec3d1.xCoord;
        this.fogColorGreen = (float) vec3d1.yCoord;
        this.fogColorBlue = (float) vec3d1.zCoord;
        this.fogColorRed += (f2 - this.fogColorRed) * f1;
        this.fogColorGreen += (f3 - this.fogColorGreen) * f1;
        this.fogColorBlue += (f4 - this.fogColorBlue) * f1;
        float f5 = world.getStarBrightness(f);// world.func_27162_g(f);
        float f6 = world.getStarBrightness(f);
        float f7;

        if (f5 > 0.0F) {
            f6 = 1.0F - f5 * 0.5F;
            f7 = 1.0F - f5 * 0.4F;
            this.fogColorRed *= f6;
            this.fogColorGreen *= f6;
            this.fogColorBlue *= f7;
        }

        if (f6 > 0.0F) {
            f7 = 1.0F - f6 * 0.5F;
            this.fogColorRed *= f7;
            this.fogColorGreen *= f7;
            this.fogColorBlue *= f7;
        }

        if (this.cloudFog) {
            Vec3D vec3d2 = world.drawClouds(f);//world.func_628_d(f);

            this.fogColorRed = (float) vec3d2.xCoord;
            this.fogColorGreen = (float) vec3d2.yCoord;
            this.fogColorBlue = (float) vec3d2.zCoord;
        } else if (entityliving.isInsideOfMaterial(Material.water)) {
            this.fogColorRed = 0.02F;
            this.fogColorGreen = 0.02F;
            this.fogColorBlue = 0.2F;
        } else if (entityliving.isInsideOfMaterial(Material.lava)) {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
        }

        f7 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * f;
        this.fogColorRed *= f7;
        this.fogColorGreen *= f7;
        this.fogColorBlue *= f7;
        if (this.mc.gameSettings.anaglyph) {
            float f8 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            float f9 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float f10 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;

            this.fogColorRed = f8;
            this.fogColorGreen = f9;
            this.fogColorBlue = f10;
        }

        GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
    }

    private void setupFog(int i, float f) {
        EntityLiving entityliving = this.mc.renderViewEntity;

        GL11.glFog(2918 /*GL_FOG_COLOR*/, this.func_908_a(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f1;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;

        if (this.cloudFog) {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 0.1F);
            f1 = 1.0F;
            f2 = 1.0F;
            f3 = 1.0F;
            if (this.mc.gameSettings.anaglyph) {
                f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
                f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            }
        } else if (entityliving.isInsideOfMaterial(Material.water)) {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            f1 = 0.1F;
            if (Config.isClearWater()) {
                f1 = 0.02F;
            }

            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, f1);
            f2 = 0.4F;
            f3 = 0.4F;
            f4 = 0.9F;
            if (this.mc.gameSettings.anaglyph) {
                f5 = (f2 * 30.0F + f3 * 59.0F + f4 * 11.0F) / 100.0F;
                f6 = (f2 * 30.0F + f3 * 70.0F) / 100.0F;
                float f7 = (f2 * 30.0F + f4 * 70.0F) / 100.0F;
            }
        } else if (entityliving.isInsideOfMaterial(Material.lava)) {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 2048 /*GL_EXP*/);
            GL11.glFogf(2914 /*GL_FOG_DENSITY*/, 2.0F);
            f1 = 0.4F;
            f2 = 0.3F;
            f3 = 0.3F;
            if (this.mc.gameSettings.anaglyph) {
                f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
                f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
                f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            }
        } else {
            GL11.glFogi(2917 /*GL_FOG_MODE*/, 9729 /*GL_LINEAR*/);
            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                if (Config.isFogFancy()) {
                    GL11.glFogi('\u855a', '\u855b');
                } else {
                    GL11.glFogi('\u855a', '\u855c');
                }
            }

            f1 = Config.getFogStart();
            f2 = 1.0F;
            if (i < 0) {
                f1 = 0.0F;
                f2 = 0.8F;
            }

            if (this.mc.theWorld.worldProvider.isNether) {
                f1 = 0.0F;
                f2 = 1.0F;
            }

            GL11.glFogf(2915 /*GL_FOG_START*/, this.farPlaneDistance * f1);
            GL11.glFogf(2916 /*GL_FOG_END*/, this.farPlaneDistance * f2);
        }

        GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);
        GL11.glColorMaterial(1028 /*GL_FRONT*/, 4608 /*GL_AMBIENT*/);
    }

    private FloatBuffer func_908_a(float f, float f1, float f2, float f3) {
        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(f).put(f1).put(f2).put(f3);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }
}
