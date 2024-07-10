package dev.endoy.bungeeutilisalsx.common.protocolize.guis.parties;

import dev.endoy.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

public class PartyGuiConfig extends GuiConfig
{
    public PartyGuiConfig()
    {
        super( "/configurations/gui/party/party.yml", PartyGuiConfigItem.class );
    }
}
