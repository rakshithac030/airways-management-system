package com.app.AirportDao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.entity.UserEntity;

@Component
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserEntity findById(Long userId) {
        Session session = null;
        UserEntity user = null;

        try {
            session = sessionFactory.openSession();
            user = session.get(UserEntity.class, userId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return user;
    }

    @Override
    public UserEntity findByUsername(String username) {
        Session session = null;
        UserEntity user = null;

        try {
            session = sessionFactory.openSession();
            String hql = "FROM UserEntity u WHERE u.username = :username";
            Query<UserEntity> query = session.createQuery(hql, UserEntity.class);
            query.setParameter("username", username);
            user = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return user;
    }

    @Override
    public UserEntity findByEmail(String email) {
        Session session = null;
        UserEntity user = null;

        try {
            session = sessionFactory.openSession();
            String hql = "FROM UserEntity u WHERE u.email = :email";
            Query<UserEntity> query = session.createQuery(hql, UserEntity.class);
            query.setParameter("email", email);
            user = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return user;
    }

    @Override
    public List<UserEntity> findAll() {
        Session session = null;
        List<UserEntity> users = null;

        try {
            session = sessionFactory.openSession();
            String hql = "FROM UserEntity";
            Query<UserEntity> query = session.createQuery(hql, UserEntity.class);
            users = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return users;
    }

    @Override
    public boolean saveUser(UserEntity user) {
        Session session = null;
        boolean isSaved = false;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            isSaved = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) session.close();
        }

        return isSaved;
    }

    @Override
    public boolean updateUser(UserEntity user) {
        Session session = null;
        boolean isUpdated = false;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) session.close();
        }

        return isUpdated;
    }

    @Override
    public boolean deleteUser(Long userId) {
        Session session = null;
        boolean isDeleted = false;

        try {
            session = sessionFactory.openSession();
            UserEntity user = session.get(UserEntity.class, userId);
            if (user != null) {
                session.beginTransaction();
                session.delete(user);
                session.getTransaction().commit();
                isDeleted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) session.close();
        }

        return isDeleted;
    }
        
    @Override
    public long countUsers() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(u) from UserEntity u", Long.class
            ).uniqueResult();
        } finally {
            session.close();
        }
    }

}
