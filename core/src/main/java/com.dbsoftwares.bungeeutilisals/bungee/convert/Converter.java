package com.dbsoftwares.bungeeutilisals.bungee.convert;

public abstract class Converter {

    private ConverterType type;

    public Converter(ConverterType type) {
        // TODO: Make Converter for LiteBans, BanHammer, BanManagementConverter, BungeeAdminTools, ...
        // TODO: Make converter for Party & Friends
        this.type = type;
    }

    public ConverterType getType() {
        return type;
    }

    public abstract void startConverter();
}