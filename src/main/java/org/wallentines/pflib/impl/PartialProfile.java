package org.wallentines.pflib.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.UUID;

public record PartialProfile(String name, PropertyMap properties) {

    public static PartialProfile fromProfile(GameProfile profile) {
        return new PartialProfile(profile.getName(), profile.getProperties());
    }

    public GameProfile toProfile(UUID uuid) {
        GameProfile profile = new GameProfile(uuid, name);
        profile.getProperties().putAll(properties);
        return profile;
    }

    public static final Codec<PartialProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(PartialProfile::name),
            ExtraCodecs.PROPERTY_MAP.fieldOf("properties").forGetter(PartialProfile::properties)
    ).apply(instance, PartialProfile::new));


}
