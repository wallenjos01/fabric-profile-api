package org.wallentines.pflib.impl;

import com.mojang.authlib.GameProfile;

public interface PlayerExtension {

    GameProfile getLoginProfile();

    void setLoginProfile(GameProfile profile);

    void setProfile(GameProfile profile);
}
