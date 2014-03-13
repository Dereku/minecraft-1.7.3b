package net.minecraft.src;

import net.minecraft.src.J_Functor;
import net.minecraft.src.J_JsonNodeDoesNotMatchChainedJsonNodeSelectorException;

abstract class J_LeafFunctor implements J_Functor {

   public final Object applyTo(Object var1) {
      if(!this.matchsNode(var1)) {
         throw J_JsonNodeDoesNotMatchChainedJsonNodeSelectorException.func_27322_a(this);
      } else {
         return this.typeSafeApplyTo(var1);
      }
   }

   protected abstract Object typeSafeApplyTo(Object var1);
}
