package me.utils;

import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType;
import net.minecraft.client.*;
import net.minecraft.potion.*;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.classProvider;
import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;

public class ValueUtil
{

    
    public static double getMotion(final double initialSpeed, final double speedMultiplier) {
        double speed = initialSpeed;
        if (mc.getThePlayer().isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED))) {
            final int effect = mc.getThePlayer().getActivePotionEffect(classProvider.getPotionEnum(PotionType.MOVE_SPEED)).getAmplifier();
            speed *= 1.0 + speedMultiplier * (effect + 1.0);
        }
        return speed;
    }
    
    public static double getModifiedMotionY(double mY) {
        if (mc.getThePlayer().isPotionActive(classProvider.getPotionEnum(PotionType.JUMP))) {
            mY += (mc.getThePlayer().getActivePotionEffect(classProvider.getPotionEnum(PotionType.JUMP)).getAmplifier() + 1) * 0.1;
        }
        return mY;
    }
    
    public static double getBaseMotionY() {
        double motion = 0.41999998688697815;
        if (mc.getThePlayer().isPotionActive(classProvider.getPotionEnum(PotionType.JUMP))) {
            motion += (mc.getThePlayer().getActivePotionEffect(classProvider.getPotionEnum(PotionType.JUMP)).getAmplifier() + 1) * 0.1;
        }
        return motion;
    }
    

}
