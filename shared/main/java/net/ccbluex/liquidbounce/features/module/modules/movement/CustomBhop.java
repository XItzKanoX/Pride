package net.ccbluex.liquidbounce.features.module.modules.movement;


import me.utils.ValueUtil;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;

@ModuleInfo(name = "CustomBhop", description = "custom !", category = ModuleCategory.MOVEMENT)
public class CustomBhop extends Module {
    public final ListValue jumpModeSet = new  ListValue("Mode",  new String[] { "Custom", "Legit" }, "Custom");
    public final ListValue speedModeSet = new  ListValue("Mode",  new String[] { "Custom", "Legit", "Ground", "None" }, "Custom");
    public final ListValue airFrictionMode = new  ListValue("Friction", new String[] { "None", "Low", "Normal", "High" }, "Normal");

    public final FloatValue timerSpeedSet = new  FloatValue("Timer Speed", 1, 0, 3);
    public final BoolValue autoJumpSet = new BoolValue("Auto", true);
    public final FloatValue customJumpHeightSet = new  FloatValue("Height", 0.42f, 0.06f, 1.3f);
    public final FloatValue groundSpeedSet = new  FloatValue("Ground Multiplier", 1.5f, 0.0f, 3.0f);
    public final FloatValue speedSet = new  FloatValue("Speed", 0.31f, 0.1f, 2);
    public final FloatValue speedPotionMultSet = new  FloatValue("Potion Multiplier", 0.02f, 0.0f, 0.2f);
    public final FloatValue strafeTicksSet = new  FloatValue("Ticks", 1.0f, 1.0f, 20.0f);
    public final BoolValue stopMotionSet = new  BoolValue("Stop Motion", true);

    double dec = 0.0;
    double speed = 0.0;


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.getTimer().setTimerSpeed(1f);
    }

    @EventTarget
    public void onMove(final MoveEvent e) {
        mc.getTimer().setTimerSpeed(this.timerSpeedSet.getValue());
        final String mode = this.airFrictionMode.get();
        switch (mode) {
            case "Low": {
                this.dec += 0.0021;
                break;
            }
            case "Normal": {
                this.dec += 0.0101;
                break;
            }
            case "High": {
                this.dec += 0.02314;
                break;
            }
            case "None": {
                this.dec = 0.0;
                break;
            }
        }
        if (mc.getThePlayer().getOnGround()) {
            this.dec = 0.0;
            this.speed = this.speedSet.getValue() * this.groundSpeedSet.getValue();
            if (this.autoJumpSet.get()) {
                final String mode2 = this.jumpModeSet.get();
                switch (mode2) {
                    case "Custom": {
                        e.setY(ValueUtil.getModifiedMotionY(this.customJumpHeightSet.getValue()));
                        break;
                    }
                    case "Legit": {
                        e.setY(ValueUtil.getBaseMotionY());
                        break;
                    }
                }
            }
        }
        else {
            this.speed = this.speedSet.getValue();
        }
        this.speed -= this.dec;
        if (mc.getThePlayer().getTicksExisted() % this.strafeTicksSet.getValue() == 0.0 || mc.getThePlayer().getOnGround()) {
            if (MovementUtils.isMoving()) {
                final String mode3 = this.speedModeSet.get();
                switch (mode3) {
                    case "Custom": {
                        MovementUtils.setSpeed1((float)ValueUtil.getMotion(this.speed, this.speedPotionMultSet.getValue()));
                        break;
                    }
                    case "Legit": {
                        MovementUtils.setSpeed1(MovementUtils.getSpeed1());
                        break;
                    }
                    case "Ground": {
                        if (mc.getThePlayer().getOnGround()) {
                            MovementUtils.setSpeed1((float) ValueUtil.getMotion(this.speed, this.speedPotionMultSet.getValue()));
                            break;
                        }
                        break;
                    }
                }
            }
            else if (this.stopMotionSet.get()) {
                mc.getThePlayer().setMotionX(0.0);
                mc.getThePlayer().setMotionZ( 0.0);
            }
        }
    }

}
