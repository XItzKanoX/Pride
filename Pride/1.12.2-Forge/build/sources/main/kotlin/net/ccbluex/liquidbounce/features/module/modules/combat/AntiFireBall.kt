/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketUseEntity
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.projectile.EntityFireball

@ModuleInfo(name = "AntiFireBall", category = ModuleCategory.COMBAT, description = "Fuck")
class AntiFireBall : Module() {

    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val rotationValue = BoolValue("Rotation", true)

    private val timer = MSTimer()

    @EventTarget
    private fun onUpdate(event: UpdateEvent) {
        for (entity in mc.theWorld!!.loadedEntityList) {
            if (entity is EntityFireball && mc.thePlayer!!.getDistanceToEntity(entity) < 5.5 && timer.hasTimePassed(300)) {
                if (rotationValue.get()) {
                    RotationUtils.setTargetRotation(RotationUtils.getRotationsNonLivingEntity(entity))
                }

                mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketUseEntity(entity, ICPacketUseEntity.WAction.ATTACK))

                if (swingValue.equals("Normal")) {
                    mc.thePlayer!!.swingItem()
                } else if (swingValue.equals("Packet")) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())
                }

                timer.reset()
                break
            }
        }
    }
}
