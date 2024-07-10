package dev.endoy.bungeeutilisalsx.common.api.user;

import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import com.google.gson.internal.LazilyParsedNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSetting
{

    private final UserSettingType settingType;
    private Object value;

    public boolean isBoolean()
    {
        return this.value instanceof Boolean;
    }

    public boolean getAsBoolean()
    {
        if ( this.isNumber() )
        {
            return this.getAsNumber().intValue() == 1;
        }
        return this.isBoolean() ? (Boolean) this.value : Boolean.parseBoolean( this.getAsString() );
    }

    public boolean isNumber()
    {
        return this.value instanceof Number || MathUtils.isInteger( this.value ) || MathUtils.isDouble( this.value );
    }

    public Number getAsNumber()
    {
        return this.value instanceof String ? new LazilyParsedNumber( (String) this.value ) : (Number) this.value;
    }

    public boolean isString()
    {
        return this.value instanceof String;
    }

    public String getAsString()
    {
        if ( this.isNumber() )
        {
            return this.getAsNumber().toString();
        }
        else
        {
            return this.isBoolean() ? ( (Boolean) this.value ).toString() : (String) this.value;
        }
    }

    public double getAsDouble()
    {
        return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble( this.getAsString() );
    }

    public long getAsLong()
    {
        return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong( this.getAsString() );
    }

    public int getAsInt()
    {
        return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt( this.getAsString() );
    }
}
