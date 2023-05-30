package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Qualifier
// тесты для UserDbStorageDao
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    @Order(1)
    public void addUserShouldAddUserCorrectly() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);

        assertEquals(1, userStorage.getUserById(1).getId());
        assertEquals(user, userStorage.getUserById(1));
    }

    @Test
    @Order(2)
    public void updateUserShouldUpdateUserCorrectly() {

        User userUpdated = new User("userUpdated@gmail.com", "userUpdated", LocalDate.of(1999, 01, 01));
        userUpdated.setId(1);
        userStorage.updateUser(userUpdated);

        assertEquals("userUpdated", userStorage.getUserById(1).getLogin());
    }

    @Test
    @Order(3)
    public void deleteUserShouldDeleteUser() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);
        Integer id = user.getId();

        userStorage.deleteUser(id);

        final Exception exception = assertThrows(
                ru.yandex.practicum.filmorate.exception.NotFoundException.class,
                () -> userStorage.getUserById(id)
        );
        assertEquals("Отсутствуют данные в БД по указанному ID.", exception.getMessage());
    }

    @Test
    @Order(4)
    public void getAllUsersShouldProvideAllUsersUponRequest() {
        //userStorage.deleteUser(1);
        User user1 = new User("user1@gmail5.com", "user1", LocalDate.of(2000, 01, 01));
        User user2 = new User("user2@gmail5.com", "user2", LocalDate.of(2000, 01, 01));
        User user3 = new User("user3@gmail5.com", "user3", LocalDate.of(2002, 01, 01));
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user1);
        allUsers.add(user2);
        allUsers.add(user3);

        assertEquals(user1, userStorage.getUserById(user1.getId()));
        assertEquals(user2, userStorage.getUserById(user2.getId()));
        assertEquals(user3, userStorage.getUserById(user3.getId()));
    }

    // getFriendListById
    @Test
    @Order(5)
    public void getFriendListByIdShouldProvideFriendList() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        Integer user1Id = userStorage.getUserById(user.getId()).getId();
        Integer user2Id = userStorage.getUserById(user2.getId()).getId();

        userStorage.addFriend(user1Id, user2Id);
        userStorage.addFriend(user2Id, user1Id);

        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        friendsOfUser1.add(user2Id);
        friendsOfUser2.add(user1Id);

        assertEquals(friendsOfUser1, userStorage.getFriendListById(user1Id));
        assertEquals(friendsOfUser2, userStorage.getFriendListById(user2Id));
    }

    //deleteFromFriendListById
    @Test
    @Order(6)
    public void deleteFromFriendListByIdShouldDeleteFromFriendList() {
        User user = new User("user456@gmail.com", "user10", LocalDate.of(1999, 01, 01));
        User user2 = new User("user278@gmail.com", "user11", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        Integer user1Id = userStorage.getUserById(user.getId()).getId();
        Integer user2Id = userStorage.getUserById(user2.getId()).getId();

        userStorage.addFriend(user1Id, user2Id);
        userStorage.addFriend(user2Id, user1Id);
        userStorage.deleteFromFriendListById(user1Id, user2Id);

        Set<Integer> friendsOfUser1 = new HashSet<>();

        assertEquals(friendsOfUser1, userStorage.getFriendListById(user1Id));
    }

    //addFriend
    @Test
    @Order(7)
    public void addFriendShouldAddFriendToFriendList() {
        User user = new User("user258@gmail.com", "user258", LocalDate.of(1999, 01, 01));
        User user2 = new User("user8522@gmail.com", "user285", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        Integer user1Id = userStorage.getUserById(user.getId()).getId();
        Integer user2Id = userStorage.getUserById(user2.getId()).getId();
        userStorage.addFriend(user1Id, user2Id);
        userStorage.addFriend(user2Id, user1Id);

        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        friendsOfUser1.add(user2Id);
        friendsOfUser2.add(user1Id);

        assertEquals(friendsOfUser1, userStorage.getFriendListById(user1Id));
        assertEquals(friendsOfUser2, userStorage.getFriendListById(user2Id));
    }
}
