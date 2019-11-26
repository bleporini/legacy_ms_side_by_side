package myretailer.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TblOrder {
    private final Integer pkidOrder;
    private final Integer fkidCustomer;
    private final BigDecimal total;

    @JsonCreator
    public TblOrder(
            @JsonProperty("pkid_order") Integer pkidOrder,
            @JsonProperty("fkid_customer") Integer fkidCustomer,
            @JsonProperty("total") BigDecimal total) {
        this.pkidOrder = pkidOrder;
        this.fkidCustomer = fkidCustomer;
        this.total = total;
    }

    public static class Container extends DebeziumContainer<TblOrder> {
        @JsonCreator
        public Container(
                @JsonProperty("before") TblOrder before,
                @JsonProperty("after") TblOrder after) {
            super(before, after);
        }
    }

}

