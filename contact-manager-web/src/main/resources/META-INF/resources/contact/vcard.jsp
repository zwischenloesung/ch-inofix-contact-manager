<%-- 
    contact/vcard.jsp: Edit the vCard String of the contact.
    
    Created:    2015-05-08 15:42 by Christian Berndt
    Modified:   2017-07-25 22:27 by Christian Berndt
    Version:    1.0.8
--%>

<%@ include file="/init.jsp"%>

<%
    Contact contact_ = (Contact) request.getAttribute(ContactManagerWebKeys.CONTACT);

    boolean hasUpdatePermission = false;

    if (contact_ == null) {

        contact_ = ContactServiceUtil.createContact();
        hasUpdatePermission = true;

    } else {

        hasUpdatePermission = ContactPermission.contains(permissionChecker, contact_, ActionKeys.UPDATE);
    }

    String namespace = liferayPortletResponse.getNamespace();
%>

<aui:row>
    <aui:fieldset cssClass="col-md-12" helpMessage="v-card-help" label="v-card" markupView="<%= markupView %>">

		<aui:input name="vCard" type="textarea" cssClass="v-card"
			value="<%=contact_.getCard()%>" disabled="true" label="" />

        <%-- 
		<portlet:resourceURL var="serveVCardURL" id="serveVCard">
			<portlet:param name="contactId"
				value="<%= String.valueOf(contact_.getContactId()) %>" />
		</portlet:resourceURL>

		<aui:button href="<%=serveVCardURL%>" value="download" />
        --%>
        
    </aui:fieldset>
</aui:row>
