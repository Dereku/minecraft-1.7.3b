package net.minecraft.src;

import net.minecraft.src.StatsSyncher;

class ThreadStatSyncherReceive extends Thread {

   // $FF: synthetic field
   final StatsSyncher syncher;


   ThreadStatSyncherReceive(StatsSyncher var1) {
      this.syncher = var1;
   }

   public void run() {
      try {
         if(StatsSyncher.func_27422_a(this.syncher) != null) {
            StatsSyncher.func_27412_a(this.syncher, StatsSyncher.func_27422_a(this.syncher), StatsSyncher.func_27423_b(this.syncher), StatsSyncher.func_27411_c(this.syncher), StatsSyncher.func_27413_d(this.syncher));
         } else if(StatsSyncher.func_27423_b(this.syncher).exists()) {
            StatsSyncher.func_27421_a(this.syncher, StatsSyncher.func_27409_a(this.syncher, StatsSyncher.func_27423_b(this.syncher), StatsSyncher.func_27411_c(this.syncher), StatsSyncher.func_27413_d(this.syncher)));
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      } finally {
         StatsSyncher.setBusy(this.syncher, false);
      }

   }
}
