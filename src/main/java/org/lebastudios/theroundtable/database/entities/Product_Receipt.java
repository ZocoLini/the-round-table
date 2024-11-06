package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Table(name = "PRODUCT_RECEIPT")
@Entity
public class Product_Receipt
{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private int id;

    @Column(name = "QUANTITY", nullable = false)
    @Getter @Setter private BigDecimal quantity;

    /**
     * The total amount paid for the product. Taxes included. 
     * This is the price of the product multiplied by the 
     * quantity. The value variable is the price of the product with taxes.
     */
    @Column(name = "TOTAL_VALUE", nullable = false)
    @Getter @Setter private BigDecimal totalValue;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "RECEIPT_ID", referencedColumnName = "ID")
    @Getter @Setter private Receipt receipt;

    @Column(name = "PRODUCT_VALUE", nullable = false)
    private BigDecimal productValue;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "TAXES")
    private BigDecimal taxes = new BigDecimal("0.10");

    @Column(name = "TAXES_INCLUDED", nullable = false)
    private Boolean taxesIncluded = true;
    
    public Product_Receipt(Product product, BigDecimal quantity)
    {
        this.quantity = quantity;
        setProduct(product);
    }
    
    public void setProduct(Product product)
    {
        if (quantity == null) throw new IllegalStateException("Quantity should be set before setting the product.");
        
        productName = product.getName();
        taxesIncluded = product.getTaxesIncluded();
        productValue = taxesIncluded ? product.getPrice() : product.getNotTaxedPrice();
        totalValue = product.getPrice().multiply(quantity);
        taxes = product.getTaxes();
    }
    
    public Product getProduct()
    {
        var product = new Product();
        product.setName(productName);
        product.setPrice(productValue);
        product.setTaxes(taxes);
        product.setTaxesIncluded(taxesIncluded);
        return product;
    }
}
