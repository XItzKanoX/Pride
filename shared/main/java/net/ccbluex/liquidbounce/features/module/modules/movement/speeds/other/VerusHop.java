/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;


import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class VerusHop extends SpeedMode {

    public VerusHop() {
        super("VerusHop");
    }

    public void onMotion() {
    }
    @Override
    public void onUpdate() {
        if (!mc2.player.isInWeb && !mc2.player.isInLava() && !mc2.player.isInWater() && !mc2.player.isOnLadder() && mc2.player.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc2.gameSettings.keyBindJump.pressed = false;
                if (mc2.player.onGround) {
                    mc2.player.jump();
                    MovementUtils.strafe(0.48F);
                }
                MovementUtils.strafe();
            }
        }
    }
    @Override
    public void onMove(MoveEvent event) {
    }
}
