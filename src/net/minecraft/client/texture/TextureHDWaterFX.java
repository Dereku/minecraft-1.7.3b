package net.minecraft.client.texture;

import net.minecraft.client.block.Block;
import net.minecraft.src.Config;
import net.minecraft.client.texture.TextureFX;
import net.minecraft.client.texture.TexturePackBase;

public class TextureHDWaterFX extends TextureFX implements TextureHDFX {

    private TexturePackBase texturePackBase;
    private int tileWidth = 0;
    protected float[] buf1;
    protected float[] buf2;
    protected float[] buf3;
    protected float[] buf4;
    private int tickCounter;

    public TextureHDWaterFX() {
        super(Block.waterMoving.blockIndexInTexture);
        this.tileWidth = 16;
        this.imageData = new byte[this.tileWidth * this.tileWidth * 4];
        this.buf1 = new float[this.tileWidth * this.tileWidth];
        this.buf2 = new float[this.tileWidth * this.tileWidth];
        this.buf3 = new float[this.tileWidth * this.tileWidth];
        this.buf4 = new float[this.tileWidth * this.tileWidth];
        this.tickCounter = 0;
    }

    @Override
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
        this.tickCounter = 0;
    }

    @Override
    public void setTexturePackBase(TexturePackBase tpb) {
        this.texturePackBase = tpb;
    }

    @Override
    public void onTick() {
        if (!Config.isAnimatedWater()) {
            this.imageData = null;
        }

        if (this.imageData != null) {
            ++this.tickCounter;
            int widthMask = this.tileWidth - 1;

            int af;
            int i;
            float f1;
            int r;
            int g;

            for (af = 0; af < this.tileWidth; ++af) {
                for (i = 0; i < this.tileWidth; ++i) {
                    f1 = 0.0F;

                    for (int f2 = af - 1; f2 <= af + 1; ++f2) {
                        r = f2 & widthMask;
                        g = i & widthMask;
                        f1 += this.buf1[r + g * this.tileWidth];
                    }

                    this.buf2[af + i * this.tileWidth] = f1 / 3.3F + this.buf3[af + i * this.tileWidth] * 0.8F;
                }
            }

            for (af = 0; af < this.tileWidth; ++af) {
                for (i = 0; i < this.tileWidth; ++i) {
                    this.buf3[af + i * this.tileWidth] += this.buf4[af + i * this.tileWidth] * 0.05F;
                    if (this.buf3[af + i * this.tileWidth] < 0.0F) {
                        this.buf3[af + i * this.tileWidth] = 0.0F;
                    }

                    this.buf4[af + i * this.tileWidth] -= 0.1F;
                    if (Math.random() < 0.05D) {
                        this.buf4[af + i * this.tileWidth] = 0.5F;
                    }
                }
            }

            float[] afloat = this.buf2;

            this.buf2 = this.buf1;
            this.buf1 = afloat;

            for (i = 0; i < this.tileWidth * this.tileWidth; ++i) {
                f1 = this.buf1[i];
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                }

                if (f1 < 0.0F) {
                    f1 = 0.0F;
                }

                float f = f1 * f1;

                r = (int) (32.0F + f * 32.0F);
                g = (int) (50.0F + f * 64.0F);
                int b = 255;
                int a = (int) (146.0F + f * 50.0F);

                if (this.anaglyphEnabled) {
                    int i3 = (r * 30 + g * 59 + b * 11) / 100;
                    int j3 = (r * 30 + g * 70) / 100;
                    int k3 = (r * 30 + b * 70) / 100;

                    r = i3;
                    g = j3;
                    b = k3;
                }

                this.imageData[i * 4 + 0] = (byte) r;
                this.imageData[i * 4 + 1] = (byte) g;
                this.imageData[i * 4 + 2] = (byte) b;
                this.imageData[i * 4 + 3] = (byte) a;
            }

        }
    }
}
