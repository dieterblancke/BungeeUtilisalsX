package com.dbsoftwares.bungeeutilisals.convert.converters;

import com.dbsoftwares.bungeeutilisals.convert.Converter;
import com.dbsoftwares.bungeeutilisals.convert.ConverterType;

public class BanManagementConverter extends Converter {

    public BanManagementConverter() {
        super(ConverterType.BANMANAGEMENT);
    }

    @Override
    public void startConverter() {
        // TODO: Connect to the BanManager database.
    }
}