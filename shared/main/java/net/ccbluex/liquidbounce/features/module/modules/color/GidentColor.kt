package net.ccbluex.liquidbounce.features.module.modules.color

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "Gident", description = "Custom", category = ModuleCategory.COLOR)
class Gident : Module() {


    companion object {
        @JvmField
         val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)

        @JvmField
      val redValue = IntegerValue("Red", 255, 0, 255)
        @JvmField
         val greenValue = IntegerValue("Green", 255, 0, 255)
        @JvmField
         val blueValue = IntegerValue("Blue", 255, 0, 255)
        @JvmField
        val redValue2= IntegerValue("Red2", 255, 0, 255)
        @JvmField
        val greenValue2= IntegerValue("Green2", 255, 0, 255)
        @JvmField
        val blueValue2 = IntegerValue("Blue2", 255, 0, 255)
    }



}
