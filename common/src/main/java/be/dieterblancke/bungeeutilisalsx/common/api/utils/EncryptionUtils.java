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

package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtils
{

    private static SecretKeySpec secretKey;
    private static byte[] key;

    private static SecretKeySpec createKey( final String key )
    {
        try
        {
            final MessageDigest sha = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = key.getBytes( StandardCharsets.UTF_8 );
            bytes = sha.digest( bytes );
            bytes = Arrays.copyOf( bytes, 16 );
            return secretKey = new SecretKeySpec( bytes, "AES" );
        }
        catch ( NoSuchAlgorithmException ignored )
        {
        }
        return null;
    }

    public static String encrypt( final String text, final String secret )
    {
        try
        {
            final SecretKeySpec key = createKey( secret );
            final Cipher cipher = Cipher.getInstance( "AES/ECB/PKCS5Padding" );

            cipher.init( Cipher.ENCRYPT_MODE, secretKey );
            return Base64.getEncoder().encodeToString( cipher.doFinal( text.getBytes( StandardCharsets.UTF_8 ) ) );
        }
        catch ( Exception ignored )
        {
        }
        return null;
    }

    public static String decrypt( final String text, final String secret )
    {
        try
        {
            final SecretKeySpec key = createKey( secret );
            final Cipher cipher = Cipher.getInstance( "AES/ECB/PKCS5PADDING" );

            cipher.init( Cipher.DECRYPT_MODE, secretKey );
            return new String( cipher.doFinal( Base64.getDecoder().decode( text ) ) );
        }
        catch ( Exception ignored )
        {
        }
        return null;
    }
}
