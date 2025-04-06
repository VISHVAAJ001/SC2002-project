package com.ntu.fdae.group1.bto.repository.booking;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBookingRepository implements IBookingRepository {
    private Map<String, Booking> bookings = new HashMap<>();

    @Override
    public void save(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    @Override
    public Booking findById(String bookingId) {
        return bookings.get(bookingId);
    }

    @Override
    public Booking findByApplicantNric(String applicantNRIC) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicantNRIC().equals(applicantNRIC)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public Booking findByApplicationId(String applicationId) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicationId().equals(applicationId)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

	@Override
	public void saveAll(Map<String, Booking> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Booking> loadAll() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
