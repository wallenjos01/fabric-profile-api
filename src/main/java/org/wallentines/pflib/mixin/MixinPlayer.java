package org.wallentines.pflib.mixin;

import com.mojang.authlib.GameProfile;

import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.pflib.impl.ProfilePatch;
import org.wallentines.pflib.impl.PlayerExtension;

@Mixin(Player.class)
@Implements(@Interface(iface = PlayerExtension.class, prefix = "profilelib$"))
public abstract class MixinPlayer extends Avatar {

    @Final
    @Shadow
    @Mutable
    private GameProfile gameProfile;

    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private GameProfile profilelib$loginProfile;

    @Unique
    private ProfilePatch profilelib$profilePatch;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstruct(Level level, GameProfile gameProfile, CallbackInfo ci) {
        this.profilelib$loginProfile = gameProfile;
        this.profilelib$profilePatch = ProfilePatch.EMPTY;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onSave(ValueOutput data, CallbackInfo ci) {
        if (gameProfile != profilelib$loginProfile) {
            data.store("profile", ProfilePatch.CODEC, profilelib$profilePatch);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onLoad(ValueInput data, CallbackInfo ci) {
        gameProfile = profilelib$loginProfile;
        data.read("profile", ProfilePatch.CODEC)
                .ifPresentOrElse(profilePatch -> {
                    profilelib$patchProfile(profilePatch);
                }, () -> {
                    profilelib$profilePatch = ProfilePatch.EMPTY;
                });
    }

    @Unique
    public GameProfile profilelib$getLoginProfile() {
        return profilelib$loginProfile;
    }

    @Unique
    public void profilelib$setLoginProfile(GameProfile profile) {
        profilelib$loginProfile = profile;
    }

    @Unique
    public ProfilePatch profilelib$getProfilePatch() {
        return profilelib$profilePatch;
    }

    @Unique
    public void profilelib$patchProfile(ProfilePatch profile) {
        profilelib$profilePatch = profile;
        gameProfile = profile.apply(profilelib$loginProfile);
    }

}
