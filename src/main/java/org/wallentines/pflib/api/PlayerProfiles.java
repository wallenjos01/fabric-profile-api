package org.wallentines.pflib.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.wallentines.pflib.impl.PlayerExtension;
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
     * Changes the given player's username. Changes will not be synced to clients.
     * @param player The player to modify
     * @param name The player's new username
     * @see PlayerProfiles#refreshPlayer
     */
    static void setPlayerName(ServerPlayer player, String name) {

        GameProfile profile = new GameProfile(player.getUUID(), name);
        profile.getProperties().putAll(player.getGameProfile().getProperties());

        PlayerProfilesImpl.setPlayerProfile(player, profile);
    }

    /**
     * Changes the given player's skin. Changes will not be synced to clients.
     * @param player The player to modify
     * @param value The player's new skin value (base64 encoded)
     * @param signature A signature for the above value. Vanilla clients will reject signatures not created by Mojang.
     * @see PlayerProfiles#refreshPlayer
     */
    static void setPlayerSkin(ServerPlayer player, String value, String signature) {

        GameProfile existing = player.getGameProfile();

        GameProfile profile = new GameProfile(existing.getId(), existing.getName());
        PropertyMap properties = profile.getProperties();
        properties.put("textures", new Property("textures", value, signature));

        setPlayerProfile(player, profile);
    }

    /**
     * Reset the given player's profile to the one they logged in with. Changes will not be synced to clients.
     * @param player The player to modify
     * @see PlayerProfiles#refreshPlayer
     */
    static void resetPlayerProfile(ServerPlayer player) {
        PlayerProfilesImpl.resetPlayerProfile(player);
    }

    /**
     * Refreshes a player's profile on clients
     * @param player The player to refresh
     */
    static void refreshPlayer(ServerPlayer player) {
        PlayerProfilesImpl.updatePlayer(player);
    }

    /**
     * Gets the GameProfile a player logged in with
     * @param player The player to find the profile for
     * @return The player's login profile
     */
    static GameProfile getLoginProfile(ServerPlayer player) {
        return ((PlayerExtension) player).getLoginProfile();
    }

}
