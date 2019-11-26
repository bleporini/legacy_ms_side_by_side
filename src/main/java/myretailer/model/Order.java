package myretailer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
public class Order {

    private final Integer pkidOrder;
    private final Integer fkidCustomer;
    private final BigDecimal total;
    private final String customer;

    private final List<OrderItem> items;

    public static OrderBuilder fromTblOrder(TblOrder tblOrder){
        return Order.builder()
                .pkidOrder(tblOrder.getPkidOrder())
                .fkidCustomer(tblOrder.getFkidCustomer())
                .total(tblOrder.getTotal());
    }

    @JsonCreator
    public Order(
            @JsonProperty("pkidOrder") Integer pkidOrder,
            @JsonProperty("fkidCustomer") Integer fkidCustomer,
            @JsonProperty("total") BigDecimal total,
            @JsonProperty("customer") String customer,
            @JsonProperty("items") List<OrderItem> items) {
        this.pkidOrder = pkidOrder;
        this.fkidCustomer = fkidCustomer;
        this.total = total;
        this.customer = customer;
        this.items = items;
    }

    public static OrderBuilder buildFromOrder(final Order order) {
        return builder()
                .items(order.getItems())
                .customer(order.customer)
                .total(order.total)
                .fkidCustomer(order.fkidCustomer)
                .pkidOrder(order.pkidOrder);
    }

    @Builder
    @Getter
    public static class OrderItem{
        private final Integer pkidOrderItem;
        private final Integer fkidOrder;
        private final Integer fkidProduct;
        private final BigDecimal quantity;
        private final BigDecimal price;
        private final String name;

        public static OrderItemBuilder fromTblOrderItem(TblOrderItem tblOrderItem) {
            return OrderItem.builder()
                    .pkidOrderItem(tblOrderItem.getPkidOrderItem())
                    .fkidOrder(tblOrderItem.getFkidOrder())
                    .fkidProduct(tblOrderItem.getFkidProduct())
                    .quantity(tblOrderItem.getQuantity())
                    .price(tblOrderItem.getPrice());
        }

        @JsonCreator
        public OrderItem(
                @JsonProperty("pkidOrderItem") Integer pkidOrderItem,
                @JsonProperty("fkidOrder") Integer fkidOrder,
                @JsonProperty("fkidProduct") Integer fkidProduct,
                @JsonProperty("quantity") BigDecimal quantity,
                @JsonProperty("price") BigDecimal price,
                @JsonProperty("name") String name) {
            this.pkidOrderItem = pkidOrderItem;
            this.fkidOrder = fkidOrder;
            this.fkidProduct = fkidProduct;
            this.quantity = quantity;
            this.price = price;
            this.name = name;
        }
    }
}
