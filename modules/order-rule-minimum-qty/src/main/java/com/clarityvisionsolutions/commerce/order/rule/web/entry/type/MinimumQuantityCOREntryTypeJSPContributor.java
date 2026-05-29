package com.clarityvisionsolutions.commerce.order.rule.web.entry.type;

import com.clarityvisionsolutions.commerce.order.rule.web.display.context.MinimumQuantityDisplayContext;

import com.liferay.commerce.order.rule.entry.type.COREntryTypeJSPContributor;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = "commerce.order.rule.entry.type.jsp.contributor.key=minimum-quantity-order-rule",
	service = COREntryTypeJSPContributor.class
)
public class MinimumQuantityCOREntryTypeJSPContributor
	implements COREntryTypeJSPContributor {

	@Override
	public void render(
			long corEntryId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		COREntry corEntry = _corEntryLocalService.getCOREntry(corEntryId);

		MinimumQuantityDisplayContext minimumQuantityDisplayContext =
			new MinimumQuantityDisplayContext(corEntry);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, minimumQuantityDisplayContext);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/minimum_quantity.jsp");
	}

	@Reference
	private COREntryLocalService _corEntryLocalService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(target = "(osgi.web.symbolicname=com.clarityvisionsolutions.commerce.order.rule.min)")
	private ServletContext _servletContext;

}