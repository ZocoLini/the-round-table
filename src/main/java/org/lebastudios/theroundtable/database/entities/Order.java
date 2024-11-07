package org.lebastudios.theroundtable.database.entities;

import lombok.Data;
import lombok.Setter;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.maths.BigDecimalOperations;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Data
public class Order
{
    private String orderName;
    private int unknownPeoductsCount = 0;
    @Setter private Map<Product, BigDecimal> products = new LinkedHashMap<>();
    @Setter private Set<Integer> separatosPositions = new HashSet<>();

    public static Order createFromReceipt(int receiptId)
    {
        var order = new Order();

        final var unknownProduct = new Product();
        unknownProduct.setName(LangFileLoader.getTranslation("phrase.unknownproduct"));

        Database.getInstance().connectQuery(session ->
        {
            var receipt = session.get(Receipt.class, receiptId);

            for (var receiptItem : receipt.getProducts())
            {
                order.addProduct(receiptItem.getProduct(), receiptItem.getQuantity(), false);
            }

            unknownProduct.setPrice(receipt.getUnknownProductsValue());
        });

        if (!unknownProduct.getPrice().equals(new BigDecimal(0)))
        {
            order.addProduct(unknownProduct, new BigDecimal(1), false);
        }

        return order;
    }

    public void addProduct(Product product, BigDecimal quantity, boolean isCustomProduct)
    {
        if (isCustomProduct)
        {
            unknownPeoductsCount++;
            product.setName(product.getName() + " (" + unknownPeoductsCount + ")");
        }

        products.put(product, products.getOrDefault(product, BigDecimal.ZERO).add(quantity));
    }

    public BigDecimal getTotal()
    {
        BigDecimal total = BigDecimal.ZERO;

        for (var entry : products.entrySet())
        {
            total = total.add(entry.getKey().getPrice().multiply(entry.getValue()));
        }

        return total;
    }

    /**
     * This Method always returns getTotal() + €. The currency should be decided at runtime.
     */
    @Deprecated
    public String getTotalStringRepresentation()
    {
        return BigDecimalOperations.toString(getTotal()) + " €";
    }
    
    public BigDecimal getTotalWithoutTaxes()
    {
        BigDecimal total = BigDecimal.ZERO;

        for (var entry : products.entrySet())
        {
            total = total.add(entry.getKey().getNotTaxedPrice().multiply(entry.getValue()));
        }

        return total;
    }
    
    public BigDecimal getTotalTaxes()
    {
        return getTotal().subtract(getTotalWithoutTaxes());
    }
    
    public void removeProductQty(Product product, BigDecimal quantity)
    {
        if (!products.containsKey(product))
        {
            System.err.println("Product not found in actual order");
            return;
        }

        products.put(product, products.get(product).subtract(quantity));

        if (products.get(product).compareTo(BigDecimal.ZERO) <= 0)
        {
            products.remove(product);
            fixSeparatorsPositions();
        }
    }

    private void fixSeparatorsPositions()
    {
        var newSeparators = new HashSet<Integer>();
        var productsQty = products.entrySet().size();

        for (int i = 0; i < productsQty; i++)
        {
            if (separatosPositions.contains(i))
            {
                newSeparators.add(i);
            }
        }

        separatosPositions = newSeparators;
    }

    public void removeProduct(Product product)
    {
        if (!products.containsKey(product))
        {
            System.err.println("Product not found in actual order");
            return;
        }

        products.remove(product);
        fixSeparatorsPositions();
    }

    public void reset()
    {
        products.clear();
        separatosPositions.clear();
        unknownPeoductsCount = 0;
    }
}
