package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.PromotionRunDialog
import net.nemerosa.ontrack.acceptance.browser.dialogs.ValidationRunDialog

class BuildPage extends AbstractHeaderPage {

    BuildPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/build/${parameters.id}"
    }

    ValidationRunDialog validate() {
        def validateCommand = $('#validate')
        browser.waitUntil { validateCommand.displayed }
        validateCommand.click()
        ValidationRunDialog dialog = new ValidationRunDialog(browser).waitFor()
        return dialog
    }

    PromotionRunDialog promote() {
        def promoteCommand = $('#promote')
        browser.waitUntil { promoteCommand.displayed }
        promoteCommand.click()
        PromotionRunDialog dialog = new PromotionRunDialog(browser).waitFor()
        return dialog
    }
}
