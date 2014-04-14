package com.github.mwedgwood.repository;

import com.github.mwedgwood.model.Users;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class UserRepositoryTest {

    @Test
    public void testFindById() throws Exception {
        UserRepository userRepository = new UserRepository();
        Users users = userRepository.findById(1);

        assertNotNull(users);
    }
}
