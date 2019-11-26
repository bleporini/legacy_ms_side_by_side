package myretailer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TblCustomer{

    private final Integer pkidCustomer;
    private final String firstName;
    private final String lastName;

    @JsonCreator
    public TblCustomer(
            @JsonProperty("pkid_customer") Integer pkidCustomer,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName) {
        this.pkidCustomer = pkidCustomer;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static class Container extends DebeziumContainer<TblCustomer> {
        @JsonCreator
        public Container(
                @JsonProperty("before") TblCustomer before,
                @JsonProperty("after") TblCustomer after) {
            super(before, after);
        }
    }

}
