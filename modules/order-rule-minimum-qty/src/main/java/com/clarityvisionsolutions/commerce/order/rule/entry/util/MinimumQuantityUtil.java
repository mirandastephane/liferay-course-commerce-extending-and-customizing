package com.clarityvisionsolutions.commerce.order.rule.entry.type.util;

import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

public class MinimumQuantityUtil {

	public static Double getMinimumQuantity(COREntry corEntry) {
		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		return GetterUtil.getDouble(
			typeSettingsUnicodeProperties.getProperty("minimum-quantity"));
	}

}