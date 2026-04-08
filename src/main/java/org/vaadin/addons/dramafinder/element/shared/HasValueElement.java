package org.vaadin.addons.dramafinder.element.shared;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Locator.ClickOptions;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Mixin for components that expose a textual {@code value} through an input slot.
 */
public interface HasValueElement extends HasLocatorElement {

    /** Locator for the native input element inside the component. */
    default Locator getInputLocator() {
        return getLocator().locator("*[slot=\"input\"]").first(); // slot="helper"
    }

    /** Get the current string value. */
    default String getValue() {
        return getLocator().evaluate("el => el.value").toString();
    }

    /**
     * Set the field value by filling the input and dispatching a change event.
     */
    default void setValue(String value) {
        getInputLocator().fill(value);
        getLocator().dispatchEvent("change");

        //RJM: Playwright is supposed to trigger this event with the fill operation. It doesn't (always).
        //Even triggering this even does not guarantee the backend will be informed, allowing it to validate

        if (this instanceof HasValidationPropertiesElement) {
        	//Need to REALLY trigger a change event so a roundtrip to the server can validate
        	//The dispatchEvent(change) does not cause a roundtrip
          getInputLocator().blur();
          assertThat(getInputLocator()).not().isFocused();


//        	Locator bodyLocator = getLocator().page().locator("css=body");
//        	//may work fine. May timeout with pw debug message: <html lang="en" theme="light">…</html> intercepts pointer events
//        	//So attempt with a very short timeout. If it fails, try a tab
//        	//Problem this clicks in the center of the body, which can be intercepted something else.
//        	//If that something else is a ComboBox, it will open and prevent the next operation from succeeding (locator not visible)
//        	//So click the position 0,0 because nothing should be at that location
//        	//Nope. If this is inside a Dialog, clicking the body element closes the Dialog.
//        	try {
//        		bodyLocator.click(new ClickOptions().setTimeout(100).setForce(true).setPosition(0, 0));
//        	} catch (Exception ignore) {
//        		// failed to click the body.
//        		//try to find a random label, since that can be clicked without side-effects.
//          	Locator labelLocator = getLocator().page().locator("css=label");
//          	if (labelLocator.count() > 0) {
//          		labelLocator.first().click();
//          		return;
//          	}
//          	//Settle for tab out, but this runs the risk of side-effects.
//          	//A second problem is if this is the last element in the container. Then tab might do nothing.
//        		bodyLocator.press("Tab");
//        	}
        }

    }

    /** Clear the input value. */
    default void clear() {
        setValue("");
    }

    /** Assert that the input value matches the expected string. */
    default void assertValue(String value) {
        assertThat(getInputLocator()).hasValue(value);
    }
}
