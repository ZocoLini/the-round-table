package org.lebastudios.theroundtable.database.entities;

import jakarta.persistence.*;
import lombok.*;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.events.DatabaseEntitiesEvents;
import org.lebastudios.theroundtable.maths.BigDecimalOperations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "PRODUCT")
public class Product
{
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NAME", nullable = false)
    private String name = "Unknown Product";

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "IMG_PATH", nullable = false, length = 999999)
    private String imgPath = "";

    /**
     * The taxes that are applied to the product. Values between 0 and 1.
     */
    @Column(name = "TAXES")
    private BigDecimal taxes = new BigDecimal("0.10");

    @Column(name = "TAXES_INCLUDED", nullable = false)
    private Boolean taxesIncluded = true;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "CATEGORY_NAME", referencedColumnName = "CATEGORY_NAME")
    @JoinColumn(name = "SUB_CATEGORY_NAME", referencedColumnName = "NAME")
    private SubCategory subCategory;

    public static void removeProductFromDB(int productId)
    {
        Database.getInstance().connectTransaction(session ->
        {
            var entity = session.get(Product.class, productId);
            
            session.remove(entity);
        });

        DatabaseEntitiesEvents.onProductsModified.invoke();
    }

    /**
     * Returns the price of the product with taxes.
     * @return The price of the product with taxes.
     */
    public BigDecimal getPrice()
    {
        return taxesIncluded ? price : price.add(price.multiply(taxes));
    }
    
    public BigDecimal getNotTaxedPrice()
    {
        return taxesIncluded ? BigDecimalOperations.dividePrecise(price, taxes.add(BigDecimal.ONE)) : price;
    }

    @Override
    public int hashCode()
    {
        int result = id;
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(price);
        return result;
    }

    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;

        return id == product.id && Objects.equals(name, product.name) &&
                Objects.equals(price, product.price);
    }
}
