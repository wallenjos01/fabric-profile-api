package org.wallentines.pflib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.wallentines.pflib.api.PlayerProfiles;


@Mixin(PlayerList.class)
public class MixinPlayerList {

    @WrapOperation(method="placeNewPlayer", at=@At(value="INVOKE", target="Ljava/lang/String;equalsIgnoreCase(Ljava/lang/String;)Z", ordinal=0))
    private boolean preventRename(String instance, String anotherString, Operation<Boolean> original, @Local(argsOnly = true) ServerPlayer player) {
        if(PlayerProfiles.getLoginProfile(player) != player.getGameProfile()) {
            return true;
        }
        return original.call(instance, anotherString);
    }

}
