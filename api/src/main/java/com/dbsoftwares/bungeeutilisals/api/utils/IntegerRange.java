package com.dbsoftwares.bungeeutilisals.api.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IntegerRange
{

    private final int min;
    private final int max;

    public int keepWithinRange(final Number number) {
        final int i = number.intValue();
        if (i < min) {
            return min;
        } else {
            return Math.min( i, max );
        }
    }
}
