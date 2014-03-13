package net.minecraft.src;

import java.util.Arrays;
import net.minecraft.src.J_ChainedFunctor;
import net.minecraft.src.J_JsonArrayNodeSelector;
import net.minecraft.src.J_JsonElementNodeSelector;
import net.minecraft.src.J_JsonFieldNodeSelector;
import net.minecraft.src.J_JsonNodeFactories;
import net.minecraft.src.J_JsonNodeSelector;
import net.minecraft.src.J_JsonObjectNodeSelector;
import net.minecraft.src.J_JsonStringNode;
import net.minecraft.src.J_JsonStringNodeSelector;

public final class J_JsonNodeSelectors {

   public static J_JsonNodeSelector func_27349_a(Object ... var0) {
      return func_27352_a(var0, new J_JsonNodeSelector(new J_JsonStringNodeSelector()));
   }

   public static J_JsonNodeSelector func_27346_b(Object ... var0) {
      return func_27352_a(var0, new J_JsonNodeSelector(new J_JsonArrayNodeSelector()));
   }

   public static J_JsonNodeSelector func_27353_c(Object ... var0) {
      return func_27352_a(var0, new J_JsonNodeSelector(new J_JsonObjectNodeSelector()));
   }

   public static J_JsonNodeSelector func_27348_a(String var0) {
      return func_27350_a(J_JsonNodeFactories.func_27316_a(var0));
   }

   public static J_JsonNodeSelector func_27350_a(J_JsonStringNode var0) {
      return new J_JsonNodeSelector(new J_JsonFieldNodeSelector(var0));
   }

   public static J_JsonNodeSelector func_27351_b(String var0) {
      return func_27353_c(new Object[0]).with(func_27348_a(var0));
   }

   public static J_JsonNodeSelector func_27347_a(int var0) {
      return new J_JsonNodeSelector(new J_JsonElementNodeSelector(var0));
   }

   public static J_JsonNodeSelector func_27354_b(int var0) {
      return func_27346_b(new Object[0]).with(func_27347_a(var0));
   }

   private static J_JsonNodeSelector func_27352_a(Object[] var0, J_JsonNodeSelector var1) {
      J_JsonNodeSelector var2 = var1;

      for(int var3 = var0.length - 1; var3 >= 0; --var3) {
         if(var0[var3] instanceof Integer) {
            var2 = func_27345_a(func_27354_b(((Integer)var0[var3]).intValue()), var2);
         } else {
            if(!(var0[var3] instanceof String)) {
               throw new IllegalArgumentException("Element [" + var0[var3] + "] of path elements" + " [" + Arrays.toString(var0) + "] was of illegal type [" + var0[var3].getClass().getCanonicalName() + "]; only Integer and String are valid.");
            }

            var2 = func_27345_a(func_27351_b((String)var0[var3]), var2);
         }
      }

      return var2;
   }

   private static J_JsonNodeSelector func_27345_a(J_JsonNodeSelector var0, J_JsonNodeSelector var1) {
      return new J_JsonNodeSelector(new J_ChainedFunctor(var0, var1));
   }
}
