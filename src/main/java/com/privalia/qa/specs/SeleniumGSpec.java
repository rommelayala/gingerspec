package com.privalia.qa.specs;

import com.privalia.qa.utils.PreviousWebElements;
import cucumber.api.java.en.Then;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;

import static com.privalia.qa.assertions.Assertions.assertThat;

/**
 * Generic Selenium Specs.
 * @see <a href="SeleniumGSpec-annotations.html">Selenium Steps &amp; Matching Regex</a>
 */
public class SeleniumGSpec extends BaseGSpec {


    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public SeleniumGSpec(CommonG spec) {
        this.commonspec = spec;

    }


    /**
     * Checks that a web elements exists in the page and is of the type specified. This method is similar to {@link ThenGSpec#assertSeleniumNElementExists(Integer, String, String)}
     * but implements a pooling mechanism with a maximum pooling time instead of a static wait
     * @param poolingInterval   Time between consecutive condition evaluations
     * @param poolMaxTime       Maximum time to wait for the condition to be true
     * @param elementsCount     integer. Expected number of elements.
     * @param method            class of element to be searched
     * @param element           webElement searched in selenium context
     * @param type              The expected style of the element: visible, clickable, present, hidden
     * @throws Throwable        Throwable
     */
    @Then("^I check every '(\\d+)' seconds for at least '(\\d+)' seconds until '(\\d+)' elements exists with '([^:]*?):(.+?)' and is '(visible|clickable|present|hidden)'$")
    public void waitWebElementWithPooling(int poolingInterval, int poolMaxTime, int elementsCount, String method, String element, String type) throws Throwable {
        List<WebElement> wel = commonspec.locateElementWithPooling(poolingInterval, poolMaxTime, method, element, elementsCount, type);
        PreviousWebElements pwel = new PreviousWebElements(wel);
        commonspec.setPreviousWebElements(pwel);
    }

    /**
     * Checks if an alert message is open in the current page. The function implements a pooling interval to check if the condition is true
     * @param poolingInterval   Time between consecutive condition evaluations
     * @param poolMaxTime       Maximum time to wait for the condition to be true
     * @throws Throwable        Throwable
     */
    @Then("^I check every '(\\d+)' seconds for at least '(\\d+)' seconds until an alert appears$")
    public void waitAlertWithPooling(int poolingInterval, int poolMaxTime) throws Throwable {
        Alert alert = commonspec.waitAlertWithPooling(poolingInterval, poolMaxTime);
        commonspec.setSeleniumAlert(alert);
    }

    /**
     * Accepts an alert message previously found
     * @throws Throwable    Throwable
     */
    @Then("^I dismiss the alert$")
    public void iAcceptTheAlert() throws Throwable {
        commonspec.dismissSeleniumAlert();
    }

    /**
     * Dismiss an alert message previously found
     * @throws Throwable    Throwable
     */
    @Then("^I accept the alert$")
    public void iDismissTheAlert() throws Throwable {
        commonspec.acceptSeleniumAlert();
    }

    /**
     * Assigns the given file (relative to schemas/) to the web elements in the given index. This step
     * is suitable for file selectors/file pickers (an input type=file), where the user must specify a
     * file in the local computer as an input in a form
     * @param fileName      Name of the file relative to schemas folder (schemas/myFile.txt)
     * @param index         Index of the web element (file input)
     * @throws Throwable    Throwable
     */
    @Then("^I assign the file in '(.+?)' to the element on index '(\\d+)'$")
    public void iSetTheFileInSchemasEmptyJsonToTheElementOnIndex(String fileName, int index) throws Throwable {

        //Get file absolute path
        String filePath = getClass().getClassLoader().getResource(fileName).getPath();

        //Assign the file absolute path to the file picker element previously set
        File f = new File(filePath);
        assertThat(this.getCommonSpec(), f.exists()).as("The file located in " + filePath + " does not exists or is not accesible").isEqualTo(true);
        commonspec.getPreviousWebElements().getPreviousWebElements().get(index).sendKeys(filePath);



    }
}
