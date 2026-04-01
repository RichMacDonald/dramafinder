package org.vaadin.addons.dramafinder.element.shared;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;

/**
 * Mixin for components that have an enabled/disabled state,
 * but do not explicitly support that property according to the spec.
 * https://github.com/microsoft/playwright-java/issues/1837
 * Instead, we can use the aria-disabled attribute
 */

public interface HasEnabledCustomElement  extends HasLocatorElement {

  /** Locator used to check enablement. Defaults to root. */
  default Locator getEnabledLocator() {
      return getLocator();
  }

  /** Whether the component is enabled. */
  default boolean isEnabled() {
      return isEnabled(true);
  }

  /** Assert that the component is enabled. */
  default void assertEnabled() {
 		assertEnabled(true);
  }

  default boolean isEnabled(boolean isEnabled) {
		String attrValue = getEnabledLocator().getAttribute("aria-disabled");
		if (isEnabled) {
			return attrValue == null || "false".equals(attrValue);
		} else {
			return "true".equals( attrValue);
		}
	}


  /** Assert that the component is enabled, as an argument. */
  default void assertEnabled(boolean isEnabled) {
  	LocatorAssertions assertedLocation = assertThat(getEnabledLocator());
//		String attrValue = getEnabledLocator().getAttribute("aria-disabled");
		if (isEnabled) {
			//aria-disabled can either be null or it can be false. The LocatorAssertions does not support an OR operation.
			assertedLocation.not().hasAttribute("aria-disabled", "false");
//			assertThat(attrValue == null || attrValue.equals("false")).as("enabled").isTrue();
		} else {
			assertedLocation.isDisabled();
			assertedLocation.hasAttribute("aria-disabled", "true");
//			assertThat(attrValue).as("null aria-disabled").isNotNull();
//			assertThat(attrValue).as("disabled").isEqualTo("true");
		}
	}

  /** Assert that the component is disabled. */
  default void assertDisabled() {
 		assertEnabled(false);
  }
}
