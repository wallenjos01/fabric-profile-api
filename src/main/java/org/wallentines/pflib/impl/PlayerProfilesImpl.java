package org.wallentines.pflib.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PlayerProfilesImpl {

    public static void setPlayerProfile(ServerPlayer player, GameProfile profile) {
        ((PlayerExtension) player).setProfile(profile);
    }

    public static void resetPlayerProfile(ServerPlayer player) {
        PlayerExtension ext = (PlayerExtension) player;
        ext.setProfile(ext.getLoginProfile());
    }


    public static void updatePlayer(ServerPlayer spl) {

        if(spl.isRemoved() || spl.isDeadOrDying()) {
            return;
        }

        MinecraftServer server = spl.getServer();
        if(server == null) {
            return;
        }

        // Make sure the player is ready to receive a respawn packet
        spl.stopRiding();
        Vec3 velocity = spl.getDeltaMovement();

        // Create Packets
        ClientboundPlayerInfoRemovePacket remove = new ClientboundPlayerInfoRemovePacket(List.of(spl.getUUID()));
        ClientboundPlayerInfoUpdatePacket add = ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(List.of(spl));

        // Player information packets should be sent to everyone
        for(ServerPlayer obs : server.getPlayerList().getPlayers()) {

            obs.connection.send(remove);
            obs.connection.send(add);
        }

        List<Pair<EquipmentSlot, ItemStack>> items = Arrays.stream(EquipmentSlot.values()).map(es -> new Pair<>(es, spl.getItemBySlot(es))).toList();

        ClientboundSetEquipmentPacket equip = new ClientboundSetEquipmentPacket(spl.getId(), items);

        ServerLevel world = (ServerLevel) spl.level();
        ClientboundRespawnPacket respawn = new ClientboundRespawnPacket(spl.createCommonSpawnInfo(world), (byte) 3);


        ClientboundSetExperiencePacket experience = new ClientboundSetExperiencePacket(spl.experienceProgress, spl.totalExperience, spl.experienceLevel);


        // Entity information packets should only be sent to observers in the same world
        Collection<ServerPlayer> observers = world.getPlayers(pl -> pl != spl);
        if(!observers.isEmpty()) {

            ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(spl.getId());
            ClientboundAddEntityPacket spawn = new ClientboundAddEntityPacket(
                    spl.getId(),
                    spl.getUUID(),
                    spl.getX(),
                    spl.getY(),
                    spl.getZ(),
                    spl.getXRot(),
                    spl.getYRot(),
                    EntityType.PLAYER,
                    0,
                    velocity,
                    spl.getYHeadRot()
            );

            List<SynchedEntityData.DataValue<?>> entityData = spl.getEntityData().getNonDefaultValues();
            ClientboundSetEntityDataPacket tracker = null;
            if (entityData != null) {
                tracker = new ClientboundSetEntityDataPacket(spl.getId(), entityData);
            }

            float headRot = spl.getYHeadRot();
            int rot = (int) headRot;
            if (headRot < (float) rot) rot -= 1;
            ClientboundRotateHeadPacket head = new ClientboundRotateHeadPacket(spl, (byte) ((rot * 256.0F) / 360.0F));

            for (ServerPlayer obs : observers) {

                obs.connection.send(destroy);
                obs.connection.send(spawn);
                obs.connection.send(head);
                obs.connection.send(equip);
                if(tracker != null) obs.connection.send(tracker);
            }
        }

        // The remaining packets should only be sent to the player themselves
        spl.connection.send(respawn);

        // The client waits for a game event after respawning to show the screen properly
        spl.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START, 0.0f));
        server.getPlayerList().sendPlayerPermissionLevel(spl);
        server.getPlayerList().sendAllPlayerInfo(spl);

        spl.connection.teleport(PositionMoveRotation.of(spl), new HashSet<>());
        spl.connection.send(equip);
        spl.connection.send(experience);

        spl.onUpdateAbilities();

        spl.setDeltaMovement(velocity);
        spl.connection.send(new ClientboundSetEntityMotionPacket(spl));

        for(MobEffectInstance effect : spl.getActiveEffects()) {
            spl.connection.send(new ClientboundUpdateMobEffectPacket(spl.getId(), effect, true));
        }

    }


}
