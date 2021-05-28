package com.finmid.bankingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "balance")
    @Builder.Default
    private BigDecimal balance = new BigDecimal("100000.00");

    private LocalDateTime createOn;

    private LocalDateTime lastModifiedOn;

    @PreUpdate
    public void preUpdate() {
        lastModifiedOn = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        createOn = LocalDateTime.now();
    }

}
