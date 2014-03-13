package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

public class Packet0KeepAlive extends Packet {

   public void processPacket(NetHandler var1) {}

   public void readPacketData(DataInputStream var1) throws IOException {}

   public void writePacketData(DataOutputStream var1) throws IOException {}

   public int getPacketSize() {
      return 0;
   }
}
