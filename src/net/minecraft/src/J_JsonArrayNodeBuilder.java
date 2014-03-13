package net.minecraft.src;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.src.J_JsonNode;
import net.minecraft.src.J_JsonNodeBuilder;
import net.minecraft.src.J_JsonNodeFactories;
import net.minecraft.src.J_JsonRootNode;

public final class J_JsonArrayNodeBuilder implements J_JsonNodeBuilder {

   private final List elementBuilders = new LinkedList();


   public J_JsonArrayNodeBuilder withElement(J_JsonNodeBuilder var1) {
      this.elementBuilders.add(var1);
      return this;
   }

   public J_JsonRootNode build() {
      LinkedList var1 = new LinkedList();
      Iterator var2 = this.elementBuilders.iterator();

      while(var2.hasNext()) {
         J_JsonNodeBuilder var3 = (J_JsonNodeBuilder)var2.next();
         var1.add(var3.buildNode());
      }

      return J_JsonNodeFactories.func_27309_a(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public J_JsonNode buildNode() {
      return this.build();
   }
}
