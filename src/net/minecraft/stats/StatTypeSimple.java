package net.minecraft.stats;

import net.minecraft.src.IStatType;
import net.minecraft.src.IStatType;
import net.minecraft.stats.StatBase;

final class StatTypeSimple implements IStatType {

   public String format(int var1) {
      return StatBase.getNumberFormat().format((long)var1);
   }
}
