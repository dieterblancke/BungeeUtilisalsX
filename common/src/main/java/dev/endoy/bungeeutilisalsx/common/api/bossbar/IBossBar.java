package dev.endoy.bungeeutilisalsx.common.api.bossbar;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface IBossBar
{

    /**
     * @return Unique Identifier of the Bossbar.
     */
    UUID getUuid();

    /**
     * @return Bossbar color.
     */
    BarColor getColor();

    void setColor( BarColor color );

    /**
     * @return Bossbar style.
     */
    BarStyle getStyle();

    void setStyle( BarStyle style );

    /**
     * @return Bossbar progress.
     */
    float getProgress();

    void setProgress( float progress );

    /**
     * @return Bossbar visibility.
     */
    boolean isVisible();

    void setVisible( boolean visible );

    @Deprecated
    String getMessage();

    @Deprecated
    void setMessage( String title );

    void setMessage( Component title );

    Component getBaseComponent();

    void addUser( User user );

    void removeUser( User user );

    /**
     * Check whether an user has the bossbar or not.
     *
     * @param user The user to check.
     * @return true if user receives bossbar, false if not.
     */
    boolean hasUser( User user );

    void clearUsers();

    /**
     * Unregisters the BossBar.
     */
    void unregister();
}