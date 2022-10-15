/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.world.Fucker.pos
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.util.math.MathHelper
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "Velocity", description = "Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Vertical", 0F, 0F, 1F)
    private val velocityTickValue = IntegerValue("VelocityTick", 1, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("HuaYuTing", "HuaYuTingJump", "Legit", "aac5.2.0", "AAC5Reduce", "Cancel", "Vulcan","Simple", "Vanilla", "Tick",  "AAC", "AACPush", "AACZero", "AACv4",
        "Reverse", "SmoothReverse", "Jump", "Glitch"), "Simple")

    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val onlyCombatValue = BoolValue("OnlyCombat", false)
    // Reverse
    private val reverseStrengthValue = FloatValue("ReverseStrength", 1F, 0.1F, 1F)
    private val reverse2StrengthValue = FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F)

    // AAC Push
    private val aacPushXZReducerValue = FloatValue("AACPushXZReducer", 2F, 1F, 3F)
    private val aacPushYReducerValue = BoolValue("AACPushYReducer", true)

    // AAc v4
    private val aacv4MotionReducerValue = FloatValue("AACv4MotionReducer", 0.62F,0F,1F)
    private val legitStrafeValue = BoolValue("LegitStrafe", false)
    private val legitFaceValue = BoolValue("LegitFace", true)
    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false
    private var velocityTick = 0
    private var huayutingjumpflag = false
    // SmoothReverse
    private var reverseHurt = false

    // AACPush
    private var jump = false

    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "jump" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                thePlayer.motionY = 0.42

                val yaw = thePlayer.rotationYaw * 0.017453292F

                thePlayer.motionX -= sin(yaw) * 0.2
                thePlayer.motionZ += cos(yaw) * 0.2
            }
            "vulcan" -> {
                if(velocityTick > 10) {
                    if(thePlayer.motionY > 0) thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    thePlayer.jumpMovementFactor = -0.00001f
                    velocityInput = false
                }
                if(thePlayer.onGround && velocityTick > 1) {
                    velocityInput = false
                }
            }
            "tick" -> {
                if(velocityTick > velocityTickValue.get()) {
                    if(thePlayer.motionY > 0) thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    thePlayer.jumpMovementFactor = -0.00001f
                    velocityInput = false
                }
                if(thePlayer.onGround && velocityTick > 1) {
                    velocityInput = false
                }
            }
            "huayuting" -> {
                if(mc.thePlayer!!.hurtTime > 0 && velocityInput) {
                    if(mc.thePlayer!!.onGround) {
                        mc.thePlayer!!.motionX *= (mc.thePlayer!!.motionX * (0.56 * Math.random()))
                        mc.thePlayer!!.motionY *= (mc.thePlayer!!.motionX * (0.77 * Math.random()))
                        mc.thePlayer!!.motionZ *= (mc.thePlayer!!.motionX * (0.56 * Math.random()))
                        mc.thePlayer!!.onGround = false
                    } else {
                        mc.thePlayer!!.motionX *= (mc.thePlayer!!.motionX * (0.77 * Math.random()))
                        mc.thePlayer!!.onGround = true
                        mc.thePlayer!!.motionZ *= (mc.thePlayer!!.motionZ * (0.77 * Math.random()))
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    velocityInput = false
                }
            }
            "glitch" -> {
                thePlayer.noClip = velocityInput

                if (thePlayer.hurtTime == 7)
                    thePlayer.motionY = 0.4

                velocityInput = false
            }
            "aac5reduce" -> {
                if (mc.thePlayer!!.hurtTime> 1 && velocityInput) {
                    mc.thePlayer!!.motionX *= 0.81
                    mc.thePlayer!!.motionZ *= 0.81
                }
                if (velocityInput && (mc.thePlayer!!.hurtTime <5 || mc.thePlayer!!.onGround) && velocityTimer.hasTimePassed(120L)) {
                    velocityInput = false
                }
            }
            "huayutingjump" -> {
                if(mc.thePlayer!!.hurtTime > 0 && huayutingjumpflag) {
                    if(mc.thePlayer!!.onGround){
                        if (mc.thePlayer!!.hurtTime <= 6) {
                            mc.thePlayer!!.motionX *= 0.600151164
                            mc.thePlayer!!.motionZ *= 0.600151164
                        }
                        if (mc.thePlayer!!.hurtTime <= 4) {
                            mc.thePlayer!!.motionX *= 0.700151164
                            mc.thePlayer!!.motionZ *= 0.700151164
                        }
                    }else if(mc.thePlayer!!.hurtTime <= 9) {
                        mc.thePlayer!!.motionX *= 0.6001421204
                        mc.thePlayer!!.motionZ *= 0.6001421204
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    huayutingjumpflag = false
                }
            }
            "reverse" -> {
                if (!velocityInput)
                    return

                if (!thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.speed * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    thePlayer.speedInAir = 0.02F
                    return
                }

                if (thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!thePlayer.onGround) {
                    if (reverseHurt)
                        thePlayer.speedInAir = reverse2StrengthValue.get()
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }

            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                thePlayer.motionX *= horizontalValue.get()
                thePlayer.motionZ *= horizontalValue.get()
                //mc.thePlayer.motionY *= verticalValue.get() ?
                velocityInput = false
            }

            "aacv4" -> {
                if (thePlayer.hurtTime>0 && !thePlayer.onGround){
                    val reduce=aacv4MotionReducerValue.get()
                    thePlayer.motionX *= reduce
                    thePlayer.motionZ *= reduce
                }
            }

            "aacpush" -> {
                if (jump) {
                    if (thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (thePlayer.hurtTime > 0 && thePlayer.motionX != 0.0 && thePlayer.motionZ != 0.0)
                        thePlayer.onGround = true

                    // Reduce Y
                    if (thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                        && !LiquidBounce.moduleManager[Speed::class.java].state)
                        thePlayer.motionY -= 0.014999993
                }

                // Reduce XZ
                if (thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducerValue.get()

                    thePlayer.motionX /= reduce
                    thePlayer.motionZ /= reduce
                }
            }

            "aaczero" -> if (thePlayer.hurtTime > 0) {
                if (!velocityInput || thePlayer.onGround || thePlayer.fallDistance > 2F)
                    return

                thePlayer.motionY -= 1.0
                thePlayer.isAirBorne = true
                thePlayer.onGround = true
            } else
                velocityInput = false
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (classProvider.isSPacketEntityVelocity(packet)) {
            val packetEntityVelocity = packet.asSPacketEntityVelocity()


            if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer)
                return

            velocityTimer.reset()

            when (modeValue.get().toLowerCase()) {
                "vulcan" -> {
                    velocityInput = true
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F) {
                        event.cancelEvent()
                    }
                    packetEntityVelocity.motionX = (packetEntityVelocity.motionX * horizontal).toInt()
                    packetEntityVelocity.motionY = (packetEntityVelocity.motionY * vertical).toInt()
                    packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * horizontal).toInt()
                }

                "huayuting" -> {
                    if(packetEntityVelocity.motionX in -150..150 || packetEntityVelocity.motionY in -150..150 || packetEntityVelocity.motionZ in -150..150){
                        packetEntityVelocity.motionX = (packetEntityVelocity.motionX * 0.0).toInt()
                        packetEntityVelocity.motionY = (packetEntityVelocity.motionY * 0.0).toInt()
                        packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * 0.0).toInt()
                    } else {
                        event.cancelEvent()
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    }

                }
                "legit" -> {
                    pos = WBlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ)
                }
                "huayutingjump" -> {
                    if(packet is SPacketEntityVelocity) {
                        huayutingjumpflag = true

                        if(mc.thePlayer!!.hurtTime != 0) {
                            event.cancelEvent()
                            packet.motionX = 0
                            packet.motionY = 0
                            packet.motionZ = 0
                        }
                    }
                }
                "aac5.2.0" -> {
                    event.cancelEvent()
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, 1.7976931348623157E+308, mc.thePlayer!!.posZ, true))
                }
                "NikoNiko","aac5reduce","aac",  "reverse", "smoothreverse", "aaczero" -> velocityInput = true

                "cancel" -> {
                    event.cancelEvent()
                }
                "vanilla" -> {
                    event.cancelEvent()
                }
                "tick" -> {
                    velocityInput = true

                    event.cancelEvent()

                }
                "simple" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()

                    packetEntityVelocity.motionX = (packetEntityVelocity.motionX * horizontal).toInt()
                    packetEntityVelocity.motionY = (packetEntityVelocity.motionY * vertical).toInt()
                    packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * horizontal).toInt()
                }

                "aac", "reverse", "smoothreverse", "aaczero" -> velocityInput = true

                "glitch" -> {
                    if (!thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }
            }
        } else if (classProvider.isSPacketExplosion(packet)) {
            // TODO: Support velocity for explosions
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if ((onlyGroundValue.get() && !mc.thePlayer!!.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }

        when (modeValue.get().toLowerCase()) {
            "legit" -> {
                if (pos == null || mc.thePlayer!!.hurtTime <= 0) {
                    return
                }

                val rot = RotationUtils.getRotations(pos!!.x.toDouble(), pos!!.y.toDouble(), pos!!.z.toDouble())
                if (legitFaceValue.get()) {
                    RotationUtils.setTargetRotation(rot)
                }
                val yaw = rot.yaw
                if (legitStrafeValue.get()) {
                    val speed = MovementUtils.speed
                    val yaw1 = Math.toRadians(yaw.toDouble())
                    mc.thePlayer!!.motionX = -Math.sin(yaw1) * speed
                    mc.thePlayer!!.motionZ = Math.cos(yaw1) * speed
                } else {
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt(f)

                        if (f < 1.0F) {
                            f = 1.0F
                        }

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                        mc.thePlayer!!.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer!!.motionZ += forward * yawCos + strafe * yawSin
                    }
                }
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "aacpush" -> {
                jump = true

                if (!thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }
            "aaczero" -> if (thePlayer.hurtTime > 0)
                event.cancelEvent()
        }
    }
}
