package org.lebastudios.theroundtable.database.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lebastudios.theroundtable.maths.BigDecimalOperations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest
{
    private static Order order;
    private static Product product;
    
    @BeforeAll
    static void setUp()
    {
        order = new Order();
        
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(2.60));
        product.setTaxes(BigDecimal.valueOf(0.21));
        product.setTaxesIncluded(false);
        
        order.addProduct(product, BigDecimal.valueOf(2), false);
        
        product = new Product();
        product.setPrice(BigDecimal.valueOf(1.50));
        product.setTaxes(BigDecimal.valueOf(0.10));
        product.setTaxesIncluded(true);
        
        order.addProduct(product, BigDecimal.valueOf(3), false);
    }

    @Test
    void getTotal()
    {
        assertEquals(order.getTotal(), BigDecimal.valueOf(10.792));
    }

    @Test
    void getTotalWithoutTaxes()
    {
        assertEquals(BigDecimalOperations.round(order.getTotalWithoutTaxes()), BigDecimal.valueOf(9.29));
    }

    @Test
    void getTotalTaxes()
    {
        assertEquals(BigDecimalOperations.toString(order.getTotalTaxes()), "1.50");
    }
}