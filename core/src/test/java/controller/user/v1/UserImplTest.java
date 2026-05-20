package controller.user.v1;

import domain.User;
import enums.user.v1.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.createUser.v1.CreateUserOutput;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserImplTest {
    UserImpl userImpl;
    User admin, newUser;
    @BeforeEach
    void setUp() throws Exception {
        admin = new User("admin", "1234", UserRole.ADMIN);
        admin.setId(1);
        newUser = new User("new", "1234", UserRole.PL);
        newUser.setId(2);

        UserRepository repository = mock(UserRepository.class);

        when(repository.save(any(), eq(1)))
                .thenReturn(2);
        when(repository.load(1))
                .thenReturn(admin);

        userImpl = new UserImpl(repository);
    }
    @Test
    void createUser() {
        CreateUserOutput output = userImpl.createUser(
                new CreateUserInput(
                        admin.getId(),
                        newUser.getLoginId(),
                        newUser.getPassword(),
                        newUser.getRole(),
                        1
                )
        );

        assertEquals(newUser.getId(), output.createdUserId());
    }
}