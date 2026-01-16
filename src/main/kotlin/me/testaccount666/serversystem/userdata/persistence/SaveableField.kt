package me.testaccount666.serversystem.userdata.persistence

import kotlin.reflect.KClass

/**
 * Annotation to mark fields that should be automatically saved to and loaded from
 * the user configuration file.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class SaveableField(
    /**
     * The path in the configuration where this field should be saved.
     * If not specified, the field name will be used with "User." prefix.
     *
     * @return The configuration path
     */
    val path: String = "",
    /**
     * The type of handler to use for this field.
     * If not specified, the system will try to find an appropriate handler based on the field type.
     *
     * @return The handler class
     */
    val handler: KClass<out FieldHandler<*>> = DefaultFieldHandler::class
)