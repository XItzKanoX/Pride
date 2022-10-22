package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "OldHitting", description = "faq", category = ModuleCategory.RENDER)
class OldHitting : Module() {
    private val modeValue = ListValue("Mode", arrayOf( "Pride", "Vanilla", "WindMill", "Push", "SideDown"), "Vanilla")
    private val onlySword = BoolValue("Only-Sword", true)

    fun getModeValue(): ListValue {
        return modeValue
    }

    fun getOnlySword(): BoolValue {
        return onlySword
    }

    override val tag: String
        get() = modeValue.get()

}
