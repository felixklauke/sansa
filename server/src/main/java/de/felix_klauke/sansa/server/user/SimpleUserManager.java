package de.felix_klauke.sansa.server.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SimpleUserManager implements IUserManager {

    private final Set<IUser> currentUsers;

    public SimpleUserManager() {
        this.currentUsers = new HashSet<>();
    }

    @Override
    public IUser authenticateUser(String userName, String password) {
        for (IUser currentUser : this.currentUsers) {
            if (!(Objects.equals(currentUser.getUserName(), userName))) {
                continue;
            }

            return currentUser.checkPassword(password) ? currentUser : null;
        }

        return null;
    }

    @Override
    public boolean userExists(String userName) {
        return this.currentUsers.stream().map(IUser::getUserName).anyMatch(name -> Objects.equals(name, userName));
    }

    @Override
    public void registerUser(IUser user) {
        this.currentUsers.add(user);
    }

    @Override
    public IUser authenticateUser(String userName) {
        for (IUser currentUser : this.currentUsers) {
            if (Objects.equals(currentUser.getUserName(), userName) && !currentUser.needsAuthentication()) {
                return currentUser;
            }
        }

        return null;
    }

    @Override
    public boolean userNeedsAuthentication(String userName) {
        for (IUser currentUser : this.currentUsers) {
            if (Objects.equals(currentUser.getUserName(), userName)) {
                return currentUser.needsAuthentication();
            }
        }

        return true;
    }
}
