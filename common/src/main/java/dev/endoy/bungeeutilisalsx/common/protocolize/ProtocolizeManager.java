package dev.endoy.bungeeutilisalsx.common.protocolize;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.protocolize.guis.GuiManager;
import dev.endoy.configuration.api.ISection;
import dev.simplix.protocolize.api.inventory.Inventory;

public interface ProtocolizeManager
{

    void sendSound( User user, SoundData soundData );

    void closeInventory( User user );

    void openInventory( User user, Inventory inventory );

    GuiManager getGuiManager();

    record SoundData(String sound, String category, float volume, float pitch)
    {

        public static SoundData fromSection( final ISection section )
        {
            return new SoundData(
                    section.getString( "sound" ),
                    section.exists( "category" ) ? section.getString( "category" ) : "master",
                    section.exists( "volume" ) ? section.getFloat( "volume" ) : 1f,
                    section.exists( "pitch" ) ? section.getFloat( "pitch" ) : 1f
            );
        }

        public static SoundData fromSection( final ISection configuration, final String path )
        {
            if ( !configuration.exists( path ) )
            {
                return null;
            }

            if ( configuration.isSection( path ) )
            {
                return fromSection( configuration.getSection( path ) );
            }
            else
            {
                return new SoundData(
                        configuration.getString( path ),
                        "master",
                        1f,
                        1f
                );
            }
        }
    }
}
