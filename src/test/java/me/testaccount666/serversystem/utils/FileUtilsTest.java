package me.testaccount666.serversystem.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void deleteFile_shouldHandleNull() throws IOException {
        // Should not throw exception for null input
        assertDoesNotThrow(() -> FileUtils.deleteFile(null));
    }

    @Test
    void deleteFile_shouldDeleteExistingFile() throws IOException {
        var testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");
        assertTrue(Files.exists(testFile));

        FileUtils.deleteFile(testFile.toFile());

        assertFalse(Files.exists(testFile));
    }

    @Test
    void deleteFile_shouldThrowExceptionForNonExistentFile() {
        var nonExistentFile = tempDir.resolve("nonexistent.txt").toFile();

        var exception = assertThrows(FileNotFoundException.class, () -> FileUtils.deleteFile(nonExistentFile));

        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void deleteDirectory_shouldHandleNull() throws IOException {
        // Should not throw exception for null input
        assertDoesNotThrow(() -> FileUtils.deleteDirectory(null));
    }

    @Test
    void deleteDirectory_shouldDeleteSingleFile() throws IOException {
        var testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content");
        assertTrue(Files.exists(testFile));

        FileUtils.deleteDirectory(testFile.toFile());

        assertFalse(Files.exists(testFile));
    }

    @Test
    void deleteDirectory_shouldDeleteEmptyDirectory() throws IOException {
        var testDir = tempDir.resolve("emptyDir");
        Files.createDirectory(testDir);
        assertTrue(Files.exists(testDir));

        FileUtils.deleteDirectory(testDir.toFile());

        assertFalse(Files.exists(testDir));
    }

    @Test
    void deleteDirectory_shouldDeleteDirectoryWithFiles() throws IOException {
        var testDir = tempDir.resolve("testDir");
        Files.createDirectory(testDir);
        var file1 = testDir.resolve("file1.txt");
        var file2 = testDir.resolve("file2.txt");
        Files.writeString(file1, "content1");
        Files.writeString(file2, "content2");

        assertTrue(Files.exists(testDir));
        assertTrue(Files.exists(file1));
        assertTrue(Files.exists(file2));

        FileUtils.deleteDirectory(testDir.toFile());

        assertFalse(Files.exists(testDir));
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file2));
    }

    @Test
    void deleteDirectory_shouldDeleteNestedDirectories() throws IOException {
        var testDir = tempDir.resolve("parent");
        var childDir = testDir.resolve("child");
        var grandchildDir = childDir.resolve("grandchild");
        Files.createDirectories(grandchildDir);

        var file1 = testDir.resolve("parent.txt");
        var file2 = childDir.resolve("child.txt");
        var file3 = grandchildDir.resolve("grandchild.txt");
        Files.writeString(file1, "parent content");
        Files.writeString(file2, "child content");
        Files.writeString(file3, "grandchild content");

        assertTrue(Files.exists(testDir));
        assertTrue(Files.exists(childDir));
        assertTrue(Files.exists(grandchildDir));

        FileUtils.deleteDirectory(testDir.toFile());

        assertFalse(Files.exists(testDir));
        assertFalse(Files.exists(childDir));
        assertFalse(Files.exists(grandchildDir));
    }

    @Test
    void deleteDirectory_shouldThrowExceptionForNonExistentDirectory() {
        var nonExistentDir = tempDir.resolve("nonexistent").toFile();

        var exception = assertThrows(FileNotFoundException.class, () -> FileUtils.deleteDirectory(nonExistentDir));

        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void copyFile_shouldHandleNullInputs() throws IOException {
        // Should not throw exception for null inputs
        assertDoesNotThrow(() -> FileUtils.copyFile(null, null));
        assertDoesNotThrow(() -> FileUtils.copyFile(null, tempDir.resolve("dest.txt").toFile()));
        assertDoesNotThrow(() -> FileUtils.copyFile(tempDir.resolve("src.txt").toFile(), null));
    }

    @Test
    void copyFile_shouldCopyFileContent() throws IOException {
        var sourceFile = tempDir.resolve("source.txt");
        var content = "This is test content for copying";
        Files.writeString(sourceFile, content);

        var destFile = tempDir.resolve("destination.txt");
        FileUtils.copyFile(sourceFile.toFile(), destFile.toFile());

        assertTrue(Files.exists(destFile));
        var copiedContent = Files.readString(destFile);
        assertEquals(content, copiedContent);
    }

    @Test
    void copyFile_shouldThrowExceptionForNonExistentSource() {
        var nonExistentSource = tempDir.resolve("nonexistent.txt").toFile();
        var destination = tempDir.resolve("dest.txt").toFile();

        var exception = assertThrows(FileNotFoundException.class, () -> FileUtils.copyFile(nonExistentSource, destination));

        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void copyFile_shouldThrowExceptionForSameSourceAndDestination() throws IOException {
        var testFile = tempDir.resolve("same.txt");
        Files.writeString(testFile, "content");

        var exception = assertThrows(IOException.class, () -> FileUtils.copyFile(testFile.toFile(), testFile.toFile()));

        assertTrue(exception.getMessage().contains("Destination and source are the same"));
    }

    @Test
    void copyDirectory_shouldHandleNullInputs() throws IOException {
        // Should not throw exception for null inputs
        assertDoesNotThrow(() -> FileUtils.copyDirectory(null, null));
        assertDoesNotThrow(() -> FileUtils.copyDirectory(null, tempDir.resolve("dest").toFile()));
        assertDoesNotThrow(() -> FileUtils.copyDirectory(tempDir.resolve("src").toFile(), null));
    }

    @Test
    void copyDirectory_shouldCopySingleFile() throws IOException {
        var sourceFile = tempDir.resolve("source.txt");
        var content = "File content";
        Files.writeString(sourceFile, content);

        var destFile = tempDir.resolve("destination.txt");
        FileUtils.copyDirectory(sourceFile.toFile(), destFile.toFile());

        assertTrue(Files.exists(destFile));
        var copiedContent = Files.readString(destFile);
        assertEquals(content, copiedContent);
    }

    @Test
    void copyDirectory_shouldCopyEmptyDirectory() throws IOException {
        var sourceDir = tempDir.resolve("sourceDir");
        Files.createDirectory(sourceDir);

        var destDir = tempDir.resolve("destDir");
        FileUtils.copyDirectory(sourceDir.toFile(), destDir.toFile());

        assertTrue(Files.exists(destDir));
        assertTrue(Files.isDirectory(destDir));
    }

    @Test
    void copyDirectory_shouldCopyDirectoryWithFiles() throws IOException {
        var sourceDir = tempDir.resolve("sourceDir");
        Files.createDirectory(sourceDir);
        var file1 = sourceDir.resolve("file1.txt");
        var file2 = sourceDir.resolve("file2.txt");
        Files.writeString(file1, "content1");
        Files.writeString(file2, "content2");

        var destDir = tempDir.resolve("destDir");
        FileUtils.copyDirectory(sourceDir.toFile(), destDir.toFile());

        assertTrue(Files.exists(destDir));
        assertTrue(Files.isDirectory(destDir));

        var destFile1 = destDir.resolve("file1.txt");
        var destFile2 = destDir.resolve("file2.txt");
        assertTrue(Files.exists(destFile1));
        assertTrue(Files.exists(destFile2));
        assertEquals("content1", Files.readString(destFile1));
        assertEquals("content2", Files.readString(destFile2));
    }

    @Test
    void copyDirectory_shouldCopyNestedDirectories() throws IOException {
        var sourceDir = tempDir.resolve("source");
        var childDir = sourceDir.resolve("child");
        Files.createDirectories(childDir);

        var parentFile = sourceDir.resolve("parent.txt");
        var childFile = childDir.resolve("child.txt");
        Files.writeString(parentFile, "parent content");
        Files.writeString(childFile, "child content");

        var destDir = tempDir.resolve("destination");
        FileUtils.copyDirectory(sourceDir.toFile(), destDir.toFile());

        assertTrue(Files.exists(destDir));
        assertTrue(Files.exists(destDir.resolve("child")));
        assertTrue(Files.exists(destDir.resolve("parent.txt")));
        assertTrue(Files.exists(destDir.resolve("child").resolve("child.txt")));

        assertEquals("parent content", Files.readString(destDir.resolve("parent.txt")));
        assertEquals("child content", Files.readString(destDir.resolve("child").resolve("child.txt")));
    }

    @Test
    void copyDirectory_shouldThrowExceptionForNonExistentSource() {
        var nonExistentSource = tempDir.resolve("nonexistent").toFile();
        var destination = tempDir.resolve("dest").toFile();

        var exception = assertThrows(FileNotFoundException.class, () -> FileUtils.copyDirectory(nonExistentSource, destination));

        assertTrue(exception.getMessage().contains("File not found"));
    }

    @Test
    void copyDirectory_shouldThrowExceptionForSameSourceAndDestination() throws IOException {
        var testDir = tempDir.resolve("sameDir");
        Files.createDirectory(testDir);

        var exception = assertThrows(IOException.class, () -> FileUtils.copyDirectory(testDir.toFile(), testDir.toFile()));

        assertTrue(exception.getMessage().contains("Destination and source are the same"));
    }

    @Test
    void copyDirectory_shouldThrowExceptionWhenDestinationIsNotDirectory() throws IOException {
        var sourceDir = tempDir.resolve("source");
        Files.createDirectory(sourceDir);
        Files.writeString(sourceDir.resolve("test.txt"), "test content");

        // Create destination as a file (not directory) - this causes FileSystemException when trying to copy
        var destFile = tempDir.resolve("destFile.txt");
        Files.writeString(destFile, "existing file");

        assertTrue(Files.exists(destFile));
        assertFalse(Files.isDirectory(destFile));

        var exception = assertThrows(FileSystemException.class,
                () -> FileUtils.copyDirectory(sourceDir.toFile(), destFile.toFile()));

        System.out.println("Got message: " + exception.getMessage());

        // Why did my computer suddenly decide to translate this?
        assertTrue(exception.getMessage().contains("Not a directory") || exception.getMessage().contains("Ist kein Verzeichnis"));
    }
}