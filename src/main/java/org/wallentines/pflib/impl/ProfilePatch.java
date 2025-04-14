package org.wallentines.pflib.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record ProfilePatch(Optional<String> name, Optional<PropertyMap> properties) {

    public static final ProfilePatch EMPTY = new ProfilePatch(Optional.empty(), Optional.empty());

    public static ProfilePatch fromProfile(GameProfile profile) {
        return new ProfilePatch(Optional.of(profile.getName()), Optional.of(profile.getProperties()));
    }

    public GameProfile apply(GameProfile existing) {
        if(this == EMPTY) {
            return existing;
        }
        GameProfile profile = new GameProfile(existing.getId(), name.orElse(existing.getName()));
        profile.getProperties().putAll(properties.orElse(existing.getProperties()));
        return profile;
    }

    public static final Codec<ProfilePatch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name").forGetter(ProfilePatch::name),
            ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties").forGetter(ProfilePatch::properties)
    ).apply(instance, ProfilePatch::new));


}
