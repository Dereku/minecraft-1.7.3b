package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet;

public class Packet200Statistic extends Packet {

   public int statisticId;
   public int amount;


   public void processPacket(NetHandler var1) {
      var1.handleStatistic(this);
   }

   public void readPacketData(DataInputStream var1) throws IOException {
      this.statisticId = var1.readInt();
      this.amount = var1.readByte();
   }

   public void writePacketData(DataOutputStream var1) throws IOException {
      var1.writeInt(this.statisticId);
      var1.writeByte(this.amount);
   }

   public int getPacketSize() {
      return 6;
   }
}
