/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.find.core.savedsearches;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public abstract class AbstractSavedSearchTest<T extends SavedSearch<T>> {
    protected abstract SavedSearch.Builder<T> createBuilder();

    @Test
    public void toQueryTextWithNoConceptClusters() {
        final SavedSearch<T> search = createBuilder()
                .setQueryText("cats")
                .build();

        assertThat(search.toQueryText(), is("cats"));
    }

    @Test
    public void toQueryTextWithConceptClusters() {
        final Set<ConceptClusterPhrase> conceptClusterPhrases = new HashSet<>();

        final ConceptClusterPhrase countyClusterPhrase = new ConceptClusterPhrase("county", true, 0);
        final ConceptClusterPhrase californiaClusterPhrase = new ConceptClusterPhrase("california", false, 0);
        conceptClusterPhrases.add(countyClusterPhrase);
        conceptClusterPhrases.add(californiaClusterPhrase);

        final ConceptClusterPhrase lukeClusterPhrase = new ConceptClusterPhrase("luke skywalker", true, 1);
        conceptClusterPhrases.add(lukeClusterPhrase);

        final SavedSearch<T> search = createBuilder()
                .setQueryText("orange jedi")
                .setConceptClusterPhrases(conceptClusterPhrases)
                .build();

        final String queryText = search.toQueryText();
        final List<String> splitQueryText = Arrays.asList(queryText.split("\\s+AND\\s+"));

        assertThat(splitQueryText, hasSize(4));
        assertThat(splitQueryText.get(0), is("(orange jedi)"));
        assertThat(splitQueryText, hasItems("\"luke skywalker\"", "\"county\"", "\"california\""));
    }
}
