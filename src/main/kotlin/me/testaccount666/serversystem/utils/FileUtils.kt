package me.testaccount666.serversystem.utils

import me.testaccount666.serversystem.exceptions.NotDirectoryException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files

object FileUtils {
    @JvmStatic
    @Throws(IOException::class)
    fun deleteDirectory(file: File?) {
        if (file == null) return
        if (!file.exists()) throw FileNotFoundException("File not found: ${file.absolutePath}")

        if (!file.isDirectory) {
            deleteFile(file)
            return
        }

        val filesList = file.listFiles() ?: arrayOf();

        filesList.forEach {
            if (it.isDirectory) deleteDirectory(it)
            else deleteFile(it)
        }

        deleteFile(file)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun deleteFile(file: File?) {
        if (file == null) return

        if (!file.exists()) throw FileNotFoundException("File not found: ${file.absolutePath}")
        if (!file.delete()) throw IOException("Couldn't delete file: ${file.absolutePath}")
    }

    @JvmStatic
    @Throws(IOException::class)
    fun copyDirectory(source: File?, destination: File?) {
        if (source == null) return
        if (destination == null) return

        if (!source.exists()) throw FileNotFoundException("File not found: $source")

        if (!source.isDirectory) {
            copyFile(source, destination)
            return
        }

        if (source.canonicalPath == destination.canonicalPath) throw IOException("Destination and source are the same!")

        if (!destination.exists()) if (!destination.mkdirs()) throw IOException("Could not copy file: ${source.absolutePath}\nTo: ${destination.absolutePath}")
        else if (!destination.isDirectory) throw NotDirectoryException("Destination is not a directory!")

        val filesList = source.listFiles() ?: arrayOf<File>();
        for (file in filesList) if (file.isDirectory) copyDirectory(file, File(destination, file.name))
        else copyFile(file, File(destination, file.name))
    }

    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(source: File?, destination: File?) {
        if (source == null) return
        if (destination == null) return

        if (!source.exists()) throw FileNotFoundException("File not found: $source")

        if (source.canonicalPath == destination.canonicalPath) throw IOException("Destination and source are the same!")

        val inputStream = Files.newInputStream(source.toPath())
        val outputStream = Files.newOutputStream(destination.toPath())

        var data = inputStream.read()
        while (data != -1) {
            outputStream.write(data)
            data = inputStream.read()
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }
}

