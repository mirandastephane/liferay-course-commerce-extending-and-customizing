class ServicePlanGuaranteeBadge extends HTMLElement {
	async connectedCallback() {
		let badgeTitle = 'Artisan Protection Plan';

		try {
			if (document.readyState !== 'complete') {
				await new Promise(resolve =>
					window.addEventListener('load', resolve, {once: true})
				);
			}

			const context = this._getCommerceContext();

			if (context) {
				const {channelId, accountId, productId} = context;

				const response = await this._fetchProductData(
					channelId, accountId, productId
				);

				if (response.ok) {
					const data = await response.json();

					this._render(data, badgeTitle);

					return;
				}
			}
		}
		catch (e) {
			console.error('ServicePlanGuaranteeBadge: failed to load product data', e);
		}

		this._render(null, badgeTitle);
	}

	_render(data, badgeTitle) {
		if (data) {
			const customFields = data?.customFields || [];
			const coverageField = customFields.find(
				f => f.name === 'servicePlan_coverageDuration'
			);
			const coverageDuration = coverageField?.customValue?.data;

			if (coverageDuration) {
				const durationLabels = {
					'12 months': '12-Month Protection Plan',
					'24 months': '24-Month Protection Plan',
					'Custom': 'Custom Protection Plan',
				};
				badgeTitle = durationLabels[coverageDuration] || coverageDuration;
			}
		}

		this.innerHTML = `
			<div class="guarantee-badge">
				<div class="guarantee-badge__icon">
					<svg
						fill="none"
						height="40"
						viewBox="0 0 40 40"
						width="40"
						xmlns="http://www.w3.org/2000/svg"
					>
						<path
							d="M20 3L5 9v11c0 9.39 6.39 18.18 15 20.33C29.61 38.18 36 29.39 36 20V9L20 3z"
							fill="#1D9E75"
						/>
						<path
							d="M16.5 26.5l-5-5 1.41-1.41L16.5 23.67l10.59-10.58L28.5 14.5l-12 12z"
							fill="white"
						/>
					</svg>
				</div>

				<div class="guarantee-badge__content">
					<p class="guarantee-badge__title">${badgeTitle}</p>
					<p class="guarantee-badge__subtitle">Powered by Clarity Protection Group</p>
				</div>
			</div>
		`;
	}

	_getCommerceContext() {
		const components = window.Liferay?._components || {};

		for (const component of Object.values(components)) {
			if (component?._commerceContext) {
				const ctx = component._commerceContext;
				const channelId = ctx?.channelId ?? ctx?.channel?.id;
				const accountId = ctx?.accountId ?? ctx?.account?.id;
				const productId = ctx?.productId;

				if (channelId && productId) {
					return {
						channelId: String(channelId),
						accountId: accountId ? String(accountId) : null,
						productId: String(productId),
					};
				}
			}
		}

		return null;
	}

	_fetchProductData(channelId, accountId, productId) {
		const params = new URLSearchParams({nestedFields: 'customFields'});

		if (accountId) {
			params.set('accountId', accountId);
		}

		return fetch(
			`/o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products/${productId}?${params}`,
			{credentials: 'include'}
		);
	}
}

customElements.define('service-plan-guarantee-badge', ServicePlanGuaranteeBadge);
