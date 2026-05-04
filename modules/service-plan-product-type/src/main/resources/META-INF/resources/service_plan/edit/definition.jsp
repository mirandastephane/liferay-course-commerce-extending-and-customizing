<%@ include file="/init.jsp" %>

<div class="sheet">
	<div class="panel-group panel-group-flush">
		<aui:fieldset label="service-plan-settings">

			<aui:select label="coverage-duration" name="coverageDuration">
				<aui:option value="1-year">1 Year</aui:option>
				<aui:option value="2-years">2 Years</aui:option>
				<aui:option value="custom">Custom</aui:option>
			</aui:select>

			<%-- TODO: Add Grace Period field here --%>

			<aui:input label="max-number-of-claims" name="maxNumberOfClaims" type="number">
				<aui:validator name="digits" />
				<aui:validator name="min">0</aui:validator>
			</aui:input>

			<%-- TODO: Add Max Claim Value field here --%>

			<aui:select label="replacement-method" name="replacementMethod">
				<aui:option value="replace">Replace</aui:option>
				<aui:option value="repair">Repair</aui:option>
				<aui:option value="store-credit">Store Credit</aui:option>
			</aui:select>

			<aui:input label="underwriter" name="underwriter" type="text" />

			<aui:input label="terms-url" name="termsUrl" type="text" />

		</aui:fieldset>
	</div>
</div>
