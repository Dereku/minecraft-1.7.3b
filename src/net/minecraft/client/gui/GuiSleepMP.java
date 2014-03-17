package net.minecraft.client.gui;

import net.minecraft.entity.EntityClientPlayerMP;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.StringTranslate;
import org.lwjgl.input.Keyboard;

public class GuiSleepMP extends GuiChat {

    public GuiSleepMP(String string) {
        super(string);
    }
    

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      StringTranslate var1 = StringTranslate.getInstance();
      this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, var1.translateKey("multiplayer.stopSleeping")));
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   protected void keyTyped(char var1, int var2) {
      if(var2 == 1) {
         this.func_22115_j();
      } else if(var2 == 28) {
         String var3 = this.message.trim();
         if(var3.length() > 0) {
            this.mc.thePlayer.sendChatMessage(this.message.trim());
         }

         this.message = "";
      } else {
         super.keyTyped(var1, var2);
      }

   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      super.drawScreen(var1, var2, var3);
   }

   @Override
   protected void actionPerformed(GuiButton var1) {
      if(var1.id == 1) {
         this.func_22115_j();
      } else {
         super.actionPerformed(var1);
      }

   }

   private void func_22115_j() {
      if(this.mc.thePlayer instanceof EntityClientPlayerMP) {
         NetClientHandler var1 = ((EntityClientPlayerMP)this.mc.thePlayer).sendQueue;
         var1.addToSendQueue(new Packet19EntityAction(this.mc.thePlayer, 3));
      }

   }
}
