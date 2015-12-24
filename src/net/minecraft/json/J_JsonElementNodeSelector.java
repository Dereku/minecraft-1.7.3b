package net.minecraft.json;

import java.util.List;
import net.minecraft.json.J_JsonNode;
import net.minecraft.json.J_LeafFunctor;

final class J_JsonElementNodeSelector extends J_LeafFunctor {

   // $FF: synthetic field
   final int index;


   J_JsonElementNodeSelector(int var1) {
      this.index = var1;
   }

   public boolean matchsNode_(List var1) {
      return var1.size() > this.index;
   }

   public String shortForm() {
      return Integer.toString(this.index);
   }

   public J_JsonNode typeSafeApplyTo_(List var1) {
      return (J_JsonNode)var1.get(this.index);
   }

   public String toString() {
      return "an element at index [" + this.index + "]";
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object typeSafeApplyTo(Object var1) {
      return this.typeSafeApplyTo_((List)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean matchsNode(Object var1) {
      return this.matchsNode_((List)var1);
   }
}
