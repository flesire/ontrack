package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.structure.ID;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class ConnectedAccountTest {

    @Test
    public void none() {
        assertNull(ConnectedAccount.none().getAccount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_null() {
        ConnectedAccount.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_not_defined() {
        ConnectedAccount.of(Account.of("test", SecurityRole.USER));
    }

    @Test
    public void of() {
        Account test = Account.of("test", SecurityRole.USER).withId(ID.of(2));
        assertSame(ConnectedAccount.of(test).getAccount(), test);
    }

}
