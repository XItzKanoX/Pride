package net.ccbluex.liquidbounce.features.module.modules.color

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "Rainbow", category = ModuleCategory.COLOR, canEnable = false, description = "Custom")
class Rainbow: Module() {
    companion object {
        @JvmField
        val rainbowStart = FloatValue("Start", 0.1f, 0f, 1f)
        @JvmField
        val rainbowStop = FloatValue("Stop", 0.2f, 0f, 1f)
        @JvmField
        val rainbowSaturation = FloatValue("Saturation", 0.7f, 0f, 1f)
        @JvmField
        val rainbowBrightness = FloatValue("Brightness", 1f, 0f, 1f)
        @JvmField
        val rainbowSpeed = IntegerValue("Speed", 1500, 500, 7000)
    }
}