package net.minecraft.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.File;
import net.minecraft.client.achiviements.AchievementList;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.client.block.Block;
import net.minecraft.world.chunk.ChunkCoordinates;
import net.minecraft.world.chunk.ChunkProviderLoadOrGenerate;
import net.minecraft.src.ColorizerFoliage;
import net.minecraft.src.ColorizerGrass;
import net.minecraft.src.ColorizerWater;
import net.minecraft.client.render.EffectRenderer;
import net.minecraft.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityPlayer;
import net.minecraft.entity.EntityPlayerSP;
import net.minecraft.client.render.EntityRenderer;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.EnumOS2;
import net.minecraft.src.EnumOSMappingHelper;
import net.minecraft.src.EnumOptions;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GameWindowListener;
import net.minecraft.client.gui.GuiAchievement;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConflictWarning;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiInventory;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiUnused;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.ISaveHandler;
import net.minecraft.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.src.LoadingScreenRenderer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftError;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.MinecraftImpl;
import net.minecraft.client.models.ModelBiped;
import net.minecraft.src.MouseHelper;
import net.minecraft.src.MovementInputFromOptions;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.network.NetClientHandler;
import net.minecraft.src.OpenGlCapsChecker;
import net.minecraft.src.PlayerController;
import net.minecraft.src.PlayerControllerTest;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.RenderEngine;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.render.RenderManager;
import net.minecraft.storage.SaveConverterMcRegion;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.ScreenShotHelper;
import net.minecraft.src.Session;
import net.minecraft.src.SoundManager;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatStringFormatKeyInv;
import net.minecraft.src.Teleporter;
import net.minecraft.src.Tessellator;
import net.minecraft.client.texture.TextureCompassFX;
import net.minecraft.client.texture.TextureFlamesFX;
import net.minecraft.client.texture.TextureLavaFX;
import net.minecraft.client.texture.TextureLavaFlowFX;
import net.minecraft.client.texture.TexturePackList;
import net.minecraft.client.texture.TexturePortalFX;
import net.minecraft.client.texture.TextureWatchFX;
import net.minecraft.client.texture.TextureWaterFX;
import net.minecraft.client.texture.TextureWaterFlowFX;
import net.minecraft.src.ThreadDownloadResources;
import net.minecraft.src.ThreadSleepForever;
import net.minecraft.src.Timer;
import net.minecraft.src.UnexpectedThrowable;
import net.minecraft.src.Vec3D;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldRenderer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public abstract class Minecraft implements Runnable {

    public static final String TERRAIN_TEXTURE = "/assets/terrain.png";
    public static final String PARTICLES_TEXTURE = "/assets/particles.png";
    public static final String ITEMS_TEXTURE = "/assets/gui/items.png";
    private static Minecraft theMinecraft;
    public PlayerController playerController;
    private boolean fullscreen = false;
    private boolean hasCrashed = false;
    public int displayWidth;
    public int displayHeight;
    private OpenGlCapsChecker glCapabilities;
    private final Timer timer = new Timer(20.0F);
    public World theWorld;
    public RenderGlobal renderGlobal;
    public EntityPlayerSP thePlayer;
    public EntityLiving renderViewEntity;
    public EffectRenderer effectRenderer;
    public Session session = null;
    public String minecraftUri;
    public Canvas mcCanvas;
    public boolean hideQuitButton = true;
    public volatile boolean isGamePaused = false;
    public RenderEngine renderEngine;
    public FontRenderer fontRenderer;
    public GuiScreen currentScreen = null;
    public LoadingScreenRenderer loadingScreen = new LoadingScreenRenderer(this);
    public EntityRenderer entityRenderer;
    private ThreadDownloadResources downloadResourcesThread;
    private int ticksRan = 0;
    private int leftClickCounter = 0;
    private int tempDisplayWidth;
    private int tempDisplayHeight;
    public GuiAchievement guiAchievement = new GuiAchievement(this);
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld = false;
    public ModelBiped playerModelBiped = new ModelBiped(0.0F);
    public MovingObjectPosition objectMouseOver = null;
    public GameSettings gameSettings;
    public SoundManager sndManager = new SoundManager();
    public MouseHelper mouseHelper;
    public TexturePackList texturePackList;
    private File mcDataDir;
    private ISaveFormat saveLoader;
    public static long[] frameTimes = new long[512];
    public static long[] tickTimes = new long[512];
    public static int numRecordedFrameTimes = 0;
    public StatFileWriter statFileWriter;
    private String serverName;
    private int serverPort;
    private final TextureWaterFX textureWaterFX = new TextureWaterFX();
    private final TextureLavaFX textureLavaFX = new TextureLavaFX();
    private static File minecraftDir = null;
    public volatile boolean running = true;
    public String debug = "";
    boolean isTakingScreenshot = false;
    long prevFrameTime = -1L;
    public boolean inGameHasFocus = false;
    private int mouseTicksRan = 0;
    public boolean isRaining = false;
    long systemTime = System.currentTimeMillis();
    private int joinPlayerCounter = 0;

    @SuppressWarnings({"ResultOfObjectAllocationIgnored", "LeakingThisInConstructor"})
    public Minecraft(Component component, Canvas canvas, int displayWidth, int displayHeight, boolean isFullscreen) {
        StatList.call();
        this.tempDisplayHeight = displayHeight;
        this.fullscreen = isFullscreen;
        new ThreadSleepForever(this, "Timer hack thread");
        this.mcCanvas = canvas;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.fullscreen = isFullscreen;
        theMinecraft = this;
    }

    public void onMinecraftCrash(UnexpectedThrowable throwable) {
        this.hasCrashed = true;
        this.displayUnexpectedThrowable(throwable);
    }

    public abstract void displayUnexpectedThrowable(UnexpectedThrowable throwable);

    public void setServer(String hostname, int port) {
        this.serverName = hostname;
        this.serverPort = port;
    }

    public void startGame() throws LWJGLException {
        if (this.mcCanvas != null) {
            Graphics graphics = this.mcCanvas.getGraphics();
            if (graphics != null) {
                graphics.setColor(Color.BLACK);
                graphics.fillRect(0, 0, this.displayWidth, this.displayHeight);
                graphics.dispose();
            }

            Display.setParent(this.mcCanvas);
        } else if (this.fullscreen) {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
            if (this.displayWidth <= 0) {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
                this.displayHeight = 1;
            }
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setTitle("Minecraft Minecraft Beta 1.7.3");

        try {
            Display.create();
        } catch (LWJGLException var6) {
            var6.printStackTrace(); //TODO: Logger

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var5) {
            }

            Display.create();
        }

        this.mcDataDir = Minecraft.getMinecraftDir();
        this.saveLoader = new SaveConverterMcRegion(new File(this.mcDataDir, "saves"));
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.texturePackList = new TexturePackList(this, this.mcDataDir);
        this.renderEngine = new RenderEngine(this.texturePackList, this.gameSettings);
        this.fontRenderer = new FontRenderer(this.gameSettings);
        ColorizerWater.loadBuffer(this.renderEngine.getTextureRGBArray("/assets/misc/watercolor.png"));
        ColorizerGrass.loadBuffer(this.renderEngine.getTextureRGBArray("/assets/misc/grasscolor.png"));
        ColorizerFoliage.loadBuffer(this.renderEngine.getTextureRGBArray("/assets/misc/foliagecolor.png"));
        this.entityRenderer = new EntityRenderer(this);
        RenderManager.instance.itemRenderer = new ItemRenderer(this);
        this.loadScreen();
        Keyboard.create();
        Mouse.create();
        this.mouseHelper = new MouseHelper(this.mcCanvas);

        try {
            Controllers.create();
        } catch (LWJGLException var4) {
            var4.printStackTrace();
        }

        this.checkGLError("Pre startup");
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glShadeModel(7425 /*GL_SMOOTH*/);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glCullFace(1029 /*GL_BACK*/);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        this.checkGLError("Startup");
        this.glCapabilities = new OpenGlCapsChecker();
        this.sndManager.loadSoundSettings(this.gameSettings);
        this.renderEngine.registerTextureFX(this.textureLavaFX);
        this.renderEngine.registerTextureFX(this.textureWaterFX);
        this.renderEngine.registerTextureFX(new TexturePortalFX());
        this.renderEngine.registerTextureFX(new TextureCompassFX(this));
        this.renderEngine.registerTextureFX(new TextureWatchFX(this));
        this.renderEngine.registerTextureFX(new TextureWaterFlowFX());
        this.renderEngine.registerTextureFX(new TextureLavaFlowFX());
        this.renderEngine.registerTextureFX(new TextureFlamesFX(0));
        this.renderEngine.registerTextureFX(new TextureFlamesFX(1));
        this.renderGlobal = new RenderGlobal(this, this.renderEngine);
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);

        try {
            this.downloadResourcesThread = new ThreadDownloadResources(this.mcDataDir, this);
            this.downloadResourcesThread.start();
        } catch (Exception var3) {
        }

        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);
        if (this.serverName != null) {
            this.displayGuiScreen(new GuiConnecting(this, this.serverName, this.serverPort));
        } else {
            this.displayGuiScreen(new GuiMainMenu());
        }

    }

    private void loadScreen() throws LWJGLException {
        ScaledResolution var1 = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
        GL11.glClear(16640);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, var1.scaledWidthD, var1.scaledHeightD, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Tessellator var2 = Tessellator.instance;
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glDisable(2912 /*GL_FOG*/);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.renderEngine.getTexture("/assets/title/mojang.png"));
        var2.startDrawingQuads();
        var2.setColorOpaque_I(16777215);
        var2.addVertexWithUV(0.0D, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV((double) this.displayWidth, (double) this.displayHeight, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV((double) this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        var2.draw();
        short var3 = 256;
        short var4 = 256;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var2.setColorOpaque_I(16777215);
        this.scaledTessellator((var1.getScaledWidth() - var3) / 2, (var1.getScaledHeight() - var4) / 2, 0, 0, var3, var4);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(2912 /*GL_FOG*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glAlphaFunc(516, 0.1F);
        Display.swapBuffers();
    }

    public void scaledTessellator(int var1, int var2, int var3, int var4, int var5, int var6) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double) (var1 + 0), (double) (var2 + var6), 0.0D, (double) ((float) (var3 + 0) * var7), (double) ((float) (var4 + var6) * var8));
        var9.addVertexWithUV((double) (var1 + var5), (double) (var2 + var6), 0.0D, (double) ((float) (var3 + var5) * var7), (double) ((float) (var4 + var6) * var8));
        var9.addVertexWithUV((double) (var1 + var5), (double) (var2 + 0), 0.0D, (double) ((float) (var3 + var5) * var7), (double) ((float) (var4 + 0) * var8));
        var9.addVertexWithUV((double) (var1 + 0), (double) (var2 + 0), 0.0D, (double) ((float) (var3 + 0) * var7), (double) ((float) (var4 + 0) * var8));
        var9.draw();
    }

    public static File getMinecraftDir() {
        if (minecraftDir == null) {
            minecraftDir = getAppDir("minecraftburst");
        }

        return minecraftDir;
    }

    public static File getAppDir(String var0) {
        String userHome = System.getProperty("user.home", ".");
        File file;
        switch (EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()]) {
            case 1:
            case 2:
                file = new File(userHome, '.' + var0 + '/');
                break;
            case 3:
                String appData = System.getenv("APPDATA");
                if (appData != null) {
                    file = new File(appData, "." + var0 + '/');
                } else {
                    file = new File(userHome, '.' + var0 + '/');
                }
                break;
            case 4:
                file = new File(userHome, "Library/Application Support/" + var0);
                break;
            default:
                file = new File(userHome, var0 + '/');
        }

        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + file);
        } else {
            return file;
        }
    }

    private static EnumOS2 getOs() {
        String var0 = System.getProperty("os.name").toLowerCase();
        return var0.contains("win") ? EnumOS2.windows : (var0.contains("mac") ? EnumOS2.macos : (var0.contains("solaris") ? EnumOS2.solaris : (var0.contains("sunos") ? EnumOS2.solaris : (var0.contains("linux") ? EnumOS2.linux : (var0.contains("unix") ? EnumOS2.linux : EnumOS2.unknown)))));
    }

    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    public void displayGuiScreen(GuiScreen guiScreen) {
        if (!(this.currentScreen instanceof GuiUnused)) {
            if (this.currentScreen != null) {
                this.currentScreen.onGuiClosed();
            }

            if (this.statFileWriter != null) {
                this.statFileWriter.syncStats();
                if (guiScreen == null && this.theWorld == null) {
                    guiScreen = new GuiMainMenu();
                } else if (guiScreen == null && this.thePlayer.health <= 0) {
                    guiScreen = new GuiGameOver();
                }
            }

            if (guiScreen instanceof GuiMainMenu) {
                this.ingameGUI.clearChatMessages();
            }

            this.currentScreen = (GuiScreen) guiScreen;
            if (guiScreen != null) {
                this.setIngameNotInFocus();
                ScaledResolution scaledResolution = new ScaledResolution(this.gameSettings, this.displayWidth, this.displayHeight);
                guiScreen.setWorldAndResolution(this, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
                this.skipRenderWorld = false;
            } else {
                this.setIngameFocus();
            }

        }
    }

    private void checkGLError(String var1) {
        int errorId = GL11.glGetError();
        if (errorId != 0) {
            String var3 = GLU.gluErrorString(errorId);
            System.out.println("########## GL ERROR ##########");
            System.out.println("@ " + var1);
            System.out.println(errorId + ": " + var3);
        }

    }

    public void shutdownMinecraftApplet() {
        try {
            if (this.statFileWriter != null) {
                this.statFileWriter.syncStats();
            }
            try {
                if (this.downloadResourcesThread != null) {
                    this.downloadResourcesThread.closeMinecraft();
                }
            } catch (Exception var9) {
            }

            System.out.println("Stopping!");

            try {
                this.changeWorld1((World) null);
            } catch (Throwable var8) {
            }

            try {
                GLAllocation.deleteTexturesAndDisplayLists();
            } catch (Throwable var7) {
            }

            this.sndManager.closeMinecraft();
            Mouse.destroy();
            Keyboard.destroy();
        } finally {
            Display.destroy();
            if (!this.hasCrashed) {
                System.exit(0);
            }
        }
        System.gc();
    }

    @Override
    @SuppressWarnings({"SleepWhileInLoop", "CallToThreadYield"})
    public void run() {
        this.running = true;

        try {
            this.startGame();
        } catch (LWJGLException var17) {
            var17.printStackTrace();
            this.onMinecraftCrash(new UnexpectedThrowable("Failed to start game", var17));
            return;
        }

        try {
            long var1 = System.currentTimeMillis();
            int var3 = 0;

            while (this.running) {
                try {
                    AxisAlignedBB.clearBoundingBoxPool();
                    Vec3D.initialize();
                    if (this.mcCanvas == null && Display.isCloseRequested()) {
                        this.shutdown();
                    }

                    if (this.isGamePaused && this.theWorld != null) {
                        float var4 = this.timer.renderPartialTicks;
                        this.timer.updateTimer();
                        this.timer.renderPartialTicks = var4;
                    } else {
                        this.timer.updateTimer();
                    }

                    long var23 = System.nanoTime();

                    for (int var6 = 0; var6 < this.timer.elapsedTicks; ++var6) {
                        ++this.ticksRan;

                        try {
                            this.runTick();
                        } catch (MinecraftException var16) {
                            this.theWorld = null;
                            this.changeWorld1((World) null);
                            this.displayGuiScreen(new GuiConflictWarning());
                        }
                    }

                    long var24 = System.nanoTime() - var23;
                    this.checkGLError("Pre render");
                    RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
                    this.sndManager.func_338_a(this.thePlayer, this.timer.renderPartialTicks);
                    GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                    if (this.theWorld != null) {
                        this.theWorld.updatingLighting();
                    }

                    if (!Keyboard.isKeyDown(65)) {
                        Display.update();
                    }

                    if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
                        this.gameSettings.thirdPersonView = false;
                    }

                    if (!this.skipRenderWorld) {
                        if (this.playerController != null) {
                            this.playerController.setPartialTime(this.timer.renderPartialTicks);
                        }

                        this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
                    }

                    if (!Display.isActive()) {
                        if (this.fullscreen) {
                            this.toggleFullscreen();
                        }

                        Thread.sleep(10L);
                    }

                    if (this.gameSettings.showDebugInfo) {
                        this.displayDebugInfo(var24);
                    } else {
                        this.prevFrameTime = System.nanoTime();
                    }

                    this.guiAchievement.updateAchievementWindow();
                    Thread.yield();
                    if (Keyboard.isKeyDown(65)) {
                        Display.update();
                    }

                    this.screenshotListener();
                    if (this.mcCanvas != null && !this.fullscreen && (this.mcCanvas.getWidth() != this.displayWidth || this.mcCanvas.getHeight() != this.displayHeight)) {
                        this.displayWidth = this.mcCanvas.getWidth();
                        this.displayHeight = this.mcCanvas.getHeight();
                        if (this.displayWidth <= 0) {
                            this.displayWidth = 1;
                        }

                        if (this.displayHeight <= 0) {
                            this.displayHeight = 1;
                        }

                        this.resize(this.displayWidth, this.displayHeight);
                    }

                    this.checkGLError("Post render");
                    ++var3;

                    for (this.isGamePaused = !this.isMultiplayerWorld() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame(); System.currentTimeMillis() >= var1 + 1000L; var3 = 0) {
                        this.debug = var3 + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
                        WorldRenderer.chunksUpdated = 0;
                        var1 += 1000L;
                    }
                } catch (MinecraftException var18) {
                    this.theWorld = null;
                    this.changeWorld1((World) null);
                    this.displayGuiScreen(new GuiConflictWarning());
                } catch (OutOfMemoryError var19) {
                    this.func_28002_e();
                    this.displayGuiScreen(new GuiErrorScreen());
                    System.gc();
                }
            }
        } catch (Throwable var21) {
            this.onMinecraftCrash(new UnexpectedThrowable("Unexpected error", var21));
        } finally {
            this.shutdownMinecraftApplet();
        }

    }

    public void func_28002_e() {
        try {
            this.renderGlobal.func_28137_f();
        } catch (Throwable var4) {
        }

        try {
            System.gc();
            AxisAlignedBB.clearBoundingBoxes();
            Vec3D.clearVectorList();
        } catch (Throwable var3) {
        }

        try {
            System.gc();
            this.changeWorld1((World) null);
        } catch (Throwable var2) {
        }

        System.gc();
    }

    private void screenshotListener() {
        if (Keyboard.isKeyDown(60)) {
            if (!this.isTakingScreenshot) {
                this.isTakingScreenshot = true;
                this.ingameGUI.addChatMessage(ScreenShotHelper.saveScreenshot(minecraftDir, this.displayWidth, this.displayHeight));
            }
        } else {
            this.isTakingScreenshot = false;
        }

    }

    private void displayDebugInfo(long var1) {
        long var3 = 16666666L;
        if (this.prevFrameTime == -1L) {
            this.prevFrameTime = System.nanoTime();
        }

        long var5 = System.nanoTime();
        tickTimes[numRecordedFrameTimes & frameTimes.length - 1] = var1;
        frameTimes[numRecordedFrameTimes++ & frameTimes.length - 1] = var5 - this.prevFrameTime;
        this.prevFrameTime = var5;
        GL11.glClear(256);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
        Tessellator var7 = Tessellator.instance;
        var7.startDrawing(7);
        int var8 = (int) (var3 / 200000L);
        var7.setColorOpaque_I(536870912);
        var7.addVertex(0.0D, (double) (this.displayHeight - var8), 0.0D);
        var7.addVertex(0.0D, (double) this.displayHeight, 0.0D);
        var7.addVertex((double) frameTimes.length, (double) this.displayHeight, 0.0D);
        var7.addVertex((double) frameTimes.length, (double) (this.displayHeight - var8), 0.0D);
        var7.setColorOpaque_I(538968064);
        var7.addVertex(0.0D, (double) (this.displayHeight - var8 * 2), 0.0D);
        var7.addVertex(0.0D, (double) (this.displayHeight - var8), 0.0D);
        var7.addVertex((double) frameTimes.length, (double) (this.displayHeight - var8), 0.0D);
        var7.addVertex((double) frameTimes.length, (double) (this.displayHeight - var8 * 2), 0.0D);
        var7.draw();
        long var9 = 0L;

        int var11;
        for (var11 = 0; var11 < frameTimes.length; ++var11) {
            var9 += frameTimes[var11];
        }

        var11 = (int) (var9 / 200000L / (long) frameTimes.length);
        var7.startDrawing(7);
        var7.setColorOpaque_I(541065216);
        var7.addVertex(0.0D, (double) (this.displayHeight - var11), 0.0D);
        var7.addVertex(0.0D, (double) this.displayHeight, 0.0D);
        var7.addVertex((double) frameTimes.length, (double) this.displayHeight, 0.0D);
        var7.addVertex((double) frameTimes.length, (double) (this.displayHeight - var11), 0.0D);
        var7.draw();
        var7.startDrawing(1);

        for (int var12 = 0; var12 < frameTimes.length; ++var12) {
            int var13 = (var12 - numRecordedFrameTimes & frameTimes.length - 1) * 255 / frameTimes.length;
            int var14 = var13 * var13 / 255;
            var14 = var14 * var14 / 255;
            if (frameTimes[var12] > var3) {
                var7.setColorOpaque_I(-16777216 + var14 * 65536);
            } else {
                var7.setColorOpaque_I(-16777216 + var14 * 256);
            }

            long var16 = frameTimes[var12] / 200000L;
            long var18 = tickTimes[var12] / 200000L;
            var7.addVertex((double) ((float) var12 + 0.5F), (double) ((float) ((long) this.displayHeight - var16) + 0.5F), 0.0D);
            var7.addVertex((double) ((float) var12 + 0.5F), (double) ((float) this.displayHeight + 0.5F), 0.0D);
            var7.setColorOpaque_I(-16777216 + var14 * 65536 + var14 * 256 + var14 * 1);
            var7.addVertex((double) ((float) var12 + 0.5F), (double) ((float) ((long) this.displayHeight - var16) + 0.5F), 0.0D);
            var7.addVertex((double) ((float) var12 + 0.5F), (double) ((float) ((long) this.displayHeight - (var16 - var18)) + 0.5F), 0.0D);
        }

        var7.draw();
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
    }

    public void shutdown() {
        this.running = false;
    }

    public void setIngameFocus() {
        if (Display.isActive()) {
            if (!this.inGameHasFocus) {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen) null);
                this.leftClickCounter = 10000;
                this.mouseTicksRan = this.ticksRan + 10000;
            }
        }
    }

    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            if (this.thePlayer != null) {
                this.thePlayer.resetPlayerKeyState();
            }

            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu());
        }
    }

    private void sendClickBlockToController(int var1, boolean var2) {
        if (!this.playerController.isInTestMode) {
            if (!var2) {
                this.leftClickCounter = 0;
            }

            if (var1 != 0 || this.leftClickCounter <= 0) {
                if (var2 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && var1 == 0) {
                    int var3 = this.objectMouseOver.blockX;
                    int var4 = this.objectMouseOver.blockY;
                    int var5 = this.objectMouseOver.blockZ;
                    this.playerController.sendBlockRemoving(var3, var4, var5, this.objectMouseOver.sideHit);
                    this.effectRenderer.addBlockHitEffects(var3, var4, var5, this.objectMouseOver.sideHit);
                } else {
                    this.playerController.resetBlockRemoving();
                }

            }
        }
    }

    private void clickMouse(int var1) {
        if (var1 != 0 || this.leftClickCounter <= 0) {
            if (var1 == 0) {
                this.thePlayer.swingItem();
            }

            boolean var2 = true;
            if (this.objectMouseOver == null) {
                if (var1 == 0 && !(this.playerController instanceof PlayerControllerTest)) {
                    this.leftClickCounter = 10;
                }
            } else if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY) {
                if (var1 == 0) {
                    this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                }

                if (var1 == 1) {
                    this.playerController.interactWithEntity(this.thePlayer, this.objectMouseOver.entityHit);
                }
            } else if (this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {
                int var3 = this.objectMouseOver.blockX;
                int var4 = this.objectMouseOver.blockY;
                int var5 = this.objectMouseOver.blockZ;
                int var6 = this.objectMouseOver.sideHit;
                if (var1 == 0) {
                    this.playerController.clickBlock(var3, var4, var5, this.objectMouseOver.sideHit);
                } else {
                    ItemStack var7 = this.thePlayer.inventory.getCurrentItem();
                    int var8 = var7 != null ? var7.stackSize : 0;
                    if (this.playerController.sendPlaceBlock(this.thePlayer, this.theWorld, var7, var3, var4, var5, var6)) {
                        var2 = false;
                        this.thePlayer.swingItem();
                    }

                    if (var7 == null) {
                        return;
                    }

                    if (var7.stackSize == 0) {
                        this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                    } else if (var7.stackSize != var8) {
                        this.entityRenderer.itemRenderer.func_9449_b();
                    }
                }
            }

            if (var2 && var1 == 1) {
                ItemStack var9 = this.thePlayer.inventory.getCurrentItem();
                if (var9 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var9)) {
                    this.entityRenderer.itemRenderer.func_9450_c();
                }
            }

        }
    }

    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;
            if (this.fullscreen) {
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                if (this.mcCanvas != null) {
                    this.displayWidth = this.mcCanvas.getWidth();
                    this.displayHeight = this.mcCanvas.getHeight();
                } else {
                    this.displayWidth = this.tempDisplayWidth;
                    this.displayHeight = this.tempDisplayHeight;
                }

                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            }

            Display.setFullscreen(this.fullscreen);
            Display.update();
        } catch (LWJGLException var2) {
            var2.printStackTrace();
        }

    }

    private void resize(int var1, int var2) {
        if (var1 <= 0) {
            var1 = 1;
        }

        if (var2 <= 0) {
            var2 = 1;
        }

        this.displayWidth = var1;
        this.displayHeight = var2;
        if (this.currentScreen != null) {
            ScaledResolution var3 = new ScaledResolution(this.gameSettings, var1, var2);
            int var4 = var3.getScaledWidth();
            int var5 = var3.getScaledHeight();
            this.currentScreen.setWorldAndResolution(this, var4, var5);
        }

    }

    private void clickMiddleMouseButton() {
        if (this.objectMouseOver != null) {
            int var1 = this.theWorld.getBlockId(this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ);
            if (var1 == Block.grass.blockID) {
                var1 = Block.dirt.blockID;
            }

            if (var1 == Block.stairDouble.blockID) {
                var1 = Block.stairSingle.blockID;
            }

            if (var1 == Block.bedrock.blockID) {
                var1 = Block.stone.blockID;
            }

            this.thePlayer.inventory.setCurrentItem(var1, this.playerController instanceof PlayerControllerTest);
        }

    }

    public void runTick() {
        if (this.statFileWriter != null) {
            this.statFileWriter.func_27178_d();
        }
        this.ingameGUI.updateTick();
        this.entityRenderer.getMouseOver(1.0F);
        int var3;
        if (this.thePlayer != null) {
            IChunkProvider var1 = this.theWorld.getIChunkProvider();
            if (var1 instanceof ChunkProviderLoadOrGenerate) {
                ChunkProviderLoadOrGenerate var2 = (ChunkProviderLoadOrGenerate) var1;
                var3 = MathHelper.floor_float((float) ((int) this.thePlayer.posX)) >> 4;
                int var4 = MathHelper.floor_float((float) ((int) this.thePlayer.posZ)) >> 4;
                var2.setCurrentChunkOver(var3, var4);
            }
        }

        if (!this.isGamePaused && this.theWorld != null) {
            this.playerController.updateController();
        }

        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.renderEngine.getTexture(Minecraft.TERRAIN_TEXTURE));
        if (!this.isGamePaused) {
            this.renderEngine.updateDynamicTextures();
        }

        if (this.currentScreen == null && this.thePlayer != null) {
            if (this.thePlayer.health <= 0) {
                this.displayGuiScreen((GuiScreen) null);
            } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null && this.theWorld.multiplayerWorld) {
                this.displayGuiScreen(new GuiSleepMP(""));
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
            this.displayGuiScreen((GuiScreen) null);
        }

        if (this.currentScreen != null) {
            this.leftClickCounter = 10000;
            this.mouseTicksRan = this.ticksRan + 10000;
        }

        if (this.currentScreen != null) {
            this.currentScreen.handleInput();
            if (this.currentScreen != null) {
                this.currentScreen.guiParticles.update();
                this.currentScreen.updateScreen();
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput) {
            while (Mouse.next()) {
                long var5 = System.currentTimeMillis() - this.systemTime;
                if (var5 <= 200L) {
                    var3 = Mouse.getEventDWheel();
                    if (var3 != 0) {
                        this.thePlayer.inventory.changeCurrentItem(var3);
                        if (this.gameSettings.field_22275_C) {
                            if (var3 > 0) {
                                var3 = 1;
                            }

                            if (var3 < 0) {
                                var3 = -1;
                            }

                            this.gameSettings.field_22272_F += (float) var3 * 0.25F;
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                            this.setIngameFocus();
                        } else {
                            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
                                this.clickMouse(0);
                                this.mouseTicksRan = this.ticksRan;
                            }

                            if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
                                this.clickMouse(1);
                                this.mouseTicksRan = this.ticksRan;
                            }

                            if (Mouse.getEventButton() == 2 && Mouse.getEventButtonState()) {
                                this.clickMiddleMouseButton();
                            }
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0) {
                --this.leftClickCounter;
            }

            while (Keyboard.next()) {
                this.thePlayer.handleKeyPress(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 87) {
                        this.toggleFullscreen();
                    } else {
                        if (this.currentScreen != null) {
                            this.currentScreen.handleKeyboardInput();
                        } else {
                            if (Keyboard.getEventKey() == 1) {
                                this.displayInGameMenu();
                            }

                            if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                                this.forceReload();
                            }

                            if (Keyboard.getEventKey() == 59) {
                                this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                            }

                            if (Keyboard.getEventKey() == 61) {
                                this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            }

                            if (Keyboard.getEventKey() == 63) {
                                this.gameSettings.thirdPersonView = !this.gameSettings.thirdPersonView;
                            }

                            if (Keyboard.getEventKey() == 66) {
                                this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                            }

                            if (Keyboard.getEventKey() == this.gameSettings.keyBindInventory.keyCode) {
                                this.displayGuiScreen(new GuiInventory(this.thePlayer));
                            }

                            if (Keyboard.getEventKey() == this.gameSettings.keyBindDrop.keyCode) {
                                this.thePlayer.dropCurrentItem();
                            }

                            if (this.isMultiplayerWorld() && Keyboard.getEventKey() == this.gameSettings.keyBindChat.keyCode) {
                                this.displayGuiScreen(new GuiChat(""));
                            }

                            if (this.isMultiplayerWorld() && Keyboard.getEventKey() == this.gameSettings.keyBindCommand.keyCode) {
                                this.displayGuiScreen(new GuiChat("/"));
                            }
                        }

                        for (int var6 = 0; var6 < 9; ++var6) {
                            if (Keyboard.getEventKey() == 2 + var6) {
                                this.thePlayer.inventory.currentItem = var6;
                            }
                        }

                        if (Keyboard.getEventKey() == this.gameSettings.keyBindToggleFog.keyCode) {
                            this.gameSettings.setOptionValue(EnumOptions.RENDER_DISTANCE, !Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54) ? 1 : -1);
                        }
                    }
                }
            }

            if (this.currentScreen == null) {
                if (Mouse.isButtonDown(0) && (float) (this.ticksRan - this.mouseTicksRan) >= this.timer.ticksPerSecond / 4.0F && this.inGameHasFocus) {
                    this.clickMouse(0);
                    this.mouseTicksRan = this.ticksRan;
                }

                if (Mouse.isButtonDown(1) && (float) (this.ticksRan - this.mouseTicksRan) >= this.timer.ticksPerSecond / 4.0F && this.inGameHasFocus) {
                    this.clickMouse(1);
                    this.mouseTicksRan = this.ticksRan;
                }
            }

            this.sendClickBlockToController(0, this.currentScreen == null && Mouse.isButtonDown(0) && this.inGameHasFocus);
        }

        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                ++this.joinPlayerCounter;
                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.theWorld.difficultySetting = this.gameSettings.difficulty;
            if (this.theWorld.multiplayerWorld) {
                this.theWorld.difficultySetting = 3;
            }

            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }

            if (!this.isGamePaused) {
                this.renderGlobal.updateClouds();
            }

            if (!this.isGamePaused) {
                if (this.theWorld.field_27172_i > 0) {
                    --this.theWorld.field_27172_i;
                }

                this.theWorld.updateEntities();
            }

            if (!this.isGamePaused || this.isMultiplayerWorld()) {
                this.theWorld.setAllowedMobSpawns(this.gameSettings.difficulty > 0, true);
                this.theWorld.tick();
            }

            if (!this.isGamePaused && this.theWorld != null) {
                this.theWorld.randomDisplayUpdates(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            if (!this.isGamePaused) {
                this.effectRenderer.updateEffects();
            }
        }

        this.systemTime = System.currentTimeMillis();
    }

    private void forceReload() {
        System.out.println("FORCING RELOAD!");
        this.sndManager = new SoundManager();
        this.sndManager.loadSoundSettings(this.gameSettings);
        this.downloadResourcesThread.reloadResources();
    }

    public boolean isMultiplayerWorld() {
        return this.theWorld != null && this.theWorld.multiplayerWorld;
    }

    public void startWorld(String var1, String var2, long var3) {
        this.changeWorld1((World) null);
        System.gc();
        if (this.saveLoader.isOldMapFormat(var1)) {
            this.convertMapFormat(var1, var2);
        } else {
            ISaveHandler var5 = this.saveLoader.getSaveLoader(var1, false);
            World var6 = new World(var5, var2, var3, var1);
            this.initStatWriter(var6);
            if (var6.isNewWorld) {
                this.statFileWriter.readStat(StatList.createWorldStat, 1);
                this.statFileWriter.readStat(StatList.startGameStat, 1);
                this.changeWorld2(var6, "Generating level");
            } else {
                this.statFileWriter.readStat(StatList.loadWorldStat, 1);
                this.statFileWriter.readStat(StatList.startGameStat, 1);
                this.changeWorld2(var6, "Loading level");
            }
        }

    }

    public void usePortal() {
        System.out.println("Toggling dimension!!");
        if (this.thePlayer.dimension == -1) {
            this.thePlayer.dimension = 0;
        } else {
            this.thePlayer.dimension = -1;
        }

        this.theWorld.setEntityDead(this.thePlayer);
        this.thePlayer.isDead = false;
        double var1 = this.thePlayer.posX;
        double var3 = this.thePlayer.posZ;
        double var5 = 8.0D;
        World var7;
        if (this.thePlayer.dimension == -1) {
            var1 /= var5;
            var3 /= var5;
            this.thePlayer.setLocationAndAngles(var1, this.thePlayer.posY, var3, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
            if (this.thePlayer.isEntityAlive()) {
                this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
            }

            var7 = new World(this.theWorld, WorldProvider.getProviderForDimension(-1));
            this.changeWorld(var7, "Entering the Nether", this.thePlayer);
        } else {
            var1 *= var5;
            var3 *= var5;
            this.thePlayer.setLocationAndAngles(var1, this.thePlayer.posY, var3, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
            if (this.thePlayer.isEntityAlive()) {
                this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
            }

            var7 = new World(this.theWorld, WorldProvider.getProviderForDimension(0));
            this.changeWorld(var7, "Leaving the Nether", this.thePlayer);
        }

        this.thePlayer.worldObj = this.theWorld;
        if (this.thePlayer.isEntityAlive()) {
            this.thePlayer.setLocationAndAngles(var1, this.thePlayer.posY, var3, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
            this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
            (new Teleporter()).placeInPortal(this.theWorld, this.thePlayer);
        }

    }

    public void changeWorld1(World var1) {
        this.changeWorld2(var1, "");
    }

    public void changeWorld2(World var1, String var2) {
        this.changeWorld(var1, var2, (EntityPlayer) null);
    }

    public void initStatWriter(World world) {
        this.initStatWriter(world, "");
    }

    public void initStatWriter(World var1, String name) {
        File file;
        if (this.statFileWriter == null) {
            if (var1 == null) {
                file = new File(this.mcDataDir.getAbsolutePath() + File.separator + "remote" + File.separator + name);
                file.mkdirs();
                this.statFileWriter = new StatFileWriter(this.session, file);
            } else {
                file = new File(this.mcDataDir.getAbsolutePath() + File.separator + "saves" + File.separator + var1.worldName);
                this.statFileWriter = new StatFileWriter(this.session, file.getAbsoluteFile());
            }
            AchievementList.openInventory.setStatStringFormatter(new StatStringFormatKeyInv(this));
        }
    }

    public void changeWorld(World var1, String var2, EntityPlayer var3) {
        this.renderViewEntity = null;
        this.loadingScreen.printText(var2);
        this.loadingScreen.displayLoadingString("");
        this.sndManager.playStreaming((String) null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        if (this.theWorld != null) {
            this.theWorld.saveWorldIndirectly(this.loadingScreen);
        }

        this.theWorld = var1;
        if (var1 != null) {
            this.initStatWriter(var1);
            this.statFileWriter.syncStats();
            this.playerController.func_717_a(var1);
            if (!this.isMultiplayerWorld()) {
                if (var3 == null) {
                    this.thePlayer = (EntityPlayerSP) var1.func_4085_a(EntityPlayerSP.class);
                }
            } else if (this.thePlayer != null) {
                this.thePlayer.preparePlayerToSpawn();
                if (var1 != null) {
                    var1.entityJoinedWorld(this.thePlayer);
                }
            }

            if (!var1.multiplayerWorld) {
                this.preloadWorld(var2);
            }

            if (this.thePlayer == null) {
                this.thePlayer = (EntityPlayerSP) this.playerController.createPlayer(var1);
                this.thePlayer.preparePlayerToSpawn();
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            if (this.renderGlobal != null) {
                this.renderGlobal.changeWorld(var1);
            }

            if (this.effectRenderer != null) {
                this.effectRenderer.clearEffects(var1);
            }

            this.playerController.func_6473_b(this.thePlayer);
            if (var3 != null) {
                var1.emptyMethod1();
            }

            IChunkProvider var4 = var1.getIChunkProvider();
            if (var4 instanceof ChunkProviderLoadOrGenerate) {
                ChunkProviderLoadOrGenerate var5 = (ChunkProviderLoadOrGenerate) var4;
                int var6 = MathHelper.floor_float((float) ((int) this.thePlayer.posX)) >> 4;
                int var7 = MathHelper.floor_float((float) ((int) this.thePlayer.posZ)) >> 4;
                var5.setCurrentChunkOver(var6, var7);
            }

            var1.spawnPlayerWithLoadedChunks(this.thePlayer);
            if (var1.isNewWorld) {
                var1.saveWorldIndirectly(this.loadingScreen);
            }

            this.renderViewEntity = this.thePlayer;
        } else {
            this.thePlayer = null;
            this.statFileWriter = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    private void convertMapFormat(String var1, String var2) {
        this.loadingScreen.printText("Converting World to " + this.saveLoader.func_22178_a());
        this.loadingScreen.displayLoadingString("This may take a while :)");
        this.saveLoader.convertMapFormat(var1, this.loadingScreen);
        this.startWorld(var1, var2, 0L);
    }

    private void preloadWorld(String var1) {
        this.loadingScreen.printText(var1);
        this.loadingScreen.displayLoadingString("Building terrain");
        short var2 = 128;
        int var3 = 0;
        int var4 = var2 * 2 / 16 + 1;
        var4 *= var4;
        IChunkProvider var5 = this.theWorld.getIChunkProvider();
        ChunkCoordinates var6 = this.theWorld.getSpawnPoint();
        if (this.thePlayer != null) {
            var6.posX = (int) this.thePlayer.posX;
            var6.posZ = (int) this.thePlayer.posZ;
        }

        if (var5 instanceof ChunkProviderLoadOrGenerate) {
            ChunkProviderLoadOrGenerate var7 = (ChunkProviderLoadOrGenerate) var5;
            var7.setCurrentChunkOver(var6.posX >> 4, var6.posZ >> 4);
        }

        for (int var10 = -var2; var10 <= var2; var10 += 16) {
            for (int var8 = -var2; var8 <= var2; var8 += 16) {
                this.loadingScreen.setLoadingProgress(var3++ * 100 / var4);
                this.theWorld.getBlockId(var6.posX + var10, 64, var6.posZ + var8);

                while (this.theWorld.updatingLighting()) {
                }
            }
        }

        this.loadingScreen.displayLoadingString("Simulating world for a bit");
        this.theWorld.dropOldChucks();
    }

    public void installResource(String var1, File var2) {
        int var3 = var1.indexOf("/");
        String var4 = var1.substring(0, var3);
        var1 = var1.substring(var3 + 1);
        if (var4.equalsIgnoreCase("sound")) {
            this.sndManager.addSound(var1, var2);
        } else if (var4.equalsIgnoreCase("newsound")) {
            this.sndManager.addSound(var1, var2);
        } else if (var4.equalsIgnoreCase("streaming")) {
            this.sndManager.addStreaming(var1, var2);
        } else if (var4.equalsIgnoreCase("music")) {
            this.sndManager.addMusic(var1, var2);
        } else if (var4.equalsIgnoreCase("newmusic")) {
            this.sndManager.addMusic(var1, var2);
        }

    }

    public OpenGlCapsChecker getOpenGlCapsChecker() {
        return this.glCapabilities;
    }

    public String debugInfoRenders() {
        return this.renderGlobal.getDebugInfoRenders();
    }

    public String func_6262_n() {
        return this.renderGlobal.getDebugInfoEntities();
    }

    public String func_21002_o() {
        return this.theWorld.func_21119_g();
    }

    public String debugInfoEntities() {
        return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
    }

    public void respawn(boolean var1, int var2) {
        if (!this.theWorld.multiplayerWorld && !this.theWorld.worldProvider.canRespawnHere()) {
            this.usePortal();
        }

        ChunkCoordinates var3 = null;
        ChunkCoordinates var4 = null;
        boolean var5 = true;
        if (this.thePlayer != null && !var1) {
            var3 = this.thePlayer.getPlayerSpawnCoordinate();
            if (var3 != null) {
                var4 = EntityPlayer.verifyRespawnCoordinates(this.theWorld, var3);
                if (var4 == null) {
                    this.thePlayer.addChatMessage("tile.bed.notValid");
                }
            }
        }

        if (var4 == null) {
            var4 = this.theWorld.getSpawnPoint();
            var5 = false;
        }

        IChunkProvider var6 = this.theWorld.getIChunkProvider();
        if (var6 instanceof ChunkProviderLoadOrGenerate) {
            ChunkProviderLoadOrGenerate var7 = (ChunkProviderLoadOrGenerate) var6;
            var7.setCurrentChunkOver(var4.posX >> 4, var4.posZ >> 4);
        }

        this.theWorld.setSpawnLocation();
        this.theWorld.updateEntityList();
        int var8 = 0;
        if (this.thePlayer != null) {
            var8 = this.thePlayer.entityId;
            this.theWorld.setEntityDead(this.thePlayer);
        }

        this.renderViewEntity = null;
        this.thePlayer = (EntityPlayerSP) this.playerController.createPlayer(this.theWorld);
        this.thePlayer.dimension = var2;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        if (var5) {
            this.thePlayer.setPlayerSpawnCoordinate(var3);
            this.thePlayer.setLocationAndAngles((double) ((float) var4.posX + 0.5F), (double) ((float) var4.posY + 0.1F), (double) ((float) var4.posZ + 0.5F), 0.0F, 0.0F);
        }

        this.playerController.flipPlayer(this.thePlayer);
        this.theWorld.spawnPlayerWithLoadedChunks(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.entityId = var8;
        this.thePlayer.func_6420_o();
        this.playerController.func_6473_b(this.thePlayer);
        this.preloadWorld("Respawning");
        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen((GuiScreen) null);
        }

    }

    public static void startMainThread(String var0, String var1, String var2) {
        boolean var3 = false;
        Frame var5 = new Frame("Minecraft");
        Canvas var6 = new Canvas();
        var5.setLayout(new BorderLayout());
        var5.add(var6, "Center");
        var6.setPreferredSize(new Dimension(854, 480));
        var5.pack();
        var5.setLocationRelativeTo((Component) null);
        MinecraftImpl var7 = new MinecraftImpl(var5, var6, 854, 480, var3, var5);
        Thread var8 = new Thread(var7, "Minecraft main thread");
        var8.setPriority(10);
        var7.minecraftUri = "www.minecraft.net";
        if (var0 != null && var1 != null) {
            var7.session = new Session(var0, var1);
        } else {
            var7.session = new Session("Player" + System.currentTimeMillis() % 1000L, "");
        }

        if (var2 != null) {
            String[] var9 = var2.split(":");
            var7.setServer(var9[0], Integer.parseInt(var9[1]));
        }

        var5.setVisible(true);
        var5.addWindowListener(new GameWindowListener(var7, var8));
        var8.start();
    }

    public NetClientHandler getSendQueue() {
        return this.thePlayer instanceof EntityClientPlayerMP ? ((EntityClientPlayerMP) this.thePlayer).sendQueue : null;
    }

    public static void main(String[] var0) {
        String var1 = null, var2 = null;
        if (var0.length > 0) {
            var1 = var0[0];
        }

        if (var0.length > 1) {
            var2 = var0[1];
        }

        startMainThread(var1, var2, null);
    }

    public static boolean isGuiEnabled() {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion;
    }

    public static boolean isDebugInfoEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.showDebugInfo;
    }

}
