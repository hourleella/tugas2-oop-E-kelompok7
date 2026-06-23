package model;

public class SportMatch extends Event {

    public SportMatch() {
        super();
    }

    @Override
    public double calculateTicketPrice(String category) {

        double base = super.getBasePrice(); 

        if (category == null) {
            return base;
        }

        switch (category.toLowerCase()) {
            case "tribune":
                return base * 1.0;
            case "vip":
                return base * 2.5;
            case "vvip":
                return base * 5.0;
            default:
                return base * 1.0;
        }
    }
}