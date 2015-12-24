package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.src.SorterStatsItem;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.src.Tessellator;

public class GuiSlotStatsItem extends GuiSlotStats {

   // $FF: synthetic field
   public final GuiStats field_27275_a;


   public GuiSlotStatsItem(GuiStats var1) {
      super(var1);
      this.field_27275_a = var1;
      this.field_27273_c = new ArrayList();
      Iterator var2 = StatList.field_25186_c.iterator();

      while(var2.hasNext()) {
         StatCrafting var3 = (StatCrafting)var2.next();
         boolean var4 = false;
         int var5 = var3.func_25072_b();
         if(GuiStats.getStatsFileWriter(var1).writeStat(var3) > 0) {
            var4 = true;
         } else if(StatList.objectBreakStats[var5] != null && GuiStats.getStatsFileWriter(var1).writeStat(StatList.objectBreakStats[var5]) > 0) {
            var4 = true;
         } else if(StatList.objectCraftStats[var5] != null && GuiStats.getStatsFileWriter(var1).writeStat(StatList.objectCraftStats[var5]) > 0) {
            var4 = true;
         }

         if(var4) {
            this.field_27273_c.add(var3);
         }
      }

      this.field_27272_d = new SorterStatsItem(this, var1);
   }

   @Override
   protected void func_27260_a(int var1, int var2, Tessellator var3) {
      super.func_27260_a(var1, var2, var3);
      if(this.field_27268_b == 0) {
         GuiStats.drawSprite(this.field_27275_a, var1 + 115 - 18 + 1, var2 + 1 + 1, 72, 18);
      } else {
         GuiStats.drawSprite(this.field_27275_a, var1 + 115 - 18, var2 + 1, 72, 18);
      }

      if(this.field_27268_b == 1) {
         GuiStats.drawSprite(this.field_27275_a, var1 + 165 - 18 + 1, var2 + 1 + 1, 18, 18);
      } else {
         GuiStats.drawSprite(this.field_27275_a, var1 + 165 - 18, var2 + 1, 18, 18);
      }

      if(this.field_27268_b == 2) {
         GuiStats.drawSprite(this.field_27275_a, var1 + 215 - 18 + 1, var2 + 1 + 1, 36, 18);
      } else {
         GuiStats.drawSprite(this.field_27275_a, var1 + 215 - 18, var2 + 1, 36, 18);
      }

   }

   @Override
   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      StatCrafting var6 = this.func_27264_b(var1);
      int var7 = var6.func_25072_b();
      GuiStats.drawItemSprite(this.field_27275_a, var2 + 40, var3, var7);
      this.func_27265_a((StatCrafting)StatList.objectBreakStats[var7], var2 + 115, var3, var1 % 2 == 0);
      this.func_27265_a((StatCrafting)StatList.objectCraftStats[var7], var2 + 165, var3, var1 % 2 == 0);
      this.func_27265_a(var6, var2 + 215, var3, var1 % 2 == 0);
   }

   @Override
   protected String func_27263_a(int var1) {
      return var1 == 1?"stat.crafted":(var1 == 2?"stat.used":"stat.depleted");
   }
}
