package net.minecraft.src;

import java.util.Map;
import net.minecraft.src.StatsSyncher;

class ThreadStatSyncherSend extends Thread {

   // $FF: synthetic field
   final Map field_27233_a;
   // $FF: synthetic field
   final StatsSyncher syncher;


   ThreadStatSyncherSend(StatsSyncher var1, Map var2) {
      this.syncher = var1;
      this.field_27233_a = var2;
   }

   public void run() {
      try {
         StatsSyncher.func_27412_a(this.syncher, this.field_27233_a, StatsSyncher.getUnsentDataFile(this.syncher), StatsSyncher.getUnsentTempFile(this.syncher), StatsSyncher.getUnsentOldFile(this.syncher));
      } catch (Exception var5) {
         var5.printStackTrace();
      } finally {
         StatsSyncher.setBusy(this.syncher, false);
      }

   }
}
