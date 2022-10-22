package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.ListValue
import kotlin.math.*

/***
 * Skid from https://github.com/ai/easings.net
 */
object EaseUtils {
    @JvmStatic
    fun easeInSine(x: Double): Double {
        return 1 - cos((x * PI) / 2)
    }

    @JvmStatic
    fun easeOutSine(x: Double): Double {
        return sin((x * PI) / 2)
    }

    @JvmStatic
    fun easeInOutSine(x: Double): Double {
        return -(cos(PI * x) - 1) / 2
    }

    @JvmStatic
    fun easeInQuad(x: Double): Double {
        return x * x
    }

    @JvmStatic
    fun easeOutQuad(x: Double): Double {
        return 1 - (1 - x) * (1 - x)
    }

    @JvmStatic
    fun easeInOutQuad(x: Double): Double {
        return if (x < 0.5) {
            2 * x * x
        } else {
            1 - (-2 * x + 2).pow(2) / 2
        }
    }

    @JvmStatic
    fun easeInCubic(x: Double): Double {
        return x * x * x
    }

    @JvmStatic
    fun easeOutCubic(x: Double): Double {
        return 1 - (1 - x).pow(3)
    }

    @JvmStatic
    fun easeInOutCubic(x: Double): Double {
        return if (x < 0.5) {
            4 * x * x * x
        } else {
            1 - (-2 * x + 2).pow(3) / 2
        }
    }

    @JvmStatic
    fun easeInQuart(x: Double): Double {
        return x * x * x * x
    }

    @JvmStatic
    fun easeOutQuart(x: Double): Double {
        return 1 - (1 - x).pow(4)
    }

    @JvmStatic
    fun easeInOutQuart(x: Double): Double {
        return if (x < 0.5) {
            8 * x * x * x * x
        } else {
            1 - (-2 * x + 2).pow(4) / 2
        }
    }

    @JvmStatic
    fun easeInQuint(x: Double): Double {
        return x * x * x * x * x
    }

    @JvmStatic
    fun easeOutQuint(x: Double): Double {
        return 1 - (1 - x).pow(5)
    }

    @JvmStatic
    fun easeInOutQuint(x: Double): Double {
        return if (x < 0.5) {
            16 * x * x * x * x * x
        } else {
            1 - (-2 * x + 2).pow(5) / 2
        }
    }

    @JvmStatic
    fun easeInExpo(x: Double): Double {
        return if (x == 0.0) {
            0.0
        } else {
            2.0.pow(10 * x - 10)
        }
    }

    @JvmStatic
    fun easeOutExpo(x: Double): Double {
        return if (x == 1.0) {
            1.0
        } else {
            1 - 2.0.pow(-10 * x)
        }
    }

    @JvmStatic
    fun easeInOutExpo(x: Double): Double {
        return if (x == 0.0) {
            0.0
        } else {
            if (x == 1.0) {
                1.0
            } else {
                if (x < 0.5) {
                    2.0.pow(20 * x - 10) / 2
                } else {
                    (2 - 2.0.pow(-20 * x + 10)) / 2
                }
            }
        }
    }

    @JvmStatic
    fun easeInCirc(x: Double): Double {
        return 1 - sqrt(1 - x.pow(2))
    }

    @JvmStatic
    fun easeOutCirc(x: Double): Double {
        return sqrt(1 - (x - 1).pow(2))
    }

    @JvmStatic
    fun easeInOutCirc(x: Double): Double {
        return if (x < 0.5) {
            (1 - sqrt(1 - (2 * x).pow(2))) / 2
        } else {
            (sqrt(1 - (-2 * x + 2).pow(2)) + 1) / 2
        }
    }

    @JvmStatic
    fun easeInBack(x: Double): Double {
        val c1 = 1.70158
        val c3 = c1 + 1

        return c3 * x * x * x - c1 * x * x
    }

    @JvmStatic
    fun easeOutBack(x: Double): Double {
        val c1 = 1.70158
        val c3 = c1 + 1

        return 1 + c3 * (x - 1).pow(3) + c1 * (x - 1).pow(2)
    }

    @JvmStatic
    fun easeInOutBack(x: Double): Double {
        val c1 = 1.70158
        val c2 = c1 * 1.525

        return if (x < 0.5) {
            ((2 * x).pow(2) * ((c2 + 1) * 2 * x - c2)) / 2
        } else {
            ((2 * x - 2).pow(2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2
        }
    }

    @JvmStatic
    fun easeInElastic(x: Double): Double {
        val c4 = (2 * Math.PI) / 3

        return if (x == 0.0) {
            0.0
        } else {
            if (x == 1.0) {
                1.0
            } else {
                (-2.0).pow(10 * x - 10) * sin((x * 10 - 10.75) * c4)
            }
        }
    }

    @JvmStatic
    fun easeOutElastic(x: Double): Double {
        val c4 = (2 * Math.PI) / 3

        return if (x == 0.0) {
            0.0
        } else {
            if (x == 1.0) {
                1.0
            } else {
                2.0.pow(-10 * x) * sin((x * 10 - 0.75) * c4) + 1
            }
        }
    }

    @JvmStatic
    fun easeInOutElastic(x: Double): Double {
        val c5 = (2 * Math.PI) / 4.5

        return if (x == 0.0) {
            0.0
        } else {
            if (x == 1.0) {
                1.0
            } else {
                if (x < 0.5) {
                    -(2.0.pow(20 * x - 10) * sin((20 * x - 11.125) * c5)) / 2
                } else {
                    (2.0.pow(-20 * x + 10) * sin((20 * x - 11.125) * c5)) / 2 + 1
                }
            }
        }
    }

    enum class EnumEasingType {
        NONE,
        SINE,
        QUAD,
        CUBIC,
        QUART,
        QUINT,
        EXPO,
        CIRC,
        BACK,
        ELASTIC;

        val friendlyName = name.substring(0, 1).toUpperCase() + name.substring(1, name.length).toLowerCase()
    }

    enum class EnumEasingOrder(val methodName: String) {
        FAST_AT_START("Out"),
        FAST_AT_END("In"),
        FAST_AT_START_AND_END("InOut")
    }

    @JvmStatic
    fun getEnumEasingList(name: String) =
        ListValue(name, EnumEasingType.values().map { it.toString() }.toTypedArray(), EnumEasingType.SINE.toString())

    @JvmStatic
    fun getEnumEasingOrderList(name: String) = ListValue(
        name,
        EnumEasingOrder.values().map { it.toString() }.toTypedArray(),
        EnumEasingOrder.FAST_AT_START.toString()
    )

    @JvmStatic
    fun apply(type: EnumEasingType, order: EnumEasingOrder, value: Double): Double {
        if (type == EnumEasingType.NONE) {
            return value
        }

        val methodName = "ease${order.methodName}${type.friendlyName}"

        this.javaClass.declaredMethods.find { it.name.equals(methodName) }.also {
            return if (it != null) {
                it.invoke(this, value) as Double
            } else {

                value
            }
        }
    }
}