package com.app.AirportDao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.entity.RunwayEntity;

@Component
public class RunwayDaoImpl implements RunwayDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public RunwayDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean saveRunway(RunwayEntity runway) {
        Session session = null;
        boolean isSaved = false;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            session.save(runway);
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
    public RunwayEntity getRunwayById(Long runwayId) {
        Session session = null;
        RunwayEntity runway = null;

        try {
            session = sessionFactory.openSession();
            runway = session.get(RunwayEntity.class, runwayId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return runway;
    }

    @Override
    public List<RunwayEntity> getRunwaysByAirportId(Long airportId) {
        Session session = null;
        List<RunwayEntity> runways = null;

        try {
            session = sessionFactory.openSession();
            String hql = "FROM RunwayEntity r JOIN FETCH r.airport WHERE r.airport.airportId = :airportId";
            Query<RunwayEntity> query = session.createQuery(hql, RunwayEntity.class);
            query.setParameter("airportId", airportId);

            runways = query.list();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return runways;
    }

    @Override
    public boolean updateRunway(RunwayEntity runway) {
        Session session = null;
        boolean isUpdated = false;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            session.update(runway);
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
    public boolean deleteRunway(Long runwayId) {
        Session session = null;
        boolean isDeleted = false;

        try {
            session = sessionFactory.openSession();
            RunwayEntity runway = session.get(RunwayEntity.class, runwayId);

            if (runway != null) {
                session.beginTransaction();
                session.delete(runway);
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
}
