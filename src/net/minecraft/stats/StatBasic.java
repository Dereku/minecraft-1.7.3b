package net.minecraft.stats;

import net.minecraft.src.IStatType;
import net.minecraft.src.IStatType;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

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
