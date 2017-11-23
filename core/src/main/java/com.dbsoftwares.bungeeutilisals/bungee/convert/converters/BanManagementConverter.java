package com.dbsoftwares.bungeeutilisals.bungee.convert.converters;

import com.dbsoftwares.bungeeutilisals.bungee.convert.Converter;
import com.dbsoftwares.bungeeutilisals.bungee.convert.ConverterType;

public class BanManagementConverter extends Converter {

    public BanManagementConverter() {
        super(ConverterType.BANMANAGEMENT);
    }

    @Override
    public void startConverter() {
        // TODO: Connect to the BanManager database.
    }
}