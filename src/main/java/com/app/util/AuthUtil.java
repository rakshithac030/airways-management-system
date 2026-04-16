package com.app.util;

import javax.servlet.http.HttpSession;

import com.app.entity.UserEntity;

public class AuthUtil {

    private AuthUtil() {
    }
    
    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("loggedInUser") != null;
    }
    
    public static boolean isAdmin(HttpSession session) {
        return isLoggedIn(session)
                && "ADMIN".equals(session.getAttribute("role"));
    }
    
    public static UserEntity getCurrentUser(HttpSession session) {
        if (!isLoggedIn(session)) return null;
        return (UserEntity) session.getAttribute("loggedInUser");
    }
}
