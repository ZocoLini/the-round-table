package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "MONEY_TRANSACTION")
public class Transaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "DATE", nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "DESCRIPTION", nullable = false, length = 99999)
    private String description = "";

    @OneToOne
    @JoinColumn(name = "RECEIPT_ID", referencedColumnName = "ID")
    private Receipt receipt;
    
    public String getDescription()
    {
        if (description == null || description.isBlank()) 
        {
            return "Transaction #" + id;
        }
        
        return description;
    }
}
