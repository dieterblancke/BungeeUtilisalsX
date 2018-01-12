package com.dbsoftwares.bungeeutilisals.api.punishments;

/*
 * Created by DBSoftwares on 24 februari 2017
 * Developer: Dieter Blancke
 * Project: CMS
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public enum PunishmentType {

    BAN(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.BansTable")),
    TEMPBAN(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.TempBansTable")),
    IPBAN(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.IPBansTable")),
    IPTEMPBAN(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.IPTempBansTable")),
    MUTE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.MutesTable")),
    TEMPMUTE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.TempMutesTable")),
    IPMUTE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.IPMutesTable")),
    IPTEMPMUTE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.IPTempMutesTable")),
    KICK(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.KicksTable")),
    WARN(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.tables.WarnsTable"));

    Class<? extends PunishmentTable> table;

    @SuppressWarnings("unchecked")
        // We know it an instance of PunishmentTable
    PunishmentType(Class<?> table) {
        this.table = (Class<? extends PunishmentTable>) table;
    }

    public boolean isRemovable() {
        return toString().contains("MUTE") || toString().contains("BAN");
    }


    // Reflection stuff
    public PunishmentTable newInstance() {
        try {
            return table.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setActive(Object instance, boolean active) {
        set(instance, "Active", active);
    }

    public void setRemovedBy(Object instance, String removedby) {
        set(instance, "Removedby", removedby);
    }

    private void set(Object instance, String methodTail, Object parameter) {
        Method set = ReflectionUtils.getMethod(table, "set" + methodTail, parameter.getClass());

        if (set != null) {
            try {
                set.invoke(instance, parameter);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public Class<? extends PunishmentTable> getTable() {
        return table;
    }
}