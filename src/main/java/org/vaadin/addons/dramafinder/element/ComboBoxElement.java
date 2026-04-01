package org.vaadin.addons.dramafinder.element;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.vaadin.addons.dramafinder.element.shared.FocusableElement;
import org.vaadin.addons.dramafinder.element.shared.HasAllowedCharPatternElement;
import org.vaadin.addons.dramafinder.element.shared.HasAriaLabelElement;
import org.vaadin.addons.dramafinder.element.shared.HasClearButtonElement;
import org.vaadin.addons.dramafinder.element.shared.HasEnabledElement;
import org.vaadin.addons.dramafinder.element.shared.HasInputFieldElement;
import org.vaadin.addons.dramafinder.element.shared.HasPlaceholderElement;
import org.vaadin.addons.dramafinder.element.shared.HasPrefixElement;
import org.vaadin.addons.dramafinder.element.shared.HasThemeElement;
import org.vaadin.addons.dramafinder.element.shared.HasTooltipElement;
import org.vaadin.addons.dramafinder.element.shared.HasValidationPropertiesElement;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.LocatorAssertions.HasCountOptions;
import com.microsoft.playwright.options.AriaRole;

/**
 * PlaywrightElement for {@code <vaadin-combo-box>}.
 * <p>
 * Provides helpers to open the overlay, filter items, and pick items by
 * visible text, along with aria/placeholder/validation mixins.
 */
@PlaywrightElement(ComboBoxElement.FIELD_TAG_NAME)
public class ComboBoxElement extends VaadinElement
        implements FocusableElement, HasAriaLabelElement, HasInputFieldElement,
        HasPrefixElement, HasThemeElement, HasPlaceholderElement,
        HasEnabledElement, HasTooltipElement, HasValidationPropertiesElement,
        HasClearButtonElement, HasAllowedCharPatternElement {

    public static final String FIELD_TAG_NAME = "vaadin-combo-box";
    public static final String FIELD_ITEM_TAG_NAME = "vaadin-combo-box-item";

    /**
     * Create a new {@code ComboBoxElement}.
     *
     * @param locator the locator for the {@code <vaadin-combo-box>} element
     */
    public ComboBoxElement(Locator locator) {
        super(locator);
    }

    @Override
    public Locator getFocusLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getAriaLabelLocator() {
        return getInputLocator();
    }

    @Override
    public Locator getEnabledLocator() {
        return getInputLocator();
    }

    /**
     * Get the selected value label.
     * <p>
     * ComboBox stores an index in its {@code value} property, so this reads
     * the displayed text from the input element instead.
     *
     * @return the displayed value or empty string when nothing is selected
     */
    @Override
    public String getValue() {
        return getInputLocator().inputValue();
    }

    /**
     * Assert that the displayed value equals the expected string.
     *
     * @param expected expected label or empty string for no selection
     */
    @Override
    public void assertValue(String expected) {
        assertThat(getInputLocator()).hasValue(expected != null ? expected : "");
    }

    /**
     * Select an item by its visible label.
     * Opens the overlay, clicks the matching item.
     * RJM: Ought to be named toggleItem()
     *
     * @param item label of the item to select
     */
    public void selectItem(String item) {
        open();
        getOverlayItem(item).click();
        close(); //belt and suspenders. sometimes may stay open, which prevents playwright from working on the next selector.
    }

    /**
     * Type filter text into the input, then click the matching item.
     *
     * @param filter text to type for filtering
     * @param item   label of the item to select
     */
    public void filterAndSelectItem(String filter, String item) {
        setFilter(filter);
        getOverlayItem(item).click(); //if there are multiple matches, it picks the first one.
        close(); //belt and suspenders. sometimes may stay open, which prevents playwright from working on the next selector.
    }

    /**
     * Type filter text into the input, then click the matching item.
     * The text is an exact match
     *
     * @param filter text to type for filtering
     * @param item   label of the item to select
     */
    public void filterAndSelectExactItem(String filter, String item) {
        setFilter(filter);
        //start and end matches the entire string. Escape the string in case it contains regex characters
        String escapedRegex = Pattern.quote(item);
  				Pattern exactMatch = Pattern.compile("^\\s*" + escapedRegex + "\\s*$");
  				getOverlayItemPainful(filter, exactMatch).click();
        close(); //belt and suspenders. sometimes may stay open, which prevents playwright from working on the next selector.
    }

    /**
     * Type into the input to trigger filtering.
     * Uses {@code pressSequentially} to fire keyboard events that the
     * ComboBox listens to for filtering.
     *
     * @param filter the filter text
     */
    public void setFilter(String filter) {
    			clear(); //RJM: Otherwise, this just appends to whatever is already selected
        open();
        getInputLocator().pressSequentially(filter);
    }

    /**
     * Get the current filter value from the DOM property.
     *
     * @return the current filter string
     */
    public String getFilter() {
        Object value = getProperty("filter");
        return value == null ? "" : value.toString();
    }

    /**
     * Open the combo box overlay.
     */
    public void open() {
        setProperty("opened", true);
    }

    /**
     * Close the combo box overlay.
     */
    public void close() {
        setProperty("opened", false);
    }

    /**
     * Whether the overlay is currently open.
     *
     * @return {@code true} when the overlay is open
     */
    public boolean isOpened() {
        return Boolean.TRUE.equals(getProperty("opened"));
    }

    /**
     * Assert that the combo box overlay is open.
     */
    public void assertOpened() {
        assertThat(getLocator()).hasAttribute("opened", "");
    }

    /**
     * Assert that the combo box overlay is closed.
     */
    public void assertClosed() {
        assertThat(getLocator()).not().hasAttribute("opened", "");
    }

    /**
     * Whether the combo box is read-only.
     *
     * @return {@code true} when read-only
     */
    public boolean isReadOnly() {
        return getLocator().getAttribute("readonly") != null;
    }

    /**
     * Assert that the combo box is read-only.
     */
    public void assertReadOnly() {
        assertThat(getLocator()).hasAttribute("readonly", "");
    }

    /**
     * Assert that the combo box is not read-only.
     */
    public void assertNotReadOnly() {
        assertThat(getLocator()).not().hasAttribute("readonly", "");
    }

    /**
     * Locator for the toggle button part.
     *
     * @return locator for the toggle button
     */
    public Locator getToggleButtonLocator() {
        return getLocator().locator("[part~=\"toggle-button\"]");
    }

    /**
     * Click the dropdown toggle button.
     */
    public void clickToggleButton() {
        getToggleButtonLocator().click();
    }

    /**
     * Count visible overlay items. (Should be renamed to getVisibleOverlayItemCount)
     *
     * @return the number of visible items
     */
    public int getOverlayItemCount() {
        return getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])").count();
    }

    /**
     * Count overlay items.
     * (Must be sure to have called open() so that the list is lazily downloaded.)
     *
     * @return the number of visible items
     */
    public int getAllOverlayItemCount() {
    			ensureOpenedOnce();
        return getLocator().locator(FIELD_ITEM_TAG_NAME).count(); //Use the Locator, not the page, otherwise you get all the other ComboBox values on the Page.
    }

    /**
     * Assert that the overlay contains exactly the expected number of items.
     *
     * @param expected expected item count
     */
    public void assertItemCount(int expected) {
        assertThat(getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")).hasCount(expected);
    }

    /**
     * Get the {@code ComboBoxElement} by its label.
     *
     * @param page  the Playwright page
     * @param label the accessible label of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Page page, String label) {
        return new ComboBoxElement(
                page.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions()
                                .setHas(page.getByRole(AriaRole.COMBOBOX,
                                        new Page.GetByRoleOptions().setName(label)))
                        ).first());
    }

    /**
     * Get the {@code ComboBoxElement} by its label within a given scope.
     *
     * @param locator the locator to search within
     * @param label   the accessible label of the field
     * @return the matching {@code ComboBoxElement}
     */
    public static ComboBoxElement getByLabel(Locator locator, String label) {
        return new ComboBoxElement(
                locator.locator(FIELD_TAG_NAME)
                        .filter(new Locator.FilterOptions().setHas(locator.getByLabel(label)))
                        .first());
    }

    //Caution: This uses inexact matching and selects the first.
    private Locator getOverlayItem(String label) {
        return getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")
                .filter(new Locator.FilterOptions()
                        .setHasText(label)).first();
    }

    //Force an exact match. Wait in case it requires a lazy evaluation
    private Locator getOverlayItemExactWait(Pattern exactMatch) {
      Locator loc = getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")
          .filter(new Locator.FilterOptions()
                  .setHasText(exactMatch));
      assertThat(loc).hasCount(1, new HasCountOptions().setTimeout(5000)); //waits for lazy download to settle. doesn't work. stays at count=0
      return loc.first();
  }

    //because you can't just expect it to be easy and for the regex to work
    //Note that the all() method does not wait, so you must have filtered before getting here.
    //I think the regex is fine, but you have to check in a loop until the lazy eval is completed.
    //The assert-locator-hasCount does not work and I don't know why. You can inspect the DOM and see that it should work, but it returns zero.
    private Locator getOverlayItemPainful(String label, Pattern exactMatch) {

      //getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")
      //    .filter(new Locator.FilterOptions()
      //            .setHasText(exactMatch)).count();

       Locator locAll = getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])")
                .filter(new Locator.FilterOptions()
                        .setHasText(label));

       //junk loop
       int iter = 0;
        while (iter++ < 100) {
        	int count = locAll.count();
        	if (count == 1) {

        	  //debug check. this fails. Either a bug with my regex or a bug in PW. And the regex is correct both in java and elsewhere
        		//Example: Searching for the text "foo". The regex is ^\s*\Qfoo\E\s*$
        		//getOverlayItemExactWait(exactMatch);

        		return locAll.first();

        	} else if (count == 0) {
        		try {
        			Thread.sleep(100);
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}

        	} else {
        		Predicate<String> pred = exactMatch.asMatchPredicate();
        		for (Locator loc : locAll.all()) {
        			if (pred.test(loc.textContent())) {
//            		getOverlayItemExactWait(exactMatch); //fails here as well
        				return loc;
        			}
        		}
        		assert(false);
        	}
        	}
        return null;
    }

    //The widget may only download a subset of values. If this needs to be tested, setPageSize(veryLarge)
    //It also needs to be opened once to download the values.
    //Nope: Even after all that, it displays a scrollable list rather than a non-scrollable list,
    //but it still only shows a subset in the DOM (about 26)
    //Note that this ignore the filter value. If you want the filtered list, call getVisibleValues()
    @Deprecated //cannot be relied on
    public List<String> getAllowableValues() {
	    	boolean currentlyClosed = !isOpened();
	    	if (currentlyClosed) {
	    		open();
	    	}
	    	List<String> answer = getLocator().locator(FIELD_ITEM_TAG_NAME).all()
	    			.stream()
	    			.map(Locator::textContent)
	    			.toList();
	    	if (currentlyClosed) {
	    		close();
	    	}
	    	return answer;
    }

    /**
     * Not visible as in "currently isOpened", but the list of items that will show when opened.
     * This is reliable after setting a filter, but (could be) incomplete is the list is large
     * and has not been fully downloaded.
     */
    public List<String> getVisibleValues() {
	    	boolean currentlyClosed = !isOpened();
	    	if (currentlyClosed) {
	    		open();
	    	}
	    	List<String> answer = getLocator().locator(FIELD_ITEM_TAG_NAME + ":not([hidden])").all()
	    			.stream()
	    			.map(Locator::textContent)
	    			.toList();
	    	if (currentlyClosed) {
	    		close();
	    	}
	    	return answer;
	  }

    /**
     * Does the list contain this item?
     * Need to open and filter to ensure it gets downloaded from a large lazy list
     *
     * @param item label of the item to select
     */
    public boolean containsItem(String item) {
        setFilter(item);
        return getAllowableValues().contains(item);
    }

    private void ensureOpenedOnce() {
    		if (!isOpened()) {
    			open();
    			close();
    		}
    }
}
