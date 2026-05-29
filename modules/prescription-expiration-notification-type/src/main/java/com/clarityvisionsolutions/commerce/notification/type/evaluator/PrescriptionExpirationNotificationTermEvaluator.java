package com.clarityvisionsolutions.commerce.notification.type.evaluator;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		property = "class.name=com.liferay.account.model.AccountEntry",
		service = NotificationTermEvaluator.class
)
public class PrescriptionExpirationNotificationTermEvaluator
		implements NotificationTermEvaluator {

	@Override
	@SuppressWarnings("unchecked")
	public String evaluate(
			NotificationTermEvaluator.Context context, Object object,
			String termName)
			throws PortalException {

		Map<String, Object> termValues = (Map<String, Object>)object;

		long accountEntryId = GetterUtil.getLong(termValues.get("id"));
		long companyId = GetterUtil.getLong(termValues.get("companyId"));

		if ("[%CUSTOMER_NAME%]".equals(termName)) {
			List<AccountEntryUserRel> rels =
					_accountEntryUserRelLocalService
							.getAccountEntryUserRelsByAccountEntryId(accountEntryId);

			if (!rels.isEmpty()) {
				User user = _userLocalService.fetchUser(
						rels.get(0).getAccountUserId());

				if (user != null) {
					return user.getFullName();
				}
			}

			return "";
		}

		if ("[%PRESCRIPTION_NUMBER%]".equals(termName)) {

			// prescriptionNumber is a localized text field (String.localized);
			// getData() must receive a Map default to match the column type.

			@SuppressWarnings("unchecked")
			Map<Locale, String> localizedData =
				(Map<Locale, String>)_expandoValueLocalService.getData(
					companyId, AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescriptionNumber", accountEntryId,
					Collections.<Locale, String>emptyMap());

			if (localizedData != null && !localizedData.isEmpty()) {
				String value = localizedData.get(LocaleUtil.getDefault());

				if (value != null) {
					return value;
				}

				return localizedData.values().iterator().next();
			}

			return "";
		}

		if ("[%PRESCRIBING_DOCTOR%]".equals(termName)) {

			// prescribingDoctor is a localized text field (String.localized)

			Map<Locale, String> localizedData =
				(Map<Locale, String>)_expandoValueLocalService.getData(
					companyId, AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescribingDoctor", accountEntryId,
					Collections.<Locale, String>emptyMap());

			if (localizedData != null && !localizedData.isEmpty()) {
				String value = localizedData.get(LocaleUtil.getDefault());

				if (value != null) {
					return value;
				}

				return localizedData.values().iterator().next();
			}

			return "";
		}

		if ("[%PRESCRIPTION_STATUS%]".equals(termName)) {

			// prescriptionStatus is a dropdown field (String.array);
			// getData() must receive a String[] default to match the column type.

			String[] statusArray =
				(String[])_expandoValueLocalService.getData(
					companyId, AccountEntry.class.getName(),
					ExpandoTableConstants.DEFAULT_TABLE_NAME,
					"prescriptionStatus", accountEntryId, new String[0]);

			return (statusArray != null && statusArray.length > 0)
				? statusArray[0]
				: "";
		}

		if ("[%EXPIRATION_DATE%]".equals(termName)) {
			// TODO 3: Read the "expirationDate" Expando value for the
			// AccountEntry and return it as a String.
			return "";
		}

		if ("[%ACCOUNT_EMAIL%]".equals(termName)) {
			List<AccountEntryUserRel> rels =
					_accountEntryUserRelLocalService
							.getAccountEntryUserRelsByAccountEntryId(accountEntryId);

			if (!rels.isEmpty()) {
				User user = _userLocalService.fetchUser(
						rels.get(0).getAccountUserId());

				if (user != null) {
					return user.getEmailAddress();
				}
			}

			return "";
		}

		return "";
	}

	private static final Log _log = LogFactoryUtil.getLog(
			PrescriptionExpirationNotificationTermEvaluator.class);

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
