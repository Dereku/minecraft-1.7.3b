package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import net.minecraft.src.MathHelper;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class GuiMainMenu extends GuiScreen {

    private static final Random rand = new Random();
    private float updateCounter = 0.0F;
    private String splashText = "missingno";
    private GuiButton multiplayerButton;

    public GuiMainMenu() {
        try {
            ArrayList var1 = new ArrayList();
            BufferedReader var2 = new BufferedReader(new InputStreamReader(GuiMainMenu.class.getResourceAsStream("/assets/title/splashes.txt"), Charset.forName("UTF-8")));
            String var3 = "";

            while ((var3 = var2.readLine()) != null) {
                var3 = var3.trim();
                if (var3.length() > 0) {
                    var1.add(var3);
                }
            }

            this.splashText = (String) var1.get(rand.nextInt(var1.size()));
        } catch (IOException var4) {
        }

    }

    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }

    @Override
    protected void keyTyped(char var1, int var2) {
    }

    @Override
    public void initGui() {
        Calendar var1 = Calendar.getInstance();
        var1.setTime(new Date());
        if (var1.get(2) + 1 == 11 && var1.get(5) == 9) {
            this.splashText = "Happy birthday, ez!";
        } else if (var1.get(2) + 1 == 6 && var1.get(5) == 1) {
            this.splashText = "Happy birthday, Notch!";
        } else if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
            this.splashText = "Happy new year!";
        }

        StringTranslate var2 = StringTranslate.getInstance();
        int var4 = this.height / 4 + 48;
        this.controlList.add(new GuiButton(1, this.width / 2 - 100, var4, var2.translateKey("menu.singleplayer")));
        this.controlList.add(this.multiplayerButton = new GuiButton(2, this.width / 2 - 100, var4 + 24, var2.translateKey("menu.multiplayer")));
        this.controlList.add(new GuiButton(3, this.width / 2 - 100, var4 + 48, var2.translateKey("menu.mods")));
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, var4 + 72 + 12, 98, 20, var2.translateKey("menu.options")));
        this.controlList.add(new GuiButton(4, this.width / 2 + 2, var4 + 72 + 12, 98, 20, var2.translateKey("menu.quit")));
        this.multiplayerButton.enabled = this.mc.session != null;
    }

    @Override
    protected void actionPerformed(GuiButton var1) {
        switch (var1.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            case 1:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiTexturePacks(this));
                break;
            case 4:
                this.mc.shutdown();
                break;
            default:
                break;
        }
    }

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        Tessellator var4 = Tessellator.instance;
        short var5 = 274;
        int var6 = this.width / 2 - var5 / 2;
        byte var7 = 30;
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, this.mc.renderEngine.getTexture("/assets/title/mclogo.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 155, 44);
        this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);
        var4.setColorOpaque_I(16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (this.width / 2 + 90), 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float var8 = 1.8F - MathHelper.abs(MathHelper.sin((float) (System.currentTimeMillis() % 1000L) / 1000.0F * 3.1415927F * 2.0F) * 0.1F);
        var8 = var8 * 100.0F / (float) (this.fontRenderer.getStringWidth(this.splashText) + 32);
        GL11.glScalef(var8, var8, var8);
        this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, 16776960);
        GL11.glPopMatrix();
        this.drawString(this.fontRenderer, "Minecraft Beta 1.7.3", 2, 2, 5263440);
        String var9 = "Copyright Mojang AB. Do not distribute.";
        this.drawString(this.fontRenderer, var9, this.width - this.fontRenderer.getStringWidth(var9) - 2, this.height - 10, 16777215);
        super.drawScreen(var1, var2, var3);
    }

}
