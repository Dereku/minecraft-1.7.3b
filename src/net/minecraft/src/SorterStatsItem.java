package net.minecraft.src;

import java.util.Comparator;
import net.minecraft.client.gui.GuiSlotStatsItem;
import net.minecraft.client.gui.GuiStats;

public class SorterStatsItem implements Comparator {

   // $FF: synthetic field
   final GuiStats statsGUI;
   // $FF: synthetic field
   final GuiSlotStatsItem slotStatsItemGUI;


   public SorterStatsItem(GuiSlotStatsItem var1, GuiStats var2) {
      this.slotStatsItemGUI = var1;
      this.statsGUI = var2;
   }

   public int func_27371_a(StatCrafting var1, StatCrafting var2) {
      int var3 = var1.func_25072_b();
      int var4 = var2.func_25072_b();
      StatBase var5 = null;
      StatBase var6 = null;
      if(this.slotStatsItemGUI.field_27271_e == 0) {
         var5 = StatList.objectBreakStats[var3];
         var6 = StatList.objectBreakStats[var4];
      } else if(this.slotStatsItemGUI.field_27271_e == 1) {
         var5 = StatList.objectCraftStats[var3];
         var6 = StatList.objectCraftStats[var4];
      } else if(this.slotStatsItemGUI.field_27271_e == 2) {
         var5 = StatList.objectUseStats[var3];
         var6 = StatList.objectUseStats[var4];
      }

      if(var5 != null || var6 != null) {
         if(var5 == null) {
            return 1;
         }

         if(var6 == null) {
            return -1;
         }

         int var7 = GuiStats.getStatsFileWriter(this.slotStatsItemGUI.field_27275_a).writeStat(var5);
         int var8 = GuiStats.getStatsFileWriter(this.slotStatsItemGUI.field_27275_a).writeStat(var6);
         if(var7 != var8) {
            return (var7 - var8) * this.slotStatsItemGUI.field_27270_f;
         }
      }

      return var3 - var4;
   }

   // $FF: synthetic method
   // $FF: bridge method
   @Override
   public int compare(Object var1, Object var2) {
      return this.func_27371_a((StatCrafting)var1, (StatCrafting)var2);
   }
}
