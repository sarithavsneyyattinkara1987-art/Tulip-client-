package dev.tulip.client.friends;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class FriendManagerTest {
    @Test
    void friendMembershipRoundTripsThroughConfigJson() {
        UUID id = UUID.randomUUID();
        assertTrue(FriendManager.toggle(id, "TulipFriend"));
        var saved = FriendManager.saveValue();
        FriendManager.remove(id);

        FriendManager.load(saved);

        assertTrue(FriendManager.isFriend(id));
        assertEquals("TulipFriend", FriendManager.getFriends().getFirst().getValue());
        assertFalse(FriendManager.toggle(id, "TulipFriend"));
        FriendManager.remove(id);
    }
}