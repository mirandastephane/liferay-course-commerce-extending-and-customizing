package com.clarityvisionsolutions.commerce.order.rule.entry.type;

import com.clarityvisionsolutions.commerce.order.rule.entry.util.MinimumQuantityUtil;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.rule.entry.type.COREntryType;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeItem;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"commerce.order.rule.entry.type.key=",
		"commerce.order.rule.entry.type.order:Integer="
	},
	service = COREntryType.class
)
public class MinimumQuantityCOREntryTypeImpl implements COREntryType {

	@Override
	public boolean evaluate(COREntry corEntry, CommerceOrder commerceOrder)
		throws PortalException {

		<%-- TODO: Add business logic for Order Rule here --%>

		return true;
	}

	@Override
	public boolean evaluate(
		COREntry corEntry, List<COREntryTypeItem> corEntryTypeItems) {

		throw new UnsupportedOperationException();
	}

	@Override
	public String getErrorMessage(
			COREntry corEntry, CommerceOrder commerceOrder, Locale locale)
		throws PortalException {

		StringBundler sb = new StringBundler();

		sb.append("Order quantity is less than the minimum quantity ");

		Double minimumQuantity = _getMinimumQuantity(corEntry);

		sb.append(minimumQuantity);

		sb.append(". Add ");

		Double delta = BigDecimalUtil.subtract(
			BigDecimal.valueOf(minimumQuantity),
			_getOrderQuantity(commerceOrder));

		sb.append(delta);

		sb.append(" more item");

		if (delta > 1) {
			sb.append("s");
		}

		sb.append(" to continue.");

		return sb.toString();
	}

	@Override
	public String getKey() {
		return "minimum-quantity-order-rule";
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(
			resourceBundle, "minimum-order-quantity");
	}

	@Override
	public boolean isActive() {
		return true;
	}

	private Double _getMinimumQuantity(COREntry corEntry) {
		return MinimumQuantityUtil.getMinimumQuantity(corEntry);
	}

	private BigDecimal _getOrderQuantity(CommerceOrder commerceOrder) {
		BigDecimal orderQuantity = BigDecimal.ZERO;

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		<%-- TODO: Loop through Order and calculate Total Quantity --%>

		return orderQuantity;
	}

}