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
    @EmbeddedId
    private ProductReceiptId id;

    @Column(name = "QUANTITY", nullable = false)
    private BigDecimal quantity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId("product_Id")
    @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @MapsId("receipt_Id")
    @JoinColumn(name = "RECEIPT_ID", referencedColumnName = "ID")
    private Receipt receipt;

    @Embeddable
    public record ProductReceiptId(int product_Id, int receipt_Id) {}
}
