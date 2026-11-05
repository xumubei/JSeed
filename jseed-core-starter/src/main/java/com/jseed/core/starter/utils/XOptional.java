
package com.jseed.core.starter.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An extension of {@link Optional}.
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public final class XOptional<T> {
    /**
     * Shared empty instance
     */
    private static final XOptional<?> EMPTY = new XOptional<>();

    /**
     * Value to check for null
     */
    private final T value;

    /**
     * Private constructor
     */
    private XOptional() {
        this.value = null;
    }

    private XOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Creates an empty XOptional.
     *
     * @return XOptional<T>
     */
    public static <T> XOptional<T> empty() {
        @SuppressWarnings("unchecked")
        XOptional<T> t = (XOptional<T>) EMPTY;
        return t;
    }

    /**
     * Wraps a value and throws {@link NullPointerException} if it is null.
     *
     * @param value value to wrap
     * @return XOptional<T>
     */
    public static <T> XOptional<T> of(T value) {
        return new XOptional<>(value);
    }


    /**
     * Wraps a value or returns an empty instance if the value is null.
     *
     * @param value value to wrap
     * @return XOptional<T>
     */
    public static <T> XOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Checks whether the value is non-null.
     *
     * @return true if the value is non-null, false otherwise
     */
    public boolean isPresent() {
        return null != value;
    }

    /**
     * Throws an exception if the value is absent.
     *
     * @return T
     */
    public T get() {
        if (null != value) {
            return value;
        }
        throw new NoSuchElementException("No value present");
    }

    /**
     * Gets the value, which may be null.
     *
     * @return T
     */
    public T end() {
        return value;
    }

    /**
     * Invokes the consumer if the value is present.
     *
     * @param consumer consumer
     * @return XOptional<T>
     */
    public XOptional<T> ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * Runs the supplied action if the value is absent.
     *
     * @param runnable action to run
     * @return XOptional<T>
     */
    public XOptional<T> notPresent(Runnable runnable) {
        if (value == null) {
            runnable.run();
        }
        return this;
    }

    /**
     * Returns an empty instance if the predicate evaluates to true.
     *
     * @param predicate filter condition
     * @return
     */
    public XOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (value == null) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }


    /**
     * Converts the value to another type.
     *
     * @param mapper conversion function
     * @return
     */
    public <U> XOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (value == null) {
            return empty();
        } else {
            return new XOptional<>(mapper.apply(value));
        }
    }

    /**
     * Returns the value if present, otherwise the supplied fallback.
     *
     * @param other fallback value
     * @return
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * Returns the value if present, otherwise the result of the supplied action.
     *
     * @param other supplier of the fallback value
     * @return
     */
    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * Returns the value if present, otherwise throws the supplied exception.
     *
     * @param throwable exception supplier
     * @throws X if the value is absent
     */
    public <X extends Throwable> XOptional<T> orElseThrow(X throwable) throws X {
        if (value == null) {
            throw throwable;
        }
        return this;
    }

    /**
     * Converts to a Java Optional.
     *
     * @return
     */
    public Optional toJavaOptional() {
        return Optional.ofNullable(value);
    }

    public <U> XOptional<U> flatMap(Function<? super T, XOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (value == null) {
            return empty();
        } else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof XOptional)) {
            return false;
        }

        XOptional<?> other = (XOptional<?>) obj;
        return Objects.equals(value, other.value);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return value != null
                ? String.format("XOptional[%s]", value)
                : "XOptional.empty";
    }
}
