package model;

import java.util.Map;

public class Seminar extends Event implements Refundable {
    public Seminar(String id, String name, String venueId, String organizerId, String date, double basePrice,
            Map<String, Integer> capacities) {
        super(id, "seminar", name, venueId, organizerId, date, basePrice, capacities);
    }

    @Override
    public double calculateTicketPrice(String category) {
        return getBasePrice();
    }

    @Override
    public double calculateRefund(int daysBeforeEvent) {
        if (daysBeforeEvent > 1) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public boolean isRefundable() {
        return true;
    }

}
