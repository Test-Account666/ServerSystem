package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for the FieldAccessor object.
 *
 * Tests reflection-based field access for getting and setting values.
 */
class FieldAccessorTest {

    class TestClass {
        var publicField: String = "public"
        private var privateField: Int = 42
        val finalField: Double = 3.14

        companion object {
            var staticField: String = "static"
        }
    }

    @Test
    fun `should create getter for public field`() {
        val getter = FieldAccessor.createGetter<TestClass, String>(TestClass::class.java, "publicField")
        val instance = TestClass()
        assertEquals("public", getter.apply(instance))
    }

    @Test
    fun `should create getter for private field`() {
        val getter = FieldAccessor.createGetter<TestClass, Int>(TestClass::class.java, "privateField")
        val instance = TestClass()
        assertEquals(42, getter.apply(instance))
    }

    @Test
    fun `should create getter with type check`() {
        val getter = FieldAccessor.createGetter<TestClass, String>(TestClass::class.java, "publicField", String::class.java)
        val instance = TestClass()
        assertEquals("public", getter.apply(instance))
    }

    @Test
    fun `should throw exception for type mismatch in getter`() {
        assertFailsWith<IllegalArgumentException> {
            FieldAccessor.createGetter(TestClass::class.java, "publicField", Int::class.java)
        }
    }

    @Test
    fun `should create setter for public field`() {
        val setter = FieldAccessor.createSetter<TestClass, String>(TestClass::class.java, "publicField")
        val instance = TestClass()
        setter.apply(instance, "new value")
        assertEquals("new value", instance.publicField)
    }

    @Test
    fun `should create setter for private field`() {
        val setter = FieldAccessor.createSetter<TestClass, Int>(TestClass::class.java, "privateField")
        val instance = TestClass()
        setter.apply(instance, 100)
        val getter = FieldAccessor.createGetter<TestClass, Int>(TestClass::class.java, "privateField")
        assertEquals(100, getter.apply(instance))
    }

    @Test
    fun `should create setter with type check`() {
        val setter = FieldAccessor.createSetter(TestClass::class.java, "publicField", String::class.java)
        val instance = TestClass()
        setter.apply(instance, "typed value")
        assertEquals("typed value", instance.publicField)
    }

    @Test
    fun `should throw exception for type mismatch in setter`() {
        assertFailsWith<IllegalArgumentException> {
            FieldAccessor.createSetter(TestClass::class.java, "publicField", Int::class.java)
        }
    }

    @Test
    fun `should throw exception for non-existent field`() {
        assertFailsWith<RuntimeException> {
            FieldAccessor.createGetter<TestClass, String>(TestClass::class.java, "nonExistentField")
        }
    }

    @Test
    fun `should handle null instance for getter`() {
        val getter = FieldAccessor.createGetter<TestClass, String>(TestClass::class.java, "publicField")
        // For static fields or when instance is null, it should handle gracefully
        // But since this is an instance field, it will throw NPE, which is expected
        assertFailsWith<RuntimeException> {
            getter.apply(null)
        }
    }

    @Test
    fun `should handle null instance for setter`() {
        val setter = FieldAccessor.createSetter<TestClass, String>(TestClass::class.java, "publicField")
        // For instance fields, setting on null instance should throw NPE
        assertFailsWith<RuntimeException> {
            setter.apply(null, "null value")
        }
    }

    @Test
    fun `should find field in superclass hierarchy`() {
        open class Parent {
            private var parentField: String = "parent"
        }

        class Child : Parent()

        val getter = FieldAccessor.createGetter<Child, String>(Child::class.java, "parentField")
        val instance = Child()
        assertEquals("parent", getter.apply(instance))
    }
}
