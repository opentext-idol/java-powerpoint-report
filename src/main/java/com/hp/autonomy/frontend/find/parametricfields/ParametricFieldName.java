package com.hp.autonomy.frontend.find.parametricfields;

import com.hp.autonomy.iod.client.api.search.FieldNames;
import lombok.Getter;

import java.util.Set;

@Getter
public class ParametricFieldName {
    private final String name;
    private final Set<FieldNames.ValueAndCount> values;

    public ParametricFieldName(final String name, final Set<FieldNames.ValueAndCount> values) {
        this.name = name;
        this.values = values;
    }
}