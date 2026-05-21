package com.clarityvisionsolutions.commerce.discount.rule;

import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleTypeJSPContributor;
import com.liferay.commerce.discount.service.CommerceDiscountRuleService;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.bean.BeanParamUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = "commerce.discount.rule.type.jsp.contributor.key=minimum-nbr-items",
	service = CommerceDiscountRuleTypeJSPContributor.class
)
public class MinimumItemDiscountRuleTypeJSPContributor
	implements CommerceDiscountRuleTypeJSPContributor {

	@Override
	public void render(
			long commerceDiscountId, long commerceDiscountRuleId,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {	
		CommerceDiscountRule _commerceDiscountRule =
				_commerceDiscountRuleService.getCommerceDiscountRule(
					commerceDiscountRuleId);
	
		String type = BeanParamUtil.getString(
			_commerceDiscountRule, httpServletRequest, "type");
		String typeSettings = _commerceDiscountRule.getSettingsProperty(type);

		httpServletRequest.setAttribute(
			"view.jsp-minimumItemsDiscountRule",
			typeSettings);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/view.jsp");
	}
	
	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private CommerceDiscountRuleService _commerceDiscountRuleService;

	@Reference(target = "(osgi.web.symbolicname=com.clarityvisionsolutions.commerce.discount.bulk)")

	private ServletContext _servletContext;
}