package com.clarityvisionsolutions.commerce.product.type.serviceplan;

import com.liferay.commerce.product.type.CPType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Registers the Service Plan product type with Liferay Commerce.
 *
 * When this bundle is deployed, the product type defined by NAME
 * appears in the product type dropdown in the Commerce Catalog.
 * Products created with this type gain the dedicated Service Plan
 * tab in the product editing UI (contributed by
 * {@link ServicePlanScreenNavigationEntry}).
 *
 * DO NOT MODIFY: OSGi wiring and interface implementation are
 * pre-configured. Your exercise section is marked below.
 */
@Component(
	property = {
		"commerce.product.type.display.order:Integer=100",
		"commerce.product.type.name=" + ServicePlanCPType.NAME
	},
	service = CPType.class
)
public class ServicePlanCPType implements CPType {

	public static final String NAME = "";
	private static final String LABEL_KEY = "";

	@Override
	public void deleteCPDefinition(long cpDefinitionId) throws PortalException {

		// No custom data to clean up for this product type.
		// A future implementation that stores service-plan attributes
		// (e.g. via expando columns or a service-builder table) would
		// delete that data here.

	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, LABEL_KEY);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Reference
	private Language _language;

}
