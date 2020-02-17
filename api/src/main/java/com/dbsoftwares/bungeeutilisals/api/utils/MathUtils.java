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

package com.dbsoftwares.bungeeutilisals.api.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class MathUtils
{

    public static final float NANO_TO_SEC = 1 / 1000000000f;
    public static final float FLOAT_ROUNDING_ERROR = 0.000001f;
    public static final float PI = 3.141592653589793238462643383279f;
    public static final float PI2 = PI * 2;
    public static final float SQRT_3 = 1.73205080757f;
    public static final float E = 2.7182818284590452354f;
    public static final float RADIANS_TO_DEGREES = 180f / PI;
    public static final float RAD_DEG = RADIANS_TO_DEGREES;
    public static final float DEGREES_TO_RADIANS = PI / 180;
    public static final float DEG_RAD = DEGREES_TO_RADIANS;
    public static final Random RANDOM_INSTANCE = new Random();
    private static final int SIN_BITS = 14;
    private static final int SIN_MASK = ~(-1 << SIN_BITS);
    private static final int SIN_COUNT = SIN_MASK + 1;
    private static final float RAD_FULL = PI * 2;
    private static final float DEG_FULL = 360;
    private static final float RAD_TO_INDEX = SIN_COUNT / RAD_FULL;
    private static final float DEG_TO_INDEX = SIN_COUNT / DEG_FULL;
    private static final int ATAN2_BITS = 7;
    private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
    private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
    private static final int ATAN2_COUNT = ATAN2_MASK + 1;
    private static final int ATAN2_DIM = (int) Math.sqrt( ATAN2_COUNT );
    private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);
    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double CEIL = 0.9999999;
    private static final double BIG_ENOUGH_CEIL = 16384.999999999996;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    private MathUtils()
    {
    }

    public static float sin( float radians )
    {
        return Sin.table[(int) (radians * RAD_TO_INDEX) & SIN_MASK];
    }

    public static float cos( float radians )
    {
        return Sin.table[(int) ((radians + PI / 2) * RAD_TO_INDEX) & SIN_MASK];
    }

    public static float sinDeg( float degrees )
    {
        return Sin.table[(int) (degrees * DEG_TO_INDEX) & SIN_MASK];
    }

    public static float cosDeg( float degrees )
    {
        return Sin.table[(int) ((degrees + 90) * DEG_TO_INDEX) & SIN_MASK];
    }

    public static boolean isInteger( Object object )
    {
        try
        {
            Integer.parseInt( object.toString() );
            return true;
        }
        catch ( Exception exc )
        {
            return false;
        }
    }

    public static Boolean isShort( Object object )
    {
        try
        {
            Short.parseShort( object.toString() );
            return true;
        }
        catch ( Exception exc )
        {
            return false;
        }
    }

    public static boolean isDouble( Object object )
    {
        try
        {
            Double.parseDouble( object.toString() );
            return true;
        }
        catch ( Exception exc )
        {
            return false;
        }
    }

    public static boolean isLong( Object object )
    {
        try
        {
            Long.parseLong( object.toString() );
            return true;
        }
        catch ( Exception exc )
        {
            return false;
        }
    }

    public static boolean isByte( Object object )
    {
        try
        {
            Byte.parseByte( object.toString() );
            return true;
        }
        catch ( Exception exc )
        {
            return false;
        }
    }

    public static boolean isFloat( Object object )
    {
        try
        {
            Float.parseFloat( object.toString() );
            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    public static boolean isBigInteger( Object object )
    {
        try
        {
            new BigInteger( object.toString() );
            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    public static boolean isBigDecimal( Object object )
    {
        try
        {
            new BigDecimal( object.toString() );
            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    public static float atan2( float y, float x )
    {
        float add;
        float mul;
        if ( x < 0 )
        {
            if ( y < 0 )
            {
                y = -y;
                mul = 1;
            }
            else
            {
                mul = -1;
            }
            x = -x;
            add = -PI;
        }
        else
        {
            if ( y < 0 )
            {
                y = -y;
                mul = -1;
            }
            else
            {
                mul = 1;
            }
            add = 0;
        }
        float invDiv = 1 / ((x < y ? y : x) * INV_ATAN2_DIM_MINUS_1);

        if ( invDiv == Float.POSITIVE_INFINITY )
        {
            return ((float) Math.atan2( y, x ) + add) * mul;
        }

        int xi = (int) (x * invDiv);
        int yi = (int) (y * invDiv);
        return (Atan2.table[yi * ATAN2_DIM + xi] + add) * mul;
    }

    public static int random( int range )
    {
        return RANDOM_INSTANCE.nextInt( range + 1 );
    }

    public static int random( int start, int end )
    {
        return start + RANDOM_INSTANCE.nextInt( end - start + 1 );
    }

    public static boolean randomBoolean()
    {
        return RANDOM_INSTANCE.nextBoolean();
    }

    public static boolean randomBoolean( float chance )
    {
        return MathUtils.random() < chance;
    }

    public static float random()
    {
        return RANDOM_INSTANCE.nextFloat();
    }

    public static float random( float range )
    {
        return RANDOM_INSTANCE.nextFloat() * range;
    }

    public static float random( float start, float end )
    {
        return start + RANDOM_INSTANCE.nextFloat() * (end - start);
    }

    public static int nextPowerOfTwo( int value )
    {
        if ( value == 0 )
        {
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

    public static boolean isPowerOfTwo( int value )
    {
        return value != 0 && (value & value - 1) == 0;
    }

    public static int clamp( int value, int min, int max )
    {
        if ( value < min )
        {
            return min;
        }
        if ( value > max )
        {
            return max;
        }
        return value;
    }

    public static short clamp( short value, short min, short max )
    {
        if ( value < min )
        {
            return min;
        }
        if ( value > max )
        {
            return max;
        }
        return value;
    }

    public static float clamp( float value, float min, float max )
    {
        if ( value < min )
        {
            return min;
        }
        if ( value > max )
        {
            return max;
        }
        return value;
    }

    public static int floor( float x )
    {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static int floorPositive( float x )
    {
        return (int) x;
    }

    public static int ceil( float x )
    {
        return (int) (x + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    public static int ceilPositive( float x )
    {
        return (int) (x + CEIL);
    }

    public static int round( float x )
    {
        return (int) (x + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    public static String formatNumber( Object n, Integer c )
    {
        StringBuilder sb = new StringBuilder().append( "##." );
        for ( int I = 0; I < c; I++ )
            sb.append( "#" );
        DecimalFormat df = new DecimalFormat( sb.toString() );
        return df.format( n );
    }

    public static int roundPositive( float x )
    {
        return (int) (x + 0.5f);
    }

    public static boolean isZero( float value )
    {
        return Math.abs( value ) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isZero( float value, float tolerance )
    {
        return Math.abs( value ) <= tolerance;
    }

    public static boolean isEqual( float a, float b )
    {
        return Math.abs( a - b ) <= FLOAT_ROUNDING_ERROR;
    }

    public static boolean isEqual( float a, float b, float tolerance )
    {
        return Math.abs( a - b ) <= tolerance;
    }

    public static <T> T getRandomFromArray( T[] array )
    {
        return array[RANDOM_INSTANCE.nextInt( array.length )];
    }

    public static double getRandomAngle()
    {
        return RANDOM_INSTANCE.nextDouble() * 2 * Math.PI;
    }

    public static double randomDouble( double min, double max )
    {
        return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
    }

    public static float randomRangeFloat( float min, float max )
    {
        return (float) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
    }

    public static <T> T getRandomFromList( List<T> list )
    {
        return list.get( randomRangeInt( 0, list.size() ) );
    }

    public static byte randomByte( int max )
    {
        return (byte) RANDOM_INSTANCE.nextInt( max + 1 );
    }

    public static int randomRangeInt( int min, int max )
    {
        return (int) (RANDOM_INSTANCE.nextDouble() < 0.5 ? ((1 - RANDOM_INSTANCE.nextDouble()) * (max - min) + min) : (RANDOM_INSTANCE.nextDouble() * (max - min) + min));
    }

    private static class Sin
    {

        static final float[] table = new float[SIN_COUNT];

        static
        {
            for ( int i = 0; i < SIN_COUNT; i++ )
            {
                table[i] = (float) Math.sin( (i + 0.5f) / SIN_COUNT * RAD_FULL );
            }
            for ( int i = 0; i < 360; i += 90 )
            {
                table[(int) (i * DEG_TO_INDEX) & SIN_MASK] = (float) Math.sin( i * DEGREES_TO_RADIANS );
            }
        }
    }

    private static class Atan2
    {

        static final float[] table = new float[ATAN2_COUNT];

        static
        {
            for ( int i = 0; i < ATAN2_DIM; i++ )
            {
                for ( int j = 0; j < ATAN2_DIM; j++ )
                {
                    float x0 = (float) i / ATAN2_DIM;
                    float y0 = (float) j / ATAN2_DIM;
                    table[j * ATAN2_DIM + i] = (float) Math.atan2( y0, x0 );
                }
            }
        }
    }
}