package be.dieterblancke.bungeeutilisalsx.common.protocolize.guis.parties;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

public class PartyGuiConfig extends GuiConfig
{
    public PartyGuiConfig()
    {
        super( "/configurations/gui/party/party.yml", PartyGuiConfigItem.class );
    }
}
