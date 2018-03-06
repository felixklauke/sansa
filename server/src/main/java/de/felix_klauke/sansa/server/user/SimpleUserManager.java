package de.felix_klauke.sansa.server.user;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class SimpleUserManager implements IUserManager {

    /**
     * All currently registered users.
     */
    private final Set<IUser> currentUsers = Sets.newConcurrentHashSet();

    @Override
    public IUser authenticateUser(String userName, String password) {
        return currentUsers.stream().filter(currentUser -> Objects.equals(currentUser.getUserName(), userName)).findFirst().filter(currentUser -> currentUser.checkPassword(password)).orElse(null);
    }

    @Override
    public boolean userExists(String userName) {
        return currentUsers.stream().map(IUser::getUserName).anyMatch(name -> Objects.equals(name, userName));
    }

    @Override
    public void registerUser(IUser user) {
        currentUsers.add(user);
    }

    @Override
    public IUser authenticateUser(String userName) {
        return currentUsers.stream().filter(currentUser -> Objects.equals(currentUser.getUserName(), userName) && !currentUser.needsAuthentication()).findFirst().orElse(null);
    }

    @Override
    public boolean userNeedsAuthentication(String userName) {
        return currentUsers.stream().filter(currentUser -> Objects.equals(currentUser.getUserName(), userName)).findFirst().map(IUser::needsAuthentication).orElse(true);
    }
}
