package net.minecraft.src;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class Config {

    private static GameSettings gameSettings = null;
    private static Minecraft minecraft = null;
    private static float[] lightLevels = null;
    private static int iconWidthTerrain = 16;
    private static int iconWidthItems = 16;
    private static Map foundClassesMap = new HashMap();
    private static boolean fontRendererUpdated = false;
    private static File logFile = null;
    public static final Boolean DEF_FOG_FANCY = Boolean.valueOf(true);
    public static final Float DEF_FOG_START = Float.valueOf(0.2F);
    public static final Boolean DEF_OPTIMIZE_RENDER_DISTANCE = Boolean.valueOf(false);
    public static final Boolean DEF_OCCLUSION_ENABLED = Boolean.valueOf(false);
    public static final Integer DEF_MIPMAP_LEVEL = Integer.valueOf(0);
    public static final Integer DEF_MIPMAP_TYPE = Integer.valueOf(9984);
    public static final Float DEF_ALPHA_FUNC_LEVEL = Float.valueOf(0.1F);
    public static final Boolean DEF_LOAD_CHUNKS_FAR = Boolean.valueOf(false);
    public static final Integer DEF_PRELOADED_CHUNKS = Integer.valueOf(0);
    public static final Integer DEF_CHUNKS_LIMIT = Integer.valueOf(25);
    public static final Integer DEF_UPDATES_PER_FRAME = Integer.valueOf(3);
    public static final Boolean DEF_DYNAMIC_UPDATES = Boolean.valueOf(false);

    private static String getVersion() {
        return "OptiFine_1.7.3_HD_G";
    }

    private static void checkOpenGlCaps() {
        log("");
        log(getVersion());
        log("" + new Date());
        log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        log("LWJGL: " + Sys.getVersion());
        log("OpenGL: " + GL11.glGetString(7937) + " version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936));
        int ver = getOpenGlVersion();
        String verStr = "" + ver / 10 + "." + ver % 10;

        log("OpenGL Version: " + verStr);
        if (!GLContext.getCapabilities().OpenGL12) {
            log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
        }

        if (!GLContext.getCapabilities().GL_NV_fog_distance) {
            log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
        }

        if (!GLContext.getCapabilities().GL_ARB_occlusion_query) {
            log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
        }

    }

    public static boolean isFancyFogAvailable() {
        return GLContext.getCapabilities().GL_NV_fog_distance;
    }

    public static boolean isOcclusionAvailable() {
        return GLContext.getCapabilities().GL_ARB_occlusion_query;
    }

    private static int getOpenGlVersion() {
        return !GLContext.getCapabilities().OpenGL11 ? 10 : (!GLContext.getCapabilities().OpenGL12 ? 11 : (!GLContext.getCapabilities().OpenGL13 ? 12 : (!GLContext.getCapabilities().OpenGL14 ? 13 : (!GLContext.getCapabilities().OpenGL15 ? 14 : (!GLContext.getCapabilities().OpenGL20 ? 15 : (!GLContext.getCapabilities().OpenGL21 ? 20 : (!GLContext.getCapabilities().OpenGL30 ? 21 : (!GLContext.getCapabilities().OpenGL31 ? 30 : (!GLContext.getCapabilities().OpenGL32 ? 31 : (!GLContext.getCapabilities().OpenGL33 ? 32 : (!GLContext.getCapabilities().OpenGL40 ? 33 : 40)))))))))));
    }

    public static void setGameSettings(GameSettings options) {
        if (Config.gameSettings == null) {
            checkOpenGlCaps();
        }

        Config.gameSettings = options;
    }

    public static boolean isUseMipmaps() {
        int mipmapLevel = getMipmapLevel();

        return mipmapLevel > 0;
    }

    public static int getMipmapLevel() {
        return Config.gameSettings == null ? Config.DEF_MIPMAP_LEVEL.intValue() : Config.gameSettings.ofMipmapLevel;
    }

    public static int getMipmapType() {
        return Config.gameSettings == null ? Config.DEF_MIPMAP_TYPE.intValue() : (Config.gameSettings.ofMipmapLinear ? 9986 : 9984);
    }

    public static boolean isUseAlphaFunc() {
        float alphaFuncLevel = getAlphaFuncLevel();

        return alphaFuncLevel > Config.DEF_ALPHA_FUNC_LEVEL.floatValue() + 1.0E-5F;
    }

    public static float getAlphaFuncLevel() {
        return Config.DEF_ALPHA_FUNC_LEVEL.floatValue();
    }

    public static boolean isFogFancy() {
        return !GLContext.getCapabilities().GL_NV_fog_distance ? false : (Config.gameSettings == null ? false : Config.gameSettings.ofFogFancy);
    }

    public static float getFogStart() {
        return Config.gameSettings == null ? Config.DEF_FOG_START.floatValue() : Config.gameSettings.ofFogStart;
    }

    public static boolean isOcclusionEnabled() {
        return Config.gameSettings == null ? Config.DEF_OCCLUSION_ENABLED.booleanValue() : Config.gameSettings.advancedOpengl;
    }

    public static boolean isOcclusionFancy() {
        return !isOcclusionEnabled() ? false : (Config.gameSettings == null ? false : Config.gameSettings.ofOcclusionFancy);
    }

    public static boolean isLoadChunksFar() {
        return Config.gameSettings == null ? Config.DEF_LOAD_CHUNKS_FAR.booleanValue() : Config.gameSettings.ofLoadFar;
    }

    public static int getPreloadedChunks() {
        return Config.gameSettings == null ? Config.DEF_PRELOADED_CHUNKS.intValue() : Config.gameSettings.ofPreloadedChunks;
    }

    public static void dbg(String s) {
        System.out.println(s);
    }

    public static void log(String s) {
        dbg(s);

        try {
            if (Config.logFile == null) {
                Config.logFile = new File(Minecraft.getMinecraftDir(), "optifog.log");
                Config.logFile.delete();
                Config.logFile.createNewFile();
            }

            FileOutputStream e = new FileOutputStream(Config.logFile, true);
            try (OutputStreamWriter logFileWriter = new OutputStreamWriter(e, "UTF-8")) {
                logFileWriter.write(s);
                logFileWriter.write("\n");
                logFileWriter.flush();
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    public static int getUpdatesPerFrame() {
        return Config.gameSettings != null ? Config.gameSettings.ofChunkUpdates : 1;
    }

    public static boolean isDynamicUpdates() {
        return Config.gameSettings != null ? Config.gameSettings.ofChunkUpdatesDynamic : true;
    }

    public static boolean isRainFancy() {
        return Config.gameSettings.ofRain == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofRain == 2;
    }

    public static boolean isWaterFancy() {
        return Config.gameSettings.ofWater == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofWater == 2;
    }

    public static boolean isRainOff() {
        return Config.gameSettings.ofRain == 3;
    }

    public static boolean isCloudsFancy() {
        return Config.gameSettings.ofClouds == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofClouds == 2;
    }

    public static boolean isCloudsOff() {
        return Config.gameSettings.ofClouds == 3;
    }

    public static boolean isTreesFancy() {
        return Config.gameSettings.ofTrees == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofTrees == 2;
    }

    public static boolean isGrassFancy() {
        return Config.gameSettings.ofGrass == 0 ? Config.gameSettings.fancyGraphics : Config.gameSettings.ofGrass == 2;
    }

    public static int limit(int val, int min, int max) {
        return val < min ? min : (val > max ? max : val);
    }

    public static float limit(float val, float min, float max) {
        return val < min ? min : (val > max ? max : val);
    }

    public static boolean isAnimatedWater() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedWater != 2 : true;
    }

    public static boolean isGeneratedWater() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedWater == 1 : true;
    }

    public static boolean isAnimatedPortal() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedPortal : true;
    }

    public static boolean isAnimatedLava() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedLava != 2 : true;
    }

    public static boolean isGeneratedLava() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedLava == 1 : true;
    }

    public static boolean isAnimatedFire() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedFire : true;
    }

    public static boolean isAnimatedRedstone() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedRedstone : true;
    }

    public static boolean isAnimatedExplosion() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedExplosion : true;
    }

    public static boolean isAnimatedFlame() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedFlame : true;
    }

    public static boolean isAnimatedSmoke() {
        return Config.gameSettings != null ? Config.gameSettings.ofAnimatedSmoke : true;
    }

    public static float getAmbientOcclusionLevel() {
        return Config.gameSettings != null ? Config.gameSettings.ofAoLevel : 0.0F;
    }

    public static float fixAoLight(float light, float defLight) {
        if (Config.lightLevels == null) {
            return light;
        } else {
            float level_0 = Config.lightLevels[0];
            float level_1 = Config.lightLevels[1];

            if (light > level_0) {
                return light;
            } else if (defLight <= level_1) {
                return light;
            } else {
                float mul = 1.0F - getAmbientOcclusionLevel();

                return light + (defLight - light) * mul;
            }
        }
    }

    public static void setLightLevels(float[] levels) {
        Config.lightLevels = levels;
    }

    public static String arrayToString(Object[] arr) {
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < arr.length; ++i) {
            Object obj = arr[i];

            if (i > 0) {
                buf.append(", ");
            }

            buf.append(String.valueOf(obj));
        }

        return buf.toString();
    }

    public static void setMinecraft(Minecraft mc) {
        Config.minecraft = mc;
    }

    public static Minecraft getMinecraft() {
        return Config.minecraft;
    }

    public static int getIconWidthTerrain() {
        return Config.iconWidthTerrain;
    }

    public static int getIconWidthItems() {
        return Config.iconWidthItems;
    }

    public static void setIconWidthItems(int iconWidth) {
        Config.iconWidthItems = iconWidth;
    }

    public static void setIconWidthTerrain(int iconWidth) {
        Config.iconWidthTerrain = iconWidth;
    }

    public static int getMaxDynamicTileWidth() {
        return 64;
    }

    public static int getSideGrassTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (!isBetterGrass()) {
            return 3;
        } else {
            if (isBetterGrassFancy()) {
                --y;
                switch (side) {
                case 2:
                    --z;
                    break;

                case 3:
                    ++z;
                    break;

                case 4:
                    --x;
                    break;

                case 5:
                    ++x;
                }

                int blockId = blockAccess.getBlockId(x, y, z);

                if (blockId != 2) {
                    return 3;
                }
            }

            return 0;
        }
    }

    public static int getSideSnowGrassTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (!isBetterGrass()) {
            return 68;
        } else {
            if (isBetterGrassFancy()) {
                switch (side) {
                case 2:
                    --z;
                    break;

                case 3:
                    ++z;
                    break;

                case 4:
                    --x;
                    break;

                case 5:
                    ++x;
                }

                int blockId = blockAccess.getBlockId(x, y, z);

                if (blockId != 78 && blockId != 80) {
                    return 68;
                }
            }

            return 66;
        }
    }

    public static boolean isBetterGrass() {
        return Config.gameSettings == null ? false : Config.gameSettings.ofBetterGrass != 3;
    }

    public static boolean isBetterGrassFancy() {
        return Config.gameSettings == null ? false : Config.gameSettings.ofBetterGrass == 2;
    }

    public static boolean isWeatherEnabled() {
        return Config.gameSettings == null ? true : Config.gameSettings.ofWeather;
    }

    public static boolean isSkyEnabled() {
        return Config.gameSettings == null ? true : Config.gameSettings.ofSky;
    }

    public static boolean isStarsEnabled() {
        return Config.gameSettings == null ? true : Config.gameSettings.ofStars;
    }

    public static boolean isFarView() {
        return Config.gameSettings == null ? false : Config.gameSettings.ofFarView;
    }

    public static void sleep(long ms) {
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }

    }

    public static boolean isTimeDayOnly() {
        return Config.gameSettings == null ? false : Config.gameSettings.ofTime == 1;
    }

    public static boolean isTimeNightOnly() {
        return Config.gameSettings == null ? false : Config.gameSettings.ofTime == 2;
    }

    public static boolean isClearWater() {
        return Config.gameSettings == null ? false : Config.gameSettings.ofClearWater;
    }
}
