package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private final String saleId;
    private final LocalDateTime timestamp;
    private final String cashierId;
    private final List<SaleItem> items = new ArrayList<>();
    private double totalAmount;
    private double storedTotal;

    private Sale(String saleId, LocalDateTime timestamp, String cashierId) {
        this.saleId = saleId;
        this.timestamp = timestamp;
        this.cashierId = cashierId;
        this.totalAmount = 0;
    }

    public Sale(String saleId, LocalDateTime timestamp, String cashierId, List<SaleItem> items) {
        this(saleId, timestamp, cashierId);
        for(SaleItem item : items){
            addItem(item);
        }
    }


}
