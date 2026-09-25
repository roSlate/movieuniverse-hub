package com.roslate.backend.repository;

import com.roslate.backend.model.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository users;

    @Test
    void findByNameReturnsTheSavedUser() {
        users.save(new AppUser("ana"));

        assertThat(users.findByName("ana")).isPresent();
        assertThat(users.findByName("ana").get().getName()).isEqualTo("ana");
    }

    @Test
    void findByNameReturnsEmptyForAnUnknownName() {
        users.save(new AppUser("ana"));

        assertThat(users.findByName("bruno")).isEmpty();
    }

    @Test
    void savingTwoUsersWithTheSameNameIsRejected() {
        users.saveAndFlush(new AppUser("ana"));

        assertThatThrownBy(() -> users.saveAndFlush(new AppUser("ana")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}