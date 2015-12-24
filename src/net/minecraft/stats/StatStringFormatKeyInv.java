package net.minecraft.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.src.IStatStringFormat;
import net.minecraft.src.IStatStringFormat;
import org.lwjgl.input.Keyboard;

public class StatStringFormatKeyInv implements IStatStringFormat {

   // $FF: synthetic field
   final Minecraft mc;


   public StatStringFormatKeyInv(Minecraft var1) {
      this.mc = var1;
   }

   public String formatString(String var1) {
      return String.format(var1, new Object[]{Keyboard.getKeyName(this.mc.gameSettings.keyBindInventory.keyCode)});
   }
}
