package dev.tulip.client.module;

import dev.tulip.client.friends.FriendManager;
import java.util.List;

public class FriendsModule extends Module {
    public FriendsModule() {
        super("Friends", ModuleCategory.MISC);
    }

    @Override
    public List<ModuleSetting> getSettings() {
        return FriendManager.getFriends().stream()
                .map(friend -> ModuleSetting.toggle(friend.getValue(), () -> FriendManager.isFriend(friend.getKey()),
                        () -> FriendManager.remove(friend.getKey())))
                .toList();
    }
}