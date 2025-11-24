package org.wallentines.pflib.impl;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record ProfilePatch(Optional<String> name, Optional<PropertyMap> properties) {

    public static final ProfilePatch EMPTY = new ProfilePatch(Optional.empty(), Optional.empty());

    public static ProfilePatch fromProfile(GameProfile profile) {
        return new ProfilePatch(Optional.of(profile.name()), Optional.of(profile.properties()));
    }

    public GameProfile apply(GameProfile existing) {
        if(this == EMPTY) {
            return existing;
        }

        ImmutableMultimap.Builder<String, Property> newProperties = ImmutableMultimap.builder();
        properties.orElse(existing.properties()).entries().forEach(ent -> {
            newProperties.put(ent.getKey(), ent.getValue());
        });
        
        GameProfile profile = new GameProfile(
            existing.id(), 
            name.orElse(existing.name()),
            new PropertyMap(newProperties.build()));

        return profile;
    }

    public static final Codec<ProfilePatch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("name").forGetter(ProfilePatch::name),
            ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties").forGetter(ProfilePatch::properties)
    ).apply(instance, ProfilePatch::new));


}
