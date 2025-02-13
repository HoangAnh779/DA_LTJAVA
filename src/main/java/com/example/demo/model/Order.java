package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String deliveryAddress;
    private float phoneNumber;
    private String email;
    private String note;
    private String paymentMethods;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
}
