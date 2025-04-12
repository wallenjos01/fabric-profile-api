package org.wallentines.pflib.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.execution.Frame;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;


public class Functions {

    public static void test(CommandSourceStack css,
                                CompoundTag tag,
                                ResourceLocation id,
                                CommandDispatcher<CommandSourceStack> dispatcher,
                                ExecutionContext<CommandSourceStack> ctx,
                                Frame frame,
                                Void data) throws CommandSyntaxException {


    }
}
