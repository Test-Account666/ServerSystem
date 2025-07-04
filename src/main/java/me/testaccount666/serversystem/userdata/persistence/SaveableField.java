package me.testaccount666.serversystem.userdata.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields that should be automatically saved to and loaded from
 * the user configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SaveableField {
    /**
     * The path in the configuration where this field should be saved.
     * If not specified, the field name will be used with "User." prefix.
     *
     * @return The configuration path
     */
    String path() default "";

    /**
     * The type of handler to use for this field.
     * If not specified, the system will try to find an appropriate handler based on the field type.
     *
     * @return The handler class
     */
    Class<? extends FieldHandler<?>> handler() default DefaultFieldHandler.class;
}