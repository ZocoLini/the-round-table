package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lebastudios.theroundtable.locale.LangFileLoader;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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

    /**
     * The amount of money that the client paid. Not the total amount of the receipt.
     */
    @Column(name = "PAYMENT_AMOUNT", nullable = false)
    private BigDecimal paymentAmount;
    
    @Column(name = "TAXES_AMOUNT", nullable = false)
    private BigDecimal taxesAmount;

    @Column(name = "UNKNOWN_PRODUCTS_VALUE", nullable = false)
    private BigDecimal unknownProductsValue = new BigDecimal("0");

    @Column(name = "CLIENT_NAME")
    private String clientName;
    
    @Column(name = "CLIENT_IDENTIFIER")
    private String clientIdentifier;

    @Column(name = "EMPLOYEE_NAME")
    private String employeeName;

    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Product_Receipt> products;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "ID")
    private Transaction transaction;

    public void setAccount(Account account)
    {
        employeeName = account.getName();
    }
    
    public void setClient(String name, String identifier)
    {
        clientName = name;
        clientIdentifier = identifier;
    }

    public void setOrder(Order order)
    {
        tableName = order.getOrderName();
        taxesAmount = order.getTotalTaxes();
    }
    
    public BigDecimal getTaxedTotal()
    {
        return transaction.getAmount();
    }

    public BigDecimal getNotTaxedTotal()
    {
        return getTaxedTotal().subtract(taxesAmount);
    }

    public String getClientString()
    {
        if (clientName == null)
        {
            return LangFileLoader.getTranslation("phrase.generalpublicclient");
        }
        else
        {
            return clientName + " - " + clientIdentifier;
        }
    }

    public String getAttendantName()
    {
        return employeeName == null
                ? "Unknown employee"
                : employeeName;
    }
}
