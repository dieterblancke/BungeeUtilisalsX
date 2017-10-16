package com.dbsoftwares.bungeeutilisals.installer;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.installer.enums.InstallType;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Installer {

    public static void main(String[] args) {
        System.out.println("█▀▀▄ █░░█ █▀▀▄ █▀▀▀ █▀▀ █▀▀ █░░█ ▀▀█▀▀ ░▀░ █░░ ░▀░ █▀▀ █▀▀█ █░░ █▀▀");
        System.out.println("█▀▀▄ █░░█ █░░█ █░▀█ █▀▀ █▀▀ █░░█ ░░█░░ ▀█▀ █░░ ▀█▀ ▀▀█ █▄▄█ █░░ ▀▀█");
        System.out.println("▀▀▀░ ░▀▀▀ ▀░░▀ ▀▀▀▀ ▀▀▀ ▀▀▀ ░▀▀▀ ░░▀░░ ▀▀▀ ▀▀▀ ▀▀▀ ▀▀▀ ▀░░▀ ▀▀▀ ▀▀▀\n");
        System.out.println("First of all, I want to thank you for buying BungeeUtilisals!");
        System.out.println("It really means everything to me, and I want to thank you for using my project.\n");
        System.out.println("This installer is a simple system which will put the BungeeUtilisals jars into your current folder.");
        System.out.println("If any bugs occur, please report this to didjee2 on SpigotMC BEFORE making a (bad) review!\n");
        System.out.println("Good luck on setting up BungeeUtilisals! And I hope that you'll have fun using BungeeUtilisals!\n\n");

        Scanner scanner = new Scanner(System.in);
        File folder = null;

        try {
            folder = new File(Installer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException ignored) {
        }
        System.out.println(folder.getPath());

        if (folder == null) {
            System.out.println("Could not find the folder you executed the jar from! Aborting ...");
            return;
        }

        for (InstallType type : InstallType.values()) {
            System.out.println("Do you want to build the " + type.toString().toLowerCase().replaceAll("_", " ")
                    + " jar into your folder?");
            System.out.println(type.getDescription());
            System.out.println();
            System.out.println("Type yes to confirm, no to skip.");

            Boolean build = scanner.nextLine().contains("y");

            if (build) {
                System.out.println("Building " + type.toString().toLowerCase() + " jar.");

                File typeFolder = new File(folder, type.getFolder());
                if (!typeFolder.exists()) {
                    System.out.println("Could not find " + type.getFolder() + " folder! Creating " + type.getFolder() + " folder ...");
                    typeFolder.mkdirs();
                }

                InputStream input = null;
                try {
                    input = new BufferedInputStream(Installer.class.getResource("/" + type.getJarName()).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File target = new File(typeFolder, type.getJar());
                if (target.exists()) {
                    Boolean delete = target.delete();

                    if (!delete) {
                        System.out.println("File " + type.getJarName() + " already exists & could not be overwritten!");
                        continue;
                    }
                }
                if (!copy(input, target.getPath())) {
                    continue;
                }

                System.out.println("Finished building " + type.getJarName() + "!");
            }
            System.out.println();
        }
    }

    private static Boolean copy(InputStream source, String destination) {
        Boolean copied = false;
        try {
            copied = true;
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignore) {
            ignore.printStackTrace();
            System.out.println("Could not build the jar! Report this error to @didjee2!");
        }
        return copied;
    }
}