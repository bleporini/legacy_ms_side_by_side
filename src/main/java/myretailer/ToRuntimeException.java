package myretailer;

public interface ToRuntimeException {

    interface CheckedSupplier<R>{
        R supply() throws Exception;
    }

    interface CheckedConsumer{
        void consume() throws Exception;
    }

    default <R> R toRuntime(CheckedSupplier<R> supplier){
        try{
            return supplier.supply();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    default void toRuntime(CheckedConsumer consumer) {
        try {
            consumer.consume();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
