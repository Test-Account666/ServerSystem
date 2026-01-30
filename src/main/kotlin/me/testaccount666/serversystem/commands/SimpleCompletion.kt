package me.testaccount666.serversystem.commands

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SimpleCompletion(
    val position: Int,
    val values: Array<String> = [],
    val isNull: Boolean = false,
)