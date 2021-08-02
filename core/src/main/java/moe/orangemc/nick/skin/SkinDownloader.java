package moe.orangemc.nick.skin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.orangemc.nick.api.SkinProperty;
import moe.orangemc.nick.api.SkinSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class SkinDownloader {
    private final Gson gson = new Gson();
    public SkinProperty fetchMojangSkin(String name) throws IOException {
        final String uuidApi = "https://api.mojang.com/users/profiles/minecraft/<username>";
        final String profileApi = "https://sessionserver.mojang.com/session/minecraft/profile/<uuid>?unsigned=false";

        return fetchSkin(uuidApi, profileApi, name, SkinSource.MOJANG);
    }

    public SkinProperty fetchLocalSkin(String name) throws IOException {
        final String uuidApi = "https://api.orangemc.moe/users/<username>";
        final String profileApi = "https://api.orangemc.moe/profile/<uuid>?unsigned=false";

        return fetchSkin(uuidApi, profileApi, name, SkinSource.BLESSING);
    }

    @SuppressWarnings({"unchecked", "unused"})
    private SkinProperty fetchSkin(String uuidApi, String profileApi, String name, SkinSource source) throws IOException {
        URL uuidUrl = new URL(uuidApi.replaceAll("<username>", name));
        URLConnection uuidConnection = uuidUrl.openConnection();

        String uuid;
        try (InputStreamReader isr = new InputStreamReader(uuidUrl.openStream())) {
            Map<String, String> uuidMap = gson.fromJson(isr, new TypeToken<Map<String, String>>() {}.getType());
            uuid = uuidMap.get("id");
        }

        URL profileUrl = new URL(profileApi.replaceAll("<uuid>", uuid));
        URLConnection urlConnection = profileUrl.openConnection();

        try (InputStreamReader isr = new InputStreamReader(profileUrl.openStream())) {
            Map<String, Object> profileMap = gson.fromJson(isr, new TypeToken<Map<String, Object>>() {}.getType());

            List<Map<String, String>> properties = (List<Map<String, String>>) profileMap.get("properties");
            for (Map<String, String> property : properties) {
                if ("textures".equals(property.get("name"))) {
                    return new SkinPropertyImpl(property.get("value"), property.get("signature"), source);
                }
            }
            throw new IOException("No texture property found.");
        }
    }
}
