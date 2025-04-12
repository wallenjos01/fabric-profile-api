package org.wallentines.pflib.api;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.wallentines.pflib.impl.PlayerProfilesImpl;

@ApiStatus.NonExtendable
public interface PlayerProfiles {

    /**
     * Changes the given player's profile. Changes will not be synced to clients.
     * @param player The player to modify
     * @param profile The player's new profile
     * @see PlayerProfiles#refreshPlayer
     */
    static void setPlayerProfile(ServerPlayer player, GameProfile profile) {
        PlayerProfilesImpl.setPlayerProfile(player, profile);
    }

    /**
     * Reset the given player's profile to the one they logged in with. Changes will not be synced to clients.
     * @param player The player to modify
     * @see PlayerProfiles#refreshPlayer
     */
    static void resetPlayerProfile(ServerPlayer player) {
        PlayerProfilesImpl.updatePlayer(player);
    }

    /**
     * Refreshes a player's profile on clients
     * @param player The player to refresh
     */
    static void refreshPlayer(ServerPlayer player) {
        PlayerProfilesImpl.updatePlayer(player);
    }

}
