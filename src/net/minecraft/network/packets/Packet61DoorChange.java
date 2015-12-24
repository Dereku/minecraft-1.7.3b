package net.minecraft.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetHandler;
import net.minecraft.network.NetHandler;
import net.minecraft.network.packets.Packet;

public class Packet61DoorChange extends Packet {

   public int sfxID;
   public int auxData;
   public int posX;
   public int posY;
   public int posZ;


   public void readPacketData(DataInputStream var1) throws IOException {
      this.sfxID = var1.readInt();
      this.posX = var1.readInt();
      this.posY = var1.readByte();
      this.posZ = var1.readInt();
      this.auxData = var1.readInt();
   }

   public void writePacketData(DataOutputStream var1) throws IOException {
      var1.writeInt(this.sfxID);
      var1.writeInt(this.posX);
      var1.writeByte(this.posY);
      var1.writeInt(this.posZ);
      var1.writeInt(this.auxData);
   }

   public void processPacket(NetHandler var1) {
      var1.handleAuxSFX(this);
   }

   public int getPacketSize() {
      return 20;
   }
}
