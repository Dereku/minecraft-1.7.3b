package net.minecraft.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetHandler;
import net.minecraft.network.NetHandler;
import net.minecraft.network.packets.Packet;

public class Packet105UpdateProgressbar extends Packet {

   public int windowId;
   public int progressBar;
   public int progressBarValue;


   public void processPacket(NetHandler var1) {
      var1.handleCraftingProgress(this);
   }

   public void readPacketData(DataInputStream var1) throws IOException {
      this.windowId = var1.readByte();
      this.progressBar = var1.readShort();
      this.progressBarValue = var1.readShort();
   }

   public void writePacketData(DataOutputStream var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.progressBar);
      var1.writeShort(this.progressBarValue);
   }

   public int getPacketSize() {
      return 5;
   }
}
