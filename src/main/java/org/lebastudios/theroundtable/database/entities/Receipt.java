package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "RECEIPT")
public class Receipt
{
    @Column(name = "ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "PAYMENT_METHOD", nullable = false)
    private String paymentMethod;

    @Column(name = "TABLE_NAME", nullable = false)
    private String tableName;

    @Column(name = "PAYMENT_AMOUNT", nullable = false)
    private BigDecimal paymentAmount;

    @Column(name = "UNKNOWN_PRODUCTS_VALUE", nullable = false)
    private BigDecimal unknownProductsValue = new BigDecimal("0");

    @Column(name = "CLIENT_ID", insertable = false, updatable = false)
    private String clientId;

    @Column(name = "ACCOUNT_ID", insertable = false, updatable = false)
    private Integer accountId;

    @Column(name = "TRANSACTION_ID", insertable = false, updatable = false)
    private int transactionId;

    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Product_Receipt> products;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "ID")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    private Account account;

    @OneToOne(optional = false)
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "ID")
    private Transaction transaction;

    public BigDecimal getTaxedTotal()
    {
        return transaction.getAmount();
    }
    
    public BigDecimal getNotTaxedTotal()
    {
        var total = BigDecimal.ZERO;
        
        for (var productReceipt : products)
        {
            total = total.add(productReceipt.getProduct().getNotTaxedPrice().multiply(productReceipt.getQuantity()));
        }
        
        total = total.add(unknownProductsValue.divide(new BigDecimal("1.1"), 2, RoundingMode.FLOOR));
        
        return total;
    }
    
    public String getCustomerName()
    {
        if (client == null)
        {
            return "Público general";
        }
        else
        {
            return client.getName() + " - " + client.getId();
        }
    }

    public String getAttendantName()
    {
        return account == null
                ? "Unknown employee"
                : account.getName();
    }
}
