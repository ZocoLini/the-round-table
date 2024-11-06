package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Session;
import org.lebastudios.theroundtable.locale.LangFileLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
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
    @Setter private String paymentMethod;

    @Column(name = "TABLE_NAME", nullable = false)
    @Setter private String tableName;

    /**
     * The amount of money that the client paid. Not the total amount of the receipt.
     */
    @Column(name = "PAYMENT_AMOUNT", nullable = false)
    @Setter private BigDecimal paymentAmount;

    @Column(name = "TAXES_AMOUNT", nullable = false)
    private BigDecimal taxesAmount;

    @Column(name = "UNKNOWN_PRODUCTS_VALUE", nullable = false)
    private BigDecimal unknownProductsValue = new BigDecimal("0");

    @Column(name = "CLIENT_NAME")
    private String clientName;

    @Column(name = "CLIENT_IDENTIFIER")
    private String clientIdentifier;

    @Column(name = "EMPLOYEE_NAME")
    @Setter private String employeeName;

    @OneToMany(mappedBy = "receipt", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Product_Receipt> products;

    @OneToOne(mappedBy = "receipt", optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    public void setOrder(Order order, Session session)
    {
        tableName = order.getOrderName();
        taxesAmount = order.getTotalTaxes();

        unknownProductsValue = new BigDecimal("0");
        
        products = new HashSet<>();
        
        for (var entry : order.getProducts().entrySet())
        {
            Product dbProduct = session.get(Product.class, entry.getKey().getId());

            if (dbProduct == null)
            {
                unknownProductsValue = unknownProductsValue.add(entry.getKey().getPrice().multiply(entry.getValue()));
                continue;
            }

            Product_Receipt productReceipt = new Product_Receipt(entry.getKey(), entry.getValue());
            productReceipt.setReceipt(this);
            
            products.add(productReceipt) ;
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(order.getTotal());
        transaction.setDate(LocalDateTime.now());
        transaction.setReceipt(this);
        this.transaction = transaction;

        session.persist(this);
        session.flush();

        transaction.setDescription(LangFileLoader.getTranslation("word.receipt") + " #" + id);
        session.merge(this);
        
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
