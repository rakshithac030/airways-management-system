package com.app.AirportDao;

import com.app.entity.UserEntity;
import java.util.List;

public interface UserDao {

    UserEntity findById(Long userId);

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    List<UserEntity> findAll();

    boolean saveUser(UserEntity user);

    boolean updateUser(UserEntity user);

    boolean deleteUser(Long userId);
    
    long countUsers();

}
