package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.client.gui.fonts.StringCache;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class FontRenderer {

    public static boolean betterFontsEnabled = true;
    public StringCache stringCache;
    public boolean dropShadowEnabled = true;

    /**
     * Array of width of all the characters in default.png
     */
    public int fontTextureName = 0;
    public int FONT_HEIGHT = 18;
    public Random fontRandom = new Random();
    private int[] colorCode = new int[32];
    private float posX;
    private float red;
    private float blue;
    private float green;
    private float alpha;
    private int textColor;
 
    public FontRenderer(GameSettings par1GameSettings) {
        int var9, var10, var11, var12;
        int var13, var15, var16;

        for (var9 = 0; var9 < 32; ++var9) {
            var10 = (var9 >> 3 & 1) * 85;
            var11 = (var9 >> 2 & 1) * 170 + var10;
            var12 = (var9 >> 1 & 1) * 170 + var10;
            var13 = (var9 & 1) * 170 + var10;

            if (var9 == 6) {
                var11 += 85;
            }

            if (par1GameSettings.anaglyph) {
                int var20 = (var11 * 30 + var12 * 59 + var13 * 11) / 100;
                var15 = (var11 * 30 + var12 * 70) / 100;
                var16 = (var11 * 30 + var13 * 70) / 100;
                var11 = var20;
                var12 = var15;
                var13 = var16;
            }

            if (var9 >= 16) {
                var11 /= 4;
                var12 /= 4;
                var13 /= 4;
            }

            this.colorCode[var9] = (var11 & 255) << 16 | (var12 & 255) << 8 | var13 & 255;
        }
        if (this.stringCache == null) {
            this.stringCache = new StringCache(this.colorCode);

            /* Read optional config file to override the default font name/size */
            Properties properties = new Properties();
            File cfg = new File(Minecraft.getMinecraftDir().getAbsolutePath() + File.separator + "BetterFonts.properties");
            try {
                properties.load(new FileInputStream(cfg));
            } catch (Exception ex) {
                properties.setProperty("fontName", "SansSerif");
                properties.setProperty("fontSize", "18");
                properties.setProperty("antialias", "false");
                properties.setProperty("dropshadow", "true");
                try {
                    properties.store(new FileOutputStream(cfg), null);
                } catch (IOException ex1) {
                    //Ignore
                }
            }
            
            try {
                dropShadowEnabled = Boolean.parseBoolean(properties.getProperty("dropshadow", "true"));
                this.stringCache.setDefaultFont(
                        properties.getProperty("fontName", "SansSerif"), 
                        Integer.parseInt(properties.getProperty("fontSize", "18")), 
                        Boolean.parseBoolean(properties.getProperty("antialias", "false")));
            } catch (Exception ex) {
                dropShadowEnabled = true;
                this.stringCache.setDefaultFont("SansSerif", 18, false);
            }
        }
    }

    /**
     * Draws the specified string with a shadow.
     */
    public int drawStringWithShadow(String par1Str, int par2, int par3, int par4) {
        return this.func_85187_a(par1Str, par2, par3, par4, true);
    }

    /**
     * Draws the specified string.
     */
    public int drawString(String par1Str, int par2, int par3, int par4) {
        return this.func_85187_a(par1Str, par2, par3, par4, false);
    }

    public int func_85187_a(String par1Str, int par2, int par3, int par4, boolean par5) {
        int var6;

        if (par5 && this.dropShadowEnabled) {
            var6 = this.renderString(par1Str, par2 + 1, par3 + 1, par4, true);
            var6 = Math.max(var6, this.renderString(par1Str, par2, par3, par4, false));
        } else {
            var6 = this.renderString(par1Str, par2, par3, par4, false);
        }

        return var6;
    }

    /**
     * Render string either left or right aligned depending on bidiFlag
     */
    private int renderStringAligned(String par1Str, int par2, int par3, int par4, int par5, boolean par6) {
        int var7 = this.getStringWidth(par1Str);
        par2 = par2 + par4 - var7;

        return this.renderString(par1Str, par2, par3, par5, par6);
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and
     * calling renderStringAtPos()
     */
    private int renderString(String par1Str, int par2, int par3, int par4, boolean par5) {
        if (par1Str == null) {
            return 0;
        } else {

            if ((par4 & -67108864) == 0) {
                par4 |= -16777216;
            }

            if (par5) {
                par4 = (par4 & 16579836) >> 2 | par4 & -16777216;
            }

            this.red = (float) (par4 >> 16 & 255) / 255.0F;
            this.blue = (float) (par4 >> 8 & 255) / 255.0F;
            this.green = (float) (par4 & 255) / 255.0F;
            this.alpha = (float) (par4 >> 24 & 255) / 255.0F;
            GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
            this.posX = (float) par2;
            this.posX += stringCache.renderString(par1Str, par2, par3, par4, par5);
            return (int) this.posX;
        }
    }

    /**
     * Returns the width of this string. Equivalent of
     * FontMetrics.stringWidth(String s).
     */
    public int getStringWidth(String par1Str) {
        return this.stringCache.getStringWidth(par1Str);
    }

    /**
     * Remove all newline characters from the end of the string
     */
    private String trimStringNewline(String par1Str) {
        while (par1Str != null && par1Str.endsWith("\n")) {
            par1Str = par1Str.substring(0, par1Str.length() - 1);
        }

        return par1Str;
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k)
     */
    public void drawSplitString(String par1Str, int par2, int par3, int par4, int par5) {
        this.textColor = par5;
        par1Str = this.trimStringNewline(par1Str);
        this.renderSplitString(par1Str, par2, par3, par4, false);
    }

    /**
     * Perform actual work of rendering a multi-line string with wordwrap and
     * with darker drop shadow color if flag is set
     */
    private void renderSplitString(String par1Str, int par2, int par3, int par4, boolean par5) {
        List var6 = Arrays.asList(this.wrapFormattedStringToWidth(par1Str, par2).split("\n"));

        for (Iterator var7 = var6.iterator(); var7.hasNext(); par3 += this.FONT_HEIGHT) {
            String var8 = (String) var7.next();
            this.renderStringAligned(var8, par2, par3, par4, this.textColor, par5);
        }
    }

    /**
     * Returns the width of the wordwrapped String (maximum length is parameter
     * k)
     */
    public int splitStringWidth(String par1Str, int par2) {
        return this.FONT_HEIGHT * Arrays.asList(this.wrapFormattedStringToWidth(par1Str, par2).split("\n")).size();
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the
     * specified width.
     */
    private String wrapFormattedStringToWidth(String par1Str, int par2) {
        int var3 = this.stringCache.sizeStringToWidth(par1Str, par2);

        if (par1Str.length() <= var3) {
            return par1Str;
        } else {
            String var4 = par1Str.substring(0, var3);
            char var5 = par1Str.charAt(var3);
            boolean var6 = var5 == 32 || var5 == 10;
            String var7 = getFormatFromString(var4) + par1Str.substring(var3 + (var6 ? 1 : 0));
            return var4 + "\n" + this.wrapFormattedStringToWidth(var7, par2);
        }
    }

    /**
     * Digests a string for nonprinting formatting characters then returns a
     * string containing only that formatting.
     */
    private static String getFormatFromString(String par0Str) {
        String var1 = "";
        int var2 = -1;
        int var3 = par0Str.length();

        while ((var2 = par0Str.indexOf(167, var2 + 1)) != -1) {
            if (var2 < var3 - 1) {
                char var4 = par0Str.charAt(var2 + 1);

                if (var4 >= 48 && var4 <= 57 || var4 >= 97 && var4 <= 102 || var4 >= 65 && var4 <= 70) {
                    var1 = "\u00a7" + var4;
                } else if (var4 >= 107 && var4 <= 111 || var4 >= 75 && var4 <= 79 || var4 == 114 || var4 == 82) {
                    var1 = var1 + "\u00a7" + var4;
                }
            }
        }

        return var1;
    }
}
