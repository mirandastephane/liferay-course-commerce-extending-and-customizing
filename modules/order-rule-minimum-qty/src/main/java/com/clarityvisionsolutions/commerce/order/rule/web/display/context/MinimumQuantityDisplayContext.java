package com.clarityvisionsolutions.commerce.order.rule.web.display.context;

import com.clarityvisionsolutions.commerce.order.rule.entry.type.util.MinimumQuantityUtil;

import com.liferay.commerce.order.rule.model.COREntry;

public class MinimumQuantityDisplayContext {

	public MinimumQuantityDisplayContext(COREntry corEntry) {
		_corEntry = corEntry;
	}

	public Double getMinimumQuantity() {
		return MinimumQuantityUtil.getMinimumQuantity(_corEntry);
	}

	private final COREntry _corEntry;

}