package com.autonomy.abc.find;

import com.autonomy.abc.config.ABCTestBase;
import com.autonomy.abc.config.TestConfig;
import com.autonomy.abc.selenium.config.ApplicationType;
import com.autonomy.abc.selenium.find.FindPage;
import com.autonomy.abc.selenium.find.Input;
import com.autonomy.abc.selenium.find.Service;
import com.autonomy.abc.selenium.menu.NavBarTabId;
import com.autonomy.abc.selenium.page.HSOElementFactory;
import com.autonomy.abc.selenium.page.keywords.CreateNewKeywordsPage;
import com.autonomy.abc.selenium.page.keywords.KeywordsPage;
import com.autonomy.abc.selenium.page.promotions.CreateNewPromotionsBase;
import com.autonomy.abc.selenium.page.promotions.CreateNewPromotionsPage;
import com.autonomy.abc.selenium.page.promotions.HSOPromotionsPage;
import com.autonomy.abc.selenium.page.promotions.PromotionsPage;
import com.autonomy.abc.selenium.page.search.SearchPage;
import com.hp.autonomy.hod.client.api.authentication.ApiKey;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationService;
import com.hp.autonomy.hod.client.api.authentication.AuthenticationServiceImpl;
import com.hp.autonomy.hod.client.api.authentication.TokenType;
import com.hp.autonomy.hod.client.api.resource.ResourceIdentifier;
import com.hp.autonomy.hod.client.api.textindex.query.fields.*;
import com.hp.autonomy.hod.client.config.HodServiceConfig;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.client.token.TokenProxy;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.autonomy.abc.framework.ABCAssert.assertThat;
import static com.autonomy.abc.framework.ABCAssert.verifyThat;
import static com.thoughtworks.selenium.SeleneseTestBase.assertNotEquals;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

public class FindITCase extends ABCTestBase {
    private FindPage find;
    private Service service;
    private Input input;
    private Logger logger = LoggerFactory.getLogger(FindITCase.class);
    private PromotionsPage promotions;
    private List<String> browserHandles;
    private final String domain = "ce9f1f3d-a780-4793-8a6a-a74b12b7d1ae";
    private final Matcher<String> noDocs = containsString("No results found");

    public FindITCase(TestConfig config, String browser, ApplicationType type, Platform platform) {
        super(config, browser, type, platform);
    }

    @Before
    public void setUp(){
        promotions = getElementFactory().getPromotionsPage();

        browserHandles = promotions.createAndListWindowHandles();

        getDriver().switchTo().window(browserHandles.get(1));
        getDriver().get("https://find.dev.idolondemand.com/");
        getDriver().manage().window().maximize();
        find = ((HSOElementFactory) getElementFactory()).getFindPage();
        input = find.getInput();
        service = find.getService();
    }

    @Test
    public void testSendKeys() throws InterruptedException {
        String searchTerm = "Fred is a chimpanzee";
        find.search(searchTerm);
        assertThat(input.getSearchTerm(), is(searchTerm));
    }

    @Test
    public void testPdfContentTypeValue(){
        find.search("red star");
        service.selectContentType("APPLICATION/PDF");
        for(String type : service.getDisplayedDocumentsDocumentTypes()){
            assertThat(type,containsString("pdf"));
        }
    }

    @Test
    public void testHtmlContentTypeValue(){
        find.search("red star");
        service.selectContentType("TEXT/HTML");
        for(String type : service.getDisplayedDocumentsDocumentTypes()){
            assertThat(type,containsString("html"));
        }
    }

    @Test
    public void testUnselectingContentTypeQuicklyDoesNotLeadToError()  {
        find.search("red star");
        service.selectContentType("APPLICATION/PDF");
        service.selectContentType("APPLICATION/PDF");
        assertThat(service.getText().toLowerCase(), not(containsString("error")));
    }

    @Test
    public void testSearch(){
        find.search("Red");
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);
        assertThat(service.getText().toLowerCase(), not(containsString("error")));
    }

    @Test
    public void testSortByRelevance() {
        getDriver().switchTo().window(browserHandles.get(0));
        body.getTopNavBar().search("stars bbc");
        SearchPage searchPage = getElementFactory().getSearchPage();
        searchPage.sortByRelevance();
        List<String> searchTitles = searchPage.getSearchResultTitles(30);

        getDriver().switchTo().window(browserHandles.get(1));
        find.search("stars bbc");

        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);

        List<String> findSearchTitles = service.getResultTitles();

        for(int i = 0; i < 30; i++){
            assertThat(findSearchTitles.get(i), is(searchTitles.get(i)));
        }
    }

    @Test
    public void testSortByDate(){
        getDriver().switchTo().window(browserHandles.get(0));
        body.getTopNavBar().search("stars bbc");
        SearchPage searchPage = getElementFactory().getSearchPage();
        searchPage.sortByDate();
        List<String> searchTitles = searchPage.getSearchResultTitles(30);

        getDriver().switchTo().window(browserHandles.get(1));
        find.search("stars bbc");

        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);
        find.sortByDate();

        List<String> findSearchTitles = service.getResultTitles();

        for(int i = 0; i < 30; i++){
            assertThat(findSearchTitles.get(i), is(searchTitles.get(i)));
        }
    }

    //TODO ALL RELATED CONCEPTS TESTS - probably better to check if text is not("Loading...") rather than not("")
    @Test
    public void testRelatedConceptsHasResults(){
        find.search("Danye West");
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.RIGHT);
        WebElement relatedConcepts = service.getRelatedConcepts();

        int i = 1;
        for(WebElement top : relatedConcepts.findElements(By.xpath("./a"))){
            assertThat(top.getText(),not(""));

            WebElement table = top.findElement(By.xpath("./following-sibling::table[1]"));

            for(WebElement entry : table.findElements(By.tagName("a"))){
                assertThat(entry.getText(),not(""));
            }
        }
    }

    @Test
    public void testRelatedConceptsNavigateOnClick(){
        find.search("Red");
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.RIGHT);
        WebElement topRelatedConcept = service.getRelatedConcepts().findElement(By.tagName("a"));

        String concept = topRelatedConcept.getText();

        topRelatedConcept.click();

        assertThat(getDriver().getCurrentUrl(), containsString(concept));
        assertThat(input.getSearchTerm(), containsString(concept));

        service.waitForSearchLoadIndicatorToDisappear(Service.Container.RIGHT);

        WebElement tableRelatedConcept = service.getRelatedConcepts().findElement(By.cssSelector("table a"));

        concept = tableRelatedConcept.getText();

        tableRelatedConcept.click();

        assertThat(getDriver().getCurrentUrl(), containsString(concept));
        assertThat(input.getSearchTerm(), containsString(concept));
    }

    @Test
    public void testRelatedConceptsHover(){
        find.search("Find");
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.RIGHT);
        WebElement topRelatedConcept = service.getRelatedConcepts().findElement(By.tagName("a"));
        WebElement tableLink = service.getRelatedConcepts().findElement(By.cssSelector("table a"));

        hoverOverElement(topRelatedConcept);

        WebElement popover = getDriver().findElement(By.className("popover"));

        new WebDriverWait(getDriver(),10).until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(popover, "Loading")));
        assertThat(popover.getText(), not(""));

        hoverOverElement(service.getResultsDiv());

        new WebDriverWait(getDriver(),2).until(ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("popover"))));

        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", tableLink);
        hoverOverElement(tableLink);

        WebElement tablePopover = new WebDriverWait(getDriver(),5).until(ExpectedConditions.visibilityOfElementLocated(By.className("popover")));
        new WebDriverWait(getDriver(),10).until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(tablePopover, "Loading")));

        assertThat(tablePopover.getText(), not(""));

    }

    //TODO delete promotion
    @Test
    public void testPinToPosition(){
        String searchTerm = "red";
        String trigger = "mate";

        getDriver().switchTo().window(browserHandles.get(0));

        navigateToPromotionsAndDelete();

        try {
            body.getTopNavBar().search(searchTerm);
            SearchPage searchPage = getElementFactory().getSearchPage();
            String documentTitle = searchPage.createAPromotion();
            CreateNewPromotionsPage createNewPromotionsPage = getElementFactory().getCreateNewPromotionsPage();
            createNewPromotionsPage.pinToPosition().click();
            createNewPromotionsPage.continueButton(CreateNewPromotionsBase.WizardStep.TYPE).click();
            createNewPromotionsPage.loadOrFadeWait();
            createNewPromotionsPage.continueButton(CreateNewPromotionsBase.WizardStep.PROMOTION_TYPE).click();
            createNewPromotionsPage.loadOrFadeWait();
            createNewPromotionsPage.addSearchTrigger(trigger);
            createNewPromotionsPage.finishButton().click();
            getElementFactory().getSearchPage(); //Wait for search page

            getDriver().switchTo().window(browserHandles.get(1));
            find.search(trigger);
            assertThat(service.getSearchResultTitle(1).getText(), is(documentTitle));
        } finally {
            getDriver().switchTo().window(browserHandles.get(0));
            navigateToPromotionsAndDelete();
        }
    }

    @Test
    public void testPinToPositionThree(){
        String searchTerm = "red";
        String trigger = "mate";

        getDriver().switchTo().window(browserHandles.get(0));

        navigateToPromotionsAndDelete();

        try {
            body.getTopNavBar().search(searchTerm);
            SearchPage searchPage = getElementFactory().getSearchPage();
            String documentTitle = searchPage.createAPromotion();
            CreateNewPromotionsPage createNewPromotionsPage = getElementFactory().getCreateNewPromotionsPage();
            createNewPromotionsPage.pinToPosition().click();
            createNewPromotionsPage.continueButton(CreateNewPromotionsBase.WizardStep.TYPE).click();
            createNewPromotionsPage.loadOrFadeWait();
            WebElement plus = createNewPromotionsPage.findElement(By.cssSelector(".current-step .plus"));
            plus.click();
            plus.click();
            createNewPromotionsPage.continueButton(CreateNewPromotionsBase.WizardStep.PROMOTION_TYPE).click();
            createNewPromotionsPage.loadOrFadeWait();
            createNewPromotionsPage.addSearchTrigger(trigger);
            createNewPromotionsPage.finishButton().click();
            getElementFactory().getSearchPage(); //Wait for search page

            getDriver().switchTo().window(browserHandles.get(1));
            find.search(trigger);
            assertThat(service.getSearchResultTitle(3).getText(), is(documentTitle));
        } finally {
            getDriver().switchTo().window(browserHandles.get(0));
            navigateToPromotionsAndDelete();
        }
    }

    private void navigateToPromotionsAndDelete(){
        body.getSideNavBar().switchPage(NavBarTabId.PROMOTIONS);
        getElementFactory().getPromotionsPage().deleteAllPromotions();
    }

    @Test
    public void testSpotlightPromotions(){
        String searchTerm = "Proper";
        String trigger = "Prim";

        getDriver().switchTo().window(browserHandles.get(0));

        navigateToPromotionsAndDelete();

        try {
            body.getTopNavBar().search(searchTerm);
            SearchPage searchPage = getElementFactory().getSearchPage();
            List<String> createdPromotions = searchPage.createAMultiDocumentPromotion(3);
            CreateNewPromotionsPage createNewPromotionsPage = getElementFactory().getCreateNewPromotionsPage();
            createNewPromotionsPage.addSpotlightPromotion("Spotlight", trigger);
            getElementFactory().getSearchPage(); //Wait for search page

            getDriver().switchTo().window(browserHandles.get(1));
            find.search(trigger);

            List<String> findPromotions = service.getPromotionsTitles();

            assertNotEquals(0, findPromotions.size());
            assertThat(findPromotions, containsInAnyOrder(createdPromotions.toArray()));

            for(WebElement promotion : service.getPromotions()){
                promotionShownCorrectly(promotion);
            }
        } finally {
            getDriver().switchTo().window(browserHandles.get(0));
            navigateToPromotionsAndDelete();
        }
    }

    @Test
    public void testStaticPromotions(){
        String title = "TITLE";
        String content = "CONTENT";
        String trigger = "LOVE";

        getDriver().switchTo().window(browserHandles.get(0));

        navigateToPromotionsAndDelete();

        try {
            ((HSOPromotionsPage) getElementFactory().getPromotionsPage()).newStaticPromotion(title, content, trigger);
            getElementFactory().getSearchPage(); //Wait to be navigated to SP

            getDriver().switchTo().window(browserHandles.get(1));
            find.search(trigger);
            List<WebElement> promotions = service.getPromotions();

            assertThat(promotions.size(), is(1));
            WebElement staticPromotion = promotions.get(0);
            assertThat(staticPromotion.findElement(By.tagName("h4")).getText(), is(title));
            assertThat(staticPromotion.findElement(By.className("result-summary")).getText(), containsString(content));
            promotionShownCorrectly(staticPromotion);
        } finally {
            getDriver().switchTo().window(browserHandles.get(0));
            navigateToPromotionsAndDelete();
        }
    }

    //THIS
    @Test
    public void testDynamicPromotions(){
        int resultsToPromote = 13;
        String trigger = "Rugby";

        getDriver().switchTo().window(browserHandles.get(0));

        navigateToPromotionsAndDelete();

        try{
            body.getTopNavBar().search("kittens");
            SearchPage searchPage = getElementFactory().getSearchPage();

            List<String> promotedDocumentTitles = searchPage.getSearchResultTitles(resultsToPromote);

            searchPage.findElement(By.className("dynamic-promotions-button")).click();
            find.loadOrFadeWait();
            WebElement dial = getDriver().findElement(By.className("dial"));
            dial.click();
            dial.sendKeys(Keys.RIGHT,
                    Keys.BACK_SPACE,
                    Keys.BACK_SPACE,
                    Keys.NUMPAD1,
                    Keys.NUMPAD3);
            System.out.println(dial.getAttribute("value"));
            getDriver().findElement(By.cssSelector(".current-step .next-step")).click();
            find.loadOrFadeWait();
            getDriver().findElement(By.cssSelector(".input-group input")).sendKeys(trigger);
            getDriver().findElement(By.cssSelector(".current-step .input-group .btn")).click();
            find.loadOrFadeWait();
            getDriver().findElement(By.cssSelector(".current-step .finish-step")).click();

            getElementFactory().getSearchPage();

            getDriver().switchTo().window(browserHandles.get(1));
            find.search(trigger);

            assertThat(service.getPromotionsTitles(),containsInAnyOrder(promotedDocumentTitles.toArray()));

            for(WebElement promotion : service.getPromotions()){
                promotionShownCorrectly(promotion);
            }

        } finally {
            getDriver().switchTo().window(browserHandles.get(0));
            navigateToPromotionsAndDelete();
        }
    }

    private void promotionShownCorrectly (WebElement promotion) {
        assertThat(promotion.getAttribute("class"),containsString("promoted-document"));
        assertThat(promotion.findElement(By.className("promoted-label")).getText(),containsString("Promoted"));
        assertTrue(promotion.findElement(By.className("icon-star")).isDisplayed());
    }

    @Test
    public void testCheckMetadata(){
        find.search("stars");

        for(WebElement searchResult : service.getResults()){
            String url = searchResult.findElement(By.className("document-reference")).getText();

            find.scrollIntoViewAndClick(searchResult.findElement(By.tagName("h4")));

            WebElement metadata = service.getViewMetadata();

            assertThat(metadata.findElement(By.xpath(".//tr[1]/td")).getText(),is(domain));
            assertThat(metadata.findElement(By.xpath(".//tr[2]/td")).getText(),is(not("")));
            assertThat(metadata.findElement(By.xpath(".//tr[3]/td")).getText(),is(url));

            service.closeViewBox();
            find.loadOrFadeWait();
        }
    }

    private void hoverOverElement(WebElement element){
        Actions builder = new Actions(getDriver());
        Dimension dimensions = element.getSize();
        builder.moveToElement(element, dimensions.getWidth() / 2, dimensions.getHeight() / 2);
        Action hover = builder.build();
        hover.perform();
    }

    @Test
    public void testFilterByIndex(){
        find.search("Sam");

        WebElement searchResult = service.getSearchResult(1);
        WebElement title = searchResult.findElement(By.tagName("h4"));

        String titleString = title.getText();
        title.click();

        WebElement metadata = service.getViewMetadata();
        String index = metadata.findElement(By.xpath(".//tr[2]/td")).getText();

        service.closeViewBox();
        service.loadOrFadeWait();

        service.filterByIndex(domain,index);
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);

        assertThat(service.getSearchResultTitle(1).getText(), is(titleString));
    }

    @Test
    public void testFilterByIndexOnlyContainsFilesFromThatIndex(){
        find.search("Happy");

        service.filterByIndex(domain, Index.RED.title);
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);
        service.getSearchResultTitle(1).click();
        do{
            assertThat(service.getViewMetadata().findElement(By.xpath(".//tr[2]/td")).getText(), is(Index.RED.title));
            service.viewBoxNextButton().click();
        } while (!service.cBoxFirstDocument());
    }

    @Test
    public void testQuicklyDoubleClickingIndexDoesNotLeadToError(){
        find.search("index");
        service.filterByIndex(domain, Index.NATURALNAVIGATOR.title);
        service.filterByIndex(domain, Index.NATURALNAVIGATOR.title);
        assertThat(service.getResultsDiv().getText().toLowerCase(), not(containsString("error")));
    }

    @Test
    public void testPreDefinedWeekHasSameResultsAsCustomWeek(){
        preDefinedDateFiltersVersusCustomDateFilters(Service.DateEnum.WEEK);
    }

    @Test
    public void testPreDefinedMonthHasSameResultsAsCustomMonth(){
        preDefinedDateFiltersVersusCustomDateFilters(Service.DateEnum.MONTH);
    }

    @Test
    public void testPreDefinedYearHasSameResultsAsCustomYear(){
        preDefinedDateFiltersVersusCustomDateFilters(Service.DateEnum.YEAR);
    }

    private void preDefinedDateFiltersVersusCustomDateFilters(Service.DateEnum period){
        find.search("Rugby");

        service.filterByDate(period);
        List<String> preDefinedResults = service.getResultTitles();
        service.filterByDate(getDateString(period),"");
        List<String> customResults = service.getResultTitles();

        assertThat(preDefinedResults.size(), is(customResults.size()));

        for(int i = 0; i < preDefinedResults.size(); i++){
            assertThat(preDefinedResults.get(i), is(customResults.get(i)));
        }
    }

    private String getDateString (Service.DateEnum period) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar cal = Calendar.getInstance();

        if (period != null) {
            switch (period) {
                case WEEK:
                    cal.add(Calendar.DATE,-7);
                    break;
                case MONTH:
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
                    break;
                case YEAR:
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
                    break;
            }
        }

        return dateFormat.format(cal.getTime());
    }

    @Test
    public void testAllParametricFieldsAreShown() throws HodErrorException {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setProxy(new HttpHost("proxy.sdc.hp.com", 8080));


        final HodServiceConfig config = new HodServiceConfig.Builder("https://api.int.havenondemand.com")
                .setHttpClient(httpClientBuilder.build()) // use a custom Apache HttpClient - useful if you're behind a proxy
                .build();

        final AuthenticationService authenticationService = new AuthenticationServiceImpl(config);
        final RetrieveIndexFieldsService retrieveIndexFieldsService = new RetrieveIndexFieldsServiceImpl(config);

        final TokenProxy tokenProxy = authenticationService.authenticateApplication(
                new ApiKey("098b8420-f85f-4164-b8a8-42263e9405a1"),
                "733d64e8-41f7-4c46-a1c8-60d083255159",
                domain,
                TokenType.simple
        );

        Set<String> parametricFields = new HashSet<>();

        for(Index i : Index.values()) {
            RetrieveIndexFieldsResponse retrieveIndexFieldsResponse = retrieveIndexFieldsService.retrieveIndexFields(tokenProxy,
                    new ResourceIdentifier(domain, i.title), new RetrieveIndexFieldsRequestBuilder().setFieldType(FieldType.parametric));

            parametricFields.addAll(retrieveIndexFieldsResponse.getAllFields());
        }

        find.search("Something");

        for(String field : parametricFields) {
            try {
                assertTrue(service.getParametricContainer(field).isDisplayed());
            } catch (ElementNotVisibleException | NotFoundException e) {
                fail("Could not find field '"+field+"'");
            }
        }
    }

    @Test
    public void testViewDocumentsOpenFromFind(){
        find.search("Review");

        for(WebElement result : service.getResults()){
            result.findElement(By.tagName("h4")).click();

            new WebDriverWait(getDriver(),20).until(new WaitForCBoxLoadIndicatorToDisappear());
            assertThat(service.getCBoxLoadedContent().getText(), not(containsString("500")));
            service.closeViewBox();
            find.loadOrFadeWait();
        }
    }

    private class WaitForCBoxLoadIndicatorToDisappear implements ExpectedCondition {
        @Override
        public Object apply(Object o) {
            return !getDriver().findElement(By.cssSelector("#cboxLoadedContent .icon-spin")).isDisplayed();
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    @Test
    public void testViewDocumentsOpenWithArrows(){
        find.search("Review");

        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);
        service.getSearchResultTitle(1).click();
        do{
            assertThat(service.getCBoxLoadedContent().getText(),not(containsString("500")));
            service.viewBoxNextButton().click();
        } while (!service.cBoxFirstDocument());
    }

    @Test
    public void testDateRemainsWhenClosingAndReopeningDateFilters(){
        find.search("Corbyn");

        String start = getDateString(Service.DateEnum.MONTH);
        String end = getDateString(Service.DateEnum.WEEK);

        service.filterByDate(start,end);
        find.loadOrFadeWait();
        service.filterByDate(Service.DateEnum.CUSTOM); //For some reason doesn't close first time
        service.filterByDate(Service.DateEnum.CUSTOM);
        find.loadOrFadeWait();
        service.filterByDate(Service.DateEnum.CUSTOM);
        find.loadOrFadeWait();

        assertThat(service.getStartDateFilter().getAttribute("value"), is(start));
        assertThat(service.getEndDateFilter().getAttribute("value"), is(end));
    }

    @Test
    public void testFileTypes(){
        for(FileType f : FileType.values()) {
            service.selectContentType(f.getSidebarString());

            for(WebElement result : service.getResults()){
                assertThat(result.findElement(By.tagName("i")).getAttribute("class"), containsString(f.getFileIconString()));
            }
        }
    }

    @Test
    public void testSynonyms() throws InterruptedException {
        getDriver().switchTo().window(browserHandles.get(0));
        body.getSideNavBar().switchPage(NavBarTabId.KEYWORDS);
        KeywordsPage keywordsPage = getElementFactory().getKeywordsPage();
        keywordsPage.deleteKeywords();

        find.loadOrFadeWait();

        getDriver().switchTo().window(browserHandles.get(1));
        find.search("iuhdsafsaubfdja");

        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);
        assertThat(service.getText(), noDocs); //TODO check error

        find.search("Cat");
        service.waitForSearchLoadIndicatorToDisappear(Service.Container.MIDDLE);
        assertThat(service.getText(), not(noDocs));
        String firstTitle = service.getSearchResultTitle(1).getText();

        getDriver().switchTo().window(browserHandles.get(0));
        body.getSideNavBar().switchPage(NavBarTabId.KEYWORDS);

        keywordsPage = getElementFactory().getKeywordsPage();
        keywordsPage.createNewKeywordsButton().click();

        CreateNewKeywordsPage createNewKeywordsPage = getElementFactory().getCreateNewKeywordsPage();
        createNewKeywordsPage.createSynonymGroup("cat iuhdsafsaubfdja","English");

        getElementFactory().getSearchPage();

        Thread.sleep(15000);

        getDriver().switchTo().window(browserHandles.get(1));
        find.search("iuhdsafsaubfdja");

        assertThat(service.getText(), not(noDocs));
        assertThat(service.getSearchResultTitle(1).getText(),is(firstTitle));
    }

    @Test
    public void testBlacklist() throws InterruptedException {
        find.search("Cat");

        assertThat(service.getText(), not(noDocs));

        find.search("Holder");

        getDriver().switchTo().window(browserHandles.get(0));

        body.getSideNavBar().switchPage(NavBarTabId.KEYWORDS);

        KeywordsPage keywordsPage = getElementFactory().getKeywordsPage();
        keywordsPage.createNewKeywordsButton().click();

        CreateNewKeywordsPage createNewKeywordsPage = getElementFactory().getCreateNewKeywordsPage();
        createNewKeywordsPage.createBlacklistedTerm("Cat","English");

        getDriver().switchTo().window(browserHandles.get(1));

        find.search("Cat");

        assertThat(service.getText(), noDocs);
    }

    @Test
    public void testSynonymGroups() throws InterruptedException {
        getDriver().switchTo().window(browserHandles.get(0));
        body.getSideNavBar().switchPage(NavBarTabId.KEYWORDS);
        KeywordsPage keywordsPage = getElementFactory().getKeywordsPage();
        keywordsPage.deleteKeywords();

        keywordsPage.createNewKeywordsButton().click();

        CreateNewKeywordsPage createNewKeywordsPage = getElementFactory().getCreateNewKeywordsPage();
        createNewKeywordsPage.createSynonymGroup("gazelle deer antelope", "English");

        getElementFactory().getSearchPage();

        getDriver().switchTo().window(browserHandles.get(1));
        find.search("gazelle");

        List<String> gazelleTitles = service.getResultTitles();

        find.search("deer");

        List<String> deerTitles = service.getResultTitles();

        verifyThat(gazelleTitles,contains(deerTitles.toArray()));

        find.search("antelope");

        List<String> antelopeTitles = service.getResultTitles();

        verifyThat(gazelleTitles,contains(antelopeTitles.toArray()));
        verifyThat(deerTitles,contains(antelopeTitles.toArray()));
    }

    private enum Index {
        DEFAULT("default_index"),
        NATURALNAVIGATOR("naturalnavigator"),
        RED("red"),
        RUGBY("rugbyworldcup"),
        BBC("bbc"),
        LABOUR("labour"),
        REDDIT("reddit");

        private final String title;

        Index(String index){
            this.title = index;
        }


        public String getTitle() {
            return title;
        }
    }

    private enum FileType {
        HTML("TEXT/HTML","html"),
        PDF("APPLICATION/PDF","pdf"),
        PLAIN("TEXT/PLAIN","file");

        private final String sidebarString;
        private final String fileIconString;

        FileType(String sidebarString, String fileIconString){
            this.sidebarString = sidebarString;
            this.fileIconString = fileIconString;
        }

        public String getFileIconString() {
            return fileIconString;
        }

        public String getSidebarString() {
            return sidebarString;
        }
    }

}
