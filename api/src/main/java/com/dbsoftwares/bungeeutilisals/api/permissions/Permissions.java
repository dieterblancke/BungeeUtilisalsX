package com.dbsoftwares.bungeeutilisals.api.permissions;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Permissions {

    /* Bypass Permissions */
    private String BUNGEEUTILISALS = "bungeeutilisals.";
    private String BYPASS_PREFIX = BUNGEEUTILISALS + "bypass.";

    public String AD_BYPASS = BYPASS_PREFIX + "antiad";
    public String CAPS_BYPASS = BYPASS_PREFIX + "anticaps";
    public String SPAM_BYPASS = BYPASS_PREFIX + "antispam";
    public String SWEAR_BYPASS = BYPASS_PREFIX + "antiswear";

    private String PUNISHMENT_PREFIX = BUNGEEUTILISALS + "punishments.";
    public String BAN_PERMISSION = PUNISHMENT_PREFIX + "ban";
}