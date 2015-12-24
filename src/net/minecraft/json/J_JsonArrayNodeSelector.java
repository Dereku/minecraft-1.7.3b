package net.minecraft.json;

import java.util.List;
import net.minecraft.json.J_JsonNode;
import net.minecraft.json.J_LeafFunctor;

final class J_JsonArrayNodeSelector extends J_LeafFunctor {

   public boolean matchsNode_(J_JsonNode var1) {
      return EnumJsonNodeType.ARRAY == var1.getType();
   }

   public String shortForm() {
      return "A short form array";
   }

   public List typeSafeApplyTo(J_JsonNode var1) {
      return var1.getElements();
   }

   public String toString() {
      return "an array";
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object typeSafeApplyTo(Object var1) {
      return this.typeSafeApplyTo((J_JsonNode)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean matchsNode(Object var1) {
      return this.matchsNode_((J_JsonNode)var1);
   }
}
