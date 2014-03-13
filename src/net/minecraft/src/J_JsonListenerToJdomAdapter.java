package net.minecraft.src;

import java.util.Stack;
import net.minecraft.src.J_ArrayNodeContainer;
import net.minecraft.src.J_FieldNodeContainer;
import net.minecraft.src.J_JsonArrayNodeBuilder;
import net.minecraft.src.J_JsonFieldBuilder;
import net.minecraft.src.J_JsonListener;
import net.minecraft.src.J_JsonNodeBuilder;
import net.minecraft.src.J_JsonNodeBuilders;
import net.minecraft.src.J_JsonObjectNodeBuilder;
import net.minecraft.src.J_JsonRootNode;
import net.minecraft.src.J_NodeContainer;
import net.minecraft.src.J_ObjectNodeContainer;

final class J_JsonListenerToJdomAdapter implements J_JsonListener {

   private final Stack field_27210_a = new Stack();
   private J_JsonNodeBuilder field_27209_b;


   J_JsonRootNode func_27208_a() {
      return (J_JsonRootNode)this.field_27209_b.buildNode();
   }

   public void func_27195_b() {}

   public void func_27204_c() {}

   public void func_27200_d() {
      J_JsonArrayNodeBuilder var1 = J_JsonNodeBuilders.func_27249_e();
      this.func_27207_a(var1);
      this.field_27210_a.push(new J_ArrayNodeContainer(this, var1));
   }

   public void func_27197_e() {
      this.field_27210_a.pop();
   }

   public void func_27194_f() {
      J_JsonObjectNodeBuilder var1 = J_JsonNodeBuilders.func_27253_d();
      this.func_27207_a(var1);
      this.field_27210_a.push(new J_ObjectNodeContainer(this, var1));
   }

   public void func_27203_g() {
      this.field_27210_a.pop();
   }

   public void func_27205_a(String var1) {
      J_JsonFieldBuilder var2 = J_JsonFieldBuilder.aJsonFieldBuilder().func_27304_a(J_JsonNodeBuilders.func_27254_b(var1));
      ((J_NodeContainer)this.field_27210_a.peek()).func_27289_a(var2);
      this.field_27210_a.push(new J_FieldNodeContainer(this, var2));
   }

   public void func_27199_h() {
      this.field_27210_a.pop();
   }

   public void func_27201_b(String var1) {
      this.func_27206_b(J_JsonNodeBuilders.func_27250_a(var1));
   }

   public void func_27196_i() {
      this.func_27206_b(J_JsonNodeBuilders.func_27251_b());
   }

   public void func_27198_c(String var1) {
      this.func_27206_b(J_JsonNodeBuilders.func_27254_b(var1));
   }

   public void func_27193_j() {
      this.func_27206_b(J_JsonNodeBuilders.func_27252_c());
   }

   public void func_27202_k() {
      this.func_27206_b(J_JsonNodeBuilders.func_27248_a());
   }

   private void func_27207_a(J_JsonNodeBuilder var1) {
      if(this.field_27209_b == null) {
         this.field_27209_b = var1;
      } else {
         this.func_27206_b(var1);
      }

   }

   private void func_27206_b(J_JsonNodeBuilder var1) {
      ((J_NodeContainer)this.field_27210_a.peek()).func_27290_a(var1);
   }
}
