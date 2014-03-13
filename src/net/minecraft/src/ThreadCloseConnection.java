package net.minecraft.src;

import net.minecraft.src.NetworkManager;

class ThreadCloseConnection extends Thread {

   // $FF: synthetic field
   final NetworkManager networkManager;


   ThreadCloseConnection(NetworkManager var1) {
      this.networkManager = var1;
   }

   public void run() {
      try {
         Thread.sleep(2000L);
         if(NetworkManager.isRunning(this.networkManager)) {
            NetworkManager.getWriteThread(this.networkManager).interrupt();
            this.networkManager.networkShutdown("disconnect.closed", new Object[0]);
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }
}
