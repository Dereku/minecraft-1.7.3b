package net.minecraft.src;

import java.io.File;
import java.util.List;
import net.minecraft.src.IChunkLoader;
import net.minecraft.src.ISaveHandler;
import net.minecraft.src.WorldInfo;
import net.minecraft.src.WorldProvider;

public class SaveHandlerMP implements ISaveHandler {

   public WorldInfo loadWorldInfo() {
      return null;
   }

   public void checkSessionLock() {}

   public IChunkLoader getChunkLoader(WorldProvider var1) {
      return null;
   }

   public void saveWorldInfoAndPlayer(WorldInfo var1, List var2) {}

   public void saveWorldInfo(WorldInfo var1) {}

   public File func_28113_a(String var1) {
      return null;
   }
}
