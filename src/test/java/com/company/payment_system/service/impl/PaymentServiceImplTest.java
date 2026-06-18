package com.company.payment_system.service.impl;


import com.company.payment_system.dto.PaymentRequest;
import com.company.payment_system.dto.PaymentResponse;
import com.company.payment_system.dto.PaymentStatusResponse;
import com.company.payment_system.entity.PaymentTransaction;
import com.company.payment_system.enums.PaymentStatus;
import com.company.payment_system.repository.PaymentTransactionRepository;
//import org.hibernate.internal.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
//import org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentTransactionRepository repository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void initiatePayment_success() {

        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("500"));
        request.setCurrency("INR");
        request.setPaymentMethod("CARD");
        request.setCustomerEmail("test@gmail.com");

        when(repository.save(any(PaymentTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.initiatePayment(request);

        assertNotNull(response.getTransactionId());
        assertEquals("PENDING", response.getStatus());

        verify(repository, times(1)).save(any(PaymentTransaction.class));
    }

    @Test
    void getPaymentTransactionId_success() {


    String transactionId = "TXN123";

    PaymentTransaction transaction = PaymentTransaction.builder()
            .transactionId(transactionId)
                    .amount(new BigDecimal("500"))
                    .currency("INR")
                    .paymentMethod("CARD")
                    .customerEmail("test@gmail.com")
                    .status(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();


    when(repository.findByTransactionId(transactionId))
            .thenReturn(Optional.of(transaction));

        PaymentStatusResponse response =
                paymentService.getPaymentByTransactionId(transactionId);

        assertNotNull(transactionId);
        assertEquals(transactionId, response.getTransactionId());
        assertEquals("PENDING", response.getStatus());
        assertEquals("INR", response.getCurrency());

        verify(repository, times(1)).findByTransactionId(transactionId);


}
}
