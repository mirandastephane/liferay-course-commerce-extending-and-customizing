package com.clarityvisionsolutions.commerce.product.type.serviceplan;

import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Adds the "Service Plan" tab category to the product definition
 * editing screen in the Commerce Catalog portlet.
 *
 * The tab is only shown for products whose type matches
 * {@link ServicePlanCPType#NAME} — that filtering is handled in
 * {@link ServicePlanScreenNavigationEntry#isVisible}.
 *
 * DO NOT MODIFY: pre-configured boilerplate.
 */
@Component(
	property = "screen.navigation.category.order:Integer=50",
	service = ScreenNavigationCategory.class
)
public class ServicePlanScreenNavigationCategory
	implements ScreenNavigationCategory {

	@Override
	public String getCategoryKey() {
		return ServicePlanCPType.NAME;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, ServicePlanCPType.NAME);
	}

	@Override
	public String getScreenNavigationKey() {
		return CPDefinitionScreenNavigationConstants.SCREEN_NAVIGATION_KEY_CP_DEFINITION_GENERAL;
	}

	@Reference
	private Language _language;

}
