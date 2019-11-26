package myretailer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TblProduct {
    private final Integer pkidProduct;
    private final String ean13;
    private final String brand;
    private final String name;

    @JsonCreator
    public TblProduct(
            @JsonProperty("pkid_product") Integer pkidProduct,
            @JsonProperty("ean13")String ean13,
            @JsonProperty("brand")String brand,
            @JsonProperty("name")String name) {
        this.pkidProduct = pkidProduct;
        this.ean13 = ean13;
        this.brand = brand;
        this.name = name;
    }

    public static class Container extends DebeziumContainer<TblProduct> {
        @JsonCreator
        public Container(
                @JsonProperty("before") TblProduct before,
                @JsonProperty("after") TblProduct after) {
            super(before, after);
        }
    }

}
