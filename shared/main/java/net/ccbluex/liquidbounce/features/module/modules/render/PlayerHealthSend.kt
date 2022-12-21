package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.minecraft.entity.EntityLivingBase
import kotlin.math.abs


@ModuleInfo(
    name = "PlayerHealthSend",
    description = "Debug Health",
    category = ModuleCategory.RENDER
)
class PlayerHealthSend : Module() {
    private val timer=TimeUtils()
    private val healthData=HashMap<Int,Float>()
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        for(entity in mc.theWorld!!.loadedEntityList){
            if(classProvider.isEntityLivingBase(event) && EntityUtils.isSelected(entity,true)){
                val lastHealth=healthData.getOrDefault(mc.thePlayer!!.entityId,mc.thePlayer!!.maxHealth)
                healthData[mc.thePlayer!!.entityId] = mc.thePlayer!!.health
                if(lastHealth==mc.thePlayer!!.health) continue
                if(lastHealth>mc.thePlayer!!.health){
                    ClientUtils.displayChatMessage("§c扣除血量§a"+(lastHealth-mc.thePlayer!!.health)+"HP"+" §f| "+"§c当前血量§a"+mc.thePlayer!!.health+"HP")
                }else{
                    ClientUtils.displayChatMessage("§c增加血量§a"+(abs(lastHealth-mc.thePlayer!!.health))+"HP"+" §f| "+"§c当前血量§a"+mc.thePlayer!!.health+"HP")
                }
            }
        }

        if (timer.delay(220F)) {
            if(mc.thePlayer!!.health<10f)
                LiquidBounce.hud.addNotification(Notification("Waring", "Low hp ", NotifyType.WARNING))
//                    ClientUtils.displayChatMessage("§c[Waring]§6您当前血量小于10§cHP")
        }
        timer.reset()
    }

    override fun handleEvents() = true


}
