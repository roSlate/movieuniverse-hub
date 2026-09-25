package com.roslate.backend.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppUserTest {

    @Test
    void constructorSetsName() {
        AppUser user = new AppUser("ana");

        assertThat(user.getName()).isEqualTo("ana");
    }

    @Test
    void idIsNullUntilTheUserIsSaved() {
        AppUser user = new AppUser("ana");

        assertThat(user.getId()).isNull();
    }
}
