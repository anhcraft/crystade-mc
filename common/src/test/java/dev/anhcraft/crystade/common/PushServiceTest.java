package dev.anhcraft.crystade.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PushServiceTest {

    @Test
    void endpointUrlIsCorrect() {
        assertEquals("https://integration.crystade.com/configuration", PushService.INTEGRATION_ENDPOINT);
    }
}
