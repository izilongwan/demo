package com.enumeration;

import org.junit.jupiter.api.Test;

public class CarEnumTest {
    @Test
    public void run() {
        CarEnum v = CarEnum.valueOf("BYD");

        runCar(v);
        runCar(CarEnum.FT);
    }

    public void runCar(CarEnum carEnum) {
        switch (carEnum) {
            case BSJ:
            case BC:
                break;

            case BM:
            case BYD:
                System.out.println(carEnum.getBrand());
                break;

            default:
                break;
        }

        carEnum.run();
    }
}
