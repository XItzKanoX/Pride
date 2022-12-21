/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "Teams", description = "防止杀戮光环攻击队友",category = ModuleCategory.MISC)
class Teams : Module() {

    private val scoreboardValue = BoolValue("ScoreboardTeam", true)
    private val colorValue = BoolValue("Color", true)
    private val gommeSWValue = BoolValue("GommeSW", false)
    private val armorColorValue = BoolValue("ArmorColor", false)

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(target: IEntityLivingBase): Boolean {
        val thePlayer = mc.thePlayer!!

        if (scoreboardValue.get() && thePlayer.team != null && target.team != null &&
            thePlayer.team!!.isSameTeam(target.team!!))
            return true

        val displayName = thePlayer.displayName

        if(armorColorValue.get()){
            val entityPlayer = target.asEntityPlayer()
            if(thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null){
                val myHead = thePlayer.inventory.armorInventory[3]
                val myItemArmor = myHead!!.item!!.asItemArmor()


                val entityHead = entityPlayer.inventory.armorInventory[3]
                var entityItemArmor = myHead.item!!.asItemArmor()

                if(myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead!!)){
                    return true
                }
            }
        }

        if (gommeSWValue.get() && displayName != null && target.displayName != null) {
            val targetName = target.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (targetName[1].isDigit() && clientName[1].isDigit())
                    return targetName[1] == clientName[1]
        }

        if (colorValue.get() && displayName != null && target.displayName != null) {
            val targetName = target.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }

}
