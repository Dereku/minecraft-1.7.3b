package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

public class Packet106Transaction extends Packet {

   public int windowId;
   public short shortWindowId;
   public boolean field_20030_c;


   public Packet106Transaction() {}

   public Packet106Transaction(int var1, short var2, boolean var3) {
      this.windowId = var1;
      this.shortWindowId = var2;
      this.field_20030_c = var3;
   }

   public void processPacket(NetHandler var1) {
      var1.handleContainerTransaction(this);
   }

   public void readPacketData(DataInputStream var1) throws IOException {
      this.windowId = var1.readByte();
      this.shortWindowId = var1.readShort();
      this.field_20030_c = var1.readByte() != 0;
   }

   public void writePacketData(DataOutputStream var1) throws IOException {
      var1.writeByte(this.windowId);
      var1.writeShort(this.shortWindowId);
      var1.writeByte(this.field_20030_c?1:0);
   }

   public int getPacketSize() {
      return 4;
   }
}
