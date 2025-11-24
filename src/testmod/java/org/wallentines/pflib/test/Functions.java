package org.wallentines.pflib.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;
import org.wallentines.pflib.api.PlayerProfiles;


public class Functions {

    public static void test(CommandSourceStack css,
                            CompoundTag tag,
                            ResourceLocation id,
                            CommandDispatcher<CommandSourceStack> dispatcher,
                            ExecutionContext<CommandSourceStack> ctx,
                            Frame frame,
                            Void data) throws CommandSyntaxException {

        ServerPlayer spl = css.getPlayerOrException();

        String name = tag == null ? "jeb_" : tag.getStringOr("name", "jeb_");


        ResolvableProfile.createUnresolved(name).resolveProfile(css.getServer().services().profileResolver()).thenAccept(rp -> {

            PlayerProfiles.setPlayerProfile(spl, rp);
            PlayerProfiles.refreshPlayer(spl);

        });

    }

    public static void reset(CommandSourceStack css,
                            CompoundTag tag,
                            ResourceLocation id,
                            CommandDispatcher<CommandSourceStack> dispatcher,
                            ExecutionContext<CommandSourceStack> ctx,
                            Frame frame,
                            Void data) throws CommandSyntaxException {

        ServerPlayer spl = css.getPlayerOrException();
        PlayerProfiles.resetPlayerProfile(spl);
        PlayerProfiles.refreshPlayer(spl);

    }
}
