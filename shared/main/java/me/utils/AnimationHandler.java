package me.utils;

import java.util.WeakHashMap;
import net.ccbluex.liquidbounce.api.enums.EnumFacingType;
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing;
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos;
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3i;
import net.ccbluex.liquidbounce.features.module.modules.render.WolrdAnim;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class AnimationHandler extends MinecraftInstance {
    private final WeakHashMap<RenderChunk, AnimationData> timeStamps = new WeakHashMap<>();
    public void preRender(RenderChunk renderChunk) {
        if (this.timeStamps.containsKey(renderChunk)) {
            int animationDuration;
            long timeDif;
            AnimationData animationData = this.timeStamps.get(renderChunk);
            long time = animationData.timeStamp;
            if (time == -1L)
                animationData.timeStamp = time = System.currentTimeMillis();
            if ((timeDif = System.currentTimeMillis() - time) < (long)(animationDuration = WolrdAnim.chunkAnimationDurationValue.get())) {
                double chunkY = renderChunk.getPosition().getY();
                int chunkPos = WolrdAnim.chunkPositionValue.get();
                switch (WolrdAnim.animmode.get()) {
                    case "UP":
                        GlStateManager.translate(0.0, (-chunkPos - chunkY - (-chunkPos - chunkY) / (double)animationDuration * (double)timeDif), 0.0);
                        break;
                    case "Down":
                        GlStateManager.translate(0.0, (chunkPos - chunkY - (chunkPos - chunkY) / (double)animationDuration * (double)timeDif), 0.0);
                        break;
                    default:
                        IEnumFacing chunkFacing = animationData.chunkFacing;
                        if (chunkFacing == null)
                            break;
                        WVec3i vec = chunkFacing.getDirectionVec();
                        double animationPosition = -(chunkPos - chunkPos / (double)animationDuration * (double)timeDif);
                        GlStateManager.translate(((double)vec.getX() * animationPosition), 0.0, ((double)vec.getZ() * animationPosition));
                        break;
                }
            } else {
                this.timeStamps.remove(renderChunk);
            }
        }
    }

    public void setPosition(RenderChunk renderChunk, WBlockPos position) {
        if (mc.getThePlayer() != null) {
            boolean flag;
            WBlockPos zeroedPlayerPosition = mc.getThePlayer().getPosition();
            zeroedPlayerPosition = zeroedPlayerPosition.add(0, -zeroedPlayerPosition.getY(), 0);
            WBlockPos zeroedCenteredChunkPos = position.add(8, -position.getY(), 8);
            flag = zeroedPlayerPosition.distanceSq(zeroedCenteredChunkPos) > 4096.0D;

            if (flag) {
                IEnumFacing chunkFacing;
                WVec3i dif = zeroedPlayerPosition.subtract(zeroedCenteredChunkPos);
                int difX = Math.abs(dif.getX());
                int difZ = Math.abs(dif.getZ());
                if (difX > difZ) {
                    if (dif.getX() > 0) {
                        chunkFacing = classProvider.getEnumFacing(EnumFacingType.EAST);
                    } else {
                        chunkFacing = classProvider.getEnumFacing(EnumFacingType.WEST);
                    }
                } else if (dif.getZ() > 0) {
                    chunkFacing = classProvider.getEnumFacing(EnumFacingType.SOUTH);
                } else {
                    chunkFacing = classProvider.getEnumFacing(EnumFacingType.NORTH);
                }
                AnimationData animationData = new AnimationData(this, -1L, chunkFacing);
                this.timeStamps.put(renderChunk, animationData);
            }
        }
    }
    private static class AnimationData {
        public long timeStamp;
        public IEnumFacing chunkFacing;
        public AnimationHandler animationHandler;

        public AnimationData(AnimationHandler animationHandler, long timeStamp, IEnumFacing chunkFacing) {
            this.animationHandler = animationHandler;
            this.timeStamp = timeStamp;
            this.chunkFacing = chunkFacing;
        }
    }
}
