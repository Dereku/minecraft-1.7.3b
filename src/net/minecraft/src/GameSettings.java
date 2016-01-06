package net.minecraft.src;

import net.minecraft.stats.StatCollector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.block.Block;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class GameSettings {

    private static final String[] RENDER_DISTANCES = new String[]{"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
    private static final String[] DIFFICULTIES = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};
    private static final String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
    private static final String[] LIMIT_FRAMERATES = new String[]{"performance.max", "performance.balanced", "performance.powersaver"};
    public float musicVolume = 1.0F;
    public float soundVolume = 1.0F;
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse = false;
    public int renderDistance = 0;
    public boolean viewBobbing = true;
    public boolean anaglyph = false;
    public boolean advancedOpengl = false;
    public int limitFramerate = 1;
    public boolean fancyGraphics = true;
    public boolean ambientOcclusion = true;
    public boolean ofFogFancy = false;
    public float ofFogStart = 0.8F;
    public int ofMipmapLevel = 0;
    public boolean ofMipmapLinear = false;
    public boolean ofLoadFar = false;
    public int ofPreloadedChunks = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothInput = false;
    public float ofBrightness = 0.0F;
    public float ofAoLevel = 0.0F;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0F;
    public int ofTrees = 0;
    public int ofGrass = 0;
    public int ofRain = 0;
    public int ofWater = 0;
    public int ofBetterGrass = 3;
    public int ofAutoSaveTicks = 4000;
    public boolean ofFastDebugInfo = false;
    public boolean ofWeather = true;
    public boolean ofSky = true;
    public boolean ofStars = true;
    public int ofChunkUpdates = 1;
    public boolean ofChunkUpdatesDynamic = true;
    public boolean ofFarView = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public int ofAnimatedWater = 0;
    public int ofAnimatedLava = 0;
    public boolean ofAnimatedFire = true;
    public boolean ofAnimatedPortal = true;
    public boolean ofAnimatedRedstone = true;
    public boolean ofAnimatedExplosion = true;
    public boolean ofAnimatedFlame = true;
    public boolean ofAnimatedSmoke = true;
    public static final int DEFAULT = 0;
    public static final int FAST = 1;
    public static final int FANCY = 2;
    public static final int OFF = 3;
    public static final int ANIM_ON = 0;
    public static final int ANIM_GENERATED = 1;
    public static final int ANIM_OFF = 2;
    public KeyBinding ofKeyBindZoom;
    public String skin = "Default";
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17);
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30);
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31);
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32);
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57);
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18);
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16);
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20);
    public KeyBinding keyBindToggleFog = new KeyBinding("key.fog", 33);
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42);
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53);
    public KeyBinding[] keyBindings;
    public Minecraft mc;
    private File optionsFile;
    public int difficulty;
    public boolean hideGUI;
    public boolean thirdPersonView;
    public boolean showDebugInfo;
    public String lastServer;
    public boolean field_22275_C;
    public boolean smoothCamera;
    public boolean field_22273_E;
    public float field_22272_F;
    public float field_22271_G;
    public int guiScale;

    public GameSettings(Minecraft minecraft, File file) {
        this.ofKeyBindZoom = new KeyBinding("Zoom", 46);
        this.keyBindings = new KeyBinding[]{this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindToggleFog, this.ofKeyBindZoom};
        this.difficulty = 2;
        this.hideGUI = false;
        this.thirdPersonView = false;
        this.showDebugInfo = false;
        this.lastServer = "";
        this.field_22275_C = false;
        this.smoothCamera = false;
        this.field_22273_E = false;
        this.field_22272_F = 1.0F;
        this.field_22271_G = 1.0F;
        this.guiScale = 0;
        this.mc = minecraft;
        this.optionsFile = new File(file, "options.txt");
        this.loadOptions();
        Config.setGameSettings(this);
    }

    public String getKeyBindingDescription(int i) {
        StringTranslate stringtranslate = StringTranslate.getInstance();

        return stringtranslate.translateKey(this.keyBindings[i].keyDescription);
    }

    public String getOptionDisplayString(int i) {
        return Keyboard.getKeyName(this.keyBindings[i].keyCode);
    }

    public void setKeyBinding(int i, int j) {
        this.keyBindings[i].keyCode = j;
        this.saveOptions();
    }

    public void setOptionFloatValue(EnumOptions enumoptions, float f) {
        if (enumoptions == EnumOptions.MUSIC) {
            this.musicVolume = f;
            this.mc.sndManager.onSoundOptionsChanged();
        }

        if (enumoptions == EnumOptions.SOUND) {
            this.soundVolume = f;
            this.mc.sndManager.onSoundOptionsChanged();
        }

        if (enumoptions == EnumOptions.SENSITIVITY) {
            this.mouseSensitivity = f;
        }

        if (enumoptions == EnumOptions.BRIGHTNESS) {
            this.ofBrightness = f;
            this.updateWorldLightLevels();
        }

        if (enumoptions == EnumOptions.CLOUD_HEIGHT) {
            this.ofCloudsHeight = f;
        }

        if (enumoptions == EnumOptions.AO_LEVEL) {
            this.ofAoLevel = f;
            this.ambientOcclusion = this.ofAoLevel > 0.0F;
            this.mc.renderGlobal.loadRenderers();
        }

    }

    private void updateWorldLightLevels() {
        if (this.mc.theWorld != null) {
            this.mc.theWorld.updatingLighting();
        }

        if (this.mc.renderGlobal != null) {
            this.mc.renderGlobal.loadRenderers();
        }

    }

    private void updateWaterOpacity() {
        byte opacity = 3;

        if (this.ofClearWater) {
            opacity = 1;
        }

        Block.waterStill.setLightOpacity(opacity);
        Block.waterMoving.setLightOpacity(opacity);
        if (this.mc.theWorld != null) {
            IChunkProvider cp = this.mc.theWorld.chunkProvider;

            if (cp != null) {
                for (int x = -512; x < 512; ++x) {
                    for (int z = -512; z < 512; ++z) {
                        if (cp.chunkExists(x, z)) {
                            Chunk c = cp.provideChunk(x, z);

                            if (c != null) {
                                byte[] data = c.skylightMap.data;

                                for (int i = 0; i < data.length; ++i) {
                                    data[i] = 0;
                                }

                                c.generateSkylightMap();
                            }
                        }
                    }
                }

                this.mc.renderGlobal.loadRenderers();
            }
        }
    }

    public void setOptionValue(EnumOptions enumoptions, int i) {
        if (enumoptions == EnumOptions.INVERT_MOUSE) {
            this.invertMouse = !this.invertMouse;
        }

        if (enumoptions == EnumOptions.RENDER_DISTANCE) {
            this.renderDistance = this.renderDistance + i & 3;
        }

        if (enumoptions == EnumOptions.GUI_SCALE) {
            this.guiScale = this.guiScale + i & 3;
        }

        if (enumoptions == EnumOptions.VIEW_BOBBING) {
            this.viewBobbing = !this.viewBobbing;
        }

        if (enumoptions == EnumOptions.ADVANCED_OPENGL) {
            if (!Config.isOcclusionAvailable()) {
                this.ofOcclusionFancy = false;
                this.advancedOpengl = false;
            } else if (!this.advancedOpengl) {
                this.advancedOpengl = true;
                this.ofOcclusionFancy = false;
            } else if (!this.ofOcclusionFancy) {
                this.ofOcclusionFancy = true;
            } else {
                this.ofOcclusionFancy = false;
                this.advancedOpengl = false;
            }

            this.mc.renderGlobal.setAllRenderesVisible();
        }

        if (enumoptions == EnumOptions.ANAGLYPH) {
            this.anaglyph = !this.anaglyph;
            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.FRAMERATE_LIMIT) {
            this.limitFramerate = (this.limitFramerate + i) % 4;
            Display.setVSyncEnabled(this.limitFramerate == 3);
        }

        if (enumoptions == EnumOptions.DIFFICULTY) {
            this.difficulty = this.difficulty + i & 3;
        }

        if (enumoptions == EnumOptions.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = !this.ambientOcclusion;
            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.FOG_FANCY) {
            if (!Config.isFancyFogAvailable()) {
                this.ofFogFancy = false;
            } else {
                this.ofFogFancy = !this.ofFogFancy;
            }
        }

        if (enumoptions == EnumOptions.FOG_START) {
            this.ofFogStart += 0.2F;
            if (this.ofFogStart > 0.81F) {
                this.ofFogStart = 0.2F;
            }
        }

        if (enumoptions == EnumOptions.MIPMAP_LEVEL) {
            ++this.ofMipmapLevel;
            if (this.ofMipmapLevel > 4) {
                this.ofMipmapLevel = 0;
            }

            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.MIPMAP_TYPE) {
            this.ofMipmapLinear = !this.ofMipmapLinear;
            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.LOAD_FAR) {
            this.ofLoadFar = !this.ofLoadFar;
            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.PRELOADED_CHUNKS) {
            this.ofPreloadedChunks += 2;
            if (this.ofPreloadedChunks > 8) {
                this.ofPreloadedChunks = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.SMOOTH_FPS) {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (enumoptions == EnumOptions.SMOOTH_INPUT) {
            this.ofSmoothInput = !this.ofSmoothInput;
        }

        if (enumoptions == EnumOptions.CLOUDS) {
            ++this.ofClouds;
            if (this.ofClouds > 3) {
                this.ofClouds = 0;
            }
        }

        if (enumoptions == EnumOptions.TREES) {
            ++this.ofTrees;
            if (this.ofTrees > 2) {
                this.ofTrees = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.GRASS) {
            ++this.ofGrass;
            if (this.ofGrass > 2) {
                this.ofGrass = 0;
            }

            RenderBlocks.fancyGrass = Config.isGrassFancy();
            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.RAIN) {
            ++this.ofRain;
            if (this.ofRain > 3) {
                this.ofRain = 0;
            }
        }

        if (enumoptions == EnumOptions.WATER) {
            ++this.ofWater;
            if (this.ofWater > 2) {
                this.ofWater = 0;
            }
        }

        if (enumoptions == EnumOptions.ANIMATED_WATER) {
            ++this.ofAnimatedWater;
            if (this.ofAnimatedWater > 2) {
                this.ofAnimatedWater = 0;
            }

            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.ANIMATED_LAVA) {
            ++this.ofAnimatedLava;
            if (this.ofAnimatedLava > 2) {
                this.ofAnimatedLava = 0;
            }

            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.ANIMATED_FIRE) {
            this.ofAnimatedFire = !this.ofAnimatedFire;
            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.ANIMATED_PORTAL) {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
            this.mc.renderEngine.refreshTextures();
        }

        if (enumoptions == EnumOptions.ANIMATED_REDSTONE) {
            this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        }

        if (enumoptions == EnumOptions.ANIMATED_EXPLOSION) {
            this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        }

        if (enumoptions == EnumOptions.ANIMATED_FLAME) {
            this.ofAnimatedFlame = !this.ofAnimatedFlame;
        }

        if (enumoptions == EnumOptions.ANIMATED_SMOKE) {
            this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        }

        if (enumoptions == EnumOptions.FAST_DEBUG_INFO) {
            this.ofFastDebugInfo = !this.ofFastDebugInfo;
        }

        if (enumoptions == EnumOptions.AUTOSAVE_TICKS) {
            this.ofAutoSaveTicks *= 10;
            if (this.ofAutoSaveTicks > '鱀') {
                this.ofAutoSaveTicks = 40;
            }
        }

        if (enumoptions == EnumOptions.BETTER_GRASS) {
            ++this.ofBetterGrass;
            if (this.ofBetterGrass > 3) {
                this.ofBetterGrass = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.WEATHER) {
            this.ofWeather = !this.ofWeather;
        }

        if (enumoptions == EnumOptions.SKY) {
            this.ofSky = !this.ofSky;
        }

        if (enumoptions == EnumOptions.STARS) {
            this.ofStars = !this.ofStars;
        }

        if (enumoptions == EnumOptions.CHUNK_UPDATES) {
            ++this.ofChunkUpdates;
            if (this.ofChunkUpdates > 5) {
                this.ofChunkUpdates = 1;
            }
        }

        if (enumoptions == EnumOptions.CHUNK_UPDATES_DYNAMIC) {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (enumoptions == EnumOptions.FAR_VIEW) {
            this.ofFarView = !this.ofFarView;
            this.mc.renderGlobal.loadRenderers();
        }

        if (enumoptions == EnumOptions.TIME) {
            ++this.ofTime;
            if (this.ofTime > 2) {
                this.ofTime = 0;
            }
        }

        if (enumoptions == EnumOptions.CLEAR_WATER) {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(EnumOptions enumoptions) {
        return enumoptions == EnumOptions.MUSIC ? this.musicVolume : (enumoptions == EnumOptions.SOUND ? this.soundVolume : (enumoptions == EnumOptions.SENSITIVITY ? this.mouseSensitivity : (enumoptions == EnumOptions.BRIGHTNESS ? this.ofBrightness : (enumoptions == EnumOptions.CLOUD_HEIGHT ? this.ofCloudsHeight : (enumoptions == EnumOptions.AO_LEVEL ? this.ofAoLevel : 0.0F)))));
    }

    public boolean getOptionOrdinalValue(EnumOptions enumoptions) {
        switch (EnumOptionsMappingHelper.enumOptionsMappingHelperArray[enumoptions.ordinal()]) {
            case 1:
                return this.invertMouse;

            case 2:
                return this.viewBobbing;

            case 3:
                return this.anaglyph;

            case 4:
                return this.advancedOpengl;

            case 5:
                return this.ambientOcclusion;

            default:
                return false;
        }
    }

    public String getKeyBinding(EnumOptions enumoptions) {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        String prefix = stringtranslate.translateKey(enumoptions.getEnumString());

        if (prefix == null) {
            prefix = enumoptions.getEnumString();
        }

        String s = prefix + ": ";

        if (enumoptions.getEnumFloat()) {
            float flag1 = this.getOptionFloatValue(enumoptions);

            return enumoptions == EnumOptions.SENSITIVITY ? (flag1 == 0.0F ? s + stringtranslate.translateKey("options.sensitivity.min") : (flag1 == 1.0F ? s + stringtranslate.translateKey("options.sensitivity.max") : s + (int) (flag1 * 200.0F) + "%")) : (flag1 == 0.0F ? s + stringtranslate.translateKey("options.off") : s + (int) (flag1 * 100.0F) + "%");
        } else if (enumoptions == EnumOptions.ADVANCED_OPENGL) {
            return !this.advancedOpengl ? s + "OFF" : (this.ofOcclusionFancy ? s + "Fancy" : s + "Fast");
        } else if (enumoptions.getEnumBoolean()) {
            boolean flag = this.getOptionOrdinalValue(enumoptions);
            return flag ? s + stringtranslate.translateKey("options.on") : s + stringtranslate.translateKey("options.off");
        } else if (enumoptions == EnumOptions.RENDER_DISTANCE) {
            return s + stringtranslate.translateKey(GameSettings.RENDER_DISTANCES[this.renderDistance]);
        } else if (enumoptions == EnumOptions.DIFFICULTY) {
            return s + stringtranslate.translateKey(GameSettings.DIFFICULTIES[this.difficulty]);
        } else if (enumoptions == EnumOptions.GUI_SCALE) {
            return s + stringtranslate.translateKey(GameSettings.GUISCALES[this.guiScale]);
        } else if (enumoptions == EnumOptions.FRAMERATE_LIMIT) {
            return this.limitFramerate == 3 ? s + "VSync" : s + StatCollector.translateToLocal(GameSettings.LIMIT_FRAMERATES[this.limitFramerate]);
        } else if (enumoptions == EnumOptions.FOG_FANCY) {
            return this.ofFogFancy ? s + "Fancy" : s + "Fast";
        } else if (enumoptions == EnumOptions.FOG_START) {
            return s + this.ofFogStart;
        } else if (enumoptions == EnumOptions.MIPMAP_LEVEL) {
            return s + this.ofMipmapLevel;
        } else if (enumoptions == EnumOptions.MIPMAP_TYPE) {
            return this.ofMipmapLinear ? s + "Linear" : s + "Nearest";
        } else if (enumoptions == EnumOptions.LOAD_FAR) {
            return this.ofLoadFar ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.PRELOADED_CHUNKS) {
            return this.ofPreloadedChunks == 0 ? s + "OFF" : s + this.ofPreloadedChunks;
        } else if (enumoptions == EnumOptions.SMOOTH_FPS) {
            return this.ofSmoothFps ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.SMOOTH_INPUT) {
            return this.ofSmoothInput ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.CLOUDS) {
            switch (this.ofClouds) {
                case 1:
                    return s + "Fast";

                case 2:
                    return s + "Fancy";

                case 3:
                    return s + "OFF";

                default:
                    return s + "Default";
            }
        } else if (enumoptions == EnumOptions.TREES) {
            switch (this.ofTrees) {
                case 1:
                    return s + "Fast";

                case 2:
                    return s + "Fancy";

                default:
                    return s + "Default";
            }
        } else if (enumoptions == EnumOptions.GRASS) {
            switch (this.ofGrass) {
                case 1:
                    return s + "Fast";

                case 2:
                    return s + "Fancy";

                default:
                    return s + "Default";
            }
        } else if (enumoptions == EnumOptions.RAIN) {
            switch (this.ofRain) {
                case 1:
                    return s + "Fast";

                case 2:
                    return s + "Fancy";

                case 3:
                    return s + "OFF";

                default:
                    return s + "Default";
            }
        } else if (enumoptions == EnumOptions.WATER) {
            switch (this.ofWater) {
                case 1:
                    return s + "Fast";

                case 2:
                    return s + "Fancy";

                case 3:
                    return s + "OFF";

                default:
                    return s + "Default";
            }
        } else if (enumoptions == EnumOptions.ANIMATED_WATER) {
            switch (this.ofAnimatedWater) {
                case 1:
                    return s + "Dynamic";

                case 2:
                    return s + "OFF";

                default:
                    return s + "ON";
            }
        } else if (enumoptions == EnumOptions.ANIMATED_LAVA) {
            switch (this.ofAnimatedLava) {
                case 1:
                    return s + "Dynamic";

                case 2:
                    return s + "OFF";

                default:
                    return s + "ON";
            }
        } else if (enumoptions == EnumOptions.ANIMATED_FIRE) {
            return this.ofAnimatedFire ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.ANIMATED_PORTAL) {
            return this.ofAnimatedPortal ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.ANIMATED_REDSTONE) {
            return this.ofAnimatedRedstone ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.ANIMATED_EXPLOSION) {
            return this.ofAnimatedExplosion ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.ANIMATED_FLAME) {
            return this.ofAnimatedFlame ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.ANIMATED_SMOKE) {
            return this.ofAnimatedSmoke ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.FAST_DEBUG_INFO) {
            return this.ofFastDebugInfo ? s + "ON" : s + "OFF";
        } else if (enumoptions == EnumOptions.AUTOSAVE_TICKS) {
            return this.ofAutoSaveTicks <= 40 ? s + "Default (2s)" : (this.ofAutoSaveTicks <= 400 ? s + "20s" : (this.ofAutoSaveTicks <= 4000 ? s + "3min" : s + "30min"));
        } else if (enumoptions == EnumOptions.BETTER_GRASS) {
            switch (this.ofBetterGrass) {
                case 1:
                    return s + "Fast";

                case 2:
                    return s + "Fancy";

                default:
                    return s + "OFF";
            }
        } else {
            return enumoptions == EnumOptions.WEATHER ? (this.ofWeather ? s + "ON" : s + "OFF") : (enumoptions == EnumOptions.SKY ? (this.ofSky ? s + "ON" : s + "OFF") : (enumoptions == EnumOptions.STARS ? (this.ofStars ? s + "ON" : s + "OFF") : (enumoptions == EnumOptions.CHUNK_UPDATES ? s + this.ofChunkUpdates : (enumoptions == EnumOptions.CHUNK_UPDATES_DYNAMIC ? (this.ofChunkUpdatesDynamic ? s + "ON" : s + "OFF") : (enumoptions == EnumOptions.FAR_VIEW ? (this.ofFarView ? s + "ON" : s + "OFF") : (enumoptions == EnumOptions.TIME ? (this.ofTime == 1 ? s + "Day Only" : (this.ofTime == 2 ? s + "Night Only" : s + "Default")) : (enumoptions == EnumOptions.CLEAR_WATER ? (this.ofClearWater ? s + "ON" : s + "OFF") : (enumoptions == EnumOptions.GRAPHICS ? (this.fancyGraphics ? s + stringtranslate.translateKey("options.graphics.fancy") : s + stringtranslate.translateKey("options.graphics.fast")) : s))))))));
        }
    }

    private void loadOptions() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }

            BufferedReader exception = new BufferedReader(new FileReader(this.optionsFile));
            String s = "";

            while ((s = exception.readLine()) != null) {
                try {
                    String[] line = s.split(":");

                    if (line[0].equals("music")) {
                        this.musicVolume = this.parseFloat(line[1]);
                    }

                    if (line[0].equals("sound")) {
                        this.soundVolume = this.parseFloat(line[1]);
                    }

                    if (line[0].equals("mouseSensitivity")) {
                        this.mouseSensitivity = this.parseFloat(line[1]);
                    }

                    if (line[0].equals("invertYMouse")) {
                        this.invertMouse = line[1].equals("true");
                    }

                    if (line[0].equals("viewDistance")) {
                        this.renderDistance = Integer.parseInt(line[1]);
                    }

                    if (line[0].equals("guiScale")) {
                        this.guiScale = Integer.parseInt(line[1]);
                    }

                    if (line[0].equals("bobView")) {
                        this.viewBobbing = line[1].equals("true");
                    }

                    if (line[0].equals("anaglyph3d")) {
                        this.anaglyph = line[1].equals("true");
                    }

                    if (line[0].equals("advancedOpengl")) {
                        this.advancedOpengl = line[1].equals("true");
                    }

                    if (line[0].equals("fpsLimit")) {
                        this.limitFramerate = Integer.parseInt(line[1]);
                        Display.setVSyncEnabled(this.limitFramerate == 3);
                    }

                    if (line[0].equals("difficulty")) {
                        this.difficulty = Integer.parseInt(line[1]);
                    }

                    if (line[0].equals("fancyGraphics")) {
                        this.fancyGraphics = line[1].equals("true");
                    }

                    if (line[0].equals("ao")) {
                        this.ambientOcclusion = line[1].equals("true");
                        if (this.ambientOcclusion) {
                            this.ofAoLevel = 1.0F;
                        } else {
                            this.ofAoLevel = 0.0F;
                        }
                    }

                    if (line[0].equals("skin")) {
                        this.skin = line[1];
                    }

                    if (line[0].equals("lastServer") && line.length >= 2) {
                        this.lastServer = line[1];
                    }

                    for (int i = 0; i < this.keyBindings.length; ++i) {
                        if (line[0].equals("key_" + this.keyBindings[i].keyDescription)) {
                            this.keyBindings[i].keyCode = Integer.parseInt(line[1]);
                        }
                    }

                    if (line[0].equals("ofFogFancy") && line.length >= 2) {
                        this.ofFogFancy = line[1].equals("true");
                    }

                    if (line[0].equals("ofFogStart") && line.length >= 2) {
                        this.ofFogStart = Float.parseFloat(line[1]);
                        if (this.ofFogStart < 0.2F) {
                            this.ofFogStart = 0.2F;
                        }

                        if (this.ofFogStart > 0.81F) {
                            this.ofFogStart = 0.8F;
                        }
                    }

                    if (line[0].equals("ofMipmapLevel") && line.length >= 2) {
                        this.ofMipmapLevel = Integer.parseInt(line[1]);
                        if (this.ofMipmapLevel < 0) {
                            this.ofMipmapLevel = 0;
                        }

                        if (this.ofMipmapLevel > 4) {
                            this.ofMipmapLevel = 4;
                        }
                    }

                    if (line[0].equals("ofMipmapLinear") && line.length >= 2) {
                        this.ofMipmapLinear = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofLoadFar") && line.length >= 2) {
                        this.ofLoadFar = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofPreloadedChunks") && line.length >= 2) {
                        this.ofPreloadedChunks = Integer.parseInt(line[1]);
                        if (this.ofPreloadedChunks < 0) {
                            this.ofPreloadedChunks = 0;
                        }

                        if (this.ofPreloadedChunks > 8) {
                            this.ofPreloadedChunks = 8;
                        }
                    }

                    if (line[0].equals("ofOcclusionFancy") && line.length >= 2) {
                        this.ofOcclusionFancy = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofSmoothFps") && line.length >= 2) {
                        this.ofSmoothFps = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofSmoothInput") && line.length >= 2) {
                        this.ofSmoothInput = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofBrightness") && line.length >= 2) {
                        this.ofBrightness = Float.parseFloat(line[1]);
                        this.ofBrightness = Config.limit(this.ofBrightness, 0.0F, 1.0F);
                        this.updateWorldLightLevels();
                    }

                    if (line[0].equals("ofAoLevel") && line.length >= 2) {
                        this.ofAoLevel = Float.parseFloat(line[1]);
                        this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
                        this.ambientOcclusion = this.ofAoLevel > 0.0F;
                    }

                    if (line[0].equals("ofClouds") && line.length >= 2) {
                        this.ofClouds = Integer.parseInt(line[1]);
                        this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                    }

                    if (line[0].equals("ofCloudsHeight") && line.length >= 2) {
                        this.ofCloudsHeight = Float.parseFloat(line[1]);
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                    }

                    if (line[0].equals("ofTrees") && line.length >= 2) {
                        this.ofTrees = Integer.parseInt(line[1]);
                        this.ofTrees = Config.limit(this.ofTrees, 0, 2);
                    }

                    if (line[0].equals("ofGrass") && line.length >= 2) {
                        this.ofGrass = Integer.parseInt(line[1]);
                        this.ofGrass = Config.limit(this.ofGrass, 0, 2);
                    }

                    if (line[0].equals("ofRain") && line.length >= 2) {
                        this.ofRain = Integer.parseInt(line[1]);
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (line[0].equals("ofWater") && line.length >= 2) {
                        this.ofWater = Integer.parseInt(line[1]);
                        this.ofWater = Config.limit(this.ofWater, 0, 3);
                    }

                    if (line[0].equals("ofAnimatedWater") && line.length >= 2) {
                        this.ofAnimatedWater = Integer.parseInt(line[1]);
                        this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
                    }

                    if (line[0].equals("ofAnimatedLava") && line.length >= 2) {
                        this.ofAnimatedLava = Integer.parseInt(line[1]);
                        this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
                    }

                    if (line[0].equals("ofAnimatedFire") && line.length >= 2) {
                        this.ofAnimatedFire = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofAnimatedPortal") && line.length >= 2) {
                        this.ofAnimatedPortal = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofAnimatedRedstone") && line.length >= 2) {
                        this.ofAnimatedRedstone = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofAnimatedExplosion") && line.length >= 2) {
                        this.ofAnimatedExplosion = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofAnimatedFlame") && line.length >= 2) {
                        this.ofAnimatedFlame = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofAnimatedSmoke") && line.length >= 2) {
                        this.ofAnimatedSmoke = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofFastDebugInfo") && line.length >= 2) {
                        this.ofFastDebugInfo = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofAutoSaveTicks") && line.length >= 2) {
                        this.ofAutoSaveTicks = Integer.parseInt(line[1]);
                        this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, '鱀');
                    }

                    if (line[0].equals("ofBetterGrass") && line.length >= 2) {
                        this.ofBetterGrass = Integer.parseInt(line[1]);
                        this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
                    }

                    if (line[0].equals("ofWeather") && line.length >= 2) {
                        this.ofWeather = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofSky") && line.length >= 2) {
                        this.ofSky = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofStars") && line.length >= 2) {
                        this.ofStars = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofChunkUpdates") && line.length >= 2) {
                        this.ofChunkUpdates = Integer.parseInt(line[1]);
                        this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
                    }

                    if (line[0].equals("ofChunkUpdatesDynamic") && line.length >= 2) {
                        this.ofChunkUpdatesDynamic = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofFarView") && line.length >= 2) {
                        this.ofFarView = Boolean.parseBoolean(line[1]);
                    }

                    if (line[0].equals("ofTime") && line.length >= 2) {
                        this.ofTime = Integer.parseInt(line[1]);
                        this.ofTime = Config.limit(this.ofTime, 0, 2);
                    }

                    if (line[0].equals("ofClearWater") && line.length >= 2) {
                        this.ofClearWater = Boolean.parseBoolean(line[1]);
                        this.updateWaterOpacity();
                    }
                } catch (Exception ex) {
                    System.out.println("Skipping bad option: " + s);
                }
            }

            exception.close();
        } catch (Exception exception1) {
            System.out.println("Failed to load options");
            exception1.printStackTrace();
        }

    }

    private float parseFloat(String s) {
        return s.equals("true") ? 1.0F : (s.equals("false") ? 0.0F : Float.parseFloat(s));
    }

    public void saveOptions() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(this.optionsFile))) {
                writer.println("music:" + this.musicVolume);
                writer.println("sound:" + this.soundVolume);
                writer.println("invertYMouse:" + this.invertMouse);
                writer.println("mouseSensitivity:" + this.mouseSensitivity);
                writer.println("viewDistance:" + this.renderDistance);
                writer.println("guiScale:" + this.guiScale);
                writer.println("bobView:" + this.viewBobbing);
                writer.println("anaglyph3d:" + this.anaglyph);
                writer.println("advancedOpengl:" + this.advancedOpengl);
                writer.println("fpsLimit:" + this.limitFramerate);
                writer.println("difficulty:" + this.difficulty);
                writer.println("fancyGraphics:" + this.fancyGraphics);
                writer.println("ao:" + this.ambientOcclusion);
                writer.println("skin:" + this.skin);
                writer.println("lastServer:" + this.lastServer);
                
                for (int i = 0; i < this.keyBindings.length; ++i) {
                    writer.println("key_" + this.keyBindings[i].keyDescription + ":" + this.keyBindings[i].keyCode);
                }
                
                writer.println("ofFogFancy:" + this.ofFogFancy);
                writer.println("ofFogStart:" + this.ofFogStart);
                writer.println("ofMipmapLevel:" + this.ofMipmapLevel);
                writer.println("ofMipmapLinear:" + this.ofMipmapLinear);
                writer.println("ofLoadFar:" + this.ofLoadFar);
                writer.println("ofPreloadedChunks:" + this.ofPreloadedChunks);
                writer.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
                writer.println("ofSmoothFps:" + this.ofSmoothFps);
                writer.println("ofSmoothInput:" + this.ofSmoothInput);
                writer.println("ofBrightness:" + this.ofBrightness);
                writer.println("ofAoLevel:" + this.ofAoLevel);
                writer.println("ofClouds:" + this.ofClouds);
                writer.println("ofCloudsHeight:" + this.ofCloudsHeight);
                writer.println("ofTrees:" + this.ofTrees);
                writer.println("ofGrass:" + this.ofGrass);
                writer.println("ofRain:" + this.ofRain);
                writer.println("ofWater:" + this.ofWater);
                writer.println("ofAnimatedWater:" + this.ofAnimatedWater);
                writer.println("ofAnimatedLava:" + this.ofAnimatedLava);
                writer.println("ofAnimatedFire:" + this.ofAnimatedFire);
                writer.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
                writer.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
                writer.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
                writer.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
                writer.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
                writer.println("ofFastDebugInfo:" + this.ofFastDebugInfo);
                writer.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
                writer.println("ofBetterGrass:" + this.ofBetterGrass);
                writer.println("ofWeather:" + this.ofWeather);
                writer.println("ofSky:" + this.ofSky);
                writer.println("ofStars:" + this.ofStars);
                writer.println("ofChunkUpdates:" + this.ofChunkUpdates);
                writer.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
                writer.println("ofFarView:" + this.ofFarView);
                writer.println("ofTime:" + this.ofTime);
                writer.println("ofClearWater:" + this.ofClearWater);
            } catch (Exception exception) {
            System.out.println("Failed to save options");
            exception.printStackTrace();
        }

    }
}
