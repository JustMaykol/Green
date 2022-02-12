package dev.maykol.green

import java.lang.reflect.Method

/**
 * @author Maykol Morales Morante (zSirSpectro)
 * Saturday, February 12, 2022
 */

data class GreenData(
    val id: String,
    val instance: Any,

    val method: Method,
    val clazz: Class<*>
)
