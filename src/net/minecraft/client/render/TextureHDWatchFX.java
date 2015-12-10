package net.minecraft.client.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.item.Item;
import net.minecraft.client.render.TextureHDFX;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;

public class TextureHDWatchFX extends TextureFX implements TextureHDFX {

    private Minecraft mc;
    private int tileWidth;
    private TexturePackBase texturePackBase;
    private int[] watchIconImageData;
    private int[] dialImageData;
    private byte[] watchBaseData;
    private byte[] dialBaseData;
    private double showAngle;
    private double angleDiff;

    public TextureHDWatchFX(Minecraft minecraft) {
        super(Item.pocketSundial.getIconFromDamage(0));
        this.mc = minecraft;
        this.tileWidth = 16;
        this.setup();
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
        this.setup();
    }

    public void setTexturePackBase(TexturePackBase tpb) {
        this.texturePackBase = tpb;
    }

    private void setup() {
        this.imageData = new byte[this.tileWidth * this.tileWidth * 4];
        this.watchIconImageData = new int[this.tileWidth * this.tileWidth];
        this.dialImageData = new int[this.tileWidth * this.tileWidth];
        this.tileImage = 1;

        try {
            BufferedImage ioexception = ImageIO.read(Minecraft.class.getResource("/assets/gui/items.png"));

            if (this.texturePackBase != null) {
                ioexception = ImageIO.read(this.texturePackBase.getResourceAsStream("/assets/gui/items.png"));
            }

            int x = this.iconIndex % 16 * this.tileWidth;
            int y = this.iconIndex / 16 * this.tileWidth;

            ioexception.getRGB(x, y, this.tileWidth, this.tileWidth, this.watchIconImageData, 0, this.tileWidth);
            ioexception = ImageIO.read(Minecraft.class.getResource("/assets/misc/dial.png"));
            if (this.texturePackBase != null) {
                ioexception = ImageIO.read(this.texturePackBase.getResourceAsStream("/assets/misc/dial.png"));
            }

            if (ioexception.getWidth() != this.tileWidth) {
                ioexception = RenderEngine.scaleBufferedImage(ioexception, this.tileWidth, this.tileWidth);
            }

            ioexception.getRGB(0, 0, this.tileWidth, this.tileWidth, this.dialImageData, 0, this.tileWidth);
            this.watchBaseData = new byte[this.watchIconImageData.length * 4];
            this.dialBaseData = new byte[this.dialImageData.length * 4];
            int tileWidth2 = this.tileWidth * this.tileWidth;

            int i;
            int a;
            int r;
            int g;
            int b;

            for (i = 0; i < tileWidth2; ++i) {
                a = this.watchIconImageData[i] >> 24 & 255;
                r = this.watchIconImageData[i] >> 16 & 255;
                g = this.watchIconImageData[i] >> 8 & 255;
                b = this.watchIconImageData[i] >> 0 & 255;
                if (r == b && g == 0 && b > 0) {
                    boolean t = false;
                }

                this.watchBaseData[i * 4 + 0] = (byte) r;
                this.watchBaseData[i * 4 + 1] = (byte) g;
                this.watchBaseData[i * 4 + 2] = (byte) b;
                this.watchBaseData[i * 4 + 3] = (byte) a;
            }

            for (i = 0; i < tileWidth2; ++i) {
                a = this.dialImageData[i] >> 24 & 255;
                r = this.dialImageData[i] >> 16 & 255;
                g = this.dialImageData[i] >> 8 & 255;
                b = this.dialImageData[i] >> 0 & 255;
                this.dialBaseData[i * 4 + 0] = (byte) r;
                this.dialBaseData[i * 4 + 1] = (byte) g;
                this.dialBaseData[i * 4 + 2] = (byte) b;
                this.dialBaseData[i * 4 + 3] = (byte) a;
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    public void onTick() {
        double d = 0.0D;

        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            float d1 = this.mc.theWorld.getCelestialAngle(1.0F);

            d = (double) (-d1 * 3.141593F * 2.0F);
            if (this.mc.theWorld.worldProvider.isNether) {
                d = Math.random() * 3.1415927410125732D * 2.0D;
            }
        }

        double d0;

        for (d0 = d - this.showAngle; d0 < -3.141592653589793D; d0 += 6.283185307179586D) {
            ;
        }

        while (d0 >= 3.141592653589793D) {
            d0 -= 6.283185307179586D;
        }

        if (d0 < -1.0D) {
            d0 = -1.0D;
        }

        if (d0 > 1.0D) {
            d0 = 1.0D;
        }

        this.angleDiff += d0 * 0.1D;
        this.angleDiff *= 0.8D;
        this.showAngle += this.angleDiff;
        double d2 = Math.sin(this.showAngle);
        double d3 = Math.cos(this.showAngle);
        int tileWidth2 = this.tileWidth * this.tileWidth;
        int widthMask = this.tileWidth - 1;
        double widthMaskD = (double) widthMask;

        for (int i = 0; i < tileWidth2; ++i) {
            int i4 = i * 4;
            int r = this.watchBaseData[i4 + 0] & 255;
            int g = this.watchBaseData[i4 + 1] & 255;
            int b = this.watchBaseData[i4 + 2] & 255;
            int a = this.watchBaseData[i4 + 3] & 255;

            if (r == b && g == 0 && b > 0) {
                double j1 = -((double) (i % this.tileWidth) / widthMaskD - 0.5D);
                double l1 = (double) (i / this.tileWidth) / widthMaskD - 0.5D;
                int i2 = r;
                int dx = (int) ((j1 * d3 + l1 * d2 + 0.5D) * (double) this.tileWidth);
                int dy = (int) ((l1 * d3 - j1 * d2 + 0.5D) * (double) this.tileWidth);
                int pos = (dx & widthMask) + (dy & widthMask) * this.tileWidth;
                int pos4 = pos * 4;

                r = (this.dialBaseData[pos4 + 0] & 255) * r / 255;
                g = (this.dialBaseData[pos4 + 1] & 255) * i2 / 255;
                b = (this.dialBaseData[pos4 + 2] & 255) * i2 / 255;
                a = this.dialBaseData[pos4 + 3] & 255;
            }

            if (this.anaglyphEnabled) {
                int ii = (r * 30 + g * 59 + b * 11) / 100;
                int k1 = (r * 30 + g * 70) / 100;
                int j = (r * 30 + b * 70) / 100;

                r = ii;
                g = k1;
                b = j;
            }

            this.imageData[i4 + 0] = (byte) r;
            this.imageData[i4 + 1] = (byte) g;
            this.imageData[i4 + 2] = (byte) b;
            this.imageData[i4 + 3] = (byte) a;
        }

    }
}
