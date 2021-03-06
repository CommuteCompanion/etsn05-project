package se.lth.base.server.data;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CredentialsTest {
    @Test
    public void saltApplied() {
        Credentials a = new Credentials("a@commutecompanion.se", "123", Role.NONE, User.NONE);
        Credentials b = new Credentials("b@commutecompanion.se", "123", Role.NONE, User.NONE);
        UUID pwd = a.generatePasswordHash(1L);
        UUID m = b.generatePasswordHash(2L);
        assertNotEquals(pwd, m);
        assertEquals(pwd, a.generatePasswordHash(1L));
    }
}
