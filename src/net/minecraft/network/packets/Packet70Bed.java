package net.minecraft.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetHandler;
import net.minecraft.network.NetHandler;
import net.minecraft.network.packets.Packet;

public class Packet70Bed extends Packet {

   public static final String[] bedChat = new String[]{"tile.bed.notValid", null, null};
   public int bedState;


   public void readPacketData(DataInputStream var1) throws IOException {
      this.bedState = var1.readByte();
   }

   public void writePacketData(DataOutputStream var1) throws IOException {
      var1.writeByte(this.bedState);
   }

   public void processPacket(NetHandler var1) {
      var1.handleBedUpdate(this);
   }

   public int getPacketSize() {
      return 1;
   }

}
