package com.dbsoftwares.bungeeutilisalsx.spigot.api.gui;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public abstract class GuiOpener
{

    private final String name;

    public abstract void openGui( Player player, String[] args );
}
