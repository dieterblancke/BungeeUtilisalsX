package dev.endoy.bungeeutilisalsx.webapi.service;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.webapi.dto.CreatePunishmentInput;
import dev.endoy.bungeeutilisalsx.webapi.dto.Punishment;
import dev.endoy.bungeeutilisalsx.webapi.dto.RemovePunishmentInput;
import com.google.common.base.Strings;
import org.springframework.stereotype.Service;

@Service
public class PunishmentService
{

    public Punishment createBan( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().insertBan(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy()
        ).join() );
    }

    public Punishment createIPBan( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().insertIPBan(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy()
        ).join() );
    }

    public Punishment createTempban( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().insertTempBan(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy(),
            input.getExpireTime()
        ).join() );
    }

    public Punishment createIPTempban( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().insertTempIPBan(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy(),
            input.getExpireTime()
        ).join() );
    }

    public Punishment createMute( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().insertMute(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy()
        ).join() );
    }

    public Punishment createIPMute( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().insertIPMute(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy()
        ).join() );
    }

    public Punishment createTempmute( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().insertTempMute(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy(),
            input.getExpireTime()
        ).join() );
    }

    public Punishment createIPTempmute( final CreatePunishmentInput input )
    {
        return Punishment.of( BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().insertTempIPMute(
            input.getUuid(),
            input.getUser(),
            input.getIp(),
            input.getReason(),
            input.getServer(),
            input.isActive(),
            input.getExecutedBy(),
            input.getExpireTime()
        ).join() );
    }

    public void removeCurrentBan( final RemovePunishmentInput input )
    {
        if ( input.getUuid() == null )
        {
            throw new IllegalArgumentException( "UUID cannot be null!" );
        }

        BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().removeCurrentBan(
            input.getUuid(),
            input.getRemovedBy(),
            input.getServer()
        ).join();
    }

    public void removeCurrentIpban( final RemovePunishmentInput input )
    {
        if ( Strings.isNullOrEmpty( input.getIp() ) )
        {
            throw new IllegalArgumentException( "IP cannot be null!" );
        }

        BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().removeCurrentIPBan(
            input.getIp(),
            input.getRemovedBy(),
            input.getServer()
        ).join();
    }

    public void removeCurrentMute( final RemovePunishmentInput input )
    {
        if ( input.getUuid() == null )
        {
            throw new IllegalArgumentException( "UUID cannot be null!" );
        }

        BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().removeCurrentMute(
            input.getUuid(),
            input.getRemovedBy(),
            input.getServer()
        ).join();
    }

    public void removeCurrentIpmute( final RemovePunishmentInput input )
    {
        if ( Strings.isNullOrEmpty( input.getIp() ) )
        {
            throw new IllegalArgumentException( "IP cannot be null!" );
        }

        BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().removeCurrentIPMute(
            input.getIp(),
            input.getRemovedBy(),
            input.getServer()
        ).join();
    }
}
