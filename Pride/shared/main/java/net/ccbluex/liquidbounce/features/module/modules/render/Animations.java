package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;


@ModuleInfo(name = "OldHitSetting", description = "Animation for blocking.", category = ModuleCategory.RENDER)
public class Animations extends Module {
    public static FloatValue itemPosX = new FloatValue("itemPosX", 0.21f, -1, 1);
    public static FloatValue itemPosY = new FloatValue("itemPosY", 0.10f, -1, 1);
    public static FloatValue itemPosZ = new FloatValue("itemPosZ", 0.07f, -1, 1);
    public static FloatValue Scale = new FloatValue("Scale", 1.03f, 0, 2);
}
