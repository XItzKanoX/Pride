
package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityPlayerSP
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.TargetStrafe
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.input.Keyboard
import java.math.BigDecimal
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementUtils : MinecraftInstance() {
    var bps = 0.0
    private  var lastX:Double = 0.0
    private  var lastY:Double = 0.0
    private  var lastZ:Double = 0.0
    val speed: Float
        get() = sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ).toFloat()

    @JvmStatic
    val isMoving: Boolean
        get() = mc.thePlayer != null && (mc.thePlayer!!.movementInput.moveForward != 0f || mc.thePlayer!!.movementInput.moveStrafe != 0f)

    fun hasMotion(): Boolean {
        return mc.thePlayer!!.motionX != 0.0 && mc.thePlayer!!.motionZ != 0.0 && mc.thePlayer!!.motionY != 0.0

    }
    @JvmStatic
    fun setSpeed(moveEvent: MoveEvent?, moveSpeed: Double) {
        setSpeed(
            moveEvent!!,
            moveSpeed,
            mc2.player.rotationYaw,
            mc2.player.movementInput.moveStrafe.toDouble(),
            mc2.player.movementInput.moveForward.toDouble()
        )
    }
    @JvmStatic
    fun getSpeed1(): Double {
        return Math.sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ)
            .toFloat()
            .toDouble()
    }

    @JvmStatic
    fun setSpeed(
        moveEvent: MoveEvent,
        moveSpeed: Double,
        pseudoYaw: Float,
        pseudoStrafe: Double,
        pseudoForward: Double
    ) {
        var forward = pseudoForward
        var strafe = pseudoStrafe
        var yaw = pseudoYaw
        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.z = 0.0
            moveEvent.x = 0.0
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (if (forward > 0.0) -45 else 45).toFloat()
                } else if (strafe < 0.0) {
                    yaw += (if (forward > 0.0) 45 else -45).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }
            val cos = Math.cos(Math.toRadians((yaw + 90.0f).toDouble()))
            val sin = Math.sin(Math.toRadians((yaw + 90.0f).toDouble()))
            moveEvent.x = forward * moveSpeed * cos + strafe * moveSpeed * sin
            moveEvent.z = forward * moveSpeed * sin - strafe * moveSpeed * cos
        }
    }
    @JvmStatic
    @JvmOverloads
    fun strafe(speed: Float = this.speed) {
        if (!isMoving) return
        val yaw = direction
        val thePlayer = mc.thePlayer!!
        thePlayer.motionX = -sin(yaw) * speed
        thePlayer.motionZ = cos(yaw) * speed
    }
    @JvmStatic
    fun setSpeed1(speed: Double) {
        val player: IEntityPlayerSP = mc.thePlayer!!
        if (isMoving) {
            val targetStrafe: TargetStrafe =
                LiquidBounce.moduleManager.getModule(TargetStrafe::class.java) as TargetStrafe
            val ka: KillAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
            if (targetStrafe.state && (!targetStrafe.holdSpaceValue.get() || Keyboard.isKeyDown(57))) {
                val target: IEntityLivingBase? = ka.target
                if (ka.state && target != null) {
                    val dist: Float = mc.thePlayer!!.getDistanceToEntity(target)
                    val radius: Float = targetStrafe.radiusValue.get()
                    setSpeed(
                        speed,
                        if (dist <= radius + 1.0E-4) 0.0f else 1.0f,
                        if (dist <= radius + 1.0) targetStrafe.direction.toFloat() else 0.0f,
                        RotationUtils.getYawToEntity(target)
                    )
                    return
                }
            }
            setSpeed(speed, player.moveForward, player.moveStrafing, player.rotationYaw)
        }
    }
    fun setSpeed(speed: Double, forward: Float, strafing: Float, yaw: Float) {
        var yaw = yaw
        val reversed = forward < 0.0f
        val strafingYaw = 90.0f * if (forward > 0.0f) 0.5f else if (reversed) -0.5f else 1.0f
        if (reversed) {
            yaw += 180.0f
        }
        if (strafing > 0.0f) {
            yaw -= strafingYaw
        } else if (strafing < 0.0f) {
            yaw += strafingYaw
        }
        val x = Math.cos(Math.toRadians((yaw + 90.0f).toDouble()))
        val z = Math.cos(Math.toRadians(yaw.toDouble()))
            mc.thePlayer!!.motionX = x * speed
        mc.thePlayer!!.motionZ = z * speed
    }
    @JvmStatic
    fun getScaffoldRotation(yaw: Float, strafe: Float): Float {
        var rotationYaw = yaw
        rotationYaw += 180f
        val forward = -0.5f
        if (strafe < 0f) rotationYaw -= 90f * forward
        if (strafe > 0f) rotationYaw += 90f * forward
        return rotationYaw
    }
    @JvmStatic
    fun forward(length: Double) {
        val thePlayer = mc.thePlayer!!
        val yaw = Math.toRadians(thePlayer.rotationYaw.toDouble())
        thePlayer.setPosition(thePlayer.posX + -sin(yaw) * length, thePlayer.posY, thePlayer.posZ + cos(yaw) * length)
    }
    fun updateBlocksPerSecond() {
        if (mc.thePlayer == null || mc.thePlayer!!.ticksExisted < 1) {
            bps = 0.0
        }
        val distance = mc.thePlayer!!.getDistance(lastX, lastY, lastZ)
        lastX = mc.thePlayer!!.posX
        lastY = mc.thePlayer!!.posY
        lastZ = mc.thePlayer!!.posZ
        bps = distance * (20 * mc.timer.timerSpeed)
    }

    fun getBlockSpeed(entityIn: EntityLivingBase): Double {
        return BigDecimal.valueOf(
            Math.sqrt(
                Math.pow(
                    entityIn.posX - entityIn.prevPosX,
                    2.0
                ) + Math.pow(entityIn.posZ - entityIn.prevPosZ, 2.0)
            ) * 20
        ).setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()
    }
    fun isOnGround(height: Double): Boolean {
        return !mc.theWorld!!.getCollidingBoundingBoxes(
                mc.thePlayer!!,
                mc.thePlayer!!.entityBoundingBox.offset(0.0, -height, 0.0)
            ).isEmpty()
    }

    fun isOnGround(entity: IEntity, height: Double): Boolean {
        return !mc.theWorld!!.getCollidingBoundingBoxes(
                entity,
                entity.entityBoundingBox.offset(0.0, -height, 0.0)
            ).isEmpty()
    }
    @JvmStatic
    val direction: Double
        get() {
            val thePlayer = mc.thePlayer!!
            var rotationYaw = thePlayer.rotationYaw
            if (thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (thePlayer.moveForward < 0f) forward = -0.5f else if (thePlayer.moveForward > 0f) forward = 0.5f
            if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }
}