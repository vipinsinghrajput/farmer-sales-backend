package com.farmerapp.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order is required")
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @NotNull(message = "Transaction ID is required")
    @Column(unique = true)
    private String transactionId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.1", message = "Amount must be greater than zero")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private LocalDateTime paymentDate = LocalDateTime.now();
}
