package dev.maykol.green

/**
 * @author Maykol Morales Morante (zSirSpectro)
 * Saturday, February 12, 2022
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GreenHandler(
    val id: String
)
