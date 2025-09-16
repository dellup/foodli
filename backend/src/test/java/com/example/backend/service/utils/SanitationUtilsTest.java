package com.example.backend.service.utils;

import com.example.backend.model.User;
import com.example.backend.utils.SanitationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SanitationUtilsTest {
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    void sanitationUserOk() {
        // Arrange
        user.setPassword("<script>kill your computer</script>");
        var expected = "&lt;script&gt;kill your computer&lt;/script&gt;";

        // Act
        SanitationUtils.sanitize(user);

        // Assert
        assertEquals(expected, user.getPassword());
    }

}
