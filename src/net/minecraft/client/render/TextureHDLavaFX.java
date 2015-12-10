package net.minecraft.client.render;

import net.minecraft.client.block.Block;
import net.minecraft.src.Config;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;

public class TextureHDLavaFX extends TextureFX implements TextureHDFX {

    private TexturePackBase texturePackBase;
    private int tileWidth = 0;
    protected float[] buf1;
    protected float[] buf2;
    protected float[] buf3;
    protected float[] buf4;

    public TextureHDLavaFX() {
        super(Block.lavaMoving.blockIndexInTexture);
        this.tileWidth = 16;
        this.imageData = new byte[this.tileWidth * this.tileWidth * 4];
        this.buf1 = new float[this.tileWidth * this.tileWidth];
        this.buf2 = new float[this.tileWidth * this.tileWidth];
        this.buf3 = new float[this.tileWidth * this.tileWidth];
        this.buf4 = new float[this.tileWidth * this.tileWidth];
    }

    public void setTileWidth(int tileWidth) {
        if (tileWidth > Config.getMaxDynamicTileWidth()) {
            tileWidth = Config.getMaxDynamicTileWidth();
        }

        this.tileWidth = tileWidth;
        this.imageData = new byte[tileWidth * tileWidth * 4];
        this.buf1 = new float[tileWidth * tileWidth];
        this.buf2 = new float[tileWidth * tileWidth];
        this.buf3 = new float[tileWidth * tileWidth];
        this.buf4 = new float[tileWidth * tileWidth];
    }

    public void setTexturePackBase(TexturePackBase tpb) {
        this.texturePackBase = tpb;
    }

    public void onTick() {
        if (!Config.isAnimatedLava()) {
            this.imageData = null;
        }

        if (this.imageData != null) {
            int widthMask = this.tileWidth - 1;

            int tileWidth2;
            int j1;
            int l1;
            int j2;
            int l2;

            for (int af = 0; af < this.tileWidth; ++af) {
                for (tileWidth2 = 0; tileWidth2 < this.tileWidth; ++tileWidth2) {
                    float k = 0.0F;
                    int f1 = (int) (MathHelper.sin((float) tileWidth2 * 3.141593F * 2.0F / 16.0F) * 1.2F);
                    int f2 = (int) (MathHelper.sin((float) af * 3.141593F * 2.0F / 16.0F) * 1.2F);

                    for (j1 = af - 1; j1 <= af + 1; ++j1) {
                        for (l1 = tileWidth2 - 1; l1 <= tileWidth2 + 1; ++l1) {
                            j2 = j1 + f1 & widthMask;
                            l2 = l1 + f2 & widthMask;
                            k += this.buf1[j2 + l2 * this.tileWidth];
                        }
                    }

                    this.buf2[af + tileWidth2 * this.tileWidth] = k / 10.0F + (this.buf3[(af + 0 & widthMask) + (tileWidth2 + 0 & widthMask) * this.tileWidth] + this.buf3[(af + 1 & widthMask) + (tileWidth2 + 0 & widthMask) * this.tileWidth] + this.buf3[(af + 1 & widthMask) + (tileWidth2 + 1 & widthMask) * this.tileWidth] + this.buf3[(af + 0 & widthMask) + (tileWidth2 + 1 & widthMask) * this.tileWidth]) / 4.0F * 0.8F;
                    this.buf3[af + tileWidth2 * this.tileWidth] += this.buf4[af + tileWidth2 * this.tileWidth] * 0.01F;
                    if (this.buf3[af + tileWidth2 * this.tileWidth] < 0.0F) {
                        this.buf3[af + tileWidth2 * this.tileWidth] = 0.0F;
                    }

                    this.buf4[af + tileWidth2 * this.tileWidth] -= 0.06F;
                    if (Math.random() < 0.005D) {
                        this.buf4[af + tileWidth2 * this.tileWidth] = 1.5F;
                    }
                }
            }

            float[] afloat = this.buf2;

            this.buf2 = this.buf1;
            this.buf1 = afloat;
            tileWidth2 = this.tileWidth * this.tileWidth;

            for (int i = 0; i < tileWidth2; ++i) {
                float f = this.buf1[i] * 2.0F;

                if (f > 1.0F) {
                    f = 1.0F;
                }

                if (f < 0.0F) {
                    f = 0.0F;
                }

                j1 = (int) (f * 100.0F + 155.0F);
                l1 = (int) (f * f * 255.0F);
                j2 = (int) (f * f * f * f * 128.0F);
                if (this.anaglyphEnabled) {
                    l2 = (j1 * 30 + l1 * 59 + j2 * 11) / 100;
                    int j3 = (j1 * 30 + l1 * 70) / 100;
                    int k3 = (j1 * 30 + j2 * 70) / 100;

                    j1 = l2;
                    l1 = j3;
                    j2 = k3;
                }

                this.imageData[i * 4 + 0] = (byte) j1;
                this.imageData[i * 4 + 1] = (byte) l1;
                this.imageData[i * 4 + 2] = (byte) j2;
                this.imageData[i * 4 + 3] = -1;
            }

        }
    }
}
