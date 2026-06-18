package com.company.payment_system.fsm;

import com.company.payment_system.enums.PaymentStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class PaymentStateMachine {

    private static final EnumMap<PaymentStatus, Set<PaymentStatus>> transitions = new EnumMap<>(PaymentStatus.class);

    static {
        transitions.put(PaymentStatus.PENDING,
                EnumSet.of(PaymentStatus.PROCESSING, PaymentStatus.SUCCESS, PaymentStatus.FAILED));

        transitions.put(PaymentStatus.PROCESSING,
                EnumSet.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED));

        transitions.put(PaymentStatus.HOLD,
                EnumSet.of(PaymentStatus.PROCESSING, PaymentStatus.FAILED));

        transitions.put(PaymentStatus.SUCCESS, EnumSet.noneOf(PaymentStatus.class));
        transitions.put(PaymentStatus.FAILED, EnumSet.noneOf(PaymentStatus.class));
        transitions.put(PaymentStatus.CANCELLED, EnumSet.noneOf(PaymentStatus.class));
    }

    public static boolean isValidTransition(PaymentStatus current, PaymentStatus next) {
        return transitions
                .getOrDefault(current, EnumSet.noneOf(PaymentStatus.class))
                .contains(next);
    }

    public static boolean isFinal(PaymentStatus status) {
        return transitions.get(status).isEmpty();
    }
}