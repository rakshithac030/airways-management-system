package com.app.AirportDao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.entity.AirportEntity;

@Component
public class AirportDaosimpl implements AirportDaos {

    private final SessionFactory sessionFactory;

    @Autowired
    public AirportDaosimpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean saveAirportEntity(AirportEntity entity) {
        Session session = null;
        boolean isSaved = false;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            Serializable id = session.save(entity);
            System.out.println("Saved AirportEntity with ID: " + id);

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
    public AirportEntity getAirportEntityByID(Long id) {
        Session session = null;
        AirportEntity entity = null;

        try {
            session = sessionFactory.openSession();
            entity = session.get(AirportEntity.class, id);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return entity;
    }

    @Override
    public AirportEntity getAirportEntityByName(String airportName, String airportLocation) {
        Session session = null;
        AirportEntity entity = null;

        try {
            session = sessionFactory.openSession();
            String hql = "FROM AirportEntity WHERE airportName = :name AND airportLocation = :location" ;
            Query<AirportEntity> query = session.createQuery(hql, AirportEntity.class);
            query.setParameter("name", airportName);
            query.setParameter("location", airportLocation);

            entity = query.uniqueResult();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return entity;
    }

    @Override
    public List<AirportEntity> getAllAirport() {
        Session session = null;
        List<AirportEntity> list = null;

        try {
            session = sessionFactory.openSession();
            String hql = "FROM AirportEntity";
            Query<AirportEntity> query = session.createQuery(hql, AirportEntity.class);

            list = query.list();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return list;
    }

    @Override
    public boolean updateAirportEntity(AirportEntity entity) {
        Session session = null;
        boolean isUpdated = false;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            session.update(entity);
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
    public boolean deleteAirportById(Long id) {
        Session session = null;
        boolean isDeleted = false;

        try {
            session = sessionFactory.openSession();
            AirportEntity entity = session.get(AirportEntity.class, id);

            if (entity != null) {
                session.beginTransaction();
                session.delete(entity);
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
    public long countAirports() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(a) from AirportEntity a", Long.class
            ).uniqueResult();
        } finally {
            session.close();
        }
    }
  
}
