package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetClientHandler;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.ThreadConnectToServer;
import net.minecraft.world.World;

public class GuiConnecting extends GuiScreen {

    private NetClientHandler clientHandler;
    private boolean cancelled = false;

    public GuiConnecting(Minecraft var1, String var2, int var3) {
        System.out.println("Connecting to " + var2 + ", " + var3);
        var1.changeWorld1((World) null);
        (new ThreadConnectToServer(this, var1, var2, var3)).start();
    }

    @Override
    public void updateScreen() {
        if (this.clientHandler != null) {
            this.clientHandler.processReadPackets();
        }

    }

    @Override
    protected void keyTyped(char var1, int var2) {
    }

    @Override
    public void initGui() {
        StringTranslate var1 = StringTranslate.getInstance();
        this.controlList.clear();
        this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.cancel")));
    }

    @Override
    protected void actionPerformed(GuiButton var1) {
        if (var1.id == 0) {
            this.cancelled = true;
            if (this.clientHandler != null) {
                this.clientHandler.disconnect();
            }

            this.mc.displayGuiScreen(new GuiMainMenu());
        }

    }

    @Override
    public void drawScreen(int var1, int var2, float var3) {
        this.drawDefaultBackground();
        StringTranslate var4 = StringTranslate.getInstance();
        if (this.clientHandler == null) {
            this.drawCenteredString(this.fontRenderer, var4.translateKey("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
            this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 16777215);
        } else {
            this.drawCenteredString(this.fontRenderer, var4.translateKey("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
            this.drawCenteredString(this.fontRenderer, this.clientHandler.field_1209_a, this.width / 2, this.height / 2 - 10, 16777215);
        }

        super.drawScreen(var1, var2, var3);
    }

    // $FF: synthetic method
    public static NetClientHandler setNetClientHandler(GuiConnecting var0, NetClientHandler var1) {
        return var0.clientHandler = var1;
    }

    // $FF: synthetic method
    public static boolean isCancelled(GuiConnecting var0) {
        return var0.cancelled;
    }

    // $FF: synthetic method
    public static NetClientHandler getNetClientHandler(GuiConnecting var0) {
        return var0.clientHandler;
    }
}
