package org.wallentines.pflib.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.pflib.impl.ProfilePatch;
import org.wallentines.pflib.impl.PlayerExtension;

@Mixin(Player.class)
@Implements(@Interface(iface= PlayerExtension.class, prefix="profilelib$"))
public abstract class MixinPlayer extends LivingEntity {

    @Final
    @Shadow
    @Mutable
    private GameProfile gameProfile;


    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract @NotNull HumanoidArm getMainArm();

    @Unique
    private GameProfile profilelib$loginProfile;

    @Unique
    private ProfilePatch profilelib$profilePatch;


    @Inject(method="<init>", at=@At("TAIL"))
    private void onConstruct(Level level, BlockPos blockPos, float f, GameProfile gameProfile, CallbackInfo ci) {
        this.profilelib$loginProfile = gameProfile;
    }

    @Inject(method="addAdditionalSaveData", at=@At("TAIL"))
    private void onSave(CompoundTag compoundTag, CallbackInfo ci) {
        if(gameProfile != profilelib$loginProfile) {
            compoundTag.put("profile", ProfilePatch.CODEC.encode(profilelib$profilePatch, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
        }
    }

    @Inject(method="readAdditionalSaveData", at=@At("TAIL"))
    private void onLoad(CompoundTag compoundTag, CallbackInfo ci) {
        gameProfile = profilelib$loginProfile;
        compoundTag.getCompound("profile").ifPresent(profileTag -> {
            NbtOps ops = NbtOps.INSTANCE;
            profilelib$patchProfile(ProfilePatch.CODEC.decode(ops, profileTag).map(Pair::getFirst).getOrThrow());
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
