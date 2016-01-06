package net.minecraft.src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class PanelCrashReport extends Panel {

    public PanelCrashReport(UnexpectedThrowable var1) {
        this.setBackground(new Color(3028036));
        this.setLayout(new BorderLayout());
        StringWriter var2 = new StringWriter();
        var1.exception.printStackTrace(new PrintWriter(var2));
        StringBuilder sysProp = new StringBuilder();
        StringBuilder panelText = new StringBuilder();
        String exception = var2.toString();

        try {
            sysProp.append("Generated ").append((new SimpleDateFormat()).format(new Date())).append("\n")
                    .append("\n")
                    .append("Minecraft: Minecraft Beta 1.7.3\n")
                    .append("OS: ").append(System.getProperty("os.name")).append(" (").append(System.getProperty("os.arch")).append(") version ").append(System.getProperty("os.version"))
                    .append("\n")
                    .append("Java: ").append(System.getProperty("java.version")).append(", ").append(System.getProperty("java.vendor")).append("\n")
                    .append("VM: ").append(System.getProperty("java.vm.name")).append(" (").append(System.getProperty("java.vm.info")).append("), ").append(System.getProperty("java.vm.vendor")).append("\n")
                    .append("LWJGL: ").append(Sys.getVersion())
                    .append("\n").append(GL11.glGetString(7936 /*GL_VENDOR*/))
                    .append("OpenGL: ").append(GL11.glGetString(7937 /*GL_RENDERER*/))
                    .append(" version ").append(GL11.glGetString(7938 /*GL_VERSION*/)).append(", ").append(GL11.glGetString(7936 /*GL_VENDOR*/)).append("\n");
        } catch (Throwable var8) {
            sysProp.append("[failed to get system properties (").append(var8).append(")]\n");
        }
        
        panelText.append("\n\n");
        if (exception.contains("Pixel format not accelerated")) {
            panelText.append("      Bad video card drivers!      \n")
                    .append("      -----------------------      \n")
                    .append("\n")
                    .append("Minecraft was unable to start because it failed to find an accelerated OpenGL mode.\n")
                    .append("This can usually be fixed by updating the video card drivers.\n");
        } else {
            panelText.append("      Minecraft has crashed!      \n")
                    .append("      ----------------------      \n")
                    .append("\n")
                    .append("Minecraft has stopped running because it encountered a problem.\n")
                    .append("\n")
                    .append("If you wish to report this, please copy this entire text and email it to support@mojang.com.\n")
                    .append("Please include a description of what you did when the error occured.\n");
        }

        
        panelText.append("\n\n\n")
                .append("--- BEGIN ERROR REPORT ").append(Integer.toHexString(panelText.toString().hashCode())).append(" --------\n")
                .append(sysProp)
                .append("\n\n")
                .append(exception)
                .append("--- END ERROR REPORT ").append(Integer.toHexString(panelText.toString().hashCode())).append(" ----------\n")
                .append("\n\n");
        TextArea var7 = new TextArea(panelText.toString(), 0, 0, 1);
        var7.setFont(new Font("Monospaced", 0, 12));
        this.add(new CanvasMojangLogo(), "North");
        this.add(new CanvasCrashReport(80), "East");
        this.add(new CanvasCrashReport(80), "West");
        this.add(new CanvasCrashReport(100), "South");
        this.add(var7, "Center");
        System.err.println(panelText.toString());
    }
}
