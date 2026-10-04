package dev.tulip.client.friends;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FriendManager {
    private static final Map<UUID, String> FRIENDS = new LinkedHashMap<>();

    private FriendManager() {
    }

    public static synchronized List<Map.Entry<UUID, String>> getFriends() {
        return FRIENDS.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue())).toList();
    }

    public static synchronized boolean isFriend(UUID uuid) {
        return FRIENDS.containsKey(uuid);
    }

    public static synchronized boolean toggle(UUID uuid, String name) {
        if (FRIENDS.remove(uuid) != null) return false;
        FRIENDS.put(uuid, name);
        return true;
    }

    public static synchronized void remove(UUID uuid) {
        FRIENDS.remove(uuid);
    }

    public static synchronized void load(JsonElement value) {
        FRIENDS.clear();
        if (value == null || !value.isJsonArray()) return;
        for (JsonElement element : value.getAsJsonArray()) {
            if (!element.isJsonObject()) continue;
            JsonObject object = element.getAsJsonObject();
            if (!object.has("uuid") || !object.has("name")) continue;
            try {
                UUID uuid = UUID.fromString(object.get("uuid").getAsString());
                String name = object.get("name").getAsString();
                if (!name.isBlank()) FRIENDS.put(uuid, name);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static synchronized JsonArray saveValue() {
        JsonArray result = new JsonArray();
        for (Map.Entry<UUID, String> entry : FRIENDS.entrySet()) {
            JsonObject friend = new JsonObject();
            friend.addProperty("uuid", entry.getKey().toString());
            friend.addProperty("name", entry.getValue());
            result.add(friend);
        }
        return result;
    }
}