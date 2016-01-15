package net.minecraft.src;

import net.minecraft.stats.StatCollector;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final Properties prop = new Properties(); //Using properties file for configs.
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
    public String texturepack = "Default";
    public KeyBinding ofKeyBindZoom = new KeyBinding("Zoom", 46);
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
    private final File optionsFile;
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
        this.keyBindings = new KeyBinding[]{
            this.keyBindForward, this.keyBindLeft, this.keyBindBack, 
            this.keyBindRight, this.keyBindJump, this.keyBindSneak, 
            this.keyBindDrop, this.keyBindInventory, this.keyBindChat, 
            this.keyBindToggleFog, this.ofKeyBindZoom
        };
        
        this.optionsFile = new File(file, "options.properties");
        
        try {
            prop.load(new FileReader(this.optionsFile));
        } catch (IOException ignore) { 
            //Really ignore?
        }
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
        switch (enumoptions) {
            case INVERT_MOUSE:
                this.invertMouse = !this.invertMouse;
                this.setProperty("invertMouse", this.invertMouse);
                break;
            case RENDER_DISTANCE:
                this.renderDistance = this.renderDistance + i & 3;
                this.setProperty("renderDistance", this.renderDistance);
                break;
            case GUI_SCALE:
                this.guiScale = this.guiScale + i & 3;
                this.setProperty("guiScale", this.guiScale);
                break;
            case VIEW_BOBBING:
                this.viewBobbing = !this.viewBobbing;
                this.setProperty("viewBobbing", this.viewBobbing);
                break;
            case ADVANCED_OPENGL:
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
                
                this.setProperty("advancedOpengl", this.advancedOpengl);
                this.setProperty("ofOcclusionFancy", this.ofOcclusionFancy);
                this.mc.renderGlobal.setAllRenderesVisible();
                break;
            case ANAGLYPH:
                this.anaglyph = !this.anaglyph;
                this.setProperty("anaglyph", this.anaglyph);
                this.mc.renderEngine.refreshTextures();
                break;
            case FRAMERATE_LIMIT:
                this.limitFramerate = (this.limitFramerate + i) % 4;
                this.setProperty("limitFramerate", this.limitFramerate);
                Display.setVSyncEnabled(this.limitFramerate == 3);
                break;
            case DIFFICULTY:
                this.difficulty = this.difficulty + i & 3;
                this.setProperty("difficulty", this.difficulty);
                break;
            case GRAPHICS:
                this.fancyGraphics = !this.fancyGraphics;
                this.setProperty("fancyGraphics", this.fancyGraphics);
                this.mc.renderGlobal.loadRenderers();
                break;
            case AMBIENT_OCCLUSION:
                this.ambientOcclusion = !this.ambientOcclusion;
                this.setProperty("ambientOcclusion", this.ambientOcclusion);
                this.mc.renderGlobal.loadRenderers();
                break;
            case FOG_FANCY:
                if (!Config.isFancyFogAvailable()) {
                    this.ofFogFancy = false;
                } else {
                    this.ofFogFancy = !this.ofFogFancy;
                }
                this.setProperty("ofFogFancy", this.ofFogFancy);
                break;
            case FOG_START:
                this.ofFogStart += 0.2F;
                if (this.ofFogStart > 0.81F) {
                    this.ofFogStart = 0.2F;
                }
                this.setProperty("ofFogStart", this.ofFogStart);
                break;
            case MIPMAP_LEVEL:
                ++this.ofMipmapLevel;
                if (this.ofMipmapLevel > 4) {
                    this.ofMipmapLevel = 0;
                }
                this.setProperty("ofMipmapLevel", this.ofMipmapLevel);
                this.mc.renderEngine.refreshTextures();
                break;
            case MIPMAP_TYPE:
                this.ofMipmapLinear = !this.ofMipmapLinear;
                this.setProperty("ofMipmapLinear", this.ofMipmapLinear);
                this.mc.renderEngine.refreshTextures();
                break;
            case LOAD_FAR:
                this.ofLoadFar = !this.ofLoadFar;
                this.setProperty("ofLoadFar", this.ofLoadFar);
                this.mc.renderGlobal.loadRenderers();
                break;
            case PRELOADED_CHUNKS:
                this.ofPreloadedChunks += 2;
                if (this.ofPreloadedChunks > 8) {
                    this.ofPreloadedChunks = 0;
                }
                this.setProperty("ofPreloadedChunks", this.ofPreloadedChunks);
                this.mc.renderGlobal.loadRenderers();
                break;
            case SMOOTH_FPS:
                this.ofSmoothFps = !this.ofSmoothFps;
                this.setProperty("ofSmoothFps", this.ofSmoothFps);
                break;
            case SMOOTH_INPUT:
                this.ofSmoothInput = !this.ofSmoothInput;
                this.setProperty("ofSmoothInput", this.ofSmoothInput);
                break;
            case CLOUDS:
                ++this.ofClouds;
                if (this.ofClouds > 3) {
                    this.ofClouds = 0;
                }
                this.setProperty("ofClouds", this.ofClouds);
                break;
            case TREES:
                ++this.ofTrees;
                if (this.ofTrees > 2) {
                    this.ofTrees = 0;
                }
                this.setProperty("ofTrees", this.ofTrees);
                this.mc.renderGlobal.loadRenderers();
                break;
            case GRASS:
                ++this.ofGrass;
                if (this.ofGrass > 2) {
                    this.ofGrass = 0;
                }
                this.setProperty("ofGrass", this.ofGrass);
                RenderBlocks.fancyGrass = Config.isGrassFancy();
                this.mc.renderGlobal.loadRenderers();
                break;
            case RAIN:
                ++this.ofRain;
                if (this.ofRain > 3) {
                    this.ofRain = 0;
                }
                this.setProperty("ofRain", this.ofRain);
                break;
            case WATER:
                ++this.ofWater;
                if (this.ofWater > 2) {
                    this.ofWater = 0;
                }
                this.setProperty("ofWater", this.ofWater);
                break;
            case ANIMATED_WATER:
                ++this.ofAnimatedWater;
                if (this.ofAnimatedWater > 2) {
                    this.ofAnimatedWater = 0;
                }
                this.setProperty("ofAnimatedWater", this.ofAnimatedWater);
                this.mc.renderEngine.refreshTextures();
                break;
            case ANIMATED_LAVA:
                ++this.ofAnimatedLava;
                if (this.ofAnimatedLava > 2) {
                    this.ofAnimatedLava = 0;
                }
                this.setProperty("ofAnimatedLava", this.ofAnimatedLava);
                this.mc.renderEngine.refreshTextures();
                break;
            case ANIMATED_FIRE:
                this.ofAnimatedFire = !this.ofAnimatedFire;
                this.setProperty("ofAnimatedFire", this.ofAnimatedFire);
                this.mc.renderEngine.refreshTextures();
                break;
            case ANIMATED_PORTAL:
                this.ofAnimatedPortal = !this.ofAnimatedPortal;
                this.setProperty("ofAnimatedPortal", this.ofAnimatedPortal);
                this.mc.renderEngine.refreshTextures();
                break;
            case ANIMATED_REDSTONE:
                this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
                this.setProperty("ofAnimatedRedstone", this.ofAnimatedRedstone);
                break;
            case ANIMATED_EXPLOSION:
                this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
                this.setProperty("ofAnimatedExplosion", this.ofAnimatedExplosion);
                break;
            case ANIMATED_FLAME:
                this.ofAnimatedFlame = !this.ofAnimatedFlame;
                this.setProperty("ofAnimatedFlame", this.ofAnimatedFlame);
                break;
            case ANIMATED_SMOKE:
                this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
                this.setProperty("ofAnimatedSmoke", this.ofAnimatedSmoke);
                break;
            case FAST_DEBUG_INFO:
                this.ofFastDebugInfo = !this.ofFastDebugInfo;
                this.setProperty("ofFastDebugInfo", this.ofFastDebugInfo);
                break;
            case AUTOSAVE_TICKS:
                this.ofAutoSaveTicks *= 10;
                if (this.ofAutoSaveTicks > 40000) {
                    this.ofAutoSaveTicks = 40;
                }
                this.setProperty("ofAutoSaveTicks", this.ofAutoSaveTicks);
                break;
            case BETTER_GRASS:
                ++this.ofBetterGrass;
                if (this.ofBetterGrass > 3) {
                    this.ofBetterGrass = 1;
                }
                this.setProperty("ofBetterGrass", this.ofBetterGrass);
                this.mc.renderGlobal.loadRenderers();
                break;
            case WEATHER:
                this.ofWeather = !this.ofWeather;
                this.setProperty("ofWeather", this.ofWeather);
                break;
            case SKY:
                this.ofSky = !this.ofSky;
                this.setProperty("ofSky", this.ofSky);
                break;
            case STARS:
                this.ofStars = !this.ofStars;
                this.setProperty("ofStars", this.ofStars);
                break;
            case CHUNK_UPDATES:
                ++this.ofChunkUpdates;
                if (this.ofChunkUpdates > 5) {
                    this.ofChunkUpdates = 1;
                }
                this.setProperty("ofChunkUpdates", this.ofChunkUpdates);
                break;
            case CHUNK_UPDATES_DYNAMIC:
                this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
                this.setProperty("ofChunkUpdatesDynamic", this.ofChunkUpdatesDynamic);
                break;
            case FAR_VIEW:
                this.ofFarView = !this.ofFarView;
                this.setProperty("ofFarView", this.ofFarView);
                this.mc.renderGlobal.loadRenderers();
                break;
            case TIME:
                ++this.ofTime;
                if (this.ofTime > 2) {
                    this.ofTime = 0;
                }
                this.setProperty("ofTime", this.ofTime);
                break;
            case CLEAR_WATER:
                this.ofClearWater = !this.ofClearWater;
                this.setProperty("ofClearWater", this.ofClearWater);
                this.updateWaterOpacity();
                break;
            default:
                System.out.println("Ignoring unknown value: " + enumoptions + ", id: " + i);
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
            this.musicVolume = this.parseFloat(this.getProperty("music", "1.0"));
            this.soundVolume = this.parseFloat(this.getProperty("sound", "1.0"));
            this.mouseSensitivity = this.parseFloat(this.getProperty("mouseSensitivity", "0.5"));
            this.invertMouse = this.getProperty("invertMouse", "false").equals("true");
            this.renderDistance = Integer.parseInt(this.getProperty("renderDistance", "1"));
            this.guiScale = Integer.parseInt(this.getProperty("guiScale", "0"));
            this.viewBobbing = this.getProperty("viewBobbing", "true").equals("true");
            this.anaglyph = this.getProperty("anaglyph", "false").equals("true");
            this.advancedOpengl = this.getProperty("advancedOpengl", "false").equals("true");
            this.limitFramerate = Integer.parseInt(this.getProperty("limitFramerate", "3"));
                Display.setVSyncEnabled(this.limitFramerate == 3);
            this.difficulty = Integer.parseInt(this.getProperty("difficulty", "1"));
            this.fancyGraphics = this.getProperty("fancyGraphics", "true").equals("true");
            this.ambientOcclusion = this.getProperty("ambientOcclusion", "true").equals("true");
                this.ofAoLevel = this.ambientOcclusion ? 1.0F : 0.0F;
            this.texturepack = this.getProperty("texturepack", "Default");
            this.lastServer = this.getProperty("lastServer", "");
            this.ofFogFancy = this.getProperty("ofFogFancy", "true").equals("true");
            this.ofFogStart = Config.limit(Float.parseFloat(this.getProperty("ofFogStart", "0.4")), 0.2F, 0.8F);
            this.ofMipmapLevel = Config.limit(Integer.parseInt(this.getProperty("ofMipmapLevel", "0")), 0, 4);
            this.ofMipmapLinear = this.getProperty("ofMipmapLinear", "false").equals("true");
            this.ofLoadFar = this.getProperty("ofLoadFar", "false").equals("true");
            this.ofPreloadedChunks = Config.limit(Integer.parseInt(this.getProperty("ofPreloadedChunks", "0")), 0, 8);
            this.ofOcclusionFancy = this.getProperty("ofOcclusionFancy", "false").equals("true");
            this.ofSmoothFps = this.getProperty("ofSmoothFps", "false").equals("true");
            this.ofSmoothInput = this.getProperty("ofSmoothInput", "false").equals("true");
            this.ofAnimatedFire = this.getProperty("ofAnimatedFire", "true").equals("true");
            this.ofAnimatedFlame = this.getProperty("ofAnimatedFlame", "true").equals("true");
            this.ofAnimatedPortal = this.getProperty("ofAnimatedPortal", "true").equals("true");
            this.ofAnimatedRedstone = this.getProperty("ofAnimatedRedstone", "true").equals("true");
            this.ofAnimatedExplosion = this.getProperty("ofAnimatedExplosion", "true").equals("true");
            this.ofAnimatedSmoke = this.getProperty("ofAnimatedSmoke", "true").equals("true");
            this.ofAnimatedWater = Config.limit(Integer.parseInt(this.getProperty("ofAnimatedWater", "0")), 0, 2);
            this.ofAnimatedLava = Config.limit(Integer.parseInt(this.getProperty("ofAnimatedLava", "0")), 0, 2);
            this.ofFastDebugInfo = this.getProperty("ofFastDebugInfo", "true").equals("true");
            this.ofBrightness = Config.limit(Float.parseFloat(this.getProperty("ofBrightness", "0.0")), 0.0F, 1.0F);
                this.updateWorldLightLevels();
            this.ofAoLevel = Config.limit(Float.parseFloat(this.getProperty("ofAoLevel", "1.0")), 0.0F, 1.0F);
                this.ambientOcclusion = this.ofAoLevel > 0.0F;
            this.ofClouds = Config.limit(Integer.parseInt(this.getProperty("ofClouds", "0")), 0, 3);
            this.ofCloudsHeight = Config.limit(Float.parseFloat(this.getProperty("ofCloudsHeight", "0.0")), 0.0F, 1.0F);
            this.ofTrees = Config.limit(Integer.parseInt(this.getProperty("ofTrees", "0")), 0, 2);
            this.ofGrass = Config.limit(Integer.parseInt(this.getProperty("ofGrass", "0")), 0, 2);
            this.ofRain = Config.limit(Integer.parseInt(this.getProperty("ofRain", "0")), 0, 3);
            this.ofWater = Config.limit(Integer.parseInt(this.getProperty("ofWater", "0")), 0, 3);
            this.ofAutoSaveTicks = Config.limit(Integer.parseInt(this.getProperty("ofAutoSaveTicks", "200")), 40, 40000);
            this.ofBetterGrass = Config.limit(Integer.parseInt(this.getProperty("ofBetterGrass", "3")), 1, 3);
            this.ofWeather = this.getProperty("ofWeather", "true").equals("true");
            this.ofSky = this.getProperty("ofSky", "true").equals("true");
            this.ofStars = this.getProperty("ofStars", "true").equals("true");
            this.ofChunkUpdates = Config.limit(Integer.parseInt(this.getProperty("ofChunkUpdates", "1")), 1, 5);
            this.ofChunkUpdatesDynamic = this.getProperty("ofChunkUpdatesDynamic", "false").equals("true");
            this.ofFarView = this.getProperty("ofFarView", "false").equals("true");
            this.ofClearWater = this.getProperty("ofClearWater", "true").equals("true");
                this.updateWaterOpacity();
            this.ofTime = Config.limit(Integer.parseInt(this.getProperty("ofTime", "0")), 0, 2);
            
            for (int i = 0; i < this.keyBindings.length; ++i) {
                this.keyBindings[i].keyCode = Integer.parseInt(
                        this.getProperty(
                                "key_" + this.keyBindings[i].keyDescription, 
                                String.valueOf(this.keyBindings[i].keyCode)
                        )
                );
            }
        } catch (Exception exception1) {
            System.err.println("Failed to load options");
            exception1.printStackTrace();
        }

    }

    private float parseFloat(String s) {
        return s.equals("true") ? 1.0F : (s.equals("false") ? 0.0F : Float.parseFloat(s));
    }

    public void saveOptions() {
        try {
            this.prop.store(new FileWriter(this.optionsFile), null);
        } catch (IOException ex) {
            Logger.getLogger(GameSettings.class.getName()).log(Level.SEVERE, "Failed to save options file.", ex);
        }
    }
    
    private String getProperty(String key, String defaultValue) {
        if (this.prop.getProperty(key) == null || this.prop.getProperty(key).equals("")) {
            this.prop.put(key, defaultValue);
            return defaultValue;
        } else {
            return this.prop.getProperty(key);
        }
    }

    private void setProperty(String key, Object value) {
        this.prop.put(key, String.valueOf(value));
    }
}
