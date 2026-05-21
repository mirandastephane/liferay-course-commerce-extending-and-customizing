package com.clarityvisionsolutions.commerce.notification.type.type;

import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.type.BaseNotificationType;
import com.liferay.notification.type.NotificationType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.internet.InternetAddress;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/*
 * SYNTAX REFERENCE
 *
 * Pattern 1 — Returning the notification type key:
 *
 *   @Override
 *   public String getType() {
 *       return TYPE_KEY;
 *   }
 *
 * Pattern 2 — Resolving a recipient from an account ID:
 *
 *   long accountEntryId = GetterUtil.getLong(setting.getValue());
 *
 *   List<AccountEntryUserRel> rels =
 *       _accountEntryUserRelLocalService
 *           .getAccountEntryUserRelsByAccountEntryId(accountEntryId);
 *
 *   if (!rels.isEmpty()) {
 *       User user = userLocalService.fetchUser(
 *           rels.get(0).getAccountUserId());
 *
 *       if (user != null) {
 *           return new Object[] {user.getEmailAddress()};
 *       }
 *   }
 *
 *   return new Object[0];
 */

/**
 * Custom notification type that delivers prescription expiration alerts
 * via email when an account's prescriptionStatus Custom Field transitions
 * to "Expiring Soon".
 *
 * Registration: property "notification.type.key" identifies this type in the
 * Notification Templates UI and routes notifications dispatched with
 * TYPE_KEY to this handler.
 *
 * DO NOT MODIFY sendNotification() or evaluateNotificationRecipientSettings()
 * — those are pre-configured infrastructure. Your exercise tasks are:
 *   TODO 1 — implement getType()
 *   TODO 2 — implement toRecipients()
 */
@Component(
	property = "notification.type.key=" + PrescriptionExpirationNotificationType.TYPE_KEY,
	service = NotificationType.class
)
public class PrescriptionExpirationNotificationType
	extends BaseNotificationType {

	public static final String TYPE_KEY = "prescription-expiration";

	/**
	 * Returns the unique key that identifies this notification type.
	 * Used by the Notification Templates UI and by the framework to route
	 * notifications to the correct handler.
	 *
	 * TODO 1: Return the unique key for this notification type.
	 * Hint: return the TYPE_KEY constant defined above.
	 */
	@Override
	public String getType() {

		// TODO 1: Return the unique key for this notification type

		return "";
	}

	@Override
	public String getTypeLanguageKey() {
		return TYPE_KEY;
	}

	/**
	 * Maps the "accountId" recipient setting from the notification context's
	 * term values. The value under key "id" in termValues is the
	 * AccountEntry's primary key, stored as a String.
	 *
	 * This map is consumed by createNotificationRecipientSettings() to build
	 * the in-memory NotificationRecipientSetting list, which is later passed
	 * to toRecipients() for email resolution.
	 *
	 * PRE-CONFIGURED — do not modify.
	 */
	@Override
	public Map<String, String> evaluateNotificationRecipientSettings(
			long companyId, NotificationContext notificationContext,
			Map<String, Object> termValues)
		throws PortalException {

		return Collections.singletonMap(
			"accountId",
			String.valueOf(GetterUtil.getLong(termValues.get("id"))));
	}

	/**
	 * Declares "accountId" as the only allowed recipient setting name.
	 * Used by validateNotificationTemplate() to guard against unknown
	 * settings being stored on a template.
	 *
	 * PRE-CONFIGURED — do not modify.
	 */
	@Override
	public Set<String> getAllowedNotificationRecipientSettingsNames() {
		return Collections.singleton("accountId");
	}

	/**
	 * Resolves the email address(es) to which the notification should be sent.
	 *
	 * The settings list contains one entry with name="accountId" and
	 * value=<accountEntryId> (a String). Use that ID to look up the account's
	 * primary user via AccountEntryUserRelLocalService, then fetch the User
	 * from UserLocalService and return their email address.
	 *
	 * TODO 2: Resolve the recipient's email from the account ID.
	 *   1. Iterate notificationRecipientSettings to find the setting whose
	 *      name equals "accountId".
	 *   2. Parse the accountEntryId with GetterUtil.getLong(setting.getValue()).
	 *   3. Call _accountEntryUserRelLocalService
	 *          .getAccountEntryUserRelsByAccountEntryId(accountEntryId)
	 *      to obtain the list of account-user relations.
	 *   4. If the list is non-empty, fetch the first user with
	 *      userLocalService.fetchUser(rels.get(0).getAccountUserId()).
	 *   5. If the user is non-null, return new Object[] {user.getEmailAddress()}.
	 *   6. Otherwise return new Object[0].
	 */
	@Override
	public Object[] toRecipients(
		List<NotificationRecipientSetting> notificationRecipientSettings) {

		for (NotificationRecipientSetting setting :
				notificationRecipientSettings) {

			if ("accountId".equals(setting.getName())) {

				// TODO 2: Resolve the recipient's email from the account ID
				// Use _accountEntryUserRelLocalService to get the account's users,
				// then use userLocalService to fetch the first user,
				// and return an array containing the user's email address.

				return new Object[0];
			}
		}

		return new Object[0];
	}

	/**
	 * Orchestrates the full email delivery pipeline for one notification.
	 *
	 * Flow:
	 *  1. Set siteDefaultLocale (required before formatLocalizedContent).
	 *  2. Stamp TYPE_KEY on the context so NotificationTermEvaluatorTracker
	 *     routes term evaluation to our evaluator via class.name lookup.
	 *  3. Format body/subject — evaluates all [%TERM%] variables.
	 *  4. Build the "accountId" recipient settings map.
	 *  5. Create in-memory NotificationRecipientSetting list.
	 *  6. Wire the queue entry, recipient, and settings onto the context
	 *     via prepareNotificationContext.
	 *  7. Resolve email address(es) via toRecipients().
	 *  8. Send one MailMessage per recipient via MailService.
	 *
	 * PRE-CONFIGURED — do not modify.
	 */
	@Override
	public void sendNotification(NotificationContext notificationContext)
		throws PortalException {

		siteDefaultLocale = portal.getSiteDefaultLocale(0);

		notificationContext.setType(TYPE_KEY);

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		Map<String, Object> termValues = notificationContext.getTermValues();

		String body = formatLocalizedContent(
			notificationTemplate.getBodyMap(), notificationContext);

		String subject = formatLocalizedContent(
			notificationTemplate.getSubjectMap(), notificationContext);

		Map<String, String> recipientSettingsMap =
			evaluateNotificationRecipientSettings(
				notificationContext.getCompanyId(), notificationContext,
				termValues);

		User user = userLocalService.fetchUser(notificationContext.getUserId());

		if (user == null) {
			_log.warn(
				"PrescriptionExpirationNotificationType: user not found for " +
					"userId=" + notificationContext.getUserId() +
					", skipping notification");

			return;
		}

		List<NotificationRecipientSetting> internalSettings =
			createNotificationRecipientSettings(user, 0, recipientSettingsMap);

		prepareNotificationContext(
			user, body, notificationContext, internalSettings, subject);

		Object[] recipients = toRecipients(
			notificationContext.getNotificationRecipientSettings());

		for (Object recipient : recipients) {
			String emailAddress = String.valueOf(recipient);

			if (!Validator.isNotNull(emailAddress)) {
				continue;
			}

			try {
				MailMessage mailMessage = new MailMessage();

				mailMessage.setFrom(
					new InternetAddress(
						"noreply@clarityvisionsolutions.com",
						"Clarity Vision Solutions"));

				mailMessage.setTo(
					new InternetAddress[] {new InternetAddress(emailAddress)});

				mailMessage.setSubject(subject);
				mailMessage.setBody(body);
				mailMessage.setHTMLFormat(true);

				_mailService.sendEmail(mailMessage);

				if (_log.isInfoEnabled()) {
					_log.info(
						"PrescriptionExpirationNotificationType: notification " +
							"sent to " + emailAddress);
				}
			}
			catch (Exception exception) {
				_log.error(
					"PrescriptionExpirationNotificationType: failed to send " +
						"email to " + emailAddress,
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PrescriptionExpirationNotificationType.class);

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private MailService _mailService;

}
