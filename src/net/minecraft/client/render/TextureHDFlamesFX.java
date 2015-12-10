package net.minecraft.client.render;

import java.util.Random;
import net.minecraft.client.block.Block;
import net.minecraft.src.Config;
import net.minecraft.src.TextureFX;
import net.minecraft.src.TexturePackBase;

public class TextureHDFlamesFX extends TextureFX implements TextureHDFX {

    private int tileWidth = 0;
    private int fireHeight = 0;
    protected float[] buf1;
    protected float[] buf2;
    private Random random = new Random();

    public TextureHDFlamesFX(int i) {
        super(Block.fire.blockIndexInTexture + i * 16);
        this.tileWidth = 16;
        this.fireHeight = this.tileWidth + this.tileWidth / 4;
        this.imageData = new byte[this.tileWidth * this.tileWidth * 4];
        this.buf1 = new float[this.tileWidth * this.fireHeight];
        this.buf2 = new float[this.tileWidth * this.fireHeight];
    }

    @Override
    public void setTileWidth(int tileWidth) {
        if (tileWidth > Config.getMaxDynamicTileWidth()) {
            tileWidth = Config.getMaxDynamicTileWidth();
        }

        this.tileWidth = tileWidth;
        this.fireHeight = tileWidth + tileWidth / 4;
        this.imageData = new byte[tileWidth * tileWidth * 4];
        this.buf1 = new float[tileWidth * this.fireHeight];
        this.buf2 = new float[tileWidth * this.fireHeight];
    }

    @Override
    public void setTexturePackBase(TexturePackBase tpb) {}

    @Override
    public void onTick() {
        if (!Config.isAnimatedFire()) {
            this.imageData = null;
        }

        if (this.imageData != null) {
            float kFire = 1.01F + 0.8F / (float) this.tileWidth;
            float kDensity = 3.0F + (float) this.tileWidth / 16.0F;

            int tileWidth2;
            int k;
            float f;
            int j1;

            for (int af = 0; af < this.tileWidth; ++af) {
                for (tileWidth2 = 0; tileWidth2 < this.fireHeight; ++tileWidth2) {
                    k = this.fireHeight - this.tileWidth / 8;
                    f = this.buf1[af + (tileWidth2 + 1) % this.fireHeight * this.tileWidth] * (float) k;

                    for (int f2 = af - 1; f2 <= af + 1; ++f2) {
                        for (j1 = tileWidth2; j1 <= tileWidth2 + 1; ++j1) {
                            if (f2 >= 0 && j1 >= 0 && f2 < this.tileWidth && j1 < this.fireHeight) {
                                f += this.buf1[f2 + j1 * this.tileWidth];
                            }

                            ++k;
                        }
                    }

                    this.buf2[af + tileWidth2 * this.tileWidth] = f / ((float) k * kFire);
                    if (tileWidth2 >= this.fireHeight - this.tileWidth / 16) {
                        this.buf2[af + tileWidth2 * this.tileWidth] = this.random.nextFloat() * this.random.nextFloat() * this.random.nextFloat() * kDensity + this.random.nextFloat() * 0.1F + 0.2F;
                    }
                }
            }

            float[] afloat = this.buf2;

            this.buf2 = this.buf1;
            this.buf1 = afloat;
            tileWidth2 = this.tileWidth * this.tileWidth;

            for (k = 0; k < tileWidth2; ++k) {
                f = this.buf1[k] * 1.8F;
                if (f > 1.0F) {
                    f = 1.0F;
                }

                if (f < 0.0F) {
                    f = 0.0F;
                }

                j1 = (int) (f * 155.0F + 100.0F);
                int l1 = (int) (f * f * 255.0F);
                int j2 = (int) (f * f * f * f * f * f * f * f * f * f * 255.0F);
                short c = 255;

                if (f < 0.5F) {
                    c = 0;
                }

                int k4;

                if (this.anaglyphEnabled) {
                    k4 = (j1 * 30 + l1 * 59 + j2 * 11) / 100;
                    int i3 = (j1 * 30 + l1 * 70) / 100;
                    int j3 = (j1 * 30 + j2 * 70) / 100;

                    j1 = k4;
                    l1 = i3;
                    j2 = j3;
                }

                k4 = k * 4;
                this.imageData[k4 + 0] = (byte) j1;
                this.imageData[k4 + 1] = (byte) l1;
                this.imageData[k4 + 2] = (byte) j2;
                this.imageData[k4 + 3] = (byte) c;
            }

        }
    }
}
