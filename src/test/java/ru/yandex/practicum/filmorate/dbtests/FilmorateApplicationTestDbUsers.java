package ru.yandex.practicum.filmorate.dbtests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Qualifier
// тесты для DbUserStorageDao

class FilmorateApplicationTestDbUsers {
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;

    @Test
    public void addUserShouldAddUserCorrectly() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);

        assertEquals(1, userStorage.getUserById(1).getId());
        assertEquals(user, userStorage.getUserById(1));
    }

    @Test
    public void updateUserShouldUpdateUserCorrectly() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        userStorage.addUser(user);
        Integer id = user.getId();

        User userUpdated = new User("userUpdated@gmail.com", "userUpdated", LocalDate.of(1999, 01, 01));
        userUpdated.setId(id);
        userStorage.updateUser(userUpdated);

        assertEquals("userUpdated", userStorage.getUserById(1).getLogin());
    }

    @Test
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
    public void getAllUsersShouldProvideAllUsersUponRequest() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));
        User user3 = new User("user3@gmail.com", "user3", LocalDate.of(2002, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(user);
        allUsers.add(user2);
        allUsers.add(user3);

        assertEquals(allUsers.size(), userStorage.getAllUsers().size());
        assertEquals(user, userStorage.getUserById(1));
        assertEquals(user2, userStorage.getUserById(2));
        assertEquals(user3, userStorage.getUserById(3));
    }

    @Test
    public void getUserByIdShouldProvideUserById() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);

        assertEquals(user, userStorage.getUserById(1));
        assertEquals(user2, userStorage.getUserById(2));
    }

    // getFriendListById
    @Test
    public void getFriendListByIdShouldProvideFriendList() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);

        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        friendsOfUser1.add(2);
        friendsOfUser2.add(1);

        assertEquals(friendsOfUser1, userStorage.getFriendListById(1));
        assertEquals(friendsOfUser2, userStorage.getFriendListById(2));
    }

    //deleteFromFriendListById
    @Test
    public void deleteFromFriendListByIdShouldDeleteFromFriendList() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);
        userStorage.deleteFromFriendListById(1, 2);

        Set<Integer> friendsOfUser1 = new HashSet<>();

        assertEquals(friendsOfUser1, userStorage.getFriendListById(1));
    }

    //addFriend
    @Test
    public void addFriendShouldAddFriendToFriendList() {
        User user = new User("user@gmail.com", "user", LocalDate.of(1999, 01, 01));
        User user2 = new User("user2@gmail.com", "user2", LocalDate.of(2000, 01, 01));

        userStorage.addUser(user);
        userStorage.addUser(user2);
        userStorage.addFriend(1, 2);
        userStorage.addFriend(2, 1);

        Set<Integer> friendsOfUser1 = new HashSet<>();
        Set<Integer> friendsOfUser2 = new HashSet<>();
        friendsOfUser1.add(2);
        friendsOfUser2.add(1);

        assertEquals(friendsOfUser1, userStorage.getFriendListById(1));
        assertEquals(friendsOfUser2, userStorage.getFriendListById(2));
    }
}
