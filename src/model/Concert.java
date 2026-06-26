package model;

import java.util.Map;
import java.util.List;

public class Concert extends Event implements Refundable {
    public Concert(String id, String name, String venueId, String organizerId, String date, double basePrice,
            Map<String, Integer> capacities) {
        super(id, "concert", name, venueId, organizerId, date, basePrice, capacities);
    }

    @Override
    public double calculateTicketPrice(String category) {
        switch (category.toLowerCase()) {
            case "vip":
                return getBasePrice() * 3.0;
            case "regular":
                return getBasePrice() * 1.0;
            case "festival":
                return getBasePrice() * 0.7;
            default:
                return 0.0;
        }
    }
    
    @Override
    public List<String> getAvailableCategories() {
        return List.of("vip", "regular", "festival");
    }    
    
    @Override
    public double calculateRefund(int daysBeforeEvent) {
        if (daysBeforeEvent > 14) {
            return 1.0;
        } else if (daysBeforeEvent >= 7) {
            return 0.5;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isRefundable() {
        return true;
    }

}
