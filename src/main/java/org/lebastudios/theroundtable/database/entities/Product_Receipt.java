package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "PRODUCT_RECEIPT")
@Entity
public class Product_Receipt
{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "RECEIPT_ID", nullable = false)
    private int receipt_Id;

    @Column(name = "QUANTITY", nullable = false)
    private BigDecimal quantity;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;
    
    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "RECEIPT_ID", referencedColumnName = "ID")
    private Receipt receipt;
}
