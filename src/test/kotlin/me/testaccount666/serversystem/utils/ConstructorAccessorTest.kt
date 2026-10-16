package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for the ConstructorAccessor object.
 *
 * Tests reflection-based constructor access for creating instances.
 */
class ConstructorAccessorTest {

    class TestClass {
        var value: String? = "default"

        constructor()

        constructor(value: String?) {
            this.value = value
        }

        constructor(value: String?, number: Int) {
            this.value = "$value-$number"
        }
    }

    class NoDefaultConstructor {
        val value: String

        constructor(value: String) {
            this.value = value
        }
    }

    @Test
    fun `should create instance with no-arg constructor`() {
        val constructor = ConstructorAccessor.createConstructor(TestClass::class.java)
        val instance = constructor.apply(null)
        assertEquals("default", instance?.value)
    }

    @Test
    fun `should create instance with single parameter constructor`() {
        val constructor = ConstructorAccessor.createConstructor(TestClass::class.java, String::class.java)
        val instance = constructor.apply("test")
        assertEquals("test", instance?.value)
    }

    @Test
    fun `should create instance with two parameter constructor`() {
        val constructor = ConstructorAccessor.createConstructor(TestClass::class.java, String::class.java, Int::class.java)
        val instance = constructor.apply("test", 42)
        assertEquals("test-42", instance?.value)
    }

    @Test
    fun `should throw exception for non-existent constructor`() {
        assertFailsWith<RuntimeException> {
            ConstructorAccessor.createConstructor(TestClass::class.java, Double::class.java)
        }

        assertFailsWith<RuntimeException> {
            ConstructorAccessor.createConstructor(NoDefaultConstructor::class.java)
        }
    }

    @Test
    fun `should handle null parameter for single param constructor`() {
        val constructor = ConstructorAccessor.createConstructor(TestClass::class.java, String::class.java)
        val instance = constructor.apply(null)
        assertEquals(null, instance?.value) // Since value is initialized to "default" but constructor sets it to null
    }
}
