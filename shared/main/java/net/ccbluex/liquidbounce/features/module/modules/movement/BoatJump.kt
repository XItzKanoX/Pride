
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketUseEntity
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.item.EntityBoat
import net.minecraft.network.play.client.CPacketInput

import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "BoatJump", category = ModuleCategory.MOVEMENT, description = "nnn")
class BoatJump : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Boost", "Launch", "Matrix"), "Boost")
    private val hBoostValue = FloatValue("HBoost", 2f, 0f, 6f)
    private val vBoostValue = FloatValue("VBoost", 2f, 0f, 6f)
    private val matrixTimerStartValue = FloatValue("MatrixTimerStart", 0.3f, 0.1f, 1f)
    private val matrixTimerAirValue = FloatValue("MatrixTimerAir", 0.5f, 0.1f, 1.5f)
    private val launchRadiusValue = FloatValue("LaunchRadius", 4F, 3F, 10F)
    private val delayValue = IntegerValue("Delay", 200, 100, 500)
    private val autoHitValue = BoolValue("AutoHit", true)

    private var jumpState = 1
    private val timer = MSTimer()
    private val hitTimer = MSTimer()
    private var lastRide = false
    private var hasStopped = false

    override fun onEnable() {
        jumpState = 1
        lastRide = false
    }

    override fun onDisable() {
        hasStopped = false
        mc.timer.timerSpeed = 1f
        mc.thePlayer!!.speedInAir = 0.02f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        // println(mc.timer.timerSpeed)
        if (mc.thePlayer!!.onGround && !mc.thePlayer!!.isRiding) {
            hasStopped = false
            mc.timer.timerSpeed = 1f
            mc.thePlayer!!.speedInAir = 0.02f
        }

        when (modeValue.get().toLowerCase()) {
            "matrix" -> {
                if (hasStopped) {
                    mc.timer.timerSpeed = matrixTimerAirValue.get()
                } else {
                    mc.timer.timerSpeed = 1f
                }
            }
        }

        if (mc.thePlayer!!.isRiding && jumpState == 1) {
            if (!lastRide) {
                timer.reset()
            }

            if (timer.hasTimePassed(delayValue.get().toLong())) {
                jumpState = 2
                when (modeValue.get().toLowerCase()) {
                    "matrix" -> {
                        mc.timer.timerSpeed = matrixTimerStartValue.get()
//                        mc.netHandler.addToSendQueue(
                        mc2.connection!!.sendPacket(
                            CPacketInput(
                                mc.thePlayer!!.moveStrafing,
                                mc.thePlayer!!.moveForward,
                                false,
                                true
                            )
                        )
                    }
                    else -> {
                        mc2.connection!!.sendPacket(
                            CPacketInput(
                                mc.thePlayer!!.moveStrafing,
                                mc.thePlayer!!.moveForward,
                                false,
                                true
                            )
                        )
                    }
                }
            }
        } else if (jumpState == 2 && !mc.thePlayer!!.isRiding) {
            val radiansYaw = mc.thePlayer!!.rotationYaw * Math.PI / 180

            when (modeValue.get().toLowerCase()) {
                "boost" -> {
                    mc.thePlayer!!.motionX = hBoostValue.get() * -sin(radiansYaw)
                    mc.thePlayer!!.motionZ = hBoostValue.get() * cos(radiansYaw)
                    mc.thePlayer!!.motionY = vBoostValue.get().toDouble()
                    jumpState = 1
                }
                "launch" -> {
                    mc.thePlayer!!.motionX += (hBoostValue.get() * 0.1) * -sin(radiansYaw)
                    mc.thePlayer!!.motionZ += (hBoostValue.get() * 0.1) * cos(radiansYaw)
                    mc.thePlayer!!.motionY += vBoostValue.get() * 0.1

                    var hasBoat = false
                    for (entity in mc.theWorld!!.loadedEntityList) {
                        if (classProvider.isEntityBoat(entity) && mc.thePlayer!!.getDistanceToEntity(entity) < launchRadiusValue.get()) {
                            hasBoat = true
                            break
                        }
                    }
                    if (!hasBoat) {
                        jumpState = 1
                    }
                }
                "matrix" -> {
                    hasStopped = true
                    mc.timer.timerSpeed = matrixTimerAirValue.get()
                    mc.thePlayer!!.motionX = hBoostValue.get() * -sin(radiansYaw)
                    mc.thePlayer!!.motionZ = hBoostValue.get() * cos(radiansYaw)
                    mc.thePlayer!!.motionY = vBoostValue.get().toDouble()
                    jumpState = 1
                }
            }

            timer.reset()
            hitTimer.reset()
        }

        lastRide = mc.thePlayer!!.isRiding

        if (autoHitValue.get() && !mc.thePlayer!!.isRiding && hitTimer.hasTimePassed(1500)) {
            for (entity in mc.theWorld!!.loadedEntityList) {
                if (entity is EntityBoat && mc.thePlayer!!.getDistanceToEntity(entity) < 3) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(entity, WVec3(0.5, 0.5, 0.5)))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(entity, ICPacketUseEntity.WAction.INTERACT))
                    hitTimer.reset()
                }
            }
        }
    }
}