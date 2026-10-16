package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Unit tests for the MethodAccessor object.
 *
 * Tests reflection-based method invocation for void and return-type methods.
 */
class MethodAccessorTest {

    class TestClass {
        var value: String = "initial"

        fun voidMethod() {
            value = "void"
        }

        fun voidMethodWithParam(param: String) {
            value = param
        }

        fun returnMethod(): String {
            return "returned"
        }

        fun returnMethodWithParam(param: Int): String {
            return "param-$param"
        }

        private fun privateVoidMethod() {
            value = "private"
        }

        private fun privateReturnMethod(): String {
            return "private-return"
        }
    }

    @Test
    fun `should create void accessor for no-param method`() {
        val accessor = MethodAccessor.createVoidAccessor(TestClass::class.java, "voidMethod")
        val instance = TestClass()
        accessor.accept(instance)
        assertEquals("void", instance.value)
    }

    @Test
    fun `should create void accessor for single param method`() {
        val accessor = MethodAccessor.createVoidAccessor<TestClass, String>(TestClass::class.java, "voidMethodWithParam", String::class.java)
        val instance = TestClass()
        accessor.accept(instance, "param")
        assertEquals("param", instance.value)
    }

    @Test
    fun `should create accessor for no-param return method`() {
        val accessor = MethodAccessor.createAccessor<TestClass, String>(TestClass::class.java, "returnMethod", String::class.java)
        val instance = TestClass()
        val result = accessor.apply(instance)
        assertEquals("returned", result)
    }

    @Test
    fun `should create accessor for single param return method`() {
        val accessor =
            MethodAccessor.createAccessor<TestClass, Int, String>(TestClass::class.java, "returnMethodWithParam", Int::class.java, String::class.java)
        val instance = TestClass()
        val result = accessor.apply(instance, 42)
        assertEquals("param-42", result)
    }

    @Test
    fun `should create void accessor for private method`() {
        val accessor = MethodAccessor.createVoidAccessor(TestClass::class.java, "privateVoidMethod")
        val instance = TestClass()
        accessor.accept(instance)
        assertEquals("private", instance.value)
    }

    @Test
    fun `should create accessor for private return method`() {
        val accessor = MethodAccessor.createAccessor<TestClass, String>(TestClass::class.java, "privateReturnMethod", String::class.java)
        val instance = TestClass()
        val result = accessor.apply(instance)
        assertEquals("private-return", result)
    }

    @Test
    fun `should throw exception for non-existent method`() {
        assertFailsWith<RuntimeException> {
            MethodAccessor.createVoidAccessor(TestClass::class.java, "nonExistentMethod")
        }
    }

    @Test
    fun `should throw exception when using return accessor for void method`() {
        assertFailsWith<IllegalArgumentException> {
            MethodAccessor.createAccessor(TestClass::class.java, "voidMethod", String::class.java)
        }
    }

    @Test
    fun `should handle null instance for void accessor`() {
        val accessor = MethodAccessor.createVoidAccessor(TestClass::class.java, "voidMethod")
        // Instance methods called on null will throw NPE
        assertFailsWith<RuntimeException> {
            accessor.accept(null)
        }
    }

    @Test
    fun `should handle null instance for return accessor`() {
        val accessor = MethodAccessor.createAccessor(TestClass::class.java, "returnMethod", String::class.java)
        // Instance methods called on null will throw NPE
        assertFailsWith<RuntimeException> {
            accessor.apply(null)
        }
    }
}
