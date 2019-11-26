package myretailer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TblOrderItem {
    private final Integer pkidOrderItem;
    private final Integer fkidOrder;
    private final Integer fkidProduct;
    private final BigDecimal quantity;
    private final BigDecimal price;

    @JsonCreator
    public TblOrderItem(
            @JsonProperty("pkid_order_item") Integer pkidOrderItem,
            @JsonProperty("fkid_order") Integer fkidOrder,
            @JsonProperty("fkid_product") Integer fkidProduct,
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("price") BigDecimal price) {
        this.pkidOrderItem = pkidOrderItem;
        this.fkidOrder = fkidOrder;
        this.fkidProduct = fkidProduct;
        this.quantity = quantity;
        this.price = price;
    }

    public static class Container extends DebeziumContainer<TblOrderItem> {
        @JsonCreator
        public Container(
                @JsonProperty("before") TblOrderItem before,
                @JsonProperty("after") TblOrderItem after) {
            super(before, after);
        }
    }

}
