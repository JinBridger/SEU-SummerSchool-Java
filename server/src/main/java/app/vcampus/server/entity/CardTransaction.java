package app.vcampus.server.entity;

import app.vcampus.server.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "card_transaction")
@Slf4j
public class CardTransaction {
    @Id
    public UUID uuid;

    @Column(nullable = false)
    public Integer cardNumber;

    public Integer amount;

    @Enumerated(EnumType.STRING)
    public TransactionType type;

    public Date time;
}
