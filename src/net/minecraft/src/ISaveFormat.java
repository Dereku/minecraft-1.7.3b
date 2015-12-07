package net.minecraft.src;

import java.util.List;
import net.minecraft.world.WorldInfo;

public interface ISaveFormat {

   String func_22178_a();

   ISaveHandler getSaveLoader(String var1, boolean var2);

   List func_22176_b();

   void flushCache();

   WorldInfo getWorldInfo(String var1);

   void func_22172_c(String var1);

   void func_22170_a(String var1, String var2);

   boolean isOldMapFormat(String var1);

   boolean convertMapFormat(String var1, IProgressUpdate var2);
}
