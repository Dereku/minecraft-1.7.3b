package net.minecraft.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.network.NetHandler;
import net.minecraft.network.NetHandler;
import net.minecraft.network.packets.Packet;

public class Packet2Handshake extends Packet {

   public String username;


   public Packet2Handshake() {}

   public Packet2Handshake(String var1) {
      this.username = var1;
   }

   public void readPacketData(DataInputStream var1) throws IOException {
      this.username = readString(var1, 32);
   }

   public void writePacketData(DataOutputStream var1) throws IOException {
      writeString(this.username, var1);
   }

   public void processPacket(NetHandler var1) {
      var1.handleHandshake(this);
   }

   public int getPacketSize() {
      return 4 + this.username.length() + 4;
   }
}
