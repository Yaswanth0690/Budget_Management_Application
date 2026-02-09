package dao;
import entity.User;
public interface UserService {
    boolean isNewUser(String userId);
    User promptForUserName();
    User validateLogin(String userId, String password);
    String generateUserId(String userName);
    User retrieveUserData(String userId);
}
