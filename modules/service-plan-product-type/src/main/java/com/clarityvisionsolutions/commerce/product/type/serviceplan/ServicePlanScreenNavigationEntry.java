package com.clarityvisionsolutions.commerce.product.type.serviceplan;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Renders the "Service Plan" tab content in the product definition
 * editing screen.
 *
 * {@link #isVisible} ensures the tab is only displayed when the
 * product being edited has the type defined by
 * {@link ServicePlanCPType#NAME}, so it never appears on Simple,
 * Virtual, Grouped, or Diagram products.
 *
 * The tab content is rendered by the JSP at
 * /service_plan/edit/definition.jsp inside this bundle's web resources.
 *
 * DO NOT MODIFY: pre-configured boilerplate.
 */
@Component(
	property = {
		"screen.navigation.category.order:Integer=50",
		"screen.navigation.entry.order:Integer=50"
	},
	service = ScreenNavigationEntry.class
)
public class ServicePlanScreenNavigationEntry
	implements ScreenNavigationEntry<CPDefinition> {

	@Override
	public String getCategoryKey() {
		return ServicePlanCPType.NAME;
	}

	@Override
	public String getEntryKey() {
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

	@Override
	public boolean isVisible(User user, CPDefinition cpDefinition) {
		if (cpDefinition == null) {
			return false;
		}

		return ServicePlanCPType.NAME.equals(
			cpDefinition.getProductTypeName());
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			long cpDefinitionId = ParamUtil.getLong(
				httpServletRequest, "cpDefinitionId");

			if (cpDefinitionId > 0) {
				CPDefinition cpDefinition =
					_cpDefinitionLocalService.getCPDefinition(cpDefinitionId);

				httpServletRequest.setAttribute(
					ServicePlanWebKeys.CP_DEFINITION, cpDefinition);
			}
		}
		catch (Exception e) {
		}

		httpServletRequest.setAttribute(
			"expandoValueLocalService", _expandoValueLocalService);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/service_plan/edit/definition.jsp");
	}

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.clarityvisionsolutions.commerce.product.type.serviceplan)"
	)
	private ServletContext _servletContext;

}
