package com.autonomy.abc.queryHelper;

import com.autonomy.abc.selenium.error.Errors;
import com.autonomy.abc.selenium.query.QueryService;
import com.autonomy.abc.shared.QueryTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static com.hp.autonomy.frontend.selenium.framework.state.TestStateAssert.verifyThat;
import static com.hp.autonomy.frontend.selenium.matchers.ElementMatchers.containsText;
import static com.hp.autonomy.frontend.selenium.matchers.StringMatchers.stringContainingAnyOf;


public class IdolQueryTestHelper<T> extends QueryTestHelper{
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryTestHelper.class);

    public IdolQueryTestHelper(final QueryService queryService){super(queryService);}

    @Override
    public void hiddenQueryOperatorText() {
        for (IdolQueryTermResult result : IdolQueryTermResult.idolResultsFor(getHiddenBooleans(),getService())) {
            if (result.errorWellExists() && result.errorContainer().isDisplayed()) {
                if(!result.correctedQuery().isDisplayed()){
                    verifyThat("Query not auto-corrected thus error is for no results",
                            result.errorContainer(),
                            containsText(Errors.Search.NO_RESULTS));
                }
                else{
                    verifyThat("Query auto-corrected so sees the Boolean",
                            result.getErrorMessage(),
                            stringContainingAnyOf(Arrays.asList(Errors.Search.CLOSING_BOOL,Errors.Search.OPENING_BOOL)));
                }
            } else {
                LOGGER.info("The error message is not displayed.");
            }
        }

    }
}