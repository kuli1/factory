package io.dddbyexamples.factory.delivery.planning.definition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import io.dddbyexamples.factory.demand.forecasting.Demand;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@Value
@AllArgsConstructor
public class DeliveryPlannerDefinition {
    @Singular
    private final Map<Demand.Schema, Map<LocalTime, Double>> definitions;
    private final boolean disabled;

    public static Map<LocalTime, Double> of(LocalTime time, Double fraction) {
        return Collections.singletonMap(time, fraction);
    }

    public <T> Map<Demand.Schema, T> map(Function<Map<LocalTime, Double>, T> timesAndFractions) {
        return definitions.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> timesAndFractions.apply(e.getValue())
                ));
    }
}
