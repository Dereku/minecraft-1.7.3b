package net.minecraft.client.gui;

import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiStats;
import net.minecraft.src.StatBase;
import net.minecraft.src.StatBase;
import net.minecraft.src.StatList;
import net.minecraft.src.StatList;
import net.minecraft.src.Tessellator;
import net.minecraft.src.Tessellator;

class GuiSlotStatsGeneral extends GuiSlot {

   // $FF: synthetic field
   final GuiStats field_27276_a;


   public GuiSlotStatsGeneral(GuiStats var1) {
      super(GuiStats.getMinecraft(var1), var1.width, var1.height, 32, var1.height - 64, 10);
      this.field_27276_a = var1;
      this.func_27258_a(false);
   }

   protected int getSize() {
      return StatList.field_25187_b.size();
   }

   protected void elementClicked(int var1, boolean var2) {}

   protected boolean isSelected(int var1) {
      return false;
   }

   protected int getContentHeight() {
      return this.getSize() * 10;
   }

   protected void drawBackground() {
      this.field_27276_a.drawDefaultBackground();
   }

   protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
      StatBase var6 = (StatBase)StatList.field_25187_b.get(var1);
      this.field_27276_a.drawString(GuiStats.getFontRenderer1(this.field_27276_a), var6.statName, var2 + 2, var3 + 1, var1 % 2 == 0?16777215:9474192);
      String var7 = var6.func_27084_a(GuiStats.getStatsFileWriter(this.field_27276_a).writeStat(var6));
      this.field_27276_a.drawString(GuiStats.getFontRenderer2(this.field_27276_a), var7, var2 + 2 + 213 - GuiStats.getFontRenderer3(this.field_27276_a).getStringWidth(var7), var3 + 1, var1 % 2 == 0?16777215:9474192);
   }
}
