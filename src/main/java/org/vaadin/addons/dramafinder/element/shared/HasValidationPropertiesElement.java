package org.vaadin.addons.dramafinder.element.shared;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import org.jspecify.annotations.Nullable;
import com.microsoft.playwright.Locator;

/**
 * Mixin for components exposing validation state and error messages.
 */
public interface HasValidationPropertiesElement extends HasLocatorElement {

    /** Locator for the error message slot. */
    default Locator getErrorMessageLocator() {
        return getLocator().locator("> [slot=\"error-message\"]").first(); // slot="helper"
    }

    /** Assert that the component is valid (not {@code invalid}). */
    default void assertValid() {
        assertThat(getLocator()).not().hasAttribute("invalid", "");
    }

    /** Assert that the component is invalid. */
    default void assertInvalid() {
        assertThat(getLocator()).hasAttribute("invalid", "");
    }

    /** Assert that the component is valid/invalid via a parameter. */
    default void assertValid(boolean valid) {
	    	if (valid) {
	    		assertValid();
	    	} else {
	    		assertInvalid();
	    	}
    }

    /** Assert that the error message equals the expected text. */
    default void assertErrorMessage(String errorMessage) {
        assertThat(getErrorMessageLocator()).hasText(errorMessage);
    }

    /** Is an error message showing? */
    default boolean hasErrorMessage() {
    		return getErrorMessageLocator().isVisible();
    }

    /** Get the error message that is showing. */
    default @Nullable String getErrorMessage() {
    		return hasErrorMessage() ? getErrorMessageLocator().textContent() : null;
    }

    /** Is the component valid (No error message showing)? */
	  	default boolean isValid() {
	  		return isValid(true);
	  	}

	    /** Is the component valid (as a parameter)? */
	  	default boolean isValid(boolean valid) {
	  		return valid != hasErrorMessage();
	  	}
}
