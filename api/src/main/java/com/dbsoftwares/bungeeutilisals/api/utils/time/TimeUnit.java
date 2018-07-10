package com.dbsoftwares.bungeeutilisals.api.utils.time;

/*
 * Added TICKS to the TimeUnit class of Oracle.
 */
public enum TimeUnit {

    NANOSECONDS {
        public long toNanos(long d) {
            return d;
        }

        public long toMicros(long d) {
            return d / (micros / nanos);
        }

        public long toMillis(long d) {
            return d / (millis / nanos);
        }

        public long toTicks(long d) {
            return d / (ticks / nanos);
        }

        public long toSeconds(long d) {
            return d / (seconds / nanos);
        }

        public long toMinutes(long d) {
            return d / (minutes / nanos);
        }

        public long toHours(long d) {
            return d / (hours / nanos);
        }

        public long toDays(long d) {
            return d / (days / nanos);
        }

        public long toWeeks(long d) {
            return d / (weeks / nanos);
        }

        public long convert(long d, TimeUnit u) {
            return u.toNanos(d);
        }

        int excessNanos(long d, long m) {
            return (int) (d - (m * millis));
        }
    },

    MICROSECONDS {
        public long toNanos(long d) {
            return x(d, micros / nanos, MAX / (micros / nanos));
        }

        public long toMicros(long d) {
            return d;
        }

        public long toMillis(long d) {
            return d / (millis / micros);
        }

        public long toTicks(long d) {
            return d / (ticks / micros);
        }

        public long toSeconds(long d) {
            return d / (seconds / micros);
        }

        public long toMinutes(long d) {
            return d / (minutes / micros);
        }

        public long toHours(long d) {
            return d / (hours / micros);
        }

        public long toDays(long d) {
            return d / (days / micros);
        }

        public long toWeeks(long d) {
            return d / (weeks / micros);
        }

        public long convert(long d, TimeUnit u) {
            return u.toMicros(d);
        }

        int excessNanos(long d, long m) {
            return (int) ((d * micros) - (m * millis));
        }
    },

    MILLISECONDS {
        public long toNanos(long d) {
            return x(d, millis / nanos, MAX / (millis / nanos));
        }

        public long toMicros(long d) {
            return x(d, millis / micros, MAX / (millis / micros));
        }

        public long toMillis(long d) {
            return d;
        }

        public long toTicks(long d) {
            return d / (ticks / millis);
        }

        public long toSeconds(long d) {
            return d / (seconds / millis);
        }

        public long toMinutes(long d) {
            return d / (minutes / millis);
        }

        public long toHours(long d) {
            return d / (hours / millis);
        }

        public long toDays(long d) {
            return d / (days / millis);
        }

        public long toWeeks(long d) {
            return d / (weeks / millis);
        }

        public long convert(long d, TimeUnit u) {
            return u.toMillis(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    },

    TICKS {
        public long toNanos(long d) {
            return x(d, ticks / nanos, MAX / (ticks / nanos));
        }

        public long toMicros(long d) {
            return x(d, ticks / micros, MAX / (ticks / micros));
        }

        public long toMillis(long d) {
            return x(d, ticks / millis, MAX / (ticks / millis));
        }

        public long toTicks(long d) {
            return d;
        }

        public long toSeconds(long d) {
            return d / (seconds / ticks);
        }

        public long toMinutes(long d) {
            return d / (minutes / ticks);
        }

        public long toHours(long d) {
            return d / (hours / ticks);
        }

        public long toDays(long d) {
            return d / (days / ticks);
        }

        public long toWeeks(long d) {
            return d / (weeks / ticks);
        }

        public long convert(long d, TimeUnit u) {
            return u.toSeconds(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    },

    SECONDS {
        public long toNanos(long d) {
            return x(d, seconds / nanos, MAX / (seconds / nanos));
        }

        public long toMicros(long d) {
            return x(d, seconds / micros, MAX / (seconds / micros));
        }

        public long toMillis(long d) {
            return x(d, seconds / millis, MAX / (seconds / millis));
        }

        public long toTicks(long d) {
            return x(d, seconds / ticks, MAX / (seconds / ticks));
        }

        public long toSeconds(long d) {
            return d;
        }

        public long toMinutes(long d) {
            return d / (minutes / seconds);
        }

        public long toHours(long d) {
            return d / (hours / seconds);
        }

        public long toDays(long d) {
            return d / (days / seconds);
        }

        public long toWeeks(long d) {
            return d / (weeks / seconds);
        }

        public long convert(long d, TimeUnit u) {
            return u.toSeconds(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    },

    MINUTES {
        public long toNanos(long d) {
            return x(d, minutes / nanos, MAX / (minutes / nanos));
        }

        public long toMicros(long d) {
            return x(d, minutes / micros, MAX / (minutes / micros));
        }

        public long toMillis(long d) {
            return x(d, minutes / millis, MAX / (minutes / millis));
        }

        public long toTicks(long d) {
            return x(d, minutes / ticks, MAX / (minutes / ticks));
        }

        public long toSeconds(long d) {
            return x(d, minutes / seconds, MAX / (minutes / seconds));
        }

        public long toMinutes(long d) {
            return d;
        }

        public long toHours(long d) {
            return d / (hours / minutes);
        }

        public long toDays(long d) {
            return d / (days / minutes);
        }

        public long toWeeks(long d) {
            return d / (weeks / minutes);
        }

        public long convert(long d, TimeUnit u) {
            return u.toMinutes(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    },

    HOURS {
        public long toNanos(long d) {
            return x(d, hours / nanos, MAX / (hours / nanos));
        }

        public long toMicros(long d) {
            return x(d, hours / micros, MAX / (hours / micros));
        }

        public long toMillis(long d) {
            return x(d, hours / millis, MAX / (hours / millis));
        }

        public long toTicks(long d) {
            return x(d, hours / ticks, MAX / (hours / ticks));
        }

        public long toSeconds(long d) {
            return x(d, hours / seconds, MAX / (hours / seconds));
        }

        public long toMinutes(long d) {
            return x(d, hours / minutes, MAX / (hours / minutes));
        }

        public long toHours(long d) {
            return d;
        }

        public long toDays(long d) {
            return d / (days / hours);
        }

        public long toWeeks(long d) {
            return d / (weeks / hours);
        }

        public long convert(long d, TimeUnit u) {
            return u.toHours(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    },

    DAYS {
        public long toNanos(long d) {
            return x(d, days / nanos, MAX / (days / nanos));
        }

        public long toMicros(long d) {
            return x(d, days / micros, MAX / (days / micros));
        }

        public long toMillis(long d) {
            return x(d, days / millis, MAX / (days / millis));
        }

        public long toTicks(long d) {
            return x(d, days / ticks, MAX / (days / ticks));
        }

        public long toSeconds(long d) {
            return x(d, days / seconds, MAX / (days / seconds));
        }

        public long toMinutes(long d) {
            return x(d, days / minutes, MAX / (days / minutes));
        }

        public long toHours(long d) {
            return x(d, days / hours, MAX / (days / hours));
        }

        public long toDays(long d) {
            return d;
        }

        public long toWeeks(long d) {
            return d / (weeks / days);
        }

        public long convert(long d, TimeUnit u) {
            return u.toDays(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    },

    WEEKS {
        public long toNanos(long d) {
            return x(d, weeks / nanos, MAX / (weeks / nanos));
        }

        public long toMicros(long d) {
            return x(d, weeks / micros, MAX / (weeks / micros));
        }

        public long toMillis(long d) {
            return x(d, weeks / millis, MAX / (weeks / millis));
        }

        public long toTicks(long d) {
            return x(d, weeks / ticks, MAX / (weeks / ticks));
        }

        public long toSeconds(long d) {
            return x(d, weeks / seconds, MAX / (weeks / seconds));
        }

        public long toMinutes(long d) {
            return x(d, weeks / minutes, MAX / (weeks / minutes));
        }

        public long toHours(long d) {
            return x(d, weeks / hours, MAX / (weeks / hours));
        }

        public long toDays(long d) {
            return x(d, weeks / days, MAX / (weeks / days));
        }

        public long toWeeks(long d) {
            return d;
        }

        public long convert(long d, TimeUnit u) {
            return u.toDays(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    };

    static final long nanos = 1L;
    static final long micros = nanos * 1000L;
    static final long millis = micros * 1000L;
    static final long ticks = millis * 50L;
    static final long seconds = ticks * 20L;
    static final long minutes = seconds * 60L;
    static final long hours = minutes * 60L;
    static final long days = hours * 24L;
    static final long weeks = days * 7L;
    static final long MAX = Long.MAX_VALUE;

    static long x(long d, long m, long over) {
        if (d > over) return Long.MAX_VALUE;
        if (d < -over) return Long.MIN_VALUE;
        return d * m;
    }

    public long convert(long sourceDuration, TimeUnit sourceUnit) {
        throw new AbstractMethodError();
    }

    public long toNanos(long duration) {
        throw new AbstractMethodError();
    }

    public long toMicros(long duration) {
        throw new AbstractMethodError();
    }

    public long toMillis(long duration) {
        throw new AbstractMethodError();
    }

    public long toSeconds(long duration) {
        throw new AbstractMethodError();
    }

    public long toTicks(long duration) {
        throw new AbstractMethodError();
    }

    public long toMinutes(long duration) {
        throw new AbstractMethodError();
    }

    public long toHours(long duration) {
        throw new AbstractMethodError();
    }

    public long toDays(long duration) {
        throw new AbstractMethodError();
    }

    public long toWeeks(long duration) {
        throw new AbstractMethodError();
    }

    abstract int excessNanos(long d, long m);

    public void timedWait(Object obj, long timeout) throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            obj.wait(ms, ns);
        }
    }

    public void timedJoin(Thread thread, long timeout)
            throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            thread.join(ms, ns);
        }
    }

    public void sleep(long timeout) throws InterruptedException {
        if (timeout > 0) {
            long ms = toMillis(timeout);
            int ns = excessNanos(timeout, ms);
            Thread.sleep(ms, ns);
        }
    }

    public static boolean isUnit(String unit) {
        for (TimeUnit time : values()) {
            if (time.toString().equalsIgnoreCase(unit)) {
                return true;
            }
        }
        return false;
    }

    public static TimeUnit valueOfOrElse(String unit, TimeUnit def) {
        if (isUnit(unit)) {
            return valueOf(unit);
        }
        return def;
    }

    public java.util.concurrent.TimeUnit toJavaTimeUnit() {
        try {
            return java.util.concurrent.TimeUnit.valueOf(toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}