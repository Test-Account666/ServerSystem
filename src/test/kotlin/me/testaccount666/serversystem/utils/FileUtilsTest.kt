package me.testaccount666.serversystem.utils

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException
import kotlin.test.*

/**
 * Unit tests for the FileUtils object.
 *
 * Tests file and directory operations including deletion and copying.
 */
class FileUtilsTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var testFile: File
    private lateinit var testDir: File
    private lateinit var nestedDir: File
    private lateinit var nestedFile: File

    @BeforeEach
    fun setup() {
        testFile = File(tempDir, "test.txt").apply { writeText("test content") }
        testDir = File(tempDir, "testDir").apply { mkdir() }
        nestedDir = File(testDir, "nested").apply { mkdir() }
        nestedFile = File(nestedDir, "nested.txt").apply { writeText("nested content") }
    }

    @Test
    fun `should delete file successfully`() {
        assertTrue(testFile.exists())
        FileUtils.deleteFile(testFile)
        assertFalse(testFile.exists())
    }

    @Test
    fun `should throw exception when deleting non-existent file`() {
        val nonExistent = File(tempDir, "nonexistent.txt")
        try {
            FileUtils.deleteFile(nonExistent)
            assert(false) { "Expected IOException" }
        } catch (e: IOException) {
            assertTrue(e.message?.contains("File not found") ?: false)
        }
    }

    @Test
    fun `should delete directory recursively`() {
        assertTrue(testDir.exists())
        assertTrue(nestedDir.exists())
        assertTrue(nestedFile.exists())

        FileUtils.deleteDirectory(testDir)

        assertFalse(testDir.exists())
        assertFalse(nestedDir.exists())
        assertFalse(nestedFile.exists())
    }

    @Test
    fun `should throw exception when deleting non-existent directory`() {
        val nonExistent = File(tempDir, "nonexistent")
        try {
            FileUtils.deleteDirectory(nonExistent)
            assert(false) { "Expected IOException" }
        } catch (e: IOException) {
            assertTrue(e.message?.contains("File not found") ?: false)
        }
    }

    @Test
    fun `should copy file successfully`() {
        val destFile = File(tempDir, "copied.txt")
        assertFalse(destFile.exists())

        FileUtils.copyFile(testFile, destFile)

        assertTrue(destFile.exists())
        assertEquals("test content", destFile.readText())
    }

    @Test
    fun `should throw exception when copying non-existent file`() {
        val nonExistent = File(tempDir, "nonexistent.txt")
        val dest = File(tempDir, "dest.txt")
        try {
            FileUtils.copyFile(nonExistent, dest)
            assert(false) { "Expected IOException" }
        } catch (e: IOException) {
            assertTrue(e.message?.contains("File not found") ?: false)
        }
    }

    @Test
    fun `should copy directory recursively`() {
        val destDir = File(tempDir, "copiedDir")
        assertFalse(destDir.exists())

        FileUtils.copyDirectory(testDir, destDir)

        assertTrue(destDir.exists())
        assertTrue(destDir.isDirectory)

        val copiedNestedDir = File(destDir, "nested")
        val copiedNestedFile = File(copiedNestedDir, "nested.txt")

        assertTrue(copiedNestedDir.exists())
        assertTrue(copiedNestedDir.isDirectory)
        assertTrue(copiedNestedFile.exists())
        assertEquals("nested content", copiedNestedFile.readText())
    }

    @Test
    fun `should throw exception when copying to same location`() {
        try {
            FileUtils.copyFile(testFile, testFile)
            assert(false) { "Expected IOException" }
        } catch (e: IOException) {
            assertTrue(e.message?.contains("Destination and source are the same") ?: false)
        }
    }

    @Test
    fun `should handle null parameters gracefully`() {
        // These should not throw exceptions
        FileUtils.deleteFile(null)
        FileUtils.deleteDirectory(null)
        FileUtils.copyFile(null, File(tempDir, "test"))
        FileUtils.copyFile(File(tempDir, "test"), null)
        FileUtils.copyDirectory(null, File(tempDir, "test"))
        FileUtils.copyDirectory(File(tempDir, "test"), null)
    }
}
