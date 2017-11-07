package com.dbsoftwares.bungeeutilisals.api.utils.file;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.plugin.Plugin;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@UtilityClass
public class FileUtils {

    public File createDefaultFile(Plugin jar, String jarPath, File targetFile, Boolean saveComments) {
        if (targetFile.exists()) {
            throw new RuntimeException(jar.getDescription().getName() + " attempted to create an already existing file!");
        }
        if (targetFile.isDirectory()) {
            throw new RuntimeException(jar.getDescription().getName() + " attempted to create a file from default!");
        }
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        try {
            @Cleanup InputStream input = jar.getResourceAsStream(jarPath);
            if (input == null) {
                throw new NullPointerException("Could not open stream to " + jarPath + " in " + jar.getDescription().getName() + "!");
            }
            if (saveComments) { // Simply copy the file from the jar to the folder.
                Files.copy(input, Paths.get(targetFile.toURI()));
            } else { // More work, checking each line for starting with a hashtag, if so: skipping.
                @Cleanup FileOutputStream output = new FileOutputStream(targetFile);
                @Cleanup InputStreamReader inputStreamReader = new InputStreamReader(input);
                @Cleanup OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);

                @Cleanup BufferedReader reader = new BufferedReader(inputStreamReader);
                @Cleanup BufferedWriter writer = new BufferedWriter(outputStreamWriter);

                String line;
                while ((line = reader.readLine()) != null) {
                    String firstChar = null;
                    for (char c : line.toCharArray()) {
                        if (firstChar != null && !firstChar.isEmpty()) {
                            break;
                        }
                        if (c == ' ') {
                            continue;
                        }
                        if (c == '#') {
                            firstChar = "#";
                            break;
                        }
                        if (c != ' ') {
                            firstChar = String.valueOf(c);
                        }
                    }

                    if (firstChar.equalsIgnoreCase("#")) {
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load default file from " + jarPath + " for " + jar.getDescription().getName() + "!", e);
        }
        return targetFile;
    }
}