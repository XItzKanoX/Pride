/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.world;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.enums.BlockType;
import net.ccbluex.liquidbounce.api.enums.EnumFacingType;
import net.ccbluex.liquidbounce.api.enums.StatType;
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock;
import net.ccbluex.liquidbounce.api.minecraft.item.IItemBlock;
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack;
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket;
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction;
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketHeldItemChange;
import net.ccbluex.liquidbounce.api.minecraft.util.*;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.utils.Rotation;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.block.PlaceInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;

import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.timer.TickTimer;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.ccbluex.liquidbounce.utils.RotationUtils.getRotations;

@ModuleInfo(name = "Scaffold", description = "Automatically places blocks beneath your feet.", category = ModuleCategory.WORLD, keyBind = Keyboard.KEY_I)
public class Scaffold extends Module {

    /**
     * OPTIONS (Tower)
     */
    // Global settings
    private final BoolValue towerEnabled = new BoolValue("EnableTower", false);
    private final ListValue towerModeValue = new ListValue("TowerMode", new String[] {
            "Jump", "Motion", "ConstantMotion", "MotionTP", "Packet", "Teleport", "AAC3.3.9", "AAC3.6.4", "Verus"
    }, "Motion");
    private final ListValue towerPlaceModeValue = new ListValue("Tower-PlaceTiming", new String[]{"Pre", "Post"}, "Post");
    private final BoolValue stopWhenBlockAbove = new BoolValue("StopWhenBlockAbove", false);
    private final BoolValue onJumpValue = new BoolValue("OnJump", false);
    private final BoolValue noMoveOnlyValue = new BoolValue("NoMove", true);
    private final FloatValue towerTimerValue = new FloatValue("TowerTimer", 1F, 0.1F, 10F);

    // Jump mode
    private final FloatValue jumpMotionValue = new FloatValue("JumpMotion", 0.42F, 0.3681289F, 0.79F);
    private final IntegerValue jumpDelayValue = new IntegerValue("JumpDelay", 0, 0, 20);

    // ConstantMotion
    private final FloatValue constantMotionValue = new FloatValue("ConstantMotion", 0.42F, 0.1F, 1F);
    private final FloatValue constantMotionJumpGroundValue = new FloatValue("ConstantMotionJumpGround", 0.79F, 0.76F, 1F);

    // Teleport
    private final FloatValue teleportHeightValue = new FloatValue("TeleportHeight", 1.15F, 0.1F, 5F);
    private final IntegerValue teleportDelayValue = new IntegerValue("TeleportDelay", 0, 0, 20);
    private final BoolValue teleportGroundValue = new BoolValue("TeleportGround", true);
    private final BoolValue teleportNoMotionValue = new BoolValue("TeleportNoMotion", false);

    /**
     * OPTIONS (Scaffold)
     */
    // Mode
    public final ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "Rewinside", "Expand"}, "Normal");

    // Delay
    private final BoolValue placeableDelay = new BoolValue("PlaceableDelay", false);
    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 0, 0, 1000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = minDelayValue.get();

            if (i > newValue)
                set(i);
        }
    };

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 0, 0, 1000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = maxDelayValue.get();

            if (i < newValue)
                set(i);
        }
    };

    // idfk what is this
    private final BoolValue smartDelay = new BoolValue("SmartDelay", true);

    // AutoBlock
    private final ListValue autoBlockMode = new ListValue("AutoBlock", new String[]{"Spoof", "Switch", "Matrix", "Off"}, "Spoof");
    private final BoolValue stayAutoBlock = new BoolValue("StayAutoBlock", false);

    //make sprint compatible with tower.add sprint tricks
    public final ListValue sprintModeValue = new ListValue("SprintMode",  new String[]{"Same", "Ground", "Air","PlaceOff"}, "Air");
    // Basic stuff
    public final BoolValue sprintValue = new BoolValue("Sprint", true);
    private final BoolValue swingValue = new BoolValue("Swing", true);
    private final BoolValue downValue = new BoolValue("Down", false);
    private final BoolValue searchValue = new BoolValue("Search", true);
    private final ListValue placeModeValue = new ListValue("PlaceTiming", new String[]{"Pre", "Post"}, "Post");

    // Eagle
    private final BoolValue eagleValue = new BoolValue("Eagle", false);
    private final BoolValue eagleSilentValue = new BoolValue("EagleSilent", false);
    private final IntegerValue blocksToEagleValue = new IntegerValue("BlocksToEagle", 0, 0, 10);
    private final FloatValue eagleEdgeDistanceValue = new FloatValue("EagleEdgeDistance", 0.2F, 0F, 0.5F);

    // Expand
    private final BoolValue omniDirectionalExpand = new BoolValue("OmniDirectionalExpand", true);
    private final IntegerValue expandLengthValue = new IntegerValue("ExpandLength", 5, 1, 6);
    // Other
    // SearchAccuracy
    private final IntegerValue searchAccuracyValue = new IntegerValue("SearchAccuracy", 8, 1, 24) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            if (getMaximum() < newValue) {
                set(getMaximum());
            } else if (getMinimum() > newValue) {
                set(getMinimum());
            }
        }
    };
    private final FloatValue xzRangeValue = new FloatValue("xzRange", 0.8F, 0.1F, 1.0F);
    private final FloatValue yRangeValue = new FloatValue("yRange", 0.8F, 0.1F, 1.0F);
    // Rotations
    private final BoolValue rotationsValue = new BoolValue("Rotations", true);
    private final BoolValue noHitCheckValue = new BoolValue("NoHitCheck", false);
    public final ListValue rotationModeValue = new ListValue("RotationMode", new String[]{"Hypixel", "Normal", "AAC", "Static", "Static2", "Static3", "Custom"}, "Normal"); // searching reason
    public final ListValue rotationLookupValue = new ListValue("RotationLookup", new String[]{"Normal", "AAC", "Same"}, "Normal");


    private final FloatValue maxTurnSpeed = new FloatValue("MaxTurnSpeed", 180F, 0F, 180F) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            final float i = minTurnSpeed.get();

            if (i > newValue)
                set(i);
        }
    };

    private final FloatValue minTurnSpeed = new FloatValue("MinTurnSpeed", 180F, 0F, 180F) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            final float i = maxTurnSpeed.get();

            if (i < newValue)
                set(i);
        }
    };
    private final IntegerValue HypixelYawValue = new IntegerValue("HypixelYaw", 180, -360, 360);
    private final IntegerValue HypixelPitchValue = new IntegerValue("HypixelPitch", 79, 60, 100);
    private final FloatValue staticPitchValue = new FloatValue("Static-Pitch", 86F, 80F, 90F);

    private final FloatValue customYawValue = new FloatValue("Custom-Yaw", 135F, -180F, 180F);
    private final FloatValue customPitchValue = new FloatValue("Custom-Pitch", 86F, -90F, 90F);
    private final BoolValue keepRotOnJumpValue = new BoolValue("KeepRotOnJump", true);

    private final BoolValue keepRotationValue = new BoolValue("KeepRotation", false);
    private final IntegerValue keepLengthValue = new IntegerValue("KeepRotationLength", 0, 0, 20);
    private final ListValue placeConditionValue = new ListValue("Place-Condition", new String[] {"Air", "FallDown", "NegativeMotion", "Always"}, "Always");

    private final BoolValue rotationStrafeValue = new BoolValue("RotationStrafe", false);

    // Zitter
    private final BoolValue zitterValue = new BoolValue("Zitter", false);
    private final ListValue zitterModeValue = new ListValue("ZitterMode", new String[]{"Teleport", "Smooth"}, "Teleport");
    private final FloatValue zitterSpeed = new FloatValue("ZitterSpeed", 0.13F, 0.1F, 0.3F);
    private final FloatValue zitterStrength = new FloatValue("ZitterStrength", 0.072F, 0.05F, 0.2F);
    private final IntegerValue zitterDelay = new IntegerValue("ZitterDelay", 100, 0, 500);

    // Game
    private final FloatValue timerValue = new FloatValue("Timer", 1F, 0.1F, 10F);
    public final FloatValue speedModifierValue = new FloatValue("SpeedModifier", 1F, 0, 2F);
//    public final FloatValue xzMultiplier = new FloatValue("XZ-Multiplier", 1F, 0F, 4F);
    private final BoolValue customSpeedValue = new BoolValue("CustomSpeed", false);
    private final FloatValue customMoveSpeedValue = new FloatValue("CustomMoveSpeed", 0.3F, 0, 5F);

    // Safety
    private final BoolValue sameYValue = new BoolValue("SameY", false);
    private final BoolValue autoJumpValue = new BoolValue("AutoJump", false);
    private final BoolValue smartSpeedValue = new BoolValue("SmartSpeed", false);
    private final BoolValue safeWalkValue = new BoolValue("SafeWalk", true);
    private final BoolValue airSafeValue = new BoolValue("AirSafe", false);
    private final BoolValue autoDisableSpeedValue = new BoolValue("AutoDisable-Speed", true);

    // Visuals
    public final ListValue counterDisplayValue = new ListValue("Counter", new String[]{"Off", "Simple", "Advanced", "Sigma", "Novoline"}, "Simple");

    private final BoolValue markValue = new BoolValue("Mark", false);
    private final IntegerValue redValue = new IntegerValue("Red", 0, 0, 255);
    private final IntegerValue greenValue = new IntegerValue("Green", 120, 0, 255);
    private final IntegerValue blueValue = new IntegerValue("Blue", 255, 0, 255);
    private final IntegerValue alphaValue = new IntegerValue("Alpha", 120, 0, 255);


    /**
     * MODULE
     */

    // Target block
    private PlaceInfo targetPlace, towerPlace;

    // Launch position
    private int launchY;
    private boolean faceBlock;

    // Rotation lock
    private Rotation lockRotation;
    private Rotation lookupRotation;

    // Auto block slot
    private int slot, lastSlot;

    // Zitter Smooth
    private boolean zitterDirection;

    // Delay
    private final MSTimer delayTimer = new MSTimer();
    private final MSTimer zitterTimer = new MSTimer();
    private long delay;

    // Eagle
    private int placedBlocksWithoutEagle = 0;
    private boolean eagleSneaking;

    // Down
    private boolean shouldGoDown = false;

    // Render thingy
    private float progress = 0;
    private long lastMS = 0L;

    // Mode stuff
    private final TickTimer timer = new TickTimer();
    private double jumpGround = 0;
    private int verusState = 0;
    private boolean verusJumped = false;

    public boolean isTowerOnly() {
        return (towerEnabled.get() && !onJumpValue.get());
    }

    public boolean towerActivation() {
        return towerEnabled.get() && (!onJumpValue.get() || mc.getGameSettings().getKeyBindJump().isKeyDown()) && (!noMoveOnlyValue.get() || !MovementUtils.isMoving());
    }

    /**
     * Enable module
     */
    @Override
    public void onEnable() {
        if (mc.getThePlayer() == null) return;

        progress = 0;
        launchY = (int) mc.getThePlayer().getPosY();
        lastSlot = mc.getThePlayer().getInventory().getCurrentItem();
        slot = mc.getThePlayer().getInventory().getCurrentItem();

        if (autoDisableSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState()) {
            LiquidBounce.moduleManager.getModule(Speed.class).setState(false);
            LiquidBounce.hud.addNotification(new Notification( "Scaffold", "Speed is disabled to prevent flags/errors." , NotifyType.INFO, 500, 1000 ));
        }

        faceBlock = false;
        lastMS = System.currentTimeMillis();
    }

    //Send jump packets, bypasses Hypixel.
    private void fakeJump() {
        mc.getThePlayer().setAirBorne(true);
        mc.getThePlayer().triggerAchievement(classProvider.getStatEnum(StatType.JUMP_STAT));
    }

    /**
     * Move player
     */
    private void move(MotionEvent event) {
        switch (towerModeValue.get().toLowerCase()) {
            case "jump":
                if (mc.getThePlayer().getOnGround() && timer.hasTimePassed(jumpDelayValue.get())) {
                    fakeJump();
                    mc.getThePlayer().setMotionY(jumpMotionValue.get());
                    timer.reset();
                }
                break;
            case "motion":
                if (mc.getThePlayer().getOnGround()) {
                    fakeJump();
                    mc.getThePlayer().setMotionY(0.42D);
                } else if (mc.getThePlayer().getMotionY() < 0.1D) mc.getThePlayer().setMotionY(0.3D);
                break;
            case "motiontp":
                if (mc.getThePlayer().getOnGround()) {
                    fakeJump();
                    mc.getThePlayer().setMotionY(0.42D);
                } else if (mc.getThePlayer().getMotionY() < 0.23D)
                    mc.getThePlayer().setPosition(mc.getThePlayer().getPosX(), (int) mc.getThePlayer().getPosY(), mc.getThePlayer().getPosZ());
                break;
            case "packet":
                if (mc.getThePlayer().getOnGround() && timer.hasTimePassed(2)) {
                    fakeJump();
                    mc.getNetHandler().addToSendQueue(classProvider.createCPacketPlayerPosition(mc.getThePlayer().getPosX(),
                            mc.getThePlayer().getPosY() + 0.42D, mc.getThePlayer().getPosZ(), false));
                    mc.getNetHandler().addToSendQueue(classProvider.createCPacketPlayerPosition(mc.getThePlayer().getPosX(),
                            mc.getThePlayer().getPosY() + 0.76D, mc.getThePlayer().getPosZ(), false));
                    mc.getThePlayer().setPosition(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY() + 1.08D, mc.getThePlayer().getPosZ());
                    timer.reset();
                }
                break;
            case "teleport":
                if (teleportNoMotionValue.get())
                    mc.getThePlayer().setMotionY(0);

                if ((mc.getThePlayer().getOnGround() || !teleportGroundValue.get()) && timer.hasTimePassed(teleportDelayValue.get())) {
                    fakeJump();
                    mc.getThePlayer().setPositionAndUpdate(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY() + teleportHeightValue.get(), mc.getThePlayer().getPosZ());
                    timer.reset();
                }
                break;
            case "constantmotion":
                if (mc.getThePlayer().getOnGround()) {
                    fakeJump();
                    jumpGround = mc.getThePlayer().getPosY();
                    mc.getThePlayer().setMotionY(constantMotionValue.get());
                }

                if (mc.getThePlayer().getPosY() > jumpGround + constantMotionJumpGroundValue.get()) {
                    fakeJump();
                    mc.getThePlayer().setPosition(mc.getThePlayer().getPosX(), (int) mc.getThePlayer().getPosY(), mc.getThePlayer().getPosZ());
                    mc.getThePlayer().setMotionY(constantMotionValue.get());
                    jumpGround = mc.getThePlayer().getPosY();
                }
                break;
            case "aac3.3.9":
                if (mc.getThePlayer().getOnGround()) {
                    fakeJump();
                    mc.getThePlayer().setMotionY(0.4001);
                }
                mc.getTimer().setTimerSpeed(1f);

                if (mc.getThePlayer().getMotionY() < 0) {
                    mc.getThePlayer().setMotionY(-0.00000945);
                    mc.getTimer().setTimerSpeed(1.6f);
                }
                break;
            case "aac3.6.4":
                if (mc.getThePlayer().getTicksExisted() % 4 == 1) {
                    mc.getThePlayer().setMotionY(0.4195464);
                    mc.getThePlayer().setPosition(mc.getThePlayer().getPosX() - 0.035, mc.getThePlayer().getPosY(), mc.getThePlayer().getPosZ());
                } else if (mc.getThePlayer().getTicksExisted() % 4 == 0) {
                    mc.getThePlayer().setMotionY(-0.5);
                    mc.getThePlayer().setPosition(mc.getThePlayer().getPosX() + 0.035, mc.getThePlayer().getPosY(), mc.getThePlayer().getPosZ());
                }
                break;
            case "verus": // thanks ratted client
                if (!mc.getTheWorld().getCollidingBoundingBoxes(mc.getThePlayer(), mc.getThePlayer().getEntityBoundingBox().offset(0, -0.01, 0)).isEmpty() && mc.getThePlayer().getOnGround() && mc.getThePlayer().isCollidedVertically()) {
                    verusState = 0;
                    verusJumped = true;
                }
                if (verusJumped) {
                    MovementUtils.strafe();
                    switch (verusState) {
                        case 0:
                            fakeJump();
                            mc.getThePlayer().setMotionY(0.41999998688697815);
                            ++verusState;
                            break;
                        case 1:
                            ++verusState;
                            break;
                        case 2:
                            ++verusState;
                            break;
                        case 3:
                            event.setOnGround(true);
                            mc.getThePlayer().setMotionY(0.0);
                            ++verusState;
                            break;
                        case 4:
                            ++verusState;
                            break;
                    }
                    verusJumped = false;
                }
                verusJumped = true;
                break;
        }
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (towerActivation()) {
            shouldGoDown = false;
            mc.getGameSettings().getKeyBindSneak().setPressed(false);
            mc.getThePlayer().setSprinting(false);
            return;
        }
        if (sprintModeValue.get().equalsIgnoreCase("PlaceOff")) {
            if (mc.getThePlayer().getOnGround());
            mc.getThePlayer().setSprinting(true);
            mc.getThePlayer().setMotionX(mc.getThePlayer().getMotionX() * 1.0);
            mc.getThePlayer().setMotionZ(mc.getThePlayer().getMotionZ() * 1.0);
        }
        mc.getTimer().setTimerSpeed(timerValue.get());
        shouldGoDown = downValue.get() && !sameYValue.get() && mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindSneak()) && getBlocksAmount() > 1;
        if (shouldGoDown)
            mc.getGameSettings().getKeyBindSneak().setPressed(false);

        // scaffold custom speed if enabled
        if (customSpeedValue.get())
            MovementUtils.strafe(customMoveSpeedValue.get());

        if (mc.getThePlayer().getOnGround()) {
            final String mode = modeValue.get();

            // Rewinside scaffold mode
            if (mode.equalsIgnoreCase("Rewinside")) {
                MovementUtils.strafe(0.2F);
                mc.getThePlayer().setMotionY(0D);
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("smooth")) {
                if (mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindRight()))
                    mc.getGameSettings().getKeyBindRight().setPressed(false);

                if (mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindLeft()))
                    mc.getGameSettings().getKeyBindLeft().setPressed(false);

                if (zitterTimer.hasTimePassed(zitterDelay.get())) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }

                if (zitterDirection) {
                    mc.getGameSettings().getKeyBindRight().setPressed(true);
                    mc.getGameSettings().getKeyBindLeft().setPressed(false);
                } else {
                    mc.getGameSettings().getKeyBindRight().setPressed(false);
                    mc.getGameSettings().getKeyBindLeft().setPressed(true);
                }
            }

            // Eagle
            if (eagleValue.get() && !shouldGoDown) {
                double dif = 0.5D;
                if (eagleEdgeDistanceValue.get() > 0) {
                    for (int i = 0; i < 4; i++) {
                        final WBlockPos WBlockPos = new WBlockPos(mc.getThePlayer().getPosX() + (i == 0 ? (-1) : i == 1 ? 1 : 0), mc.getThePlayer().getPosY() - (mc.getThePlayer().getPosY() == (int) mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D), mc.getThePlayer().getPosZ() + (i == 2 ? -1 : i == 3 ? 1 : 0));
                        final PlaceInfo placeInfo = PlaceInfo.get(WBlockPos);
                        if (BlockUtils.isReplaceable(WBlockPos) && placeInfo != null) {
                            double calcDif = i > 1 ? mc.getThePlayer().getPosZ() - WBlockPos.getZ() : mc.getThePlayer().getPosX() - WBlockPos.getX();
                            calcDif -= 0.5D;

                            if (calcDif < 0)
                                calcDif *= -1;
                            calcDif -= 0.5D;

                            if (calcDif < dif)
                                dif = calcDif;
                        }
                    }
                }
                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    final boolean shouldEagle = mc.getTheWorld().getBlockState(
                            new WBlockPos(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY() - 1D, mc.getThePlayer().getPosZ())).getBlock().equals(classProvider.getBlockEnum(BlockType.AIR))  || dif < eagleEdgeDistanceValue.get();

                    if (eagleSilentValue.get()) {
                        if (eagleSneaking != shouldEagle) {
                            mc.getNetHandler().addToSendQueue(
                                    classProvider.createCPacketEntityAction(mc.getThePlayer(), shouldEagle ?
                                            ICPacketEntityAction.WAction.START_SNEAKING :
                                            ICPacketEntityAction.WAction.STOP_SNEAKING)
                            );
                        }

                        eagleSneaking = shouldEagle;
                    } else
                        mc.getGameSettings().getKeyBindSneak().setPressed(shouldEagle);

                    placedBlocksWithoutEagle = 0;
                } else
                    placedBlocksWithoutEagle++;
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("teleport")) {
                MovementUtils.strafe(zitterSpeed.get());


                final double yaw = Math.toRadians(mc.getThePlayer().getRotationYaw() + (zitterDirection ? 90D : -90D));
                mc.getThePlayer().setMotionX(- Math.sin(yaw) * zitterStrength.get());
                mc.getThePlayer().setMotionZ(Math.cos(yaw) * zitterStrength.get());
                zitterDirection = !zitterDirection;
            }
        }

        if (sprintModeValue.get().equalsIgnoreCase("off") || (sprintModeValue.get().equalsIgnoreCase("ground") && !mc.getThePlayer().getOnGround()) || (sprintModeValue.get().equalsIgnoreCase("air") && mc.getThePlayer().getOnGround())) {
            mc.getThePlayer().setSprinting(true);
        }

        //Auto Jump thingy
        if (shouldGoDown) {
            launchY = (int) mc.getThePlayer().getPosY() - 1;
        } else if (!sameYValue.get()) {
            if ((!autoJumpValue.get() && !(smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState())) || mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindJump()) || mc.getThePlayer().getPosY() < launchY) launchY = (int) mc.getThePlayer().getPosY();
            if (autoJumpValue.get() && !LiquidBounce.moduleManager.getModule(Speed.class).getState() && MovementUtils.isMoving() && mc.getThePlayer().getOnGround()) {
                mc.getThePlayer().jump();
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.getThePlayer() == null)
            return;

        final IPacket packet = event.getPacket();

        // AutoBlock
        if (classProvider.isCPacketHeldItemChange(packet)) {
            final ICPacketHeldItemChange packetHeldItemChange = packet.asCPacketHeldItemChange();

            slot = packetHeldItemChange.getSlotId();
        }
    }

    @EventTarget
    //took it from applyrotationstrafe XD. staticyaw comes from bestnub.
    public void onStrafe(final StrafeEvent event) {
        if (lookupRotation != null && rotationStrafeValue.get()) {
            final int dif = (int) ((MathHelper.wrapDegrees(mc.getThePlayer().getRotationYaw() - lookupRotation.getYaw() - 23.5F - 135) + 180) / 45);

            final float yaw = lookupRotation.getYaw();
            final float strafe = event.getStrafe();
            final float forward = event.getForward();
            final float friction = event.getFriction();
            float calcForward = 0F;
            float calcStrafe = 0F;
            /*
            Rotation Dif

            7 \ 0 / 1     +  +  +      +  |  -
            6   +   2     -- F --      +  S  -
            5 / 4 \ 3     -  -  -      +  |  -
            */
            switch (dif) {
                case 0: {
                    calcForward = forward;
                    calcStrafe = strafe;
                    break;
                }
                case 1: {
                    calcForward += forward;
                    calcStrafe -= forward;
                    calcForward += strafe;
                    calcStrafe += strafe;
                    break;
                }
                case 2: {
                    calcForward = strafe;
                    calcStrafe = -forward;
                    break;
                }
                case 3: {
                    calcForward -= forward;
                    calcStrafe -= forward;
                    calcForward += strafe;
                    calcStrafe -= strafe;
                    break;
                }
                case 4: {
                    calcForward = -forward;
                    calcStrafe = -strafe;
                    break;
                }
                case 5: {
                    calcForward -= forward;
                    calcStrafe += forward;
                    calcForward -= strafe;
                    calcStrafe -= strafe;
                    break;
                }
                case 6: {
                    calcForward = -strafe;
                    calcStrafe = forward;
                    break;
                }
                case 7: {
                    calcForward += forward;
                    calcStrafe += forward;
                    calcForward -= strafe;
                    calcStrafe += strafe;
                    break;
                }
            }

            if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
                calcForward *= 0.5f;
            }

            if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
                calcStrafe *= 0.5f;
            }

            float f = calcStrafe * calcStrafe + calcForward * calcForward;

            if (f >= 1.0E-4F) {
                f = MathHelper.sqrt(f);

                if (f < 1.0F)
                    f = 1.0F;

                f = friction / f;
                calcStrafe *= f;
                calcForward *= f;

                final float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180F));
                final float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180F));

                mc.getThePlayer().setMotionX(calcStrafe * yawCos - calcForward * yawSin);
                mc.getThePlayer().setMotionZ(calcForward * yawCos + calcStrafe * yawSin);
            }
            event.cancelEvent();
        }
    }

    private boolean shouldPlace() {
        boolean placeWhenAir = placeConditionValue.get().equalsIgnoreCase("air");
        boolean placeWhenFall = placeConditionValue.get().equalsIgnoreCase("falldown");
        boolean placeWhenNegativeMotion = placeConditionValue.get().equalsIgnoreCase("negativemotion");
        boolean alwaysPlace = placeConditionValue.get().equalsIgnoreCase("always");
        return towerActivation() || alwaysPlace || (placeWhenAir && !mc.getThePlayer().getOnGround()) || (placeWhenFall && mc.getThePlayer().getFallDistance() > 0) || (placeWhenNegativeMotion && mc.getThePlayer().getMotionY() < 0);
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        // XZReducer
//        mc.getThePlayer().setMotionX(xzMultiplier.get());
//        mc.getThePlayer().setMotionZ(xzMultiplier.get());

        // Lock Rotation
        if (rotationsValue.get() && keepRotationValue.get() && lockRotation != null)
            RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation, lockRotation,  RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())));

        final String mode = modeValue.get();
        final EventState eventState = event.getEventState();

        // i think patches should be here instead
//        for (int i = 0; i < 8; i++) {
//            if (mc.getThePlayer().getInventory().getMainInventory()[i] != null
//                    && mc.getThePlayer().getInventory().getMainInventory()[i].stackSize <= 0)
//                mc.getThePlayer().getInventory().getMainInventory()[i] = null;
//        }

        if ((!rotationsValue.get() || noHitCheckValue.get() || faceBlock) && placeModeValue.get().equalsIgnoreCase(eventState.getStateName()) && !towerActivation()) {
            place(false);
        }

        if (eventState == EventState.PRE && !towerActivation()) {
            if (!shouldPlace() || (!autoBlockMode.get().equalsIgnoreCase("Off") ? InventoryUtils.findAutoBlockBlock() == -1 : mc.getThePlayer().getHeldItem() == null ||
                    !(mc.getThePlayer().getHeldItem().getItem() instanceof ItemBlock)))
                return;

            findBlock(mode.equalsIgnoreCase("expand") && !towerActivation());
        }

        if (targetPlace == null) {
            if (placeableDelay.get())
                delayTimer.reset();
        }

        if (!towerActivation()) {
            verusState = 0;
            towerPlace = null;
            return;
        }

        mc.getTimer().setTimerSpeed(towerTimerValue.get());

        if (towerPlaceModeValue.get().equalsIgnoreCase(eventState.getStateName())) place(true);

        if (eventState == EventState.PRE) {
            towerPlace = null;
            timer.update();

            final boolean isHeldItemBlock = mc.getThePlayer().getHeldItem() != null && mc.getThePlayer().getHeldItem().getItem() instanceof ItemBlock;
            if (InventoryUtils.findAutoBlockBlock() != -1 || isHeldItemBlock) {
                launchY = (int)mc.getThePlayer().getPosY();

                if (towerModeValue.get().equalsIgnoreCase("verus") || !stopWhenBlockAbove.get() || BlockUtils.getBlock(new WBlockPos(mc.getThePlayer().getPosX(),
                        mc.getThePlayer().getPosY() + 2, mc.getThePlayer().getPosZ())) instanceof BlockAir)
                    move(event);

                final WBlockPos WBlockPos = new WBlockPos(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY() - 1D, mc.getThePlayer().getPosZ());
                if (mc.getTheWorld().getBlockState(WBlockPos).getBlock() instanceof BlockAir) {
                    if (search(WBlockPos, true, true) && rotationsValue.get()) {
                        final VecRotation vecRotation = RotationUtils.faceBlock(WBlockPos);

                        if (vecRotation != null) {
                            RotationUtils.setTargetRotation(RotationUtils.limitAngleChange(RotationUtils.serverRotation, vecRotation.getRotation(),  RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())));
                            towerPlace.setVec3(vecRotation.getVec());
                        }
                    }
                }
            }
        }
    }

    /**
     * Search for new target block
     */
    private void findBlock(final boolean expand) {
        final WBlockPos WBlockPosition = shouldGoDown ? (mc.getThePlayer().getPosY() == (int) mc.getThePlayer().getPosY() + 0.5D ? new WBlockPos(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY() - 0.6D, mc.getThePlayer().getPosZ())
                : new WBlockPos(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY() - 0.6, mc.getThePlayer().getPosZ()).down()) :
                (!towerActivation() && (sameYValue.get() || ((autoJumpValue.get() || (smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState())) && mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindJump()))) && launchY <= mc.getThePlayer().getPosY() ? (new WBlockPos(mc.getThePlayer().getPosX(), launchY - 1, mc.getThePlayer().getPosZ())) :
                        (mc.getThePlayer().getPosY() == (int) mc.getThePlayer().getPosY() + 0.5D ? new WBlockPos(mc.getThePlayer())
                                : new WBlockPos(mc.getThePlayer().getPosX(), mc.getThePlayer().getPosY(), mc.getThePlayer().getPosZ()).down()));

        if (!expand && (!BlockUtils.isReplaceable(WBlockPosition) || search(WBlockPosition, !shouldGoDown, false)))
            return;

        if (expand) {
            double yaw = Math.toRadians(mc.getThePlayer().getRotationYaw());
            int x = omniDirectionalExpand.get() ? (int) Math.round(-Math.sin(yaw)) : mc.getThePlayer().getHorizontalFacing().getDirectionVec().getX();
            int z = omniDirectionalExpand.get() ? (int) Math.round(Math.cos(yaw)) : mc.getThePlayer().getHorizontalFacing().getDirectionVec().getZ();

            for (int i = 0; i < expandLengthValue.get(); i++) {
                if (search(WBlockPosition.add(x * i, 0, z * i), false, false))
                    return;
            }
        } else if (searchValue.get()) {
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(WBlockPosition.add(x, 0, z), !shouldGoDown, false))
                        return;
        }
    }

    /**
     * Place target block
     */
    private void place(boolean towerActive) {
        if (sprintModeValue.get().equalsIgnoreCase("PlaceOff")) {
            mc.getThePlayer().setSprinting(false);
            mc.getThePlayer().setMotionX(mc.getThePlayer().getMotionX() * 1.0);
            mc.getThePlayer().setMotionZ(mc.getThePlayer().getMotionZ() * 1.0);
        }
        if ((towerActive ? towerPlace : targetPlace) == null) {
            if (placeableDelay.get())
                delayTimer.reset();
            return;
        }

        if (!towerActivation() && (!delayTimer.hasTimePassed(delay) || (smartDelay.get() && mc.getRightClickDelayTimer() > 0) || ((sameYValue.get() || ((autoJumpValue.get() || (smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState())) && mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindJump()))) && launchY - 1 != (int) (towerActive ? towerPlace : targetPlace).getVec3().getYCoord())))
            return;

        int blockSlot = -1;
        IItemStack itemStack = mc.getThePlayer().getHeldItem();

        if (mc.getThePlayer().getHeldItem() == null || !(mc.getThePlayer().getHeldItem().getItem() instanceof ItemBlock)) {
            if (autoBlockMode.get().equalsIgnoreCase("Off"))
                return;

            blockSlot = InventoryUtils.findAutoBlockBlock();

            if (blockSlot == -1)
                return;


            if (autoBlockMode.get().equalsIgnoreCase("Matrix") && blockSlot - 36 != this.slot) {
                mc.getNetHandler().addToSendQueue(classProvider.createCPacketHeldItemChange(blockSlot - 36));
            }

            if (autoBlockMode.get().equalsIgnoreCase("Spoof")) {
                mc.getNetHandler().addToSendQueue( classProvider.createCPacketHeldItemChange(blockSlot - 36));
                itemStack = mc.getThePlayer().getInventoryContainer().getSlot(blockSlot).getStack();
            } else {
                mc.getThePlayer().getInventory().setCurrentItem(blockSlot - 36);
                mc.getPlayerController().updateController();
            }
        }

        // blacklist check
        if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemBlock) {
            final IItemBlock itemBlock = itemStack.getItem().asItemBlock();
            final IBlock block = itemBlock.getBlock();
            if (InventoryUtils.BLOCK_BLACKLIST.contains(block) || !block.isFullCube(block.getDefaultState())  || itemStack.getStackSize() <= 0) return;
        }

        if (mc.getPlayerController().onPlayerRightClick(mc.getThePlayer(),mc.getTheWorld(), itemStack, (towerActive ? towerPlace : targetPlace).getBlockPos(),
                (towerActive ? towerPlace : targetPlace).getEnumFacing(), (towerActive ? towerPlace : targetPlace).getVec3())) {
            delayTimer.reset();
            delay = (!placeableDelay.get() ? 0L : TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get()));

            if (mc.getThePlayer().getOnGround()) {
                final float modifier = speedModifierValue.get();


                mc.getThePlayer().setMotionX(mc.getThePlayer().getMotionX() * modifier);
                mc.getThePlayer().setMotionZ(mc.getThePlayer().getMotionZ() * modifier);
            }

            if (swingValue.get())
                mc.getThePlayer().swingItem();
            else
                mc.getNetHandler().addToSendQueue(classProvider.createCPacketAnimation());
        }

        // Reset
        if (towerActive)
            this.towerPlace = null;
        else
            this.targetPlace = null;

        if (!stayAutoBlock.get() && blockSlot >= 0 && !autoBlockMode.get().equalsIgnoreCase("Switch"))
            mc.getNetHandler().addToSendQueue(classProvider.createCPacketHeldItemChange(mc.getThePlayer().getInventory().getCurrentItem()));
    }


    /**
     * Disable scaffold module
     */
    @Override
    public void onDisable() {
        if (mc.getThePlayer() == null) return;

        if (mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindSneak())) {
            mc.getGameSettings().getKeyBindSneak().setPressed(false);

            if (eagleSneaking)
                mc.getNetHandler().addToSendQueue(classProvider.createCPacketEntityAction(mc.getThePlayer(), ICPacketEntityAction.WAction.STOP_SNEAKING));
        }

        if (mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindRight()))
            mc.getGameSettings().getKeyBindRight().setPressed(false);

        if (mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindLeft()))
            mc.getGameSettings().getKeyBindLeft().setPressed(false);

        lockRotation = null;
        lookupRotation = null;
        mc.getTimer().setTimerSpeed(1f);
        shouldGoDown = false;
        faceBlock = false;

        if (lastSlot != mc.getThePlayer().getInventory().getCurrentItem() && autoBlockMode.get().equalsIgnoreCase("switch")) {
            mc.getThePlayer().getInventory().setCurrentItem(lastSlot);
            mc.getPlayerController().updateController();
        }

        if (slot != mc.getThePlayer().getInventory().getCurrentItem() && autoBlockMode.get().equalsIgnoreCase("spoof"))
            mc.getNetHandler().addToSendQueue(classProvider.createCPacketHeldItemChange(mc.getThePlayer().getInventory().getCurrentItem()));
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    public void onMove(final MoveEvent event) {
        if (!safeWalkValue.get() || shouldGoDown)
            return;

        if (airSafeValue.get() || mc.getThePlayer().getOnGround())
            event.setSafeWalk(true);
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if (towerActivation())
            event.cancelEvent();
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        progress = (float) (System.currentTimeMillis() - lastMS) / 100F;
        if (progress >= 1) progress = 1;

        String counterMode = counterDisplayValue.get();
        final IScaledResolution scaledResolution = classProvider.createScaledResolution(mc);

        final String info = getBlocksAmount() + " blocks";
        int infoWidth = Fonts.font25.getStringWidth(info);
        int infoWidth2 = Fonts.minecraftFont.getStringWidth(getBlocksAmount()+"");
        if (counterMode.equalsIgnoreCase("simple")) {
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) - 1, scaledResolution.getScaledHeight() / 2 - 36, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) + 1, scaledResolution.getScaledHeight() / 2 - 36, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 35, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 37, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount()+"", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 36, -1, false);
        }
        if (counterMode.equalsIgnoreCase("advanced")) {
            boolean canRenderStack = (slot >= 0 && slot < 9 && mc.getThePlayer().getInventory().getMainInventory().get(slot) != null && mc.getThePlayer().getInventory().getMainInventory().get(slot).getItem() != null && mc.getThePlayer().getInventory().getMainInventory().get(slot).getItem() instanceof ItemBlock);

            RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 40, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 39, (getBlocksAmount() > 1 ? 0xFFFFFFFF : 0xFFFF1010));
            RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 39, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 26, 0xA0000000);

            if (canRenderStack) {
                RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 8, scaledResolution.getScaledHeight() / 2 - 25, scaledResolution.getScaledWidth() / 2 - 8);
                renderItemStack(mc.getThePlayer().getInventory().getMainInventory().get(slot), 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.font25.drawCenteredString(info, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 - 36, -1);
        }

        if (counterMode.equalsIgnoreCase("sigma")) {
            GlStateManager.translate(0, -14F - (progress * 4F), 0);
            //GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(0.15F, 0.15F, 0.15F, progress);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2 - 3, scaledResolution.getScaledHeight() - 60);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() - 57);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2 + 3, scaledResolution.getScaledHeight() - 60);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            //GL11.glPopMatrix();
            RenderUtils.drawRoundedRect((float) scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, (float) scaledResolution.getScaledHeight() - 60, (float) scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, (float) scaledResolution.getScaledHeight() - 74, (int) 2F, new Color(0.15F, 0.15F, 0.15F, progress).getRGB());
            GlStateManager.resetColor();
            Fonts.font25.drawCenteredString(info, scaledResolution.getScaledWidth() / 2 + 0.1F, scaledResolution.getScaledHeight() - 70, new Color(1F, 1F, 1F, 0.8F * progress).getRGB(), false);
            GlStateManager.translate(0, 14F + (progress * 4F), 0);
        }

        if (counterMode.equalsIgnoreCase("novoline")) {
            if (slot >= 0 && slot < 9 && mc.getThePlayer().getInventory().getMainInventory().get(slot) != null && mc.getThePlayer().getInventory().getMainInventory().get(slot).getItem() != null && mc.getThePlayer().getInventory().getMainInventory().get(slot).getItem() instanceof ItemBlock) {
                //RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 22, scaledResolution.getScaledHeight() / 2 + 16, scaledResolution.getScaledWidth() / 2 - 22);
                renderItemStack(mc.getThePlayer().getInventory().getMainInventory().get(slot), 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.minecraftFont.drawString(getBlocksAmount()+" blocks", scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 20, -1, true);
        }
    }

    private void renderItemStack(IItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.getFontRendererObj(), stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (!markValue.get())
            return;

        double yaw = Math.toRadians(mc.getThePlayer().getRotationYaw());
        int x = omniDirectionalExpand.get() ? (int) Math.round(-Math.sin(yaw)) : mc.getThePlayer().getHorizontalFacing().getDirectionVec().getX();
        int z = omniDirectionalExpand.get() ? (int) Math.round(Math.cos(yaw)) : mc.getThePlayer().getHorizontalFacing().getDirectionVec().getZ();

        for (int i = 0; i < ((modeValue.get().equalsIgnoreCase("Expand") && !towerActivation()) ? expandLengthValue.get() + 1 : 2); i++) {
            final WBlockPos WBlockPos = new WBlockPos(
                    mc.getThePlayer().getPosX() + x * i,
                    (!towerActivation()
                            && (sameYValue.get() ||
                            ((autoJumpValue.get() ||
                                    (smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState()))
                                    && mc.getGameSettings().isKeyDown(mc.getGameSettings().getKeyBindJump())))
                            && launchY <= mc.getThePlayer().getPosY()) ? launchY - 1 : (mc.getThePlayer().getPosY() - (mc.getThePlayer().getPosY() == (int) mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D) - (shouldGoDown ? 1D : 0)),
                    mc.getThePlayer().getPosZ() + z * i);
            final PlaceInfo placeInfo = PlaceInfo.get(WBlockPos);

            if (BlockUtils.isReplaceable(WBlockPos) && placeInfo != null) {
                RenderUtils.drawBlockBox(WBlockPos, new Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()), false);
                break;
            }
        }
    }
    private double calcStepSize(double range) {
        double accuracy = searchAccuracyValue.get();
        accuracy += accuracy % 2; // If it is set to uneven it changes it to even. Fixes a bug
        if (range / accuracy < 0.01D)
            return 0.01D;
        return range / accuracy;
    }
    private boolean search(final WBlockPos WBlockPosition, final boolean checks) {
        return search(WBlockPosition, checks, false);
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private boolean search(final WBlockPos blockPosition, final boolean checks, boolean towerActive) {
        faceBlock = false;
        // SearchRanges
        final double xzRV = xzRangeValue.get();
        final double xzSSV = calcStepSize(xzRV);
        final double yRV = yRangeValue.get();
        final double ySSV = calcStepSize(yRV);

        double xSearchFace = 0;
        double ySearchFace = 0;
        double zSearchFace = 0;
        if (!BlockUtils.isReplaceable(blockPosition))
            return false;


        final boolean staticYawMode = rotationLookupValue.get().equalsIgnoreCase("AAC")
                || (rotationLookupValue.get().equalsIgnoreCase("same") && (rotationModeValue.get().equalsIgnoreCase("AAC")
                || (rotationModeValue.get().contains("Static") && !rotationModeValue.get().equalsIgnoreCase("static3"))));

        final WVec3 eyesPos = new WVec3(mc.getThePlayer().getPosX(), mc.getThePlayer().getEntityBoundingBox().getMinY() + mc.getThePlayer().getEyeHeight(), mc.getThePlayer().getPosZ());

        PlaceRotation placeRotation = null;

        for (final EnumFacingType facingType : EnumFacingType.values()) {
            IEnumFacing side = classProvider.getEnumFacing(facingType);
            final WBlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtils.canBeClicked(neighbor))
                continue;

            final WVec3 dirVec = new WVec3(side.getDirectionVec());

            for (double xSearch = 0.5D - (xzRV / 2); xSearch <= 0.5D + (xzRV / 2); xSearch += xzSSV) {
                for (double ySearch = 0.5D - (yRV / 2); ySearch <= 0.5D + (yRV / 2); ySearch += ySSV) {
                    for (double zSearch = 0.5D - (xzRV / 2); zSearch <= 0.5D + (xzRV / 2); zSearch += xzSSV) {
                        final WVec3 posVec = new WVec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final WVec3 hitVec = posVec.add(new WVec3(dirVec.getXCoord() * 0.5, dirVec.getYCoord() * 0.5, dirVec.getZCoord() * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18D || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || mc.getTheWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;

                        // face block
                        for (int i = 0; i < (staticYawMode ? 2 : 1); i++) {
                            final double diffX = staticYawMode && i == 0 ? 0 : hitVec.getXCoord() - eyesPos.getXCoord();
                            final double diffY = hitVec.getYCoord() - eyesPos.getYCoord();
                            final double diffZ = staticYawMode && i == 1 ? 0 : hitVec.getZCoord() - eyesPos.getZCoord();

                            final double diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

                            Rotation rotation = new Rotation(
                                    MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                                    MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                            );

                            lookupRotation = rotation;

                            if(rotationModeValue.get().equalsIgnoreCase("hypixel") && (keepRotOnJumpValue.get() || !mc.getGameSettings().getKeyBindJump().isKeyDown()))
                                rotation = new Rotation(mc.getThePlayer().getRotationYaw() + ((mc.getThePlayer().getMovementInput().getMoveForward() > 0) ? 180 : 0) + HypixelYawValue.get(), HypixelPitchValue.get());

                            if (rotationModeValue.get().equalsIgnoreCase("static") && (keepRotOnJumpValue.get() || !mc.getGameSettings().getKeyBindJump().isKeyDown()))
                                rotation = new Rotation(MovementUtils.getScaffoldRotation(mc.getThePlayer().getRotationYaw(), mc.getThePlayer().getMoveForward()), staticPitchValue.get());

                            if ((rotationModeValue.get().equalsIgnoreCase("static2") || rotationModeValue.get().equalsIgnoreCase("static3")) && (keepRotOnJumpValue.get() || !mc.getGameSettings().getKeyBindJump().isKeyDown()))
                                rotation = new Rotation(rotation.getYaw(), staticPitchValue.get());

                            if (rotationModeValue.get().equalsIgnoreCase("custom") && (keepRotOnJumpValue.get() || !mc.getGameSettings().getKeyBindJump().isKeyDown()))
                                rotation = new Rotation(mc.getThePlayer().getRotationYaw() + customYawValue.get(), customPitchValue.get());

                            final WVec3 rotationVector = RotationUtils.getVectorForRotation(rotationLookupValue.get().equalsIgnoreCase("same") ? rotation : lookupRotation);
                            final WVec3 vector = eyesPos.addVector(rotationVector.getXCoord() * 4, rotationVector.getYCoord() * 4, rotationVector.getZCoord() * 4);
                            final IMovingObjectPosition obj = mc.getTheWorld().rayTraceBlocks(eyesPos, vector, false, false, true);

                            if (!(obj.getTypeOfHit() == IMovingObjectPosition.WMovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                                continue;

                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(placeRotation.getRotation()))
                                placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                        }
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        if (rotationsValue.get()) {
            if (minTurnSpeed.get() < 180) {
                final Rotation limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, placeRotation.getRotation(), RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get()));
                if ((int)(10 * MathHelper.wrapDegrees(limitedRotation.getYaw())) == (int)(10 * MathHelper.wrapDegrees(placeRotation.getRotation().getYaw()))
                        && (int)(10 * MathHelper.wrapDegrees(limitedRotation.getPitch())) == (int)(10 * MathHelper.wrapDegrees(placeRotation.getRotation().getPitch()))) {
                    RotationUtils.setTargetRotation(placeRotation.getRotation(), keepLengthValue.get());
                    lockRotation = placeRotation.getRotation();
                    faceBlock = true;
                } else {
                    RotationUtils.setTargetRotation(limitedRotation, keepLengthValue.get());
                    lockRotation = limitedRotation;
                    faceBlock = false;
                }
            } else {
                RotationUtils.setTargetRotation(placeRotation.getRotation(), keepLengthValue.get());
                lockRotation = placeRotation.getRotation();
                faceBlock = true;
            }

            if (rotationLookupValue.get().equalsIgnoreCase("same"))
                lookupRotation = lockRotation;
        }

        if (towerActive)
            towerPlace = placeRotation.getPlaceInfo();
        else
            targetPlace = placeRotation.getPlaceInfo();

        return true;
    }

    /**
     * @return hotbar blocks amount
     */
    private int getBlocksAmount() {
        int amount = 0;
        for (int i = 36; i < 45; i++) {
            final IItemStack itemStack = mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();

            if (itemStack != null && classProvider.isItemBlock(itemStack.getItem())) {
                final IBlock block = (itemStack.getItem().asItemBlock()).getBlock();

                IItemStack heldItem = mc.getThePlayer().getHeldItem();

                if (heldItem != null && heldItem.equals(itemStack) || !InventoryUtils.BLOCK_BLACKLIST.contains(block) && !classProvider.isBlockBush(block))
                    amount += itemStack.getStackSize();
            }
        }

        return amount;
    }

    @Override
    public String getTag() {
        return (towerActivation()) ? "Tower, " + towerPlaceModeValue.get() : placeModeValue.get();
    }
}