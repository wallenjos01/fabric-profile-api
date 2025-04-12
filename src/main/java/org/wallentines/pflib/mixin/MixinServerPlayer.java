package org.wallentines.pflib.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.wallentines.pflib.impl.PlayerExtension;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer {

    @Inject(method="restoreFrom", at=@At("TAIL"))
    private void onRestore(ServerPlayer player, boolean teleport, CallbackInfo ci) {
        PlayerExtension other = (PlayerExtension) player;
        ServerPlayer self = (ServerPlayer) (Object) this;
        PlayerExtension selfExt = (PlayerExtension) self;

        selfExt.setLoginProfile(other.getLoginProfile());

        GameProfile gp = self.getGameProfile();
        if(gp != self.getGameProfile()) {
            selfExt.setProfile(player.getGameProfile());

        }
    }

}
