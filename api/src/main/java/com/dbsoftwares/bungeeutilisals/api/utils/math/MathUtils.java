package com.dbsoftwares.bungeeutilisals.api.utils.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;


public class MathUtils {

    static public final float nanoToSec = 1 / 1000000000f;
    static public final float FLOAT_ROUNDING_ERROR = 0.000001f;
    static public final float PI = 3.141592653589793238462643383279f;
    static public final float PI2 = PI * 2;
    static public final float SQRT_3 = 1.73205080757f;
    static public final float E = 2.7182818284590452354f;
    static private final int SIN_BITS = 14;
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;
    static private final float radFull = PI * 2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;
    static public final float radiansToDegrees = 180f / PI;
    static public final float radDeg = radiansToDegrees;
    static public final float degreesToRadians = PI / 180;
    static public final float degRad = degreesToRadians;

    static private class Sin {

        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++) {
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            }
            for (int i = 0; i < 360; i += 90) {
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * degreesToRadians);
            }
        }
    }

    static public float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    static public float cos(float radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    static public float sinDeg(float degrees) {
        return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
    }

    static public float cosDeg(float degrees) {
        return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
    }

    static private final int ATAN2_BITS = 7;
    static private final int ATAN2_BITS2 = ATAN2_BITS << 1;
    static private final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    static private final int ATAN2_COUNT = ATAN2_MASK + 1;
    static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
    static private final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);

    static private class Atan2 {

        static final float[] table = new float[ATAN2_COUNT];

        static {
            for (int i = 0; i < ATAN2_DIM; i++) {
                for (int j = 0; j < ATAN2_DIM; j++) {
                    float x0 = (float) i / ATAN2_DIM;
                    float y0 = (float) j / ATAN2_DIM;
                    table[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
                }
            }
        }
    }

    public static boolean isInteger(Object object) {
        try {
            Integer.parseInt(object.toString());
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static Boolean isShort(Object object) {
        try {
            Short.parseShort(object.toString());
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isDouble(Object object) {
        try {
            Double.parseDouble(object.toString());
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isLong(Object object) {
        try {
            Long.parseLong(object.toString());
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isByte(Object object) {
        try {
            Byte.parseByte(object.toString());
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isFloat(Object object) {
        try {
            Float.parseFloat(object.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBigInteger(Object object) {
        try {
            new BigInteger(object.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBigDecimal(Object object) {
        try {
            new BigDecimal(object.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public float atan2(float y, float x) {
        float add, mul;
        if (x < 0) {
            if (y < 0) {
                y = -y;
                mul = 1;
            } else {
                mul = -1;
            }
            x = -x;
            add = -PI;
        } else {
            if (y < 0) {
                y = -y;
                mul = -1;
            } else {
                mul = 1;
            }
            add = 0;
        }
        float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

        if (invDiv == Float.POSITIVE_INFINITY) {
            return ((float) Math.atan2(y, x) + add) * mul;
        }

        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
    }

    static public Random random = new Random();

    static public int random(int range) {
        return random.nextInt(range + 1);
    }

    static public int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    static public boolean randomBoolean() {
        return random.nextBoolean();
    }

    static public boolean randomBoolean(float chance) {
        return MathUtils.random() < chance;
    }

    static public float random() {
        return random.nextFloat();
    }

    static public float random(float range) {
        return random.nextFloat() * range;
    }

    static public float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    static public int nextPowerOfTwo(int value) {
        if (value == 0) {
            return 1;
        }
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    static public boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    static public int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    static public short clamp(short value, short min, short max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    static private final int BIG_ENOUGH_INT = 16 * 1024;
    static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    static private final double CEIL = 0.9999999;
    static private final double BIG_ENOUGH_CEIL = 16384.999999999996;
    static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    static public int floor(float x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    static public int floorPositive(float x) {
        return (int) x;
    }

    static public int ceil(float x) {
        return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    static public int ceilPositive(float x) {
        return (int) (x + CEIL);
    }

    static public int round(float x) {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    static public String formatNumber(Object n, Integer c) {
        StringBuilder sb = new StringBuilder().append("##.");
        for (int I = 0; I < c; I++)
            sb.append("#");
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(n);
    }

    static public int roundPositive(float x) {
        return (int) (x + 0.5f);
    }

    static public boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    static public boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    static public boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    static public boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    public static <T> T getRandomFromArray(T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static double getRandomAngle() {
        return random.nextDouble() * 2 * Math.PI;
    }

    public static double randomDouble(double min, double max) {
        return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
    }

    public static float randomRangeFloat(float min, float max) {
        return (float) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
    }

    public static <T> T getRandomFromList(List<T> list) {
        return list.get(randomRangeInt(0, list.size()));
    }

    public static byte randomByte(int max) {
        return (byte) random.nextInt(max + 1);
    }

    public static int randomRangeInt(int min, int max) {
        return (int) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
    }
}