package com.lastcalleats.marketplace.order.factory;

import com.lastcalleats.marketplace.order.entity.PickupCodeDO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PickupCodeFactory {

    private final Map<String, PickupCodeGenerator> generatorMap;

    public PickupCodeFactory(List<PickupCodeGenerator> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(PickupCodeGenerator::getType, Function.identity()));
    }

    public PickupCodeDO generate(String type, Long orderId) {
        PickupCodeGenerator generator = generatorMap.get(type);
        if (generator == null) {
            throw new IllegalArgumentException("Unknown pickup code type: " + type);
        }
        return generator.generate(orderId);
    }
}
