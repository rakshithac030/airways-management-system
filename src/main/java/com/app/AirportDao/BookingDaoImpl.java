package com.app.AirportDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.entity.BookingEntity;
import com.app.entity.FlightEntity;
import com.app.enums.BookingStatus;

@Component
public class BookingDaoImpl implements BookingDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean saveBooking(BookingEntity booking) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(booking);
            session.getTransaction().commit();
            System.out.println("✅ Booking saved");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public BookingEntity getBookingById(Long bookingId) {
        Session session = sessionFactory.openSession();
        try {
        	return session.createQuery(
        		    "SELECT b FROM BookingEntity b " +
        		    "JOIN FETCH b.flight f " +
        		    "WHERE b.bookingId = :id",
        		    BookingEntity.class
        		).setParameter("id", bookingId)
        		 .uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<BookingEntity> getBookingsByUser(Long userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<BookingEntity> q = session.createQuery(
                "from BookingEntity b " +
                "join fetch b.user u " + 
                "join fetch b.flight f " +
                "join fetch f.sourceAirport " +
                "join fetch f.destinationAirport " +
                "where b.user.userId = :uid",
                BookingEntity.class
            );
            q.setParameter("uid", userId);
            return q.list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<BookingEntity> getAllBookings() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "from BookingEntity b " +
                "join fetch b.flight f " +
                "join fetch f.sourceAirport " +
                "join fetch f.destinationAirport " +
                "join fetch b.user u",
                BookingEntity.class
            ).list();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean updateBooking(BookingEntity booking) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(booking);
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }
    
    @Override
    public boolean confirmBookingAndDeductSeats(Long bookingId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            BookingEntity booking = session.get(BookingEntity.class, bookingId);
            if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
                session.getTransaction().rollback();
                return false;
            }

            FlightEntity flight = booking.getFlight();
            if (flight.getAvailableSeats() < booking.getSeatsBooked()) {
                session.getTransaction().rollback();
                return false;
            }

            flight.setAvailableSeats(flight.getAvailableSeats() - booking.getSeatsBooked());
            booking.setStatus(BookingStatus.CONFIRMED);

            session.update(flight);
            session.update(booking);
            session.getTransaction().commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }
    
    @Override
    public boolean cancelBookingAndRestoreSeats(Long bookingId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            BookingEntity booking = session.createQuery(
                    "SELECT b FROM BookingEntity b " +
                    "JOIN FETCH b.flight f " +
                    "WHERE b.bookingId = :id",
                    BookingEntity.class
                ).setParameter("id", bookingId)
                 .uniqueResult();
            if (booking == null || booking.getStatus() == BookingStatus.CANCELLED) {
                session.getTransaction().rollback();
                return false;
            }

            FlightEntity flight = booking.getFlight();
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatsBooked());
                session.update(flight);
            }

            booking.setStatus(BookingStatus.CANCELLED);
            session.update(booking);
            session.getTransaction().commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public long countBookings() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(b) from BookingEntity b", Long.class
            ).uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public long countByStatus(BookingStatus status) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select count(b) from BookingEntity b where b.status = :status",
                Long.class
            ).setParameter("status", status).uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public long seatsSoldToday() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "select coalesce(sum(b.seatsBooked),0) " +
                "from BookingEntity b " +
                "where b.status = 'CONFIRMED' " +
                "and date(b.bookingTime) = current_date",
                Long.class
            ).uniqueResult();
        } finally {
            session.close();
        }
    }
    
    @Override
    public long countByUserId(Long userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> q = session.createQuery(
                "SELECT COUNT(b) FROM BookingEntity b WHERE b.user.userId = :userId",
                Long.class
            );
            q.setParameter("userId", userId);
            return q.uniqueResult();
        } finally {
            session.close();
        }
    }
    
    @Override
    public BookingEntity findUpcomingBookingByUserId(Long userId) {
        Session session = sessionFactory.openSession();
        try {
            Query<BookingEntity> q = session.createQuery(
                "SELECT b FROM BookingEntity b " +
                "WHERE b.user.userId = :userId " +
                "AND b.flight.departureTime > :now " +
                "ORDER BY b.flight.departureTime ASC",
                BookingEntity.class
            );
            q.setParameter("userId", userId);
            q.setParameter("now", LocalDateTime.now());
            q.setMaxResults(1);
            return q.uniqueResult();
        } finally {
            session.close();
        }
    }
    
    @Override
    public int sumSeatsByFlightAndDate(Long flightId, LocalDate travelDate) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> q = session.createQuery(
                "SELECT COALESCE(SUM(b.seatsBooked), 0) " +
                "FROM BookingEntity b " +
                "WHERE b.flight.flightId = :fid " +
                "AND b.travelDate = :date " +
                "AND b.status = 'CONFIRMED'",
                Long.class
            );
            q.setParameter("fid", flightId);
            q.setParameter("date", travelDate);
            Long result = q.uniqueResult();
            return result != null ? result.intValue() : 0;
        } finally {
            session.close();
        }
    }
}