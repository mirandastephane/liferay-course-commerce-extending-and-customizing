package com.clarityvisionsolutions.commerce.product.renderer.serviceplan;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.content.render.CPContentRenderer;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"commerce.product.content.renderer.key=" + ServicePlanCPContentRenderer.KEY,
		"commerce.product.content.renderer.order=10",
		"commerce.product.content.renderer.type=service-plan"
	},
	service = CPContentRenderer.class
)
public class ServicePlanCPContentRenderer implements CPContentRenderer {

	public static final String KEY = "service-plan";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(resourceBundle, KEY);
	}

	@Override
	public void render(
			CPCatalogEntry cpCatalogEntry,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpCatalogEntry.getCPDefinitionId());

		httpServletRequest.setAttribute(
			ServicePlanWebKeys.CP_DEFINITION, cpDefinition);

		httpServletRequest.setAttribute(
			"expandoValueLocalService", _expandoValueLocalService);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/service_plan/render/view.jsp");
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
		target = "(osgi.web.symbolicname=com.clarityvisionsolutions.commerce.product.renderer.serviceplan)"
	)
	private ServletContext _servletContext;

}
