package com.app.AirportDao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.entity.FlightEntity;
import com.app.enums.FlightStatus;

@Component
public class FlightDaoImpl implements FlightDao {

    private final SessionFactory sessionFactory;


    @Autowired
    public FlightDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        System.out.println(" FlightDaoImpl initialized");
    }


    @Override
    public boolean saveFlight(FlightEntity flight) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            session.save(flight);

            session.getTransaction().commit();
            System.out.println("✅ Flight saved: " + flight.getFlightCode());
            return true;

        } catch (Exception e) {
            System.out.println(" Error saving flight");
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public FlightEntity getFlightById(Long flightId) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            System.out.println(" Fetching flight ID=" + flightId);
            return session.get(FlightEntity.class, flightId);
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<FlightEntity> getAllFlights() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            System.out.println(" Fetching all flights");

            Query<FlightEntity> query =
                    session.createQuery("FROM FlightEntity", FlightEntity.class);
            return query.list();

        } finally {
            if (session != null) session.close();
        }
    }
    
    @Override
    public List<FlightEntity> getFlightsFromSource(Long sourceAirportId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                FROM FlightEntity f
                WHERE f.sourceAirport.airportId = :source
                  AND f.status <> 'CANCELLED'
            """, FlightEntity.class)
            .setParameter("source", sourceAirportId)
            .list();
        }
    }

    @Override
    public List<FlightEntity> getFlightsToDestination(Long destinationAirportId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                FROM FlightEntity f
                WHERE f.destinationAirport.airportId = :dest
                  AND f.status <> 'CANCELLED'
            """, FlightEntity.class)
            .setParameter("dest", destinationAirportId)
            .list();
        }
    }

    @Override
    public boolean updateFlight(FlightEntity flight) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            session.update(flight);

            session.getTransaction().commit();
            System.out.println(" Flight updated: " + flight.getFlightCode());
            return true;

        } catch (Exception e) {
            System.out.println(" Error updating flight");
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean hardDeleteFlight(Long flightId) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            FlightEntity flight = session.get(FlightEntity.class, flightId);

            if (flight == null) {
                System.out.println(" Flight not found for hard delete");
                return false;
            }

            session.beginTransaction();
            session.delete(flight);
            session.getTransaction().commit();

            System.out.println(" Flight permanently deleted ID=" + flightId);
            return true;

        } catch (Exception e) {
            System.out.println(" Error hard deleting flight");
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public List<FlightEntity> getFlightsBySourceAndDestination(
            Long sourceAirportId,
            Long destinationAirportId
    ) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            String hql = """
                FROM FlightEntity f
                WHERE f.sourceAirport.airportId = :source
                  AND f.destinationAirport.airportId = :dest
                  AND f.status <> 'CANCELLED'
                ORDER BY f.departureTime
            """;

            Query<FlightEntity> query = session.createQuery(hql, FlightEntity.class);
            query.setParameter("source", sourceAirportId);
            query.setParameter("dest", destinationAirportId);

            System.out.println(" Searching flights: "
                    + sourceAirportId + " → " + destinationAirportId);

            return query.list();

        } finally {
            if (session != null) session.close();
        }
    }

        
    @Override
    public long countFlights() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(f) from FlightEntity f", Long.class
            ).uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public long flightsDepartingToday() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(f) from FlightEntity f " +
                "where date(f.departureTime) = current_date ",
                Long.class
            ).uniqueResult();
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<FlightEntity> getUpcomingFlightsPage(int page, int size) {

        Session session = sessionFactory.openSession();
        try {
        	return session.createQuery(
        		    "from FlightEntity f " +
        		    "where f.status in ('SCHEDULED', 'DELAYED') " +
        		    "and f.departureTime > :now " +
        		    "order by f.departureTime asc",
        		    FlightEntity.class
        		)
        		.setParameter("now", LocalDateTime.now())
        		.setFirstResult(page * size)
        		.setMaxResults(size)
        		.list();
        }finally {
            session.close();
        }
    }
    
    @Override
    public List<FlightEntity> getAllFlightsPage(int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "from FlightEntity f order by f.departureTime desc",
                FlightEntity.class
            )
            .setFirstResult(page * size)
            .setMaxResults(size)
            .list();
        } finally {
            session.close();
        }
    }
    
    @Override
    public long countByStatus(FlightStatus status) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(f) from FlightEntity f where f.status = :status",
                Long.class
            ).setParameter("status", status)
             .getSingleResult();
        } finally {
            session.close();
        }
    }
    
    @Override
    public List<FlightEntity> getRecentProblemFlights(int limit) {

        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "from FlightEntity f " +
                "where f.status in ('DELAYED', 'CANCELLED') " +
                "order by f.departureTime desc",
                FlightEntity.class
            )
            .setMaxResults(limit)
            .list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<FlightEntity> findByStatus(FlightStatus status) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                "from FlightEntity f where f.status = :status",
                FlightEntity.class
            )
            .setParameter("status", status)
            .list();
        }
    }








}
