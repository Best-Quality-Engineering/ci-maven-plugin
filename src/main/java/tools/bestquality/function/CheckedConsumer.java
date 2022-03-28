package tools.bestquality.function;

@FunctionalInterface
public interface CheckedConsumer<T, E extends Throwable> {
    void accept(T t)
            throws E;
}
