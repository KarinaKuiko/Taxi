package org.example.ride.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.ride.constants.PriceGeneratorConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PriceGenerator {

    public BigDecimal generateRandomCost() {
        Random random = new Random();
        int integerPart = random.nextInt(PriceGeneratorConstants.MAX_VALUE.intValue() + 1);
        int fractionalPart = random.nextInt(100);
        BigDecimal randomCost = new BigDecimal(integerPart + "." + String.format("%02d", fractionalPart));
        return randomCost.setScale(PriceGeneratorConstants.SCALE, RoundingMode.HALF_UP);
    }
}
