/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisalsx.common.api.utils;

public enum TimeUnit
{ // Same as the Java TimeUnit class, but added TICKS as a TimeUnit

    NANOSECONDS
            {
                @Override
                public long toNanos( long d )
                {
                    return d;
                }

                @Override
                public long toMicros( long d )
                {
                    return d / ( MICROS / NANOS );
                }

                @Override
                public long toMillis( long d )
                {
                    return d / ( MILLIS / NANOS );
                }

                @Override
                public long toTicks( long d )
                {
                    return d / ( TICK / NANOS );
                }

                @Override
                public long toSeconds( long d )
                {
                    return d / ( SECOND / NANOS );
                }

                @Override
                public long toMinutes( long d )
                {
                    return d / ( MINUTE / NANOS );
                }

                @Override
                public long toHours( long d )
                {
                    return d / ( HOUR / NANOS );
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / NANOS );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / NANOS );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toNanos( d );
                }

            },

    MICROSECONDS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, MICROS / NANOS, MAX / ( MICROS / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return d;
                }

                @Override
                public long toMillis( long d )
                {
                    return d / ( MILLIS / MICROS );
                }

                @Override
                public long toTicks( long d )
                {
                    return d / ( TICK / MICROS );
                }

                @Override
                public long toSeconds( long d )
                {
                    return d / ( SECOND / MICROS );
                }

                @Override
                public long toMinutes( long d )
                {
                    return d / ( MINUTE / MICROS );
                }

                @Override
                public long toHours( long d )
                {
                    return d / ( HOUR / MICROS );
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / MICROS );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / MICROS );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toMicros( d );
                }

            },

    MILLISECONDS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, MILLIS / NANOS, MAX / ( MILLIS / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, MILLIS / MICROS, MAX / ( MILLIS / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return d;
                }

                @Override
                public long toTicks( long d )
                {
                    return d / ( TICK / MILLIS );
                }

                @Override
                public long toSeconds( long d )
                {
                    return d / ( SECOND / MILLIS );
                }

                @Override
                public long toMinutes( long d )
                {
                    return d / ( MINUTE / MILLIS );
                }

                @Override
                public long toHours( long d )
                {
                    return d / ( HOUR / MILLIS );
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / MILLIS );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / MILLIS );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toMillis( d );
                }

            },

    TICKS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, TICK / NANOS, MAX / ( TICK / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, TICK / MICROS, MAX / ( TICK / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return x( d, TICK / MILLIS, MAX / ( TICK / MILLIS ) );
                }

                @Override
                public long toTicks( long d )
                {
                    return d;
                }

                @Override
                public long toSeconds( long d )
                {
                    return d / ( SECOND / TICK );
                }

                @Override
                public long toMinutes( long d )
                {
                    return d / ( MINUTE / TICK );
                }

                @Override
                public long toHours( long d )
                {
                    return d / ( HOUR / TICK );
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / TICK );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / TICK );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toSeconds( d );
                }

            },

    SECONDS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, SECOND / NANOS, MAX / ( SECOND / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, SECOND / MICROS, MAX / ( SECOND / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return x( d, SECOND / MILLIS, MAX / ( SECOND / MILLIS ) );
                }

                @Override
                public long toTicks( long d )
                {
                    return x( d, SECOND / TICK, MAX / ( SECOND / TICK ) );
                }

                @Override
                public long toSeconds( long d )
                {
                    return d;
                }

                @Override
                public long toMinutes( long d )
                {
                    return d / ( MINUTE / SECOND );
                }

                @Override
                public long toHours( long d )
                {
                    return d / ( HOUR / SECOND );
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / SECOND );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / SECOND );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toSeconds( d );
                }

            },

    MINUTES
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, MINUTE / NANOS, MAX / ( MINUTE / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, MINUTE / MICROS, MAX / ( MINUTE / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return x( d, MINUTE / MILLIS, MAX / ( MINUTE / MILLIS ) );
                }

                @Override
                public long toTicks( long d )
                {
                    return x( d, MINUTE / TICK, MAX / ( MINUTE / TICK ) );
                }

                @Override
                public long toSeconds( long d )
                {
                    return x( d, MINUTE / SECOND, MAX / ( MINUTE / SECOND ) );
                }

                @Override
                public long toMinutes( long d )
                {
                    return d;
                }

                @Override
                public long toHours( long d )
                {
                    return d / ( HOUR / MINUTE );
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / MINUTE );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / MINUTE );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toMinutes( d );
                }


            },

    HOURS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, HOUR / NANOS, MAX / ( HOUR / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, HOUR / MICROS, MAX / ( HOUR / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return x( d, HOUR / MILLIS, MAX / ( HOUR / MILLIS ) );
                }

                @Override
                public long toTicks( long d )
                {
                    return x( d, HOUR / TICK, MAX / ( HOUR / TICK ) );
                }

                @Override
                public long toSeconds( long d )
                {
                    return x( d, HOUR / SECOND, MAX / ( HOUR / SECOND ) );
                }

                @Override
                public long toMinutes( long d )
                {
                    return x( d, HOUR / MINUTE, MAX / ( HOUR / MINUTE ) );
                }

                @Override
                public long toHours( long d )
                {
                    return d;
                }

                @Override
                public long toDays( long d )
                {
                    return d / ( DAY / HOUR );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / HOUR );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toHours( d );
                }

            },

    DAYS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, DAY / NANOS, MAX / ( DAY / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, DAY / MICROS, MAX / ( DAY / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return x( d, DAY / MILLIS, MAX / ( DAY / MILLIS ) );
                }

                @Override
                public long toTicks( long d )
                {
                    return x( d, DAY / TICK, MAX / ( DAY / TICK ) );
                }

                @Override
                public long toSeconds( long d )
                {
                    return x( d, DAY / SECOND, MAX / ( DAY / SECOND ) );
                }

                @Override
                public long toMinutes( long d )
                {
                    return x( d, DAY / MINUTE, MAX / ( DAY / MINUTE ) );
                }

                @Override
                public long toHours( long d )
                {
                    return x( d, DAY / HOUR, MAX / ( DAY / HOUR ) );
                }

                @Override
                public long toDays( long d )
                {
                    return d;
                }

                @Override
                public long toWeeks( long d )
                {
                    return d / ( WEEK / DAY );
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toDays( d );
                }

            },

    WEEKS
            {
                @Override
                public long toNanos( long d )
                {
                    return x( d, WEEK / NANOS, MAX / ( WEEK / NANOS ) );
                }

                @Override
                public long toMicros( long d )
                {
                    return x( d, WEEK / MICROS, MAX / ( WEEK / MICROS ) );
                }

                @Override
                public long toMillis( long d )
                {
                    return x( d, WEEK / MILLIS, MAX / ( WEEK / MILLIS ) );
                }

                @Override
                public long toTicks( long d )
                {
                    return x( d, WEEK / TICK, MAX / ( WEEK / TICK ) );
                }

                @Override
                public long toSeconds( long d )
                {
                    return x( d, WEEK / SECOND, MAX / ( WEEK / SECOND ) );
                }

                @Override
                public long toMinutes( long d )
                {
                    return x( d, WEEK / MINUTE, MAX / ( WEEK / MINUTE ) );
                }

                @Override
                public long toHours( long d )
                {
                    return x( d, WEEK / HOUR, MAX / ( WEEK / HOUR ) );
                }

                @Override
                public long toDays( long d )
                {
                    return x( d, WEEK / DAY, MAX / ( WEEK / DAY ) );
                }

                @Override
                public long toWeeks( long d )
                {
                    return d;
                }

                @Override
                public long convert( long d, TimeUnit u )
                {
                    return u.toDays( d );
                }

            };

    static final long NANOS = 1L;
    static final long MICROS = NANOS * 1000L;
    static final long MILLIS = MICROS * 1000L;
    static final long TICK = MILLIS * 50L;
    static final long SECOND = TICK * 20L;
    static final long MINUTE = SECOND * 60L;
    static final long HOUR = MINUTE * 60L;
    static final long DAY = HOUR * 24L;
    static final long WEEK = DAY * 7L;
    static final long MAX = Long.MAX_VALUE;

    static long x( long d, long m, long over )
    {
        if ( d > over )
        {
            return Long.MAX_VALUE;
        }
        if ( d < -over )
        {
            return Long.MIN_VALUE;
        }
        return d * m;
    }

    public static boolean isUnit( String unit )
    {
        for ( TimeUnit time : values() )
        {
            if ( time.toString().equalsIgnoreCase( unit ) )
            {
                return true;
            }
        }
        return false;
    }

    public static TimeUnit valueOfOrElse( String unit, TimeUnit def )
    {
        if ( isUnit( unit ) )
        {
            return valueOf( unit );
        }
        return def;
    }

    public long convert( long sourceDuration, TimeUnit sourceUnit )
    {
        throw new AbstractMethodError();
    }

    public long toNanos( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toMicros( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toMillis( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toSeconds( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toTicks( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toMinutes( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toHours( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toDays( long duration )
    {
        throw new AbstractMethodError();
    }

    public long toWeeks( long duration )
    {
        throw new AbstractMethodError();
    }

    public java.util.concurrent.TimeUnit toJavaTimeUnit()
    {
        try
        {
            return java.util.concurrent.TimeUnit.valueOf( toString() );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }
}