package org.lebastudios.theroundtable.maths;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalOperations
{
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor)
    {
        return dividend.divide(divisor, 2, RoundingMode.FLOOR);
    }
}
