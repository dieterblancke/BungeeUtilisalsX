package com.dbsoftwares.bungeeutilisalsx.common.api.utils.other;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StaffRankData
{

    private final String name;
    private final String display;
    private final String permission;
    private final int priority;

}
