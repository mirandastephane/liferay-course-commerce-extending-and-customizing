package com.clarityvisionsolutions.commerce.discount.rule;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.rule.type.CommerceDiscountRuleType;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"commerce.discount.rule.type.key=",
		"commerce.discount.rule.type.order:Integer="
	},
	service = CommerceDiscountRuleType.class
)
public class MinimumItemDiscountRuleTypeImpl
	implements CommerceDiscountRuleType {

	@Override
	public boolean evaluate(
			CommerceDiscountRule commerceDiscountRule,
			CommerceContext commerceContext)
		throws PortalException {

		CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

		if (commerceOrder == null) {
			return false;
		}

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		<%-- TODO: Add business logic for Discount Rule here --%>

		if (commerceOrderItems.size() >= mininumNumberOfItems) {
			return true;
		}

		return false;
	}

	@Override
	public String getKey() {
		return "minimum-nbr-items";
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		<%-- TODO: Add Resource Key for "has-a-minimum-number-of-items" here --%> 
		/*return LanguageUtil.get(
			resourceBundle, "resource-key");
		*/
	}

	@Override
	public boolean validate(String typeSettings) {
		return true;
	}

}