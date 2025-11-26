package dao;
import entity.User;
public interface UserService {
    boolean isNewUser(String userId);
    User promptForUserName();
    String generateUserId(String userName);
    User retrieveUserData(String userId);
}
