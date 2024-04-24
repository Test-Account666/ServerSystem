package me.entity303.serversystem.utils;

import me.entity303.serversystem.exceptions.NotDirectoryException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public final class FileUtils {

    public static void DeleteDirectory(File file) throws IOException {
        if (file == null)
            return;

        if (!file.exists())
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

        if (!file.isDirectory()) {
            FileUtils.DeleteFile(file);
            return;
        }

        for (var listedFile : file.listFiles())
            if (listedFile.isDirectory())
                FileUtils.DeleteDirectory(listedFile);
            else
                FileUtils.DeleteFile(listedFile);

        FileUtils.DeleteFile(file);
    }

    public static void DeleteFile(File file) throws IOException {
        if (file == null)
            return;

        if (!file.exists())
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

        if (!file.delete())
            throw new IOException("Couldn't delete file: " + file.getAbsolutePath());
    }

    public static void CopyDirectory(File source, File destination) throws IOException {
        if (source == null)
            return;
        if (destination == null)
            return;

        if (!source.exists())
            throw new FileNotFoundException("File not found: " + source);

        if (!source.isDirectory()) {
            FileUtils.CopyFile(source, destination);
            return;
        }

        if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
            throw new IOException("Destination and source are the same!");

        if (!destination.exists())
            if (!destination.mkdirs())
                throw new IOException("Could not copy file: " + source.getAbsolutePath() + "\nTo: " + destination.getAbsolutePath());
            else if (!destination.isDirectory())
                throw new NotDirectoryException("Destination is not a directory!");

        for (var file : source.listFiles())
            if (file.isDirectory())
                FileUtils.CopyDirectory(file, new File(destination, file.getName()));
            else
                FileUtils.CopyFile(file, new File(destination, file.getName()));
    }

    public static void CopyFile(File source, File destination) throws IOException {
        if (source == null)
            return;
        if (destination == null)
            return;

        if (!source.exists())
            throw new FileNotFoundException("File not found: " + source);

        if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
            throw new IOException("Destination and source are the same!");

        var inputStream = Files.newInputStream(source.toPath());
        var outputStream = Files.newOutputStream(destination.toPath());

        var data = inputStream.read();
        while (data != -1) {
            outputStream.write(data);
            data = inputStream.read();
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
}
