package be.dieterblancke.bungeeutilisalsx.common.api.user;

import com.google.gson.internal.LazilyParsedNumber;

public class UserSetting
{

    private UserSettingType settingType;
    private Object value;

    public void set( final Object value )
    {
        this.value = value;
    }

    public boolean isBoolean()
    {
        return this.value instanceof Boolean;
    }

    public boolean getAsBoolean()
    {
        return this.isBoolean() ? (Boolean) this.value : Boolean.parseBoolean( this.getAsString() );
    }

    public boolean isNumber()
    {
        return this.value instanceof Number;
    }

    public Number getAsNumber()
    {
        return this.value instanceof String ? new LazilyParsedNumber( this.getAsString() ) : (Number) this.value;
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
