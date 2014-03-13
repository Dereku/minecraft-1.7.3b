package net.minecraft.client.gui;

import net.minecraft.src.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

public class GuiChat extends GuiScreen {

    protected String message = "";
    private int updateCounter = 0;
    private static final String allowedCharacters = ChatAllowedCharacters.allowedCharacters;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        ++this.updateCounter;
    }

    @Override
    protected void keyTyped(char var1, int var2) {
        if (var2 == 1) {
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (var2 == 28) {
            String var3 = this.message.trim();
            if (!var3.isEmpty()) {
                String var4 = this.message.trim();
                this.mc.thePlayer.sendChatMessage(var4);
            }

            this.mc.displayGuiScreen((GuiScreen) null);
        } else {
            if (var2 == 14 && this.message.length() > 0) {
                this.message = this.message.substring(0, this.message.length() - 1);
            }

            if (allowedCharacters.indexOf(var1) >= 0 && this.message.length() < 100) {
                this.message = this.message + var1;
            }

        }
    }

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.drawString(this.fontRenderer, "" + this.message + (this.updateCounter / 6 % 2 == 0 ? "|" : ""), 4, this.height - 12, 14737632);
        super.drawScreen(var1, var2, var3);
    }

    @Override
    protected void mouseClicked(int var1, int var2, int var3) {
        if (var3 == 0) {
            if (this.mc.ingameGUI.field_933_a != null) {
                if (this.message.length() > 0 && !this.message.endsWith(" ")) {
                    this.message = this.message + " ";
                }

                this.message = this.message + this.mc.ingameGUI.field_933_a;
                byte var4 = 100;
                if (this.message.length() > var4) {
                    this.message = this.message.substring(0, var4);
                }
            } else {
                super.mouseClicked(var1, var2, var3);
            }
        }

    }

}
