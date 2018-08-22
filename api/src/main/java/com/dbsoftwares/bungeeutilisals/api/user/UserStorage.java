package com.dbsoftwares.bungeeutilisals.api.user;

/*
 * Created by DBSoftwares on 26 september 2017
 * Developer: Dieter Blancke
 * Project: centrixcore
 */

import com.dbsoftwares.bungeeutilisals.api.language.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStorage {

    private UUID Uuid;
    private String userName;
    private String ip;
    private Language language;
    private Date firstLogin;
    private Date lastLogout;

}