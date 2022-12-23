package me.sound;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;

import java.awt.*;


public class Sound {
    public static Sound INSTANCE;
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static boolean canSendMotionPacket = true;
    private static boolean notificationsAllowed = false;

    public static boolean hasOptifine = false;
    public static EntityRenderer getEntityRenderer() {
        return getMinecraft().entityRenderer;
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }



    public static PlayerControllerMP getPlayerController() {
        return getMinecraft().playerController;
    }



    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

//    public static Timer getTimer() {
//        return ((IMinecraft) Minecraft.getMinecraft()).getTimer();
//    }



    public static boolean notificationsAllowed() {
        return notificationsAllowed;
    }


    public static Color getClientColor() {
        return new Color(236, 133, 209);
    }

    public static Color getAlternateClientColor() {
        return new Color(28, 167, 222);
    }

    public static void notificationsAllowed(boolean value) {
        notificationsAllowed = value;
    }

    public  Sound(){
        new SoundPlayer().playSound(SoundPlayer.SoundType.SPECIAL, LiquidBounce.moduleManager.getToggleVolume());
    }

    public void Volll(){

        new SoundPlayer().playSound(SoundPlayer.SoundType.VICTORY, LiquidBounce.moduleManager.getToggleVolume());
    }
    public boolean fastRenderDisabled(GameSettings gameSettingsIn) {
        try {
            return !((boolean) this.fastRenderDisabled(gameSettingsIn));
        } catch (Exception ignored) {
        }
        return true;
    }
}
