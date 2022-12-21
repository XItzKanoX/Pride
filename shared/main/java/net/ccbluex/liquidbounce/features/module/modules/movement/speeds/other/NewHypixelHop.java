/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;

public class NewHypixelHop extends SpeedMode {
    private boolean legitJump;
    private final MSTimer timer=new MSTimer();
    public NewHypixelHop() {
        super("HypixelHop");
    }


    @Override
    public void onEnable() {
    }

    @Override
    public void onMotion() {
    }
    private boolean stage = false;

    @Override
    public void onUpdate() {
        if (!mc2.player.isInWeb && !mc2.player.isInLava() && !mc2.player.isInWater() && !mc2.player.isOnLadder() && mc2.player.ridingEntity == null) {
            final Speed speed = (Speed) LiquidBounce.moduleManager.getModule(Speed.class);
            if (speed == null)
                return;

            if (!mc2.player.isInWeb && !mc2.player.isInLava() && !mc2.player.isInWater() && !mc2.player.isOnLadder() && mc2.player.ridingEntity == null) {
                if (MovementUtils.isMoving()) {
                    mc2.gameSettings.keyBindJump.pressed = false;
                    if (mc2.player.onGround) {
                        mc2.player.jump();
                        mc2.player.speedInAir = 0.02F;
                        MovementUtils.strafe(0.43F);
                    }
                    if (stage) {
                        if (mc2.player.fallDistance <= 0.1) {
                            mc.getTimer().setTimerSpeed(speed.HypixelTimerValue.get());
                        }
                        if (timer.hasTimePassed(650)) {
                            timer.reset();
                            stage = !stage;
                        }
                    } else {
                        if (mc2.player.fallDistance > 0.1 && mc2.player.fallDistance < 1.3) {
                            mc.getTimer().setTimerSpeed(speed.HypixelDealyTimerValue.get());
                        }
                        if (timer.hasTimePassed(400)) {
                            timer.reset();
                            stage = !stage;
                        }
                    }
                    MovementUtils.strafe();
                }

            }
        }
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}