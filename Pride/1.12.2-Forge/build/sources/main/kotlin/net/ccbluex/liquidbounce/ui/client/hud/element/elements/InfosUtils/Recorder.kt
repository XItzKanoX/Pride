package net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.network.play.server.SPacketTitle

object Recorder : Listenable{
    var syncEntity: IEntityLivingBase? = null
    var killCounts = 0
    var totalPlayed = 0
    var win = 0
    var ban = 0
    var startTime = System.currentTimeMillis()

    @EventTarget
     fun onAttack(event: AttackEvent) { syncEntity = event.targetEntity as IEntityLivingBase? //ok
    }
    @EventTarget
     fun onUpdate(event: UpdateEvent) {
        if( syncEntity!!.isDead){
            ++killCounts
            syncEntity = null
        }
    }
    @EventTarget
    private fun onPacket(event: PacketEvent) {
        if (event.packet is C00Handshake) startTime = System.currentTimeMillis()
        val message = (event.packet as SPacketChat).chatComponent.unformattedText
        val packet = event.packet
        if (packet is SPacketTitle) {
            val title = (packet.message ?: return@onPacket).formattedText
            if (title.startsWith("§6§l") && title.endsWith("§r") || title.startsWith("§c§lYOU") && title.endsWith(
                    "§r"
                ) || title.startsWith("§c§lGame") && title.endsWith("§r") || title.startsWith("§c§lWITH") && title.endsWith(
                    "§r"
                ) || title.startsWith("§c§lYARR") && title.endsWith("§r")
            ) totalPlayed++
            if (title.startsWith("§6§l") && title.endsWith("§r")) win++
        }
 if(message.contains("Reason")){
ban++
 }
    }

    override fun handleEvents(): Boolean {
        return true
    }
}