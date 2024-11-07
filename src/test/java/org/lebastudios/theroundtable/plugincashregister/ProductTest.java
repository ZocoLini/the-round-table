package org.lebastudios.theroundtable.plugincashregister;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class ProductTest
{
    private static Product product;
    
    @BeforeAll
    static void setUp()
    {
        product = new Product();
        product.setPrice(BigDecimal.valueOf(2.60));
        product.setTaxes(BigDecimal.valueOf(0.21));
        product.setTaxesIncluded(false);
    }
    
    @Test
    void getPrice()
    {
        assertEquals(product.getPrice(), BigDecimal.valueOf(3.146));
    }

    @Test
    void getNotTaxedPrice()
    {
        assertEquals(product.getNotTaxedPrice(), BigDecimal.valueOf(2.60));
    }

    @Test
    void getTaxes()
    {
        assertEquals(product.getTaxes(), BigDecimal.valueOf(0.21));
    }

    @Test
    void getTaxesIncluded()
    {
        assertFalse(product.getTaxesIncluded());
    }
}