package com.dbsoftwares.bungeeutilisals.installer;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.installer.enums.InstallType;

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

        for (InstallType type : InstallType.values()) {
            System.out.println("Do you want to build the " + type.toString().toLowerCase() + " jar into your folder?");
            System.out.println("Type yes to confirm, no to skip.");

            String input = scanner.nextLine();
            Boolean build = input.contains("y");

            if (build) {
                System.out.println("Building " + type.toString().toLowerCase() + " jar. This can take a moment.");
                // TODO: BUILD JAR
            }
            System.out.println();
        }
    }
}