package com.example.graalvm.aot;

import com.example.graalvm.entity.Order;
import com.example.graalvm.entity.User;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Serialization Hints Registrar
 *
 * ================================================================
 * Why is this needed?
 * ================================================================
 *
 * Java serialization (implements Serializable) uses reflection internally:
 *   - ObjectInputStream reads class descriptor and creates instances
 *   - ObjectOutputStream writes field values via reflection
 *
 * Without serialization hints:
 *   - new ObjectOutputStream().writeObject(user) → Serialization failure
 *   - JMS message with User payload → Deserialize failure
 *   - Redis cache storing serialized objects → Cannot read back
 *
 * Note: For GraalVM Native Image, both Java native serialization AND
 * Jackson JSON serialization need their respective hint registrations.
 * This class focuses on Java native serialization.
 *
 * JSON serialization hints are handled by ReflectionHints since
 * Jackson uses reflection for its ObjectMapper operations.
 */
public class SerializationHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerEntitySerialization(hints);
        registerEnumSerialization(hints);
    }

    /**
     * Register entity classes for Java native serialization.
     *
     * Classes implementing Serializable need this registration.
     * The native image needs to know:
     * - serialVersionUID field
     * - All non-transient fields that will be serialized
     * - readObject/writeObject methods if defined
     */
    private void registerEntitySerialization(RuntimeHints hints) {
        hints.serialization().registerType(User.class);
        hints.serialization().registerType(Order.class);

        // BigDecimal fields in Order - also serializable
        hints.serialization().registerType(java.math.BigDecimal.class);
    }

    /**
     * Register enum types for serialization.
     *
     * Enums used as field types in serializable classes
     * need separate registration.
     */
    private void registerEnumSerialization(RuntimeHints hints) {
        hints.serialization().registerType(Order.OrderStatus.class);
        hints.serialization().registerType(Order.PaymentMethod.class);
    }
}
