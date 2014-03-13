package net.minecraft.src;

import net.minecraft.src.IStatType;
import net.minecraft.src.StatBase;
import net.minecraft.src.StatList;

public class StatBasic extends StatBase {

   public StatBasic(int var1, String var2, IStatType var3) {
      super(var1, var2, var3);
   }

   public StatBasic(int var1, String var2) {
      super(var1, var2);
   }

   public StatBase registerStat() {
      super.registerStat();
      StatList.field_25187_b.add(this);
      return this;
   }
}
