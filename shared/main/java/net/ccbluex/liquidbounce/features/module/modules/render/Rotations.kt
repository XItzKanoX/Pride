/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.BowAimbot
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.world.*

import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue


@ModuleInfo(name = "Rotations", description = "Allows you to see server-sided head rotations.", category = ModuleCategory.RENDER)
class Rotations : Module() {
    val headValue = BoolValue("Head", true)
    val bodyValue = BoolValue("Body", true)
    private var playerYaw: Float? = null

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null && !bodyValue.get() && headValue.get())
            mc.thePlayer?.rotationYawHead = RotationUtils.serverRotation.yaw
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!bodyValue.get() || !shouldRotate() || mc.thePlayer == null)
            return

        val packet = event.packet
        if (classProvider.isCPacketPlayerPosLook(packet) || classProvider.isCPacketPlayerLook(packet)) {
            val packetPlayer = packet.asCPacketPlayer()

            playerYaw = packetPlayer.yaw
            if (bodyValue.get())
                mc.thePlayer!!.renderYawOffset = packetPlayer.yaw
            if (headValue.get())
                mc.thePlayer!!.rotationYawHead = packetPlayer.yaw
        } else {
            if (playerYaw != null && bodyValue.get())
                mc.thePlayer!!.renderYawOffset = playerYaw!!
            if (headValue.get())
                mc.thePlayer!!.rotationYawHead = mc.thePlayer!!.renderYawOffset
        }
    }

    private fun getState(module: Class<*>) = LiquidBounce.moduleManager[module].state

    private fun shouldRotate(): Boolean {
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        return getState(Scaffold::class.java) || getState(Tower::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||  getState(BowAimbot::class.java) ||
                getState(Fucker::class.java) || getState(CivBreak::class.java) || getState(Nuker::class.java) ||
                getState(ChestAura::class.java)
    }
}
