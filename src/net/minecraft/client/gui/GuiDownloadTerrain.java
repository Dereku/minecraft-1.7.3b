package net.minecraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.NetClientHandler;
import net.minecraft.network.NetClientHandler;
import net.minecraft.network.packets.Packet0KeepAlive;
import net.minecraft.network.packets.Packet0KeepAlive;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.StringTranslate;

public class GuiDownloadTerrain extends GuiScreen {

   private NetClientHandler netHandler;
   private int updateCounter = 0;


   public GuiDownloadTerrain(NetClientHandler var1) {
      this.netHandler = var1;
   }

   protected void keyTyped(char var1, int var2) {}

   public void initGui() {
      this.controlList.clear();
   }

   public void updateScreen() {
      ++this.updateCounter;
      if(this.updateCounter % 20 == 0) {
         this.netHandler.addToSendQueue(new Packet0KeepAlive());
      }

      if(this.netHandler != null) {
         this.netHandler.processReadPackets();
      }

   }

   protected void actionPerformed(GuiButton var1) {}

   public void drawScreen(int var1, int var2, float var3) {
      this.drawBackground(0);
      StringTranslate var4 = StringTranslate.getInstance();
      this.drawCenteredString(this.fontRenderer, var4.translateKey("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.drawScreen(var1, var2, var3);
   }
}
