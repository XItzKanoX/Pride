package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.api.minecraft.potion.IPotion;
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static net.ccbluex.liquidbounce.utils.MovementUtils.getDirection;

public class CodeTool extends MinecraftInstance {
    public static GuiIngame guiIngame;
    public static IEnumFacing enumFacing;

    public static void setSpeed(double speed) {
        Minecraft.getMinecraft().player.motionX = -Math.sin(getDirection()) * speed;
        Minecraft.getMinecraft().player.motionZ = Math.cos(getDirection()) * speed;
    }

}
