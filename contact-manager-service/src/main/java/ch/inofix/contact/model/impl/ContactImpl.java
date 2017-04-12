/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package ch.inofix.contact.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.contact.dto.CategoriesDTO;
import ch.inofix.contact.dto.EmailDTO;
import ch.inofix.contact.dto.ExpertiseDTO;
import ch.inofix.contact.dto.HobbyDTO;
import ch.inofix.contact.dto.ImppDTO;
import ch.inofix.contact.dto.InterestDTO;
import ch.inofix.contact.dto.LanguageDTO;
import ch.inofix.contact.dto.PhoneDTO;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.ExpertiseLevel;
import ezvcard.parameter.HobbyLevel;
import ezvcard.parameter.ImppType;
import ezvcard.parameter.InterestLevel;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Categories;
import ezvcard.property.Email;
import ezvcard.property.Expertise;
import ezvcard.property.FormattedName;
import ezvcard.property.Gender;
import ezvcard.property.Hobby;
import ezvcard.property.Impp;
import ezvcard.property.Interest;
import ezvcard.property.Kind;
import ezvcard.property.Language;
import ezvcard.property.Organization;
import ezvcard.property.RawProperty;
import ezvcard.property.StructuredName;
import ezvcard.property.Telephone;
import ezvcard.property.Timezone;

/**
 * The extended model implementation for the Contact service. Represents a row
 * in the &quot;Inofix_Contact&quot; database table, with each column mapped to
 * a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class.
 * Whenever methods are added, rerun ServiceBuilder to copy their definitions
 * into the {@link ch.inofix.contact.model.Contact} interface.
 * </p>
 *
 * @author Christian Berndt
 * @author Stefan Luebbers
 * @created 2015-05-07 22:17
 * @modified 2017-04-12 17:27
 * @version 1.2.1
 */
@ProviderType
public class ContactImpl extends ContactBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. All methods that expect a contact
     * model instance should use the {@link ch.inofix.contact.model.Contact}
     * interface instead.
     */
    public ContactImpl() {
    }

    @Override
    public String getCompany() {

        String str = "";

        List<Organization> organizations = getVCard().getOrganizations();

        if (organizations.size() > 0) {
            List<String> values = organizations.get(0).getValues();
            if (values.size() > 0) {
                str = values.get(0);
            }
        }

        return str;

    }

    /**
     *
     * @return
     * @since 1.1.0
     */
    @Override
    public List<CategoriesDTO> getCategoriesList() {

        List<CategoriesDTO> categoriesDTOs = new ArrayList<CategoriesDTO>();

        List<Categories> categoriesList = getVCard().getCategoriesList();

        for (Categories categories : categoriesList) {

            CategoriesDTO categoriesDTO = new CategoriesDTO();

            StringBuilder sb = new StringBuilder();
            List<String> values = categories.getValues();
            Iterator<String> iterator = values.iterator();

            while (iterator.hasNext()) {
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }

            categoriesDTO.setValue(sb.toString());
            categoriesDTO.setType(categories.getType());

            categoriesDTOs.add(categoriesDTO);
        }

        // an empty default categories
        if (categoriesDTOs.size() == 0) {
            categoriesDTOs.add(new CategoriesDTO());
        }

        return categoriesDTOs;

    }

    public EmailDTO getEmail() {

        List<Email> emails = getVCard().getEmails();

        if (emails != null) {

            for (Email email : emails) {
                Integer pref = email.getPref();
                if (pref != null) {
                    if (pref == 1) {
                        return getEmail(email);
                    }
                }
            }
        }

        Email email = getVCard().getProperty(Email.class);

        return getEmail(email);

    }

    private EmailDTO getEmail(Email email) {

        EmailDTO emailDTO = new EmailDTO();

        if (email != null) {
            emailDTO.setAddress(email.getValue());

            emailDTO.setAddress(email.getValue());

            // TODO: Add multi-type support
            StringBuilder sb = new StringBuilder();
            Set<EmailType> types = SetUtil.fromList(email.getTypes());
            if (types.size() > 0) {
                for (EmailType type : types) {
                    sb.append(type.getValue());
                }
            } else {
                sb.append("other");
            }

            emailDTO.setType(sb.toString());
        }

        return emailDTO;
    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public List<EmailDTO> getEmails() {

        List<EmailDTO> emailDTOs = new ArrayList<EmailDTO>();

        List<Email> emails = getVCard().getEmails();

        for (Email email : emails) {

            EmailDTO emailDTO = getEmail(email);

            emailDTOs.add(emailDTO);
        }

        // an empty default email
        if (emailDTOs.size() == 0) {
            emailDTOs.add(new EmailDTO());
        }

        return emailDTOs;

    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public List<ExpertiseDTO> getExpertises() {

        List<Expertise> expertises = getVCard().getExpertise();
        List<ExpertiseDTO> expertiseDTOs = new ArrayList<ExpertiseDTO>();

        for (Expertise expertise : expertises) {
            ExpertiseDTO expertiseDTO = new ExpertiseDTO();
            expertiseDTO.setValue(expertise.getValue());
            ExpertiseLevel level = expertise.getLevel();
            if (level != null) {
                expertiseDTO.setLevel(level.getValue());
            }
            expertiseDTOs.add(expertiseDTO);
        }

        // an empty default expertise
        if (expertiseDTOs.size() == 0) {
            expertiseDTOs.add(new ExpertiseDTO());
        }

        return expertiseDTOs;
    }

    @Override
    public String getFormattedName() {

        String formattedName = "";

        FormattedName fn = getVCard().getFormattedName();

        if (fn != null) {
            formattedName = fn.getValue();
        }

        return formattedName;

    }

    /**
     *
     * @return
     * @since 1.1.5
     */
    @Override
    public String getGender() {

        String str = Gender.UNKNOWN;

        Gender gender = getVCard().getGender();

        if (gender != null) {
            str = gender.getGender();
        }

        return str;

    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public List<HobbyDTO> getHobbies() {

        List<Hobby> hobbies = getVCard().getHobbies();
        List<HobbyDTO> hobbyDTOs = new ArrayList<HobbyDTO>();

        for (Hobby hobby : hobbies) {
            HobbyDTO hobbyDTO = new HobbyDTO();
            hobbyDTO.setValue(hobby.getValue());
            HobbyLevel level = hobby.getLevel();
            if (level != null) {
                hobbyDTO.setLevel(level.getValue());
            }
            hobbyDTOs.add(hobbyDTO);
        }

        // an empty default hobby
        if (hobbyDTOs.size() == 0) {
            hobbyDTOs.add(new HobbyDTO());
        }

        return hobbyDTOs;

    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public List<ImppDTO> getImpps() {

        List<ImppDTO> imppDTOs = new ArrayList<ImppDTO>();

        List<Impp> impps = getVCard().getImpps();

        for (Impp impp : impps) {

            ImppDTO imppDTO = new ImppDTO();

            StringBuilder sb = new StringBuilder();

            List<ImppType> types = impp.getTypes();

            // TODO: Add support for multiple types e.g.
            // home-skype, work-jabber, etc.
            if (types.size() > 0) {
                for (ImppType type : types) {
                    sb.append(type.getValue());
                }
            } else {
                sb.append("other");
            }

            imppDTO.setProtocol(impp.getProtocol());
            imppDTO.setType(sb.toString());

            String protocol = impp.getProtocol();
            String uri = impp.getUri().toString();

            // TODO: find a cleaner solution for this
            uri = uri.replace(protocol + ":", "");

            imppDTO.setUri(uri);

            imppDTOs.add(imppDTO);
        }

        // an empty default impp
        if (imppDTOs.size() == 0) {
            imppDTOs.add(new ImppDTO());
        }

        return imppDTOs;

    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public List<InterestDTO> getInterests() {

        List<Interest> interests = getVCard().getInterests();
        List<InterestDTO> interestDTOs = new ArrayList<InterestDTO>();

        for (Interest interest : interests) {
            InterestDTO interestDTO = new InterestDTO();
            interestDTO.setValue(interest.getValue());
            InterestLevel level = interest.getLevel();
            if (level != null) {
                interestDTO.setLevel(level.getValue());
            }
            interestDTOs.add(interestDTO);
        }

        // an empty default interest
        if (interestDTOs.size() == 0) {
            interestDTOs.add(new InterestDTO());
        }

        return interestDTOs;
    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public String getKind() {

        String str = "individual";

        Kind kind = getVCard().getKind();

        if (kind != null) {
            str = kind.getValue();
        }

        return str;

    }

    /**
     *
     * @return
     * @since 1.1.1
     */
    @Override
    public List<LanguageDTO> getLanguages() {

        List<Language> languages = getVCard().getLanguages();
        List<LanguageDTO> languageDTOs = new ArrayList<LanguageDTO>();

        for (Language language : languages) {

            LanguageDTO languageDTO = new LanguageDTO();
            languageDTO.setKey(language.getValue());

            languageDTOs.add(languageDTO);
        }

        return languageDTOs;
    }

    @Override
    public String getName() {

        String firstLast = getFullName(true);
        String lastFirst = getFullName(false);

        String name = lastFirst;

        if (Validator.isNull(firstLast)) {

            Organization organization = getVCard().getOrganization();

            if (organization != null) {

                List<String> values = organization.getValues();

                Iterator<String> iterator = values.iterator();

                StringBuilder sb = new StringBuilder();

                while (iterator.hasNext()) {

                    sb.append(iterator.next());
                    if (iterator.hasNext()) {
                        sb.append(", ");
                    }

                }

                name = sb.toString();

            }

        }

        return name;

    }

    /**
     *
     * @return the preferred phone.
     * @since 1.0.8
     */
    public PhoneDTO getPhone() {

        List<Telephone> phones = getVCard().getTelephoneNumbers();

        if (phones != null) {

            for (Telephone phone : phones) {
                Integer pref = phone.getPref();
                if (pref != null) {
                    if (pref == 1) {
                        return getPhone(phone);
                    }
                }
            }
        }

        Telephone phone = getVCard().getProperty(Telephone.class);

        return getPhone(phone);

    }

    private PhoneDTO getPhone(Telephone phone) {

        PhoneDTO phoneDTO = new PhoneDTO();

        if (phone != null) {
            phoneDTO.setNumber(phone.getText());

            StringBuilder sb = new StringBuilder();

            Set<TelephoneType> types = SetUtil.fromList(phone.getTypes());

            // TODO: Add support for multiple telephone types
            // e.g. home-fax, work-mobile, etc.
            if (types.size() > 0) {
                for (TelephoneType type : types) {
                    sb.append(type.getValue());
                }
            } else {
                sb.append("other");
            }

            phoneDTO.setType(sb.toString());
        }

        return phoneDTO;
    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public List<PhoneDTO> getPhones() {

        List<PhoneDTO> phoneDTOs = new ArrayList<PhoneDTO>();

        List<Telephone> phones = getVCard().getTelephoneNumbers();

        for (Telephone phone : phones) {

            PhoneDTO phoneDTO = getPhone(phone);

            phoneDTOs.add(phoneDTO);
        }

        // an empty default phone
        if (phoneDTOs.size() == 0) {
            phoneDTOs.add(new PhoneDTO());
        }

        return phoneDTOs;

    }

    @Override
    public String getSalutation() {

        String salutation = "";

        VCard vCard = getVCard();

        RawProperty rawProperty = vCard.getExtendedProperty("x-salutation");

        if (rawProperty != null) {
            salutation = rawProperty.getValue();
        }

        return salutation;

    }

    @Override
    public VCard getVCard() {

        String str = getCard();
        VCard vCard = null;

        if (Validator.isNotNull(str)) {
            vCard = Ezvcard.parse(str).first();
        } else {
            vCard = new VCard();
        }

        return vCard;

    }

    @Override
    public String getFullName() {
        return getFullName(false);
    }

    @Override
    public String getFullName(boolean firstLast) {

        StringBuilder sb = new StringBuilder();

        StructuredName sn = getVCard().getStructuredName();

        if (sn != null) {
            if (firstLast) {
                sb.append(sn.getGiven());
                sb.append(" ");
                sb.append(sn.getFamily());
            } else {
                sb.append(sn.getFamily());
                sb.append(", ");
                sb.append(sn.getGiven());
            }
        }

        String fullName = sb.toString();

        if (Validator.isNull(fullName)) {

            Organization organization = getVCard().getOrganization();

            if (organization != null) {

                List<String> values = organization.getValues();

                Iterator<String> iterator = values.iterator();

                while (iterator.hasNext()) {

                    sb.append(iterator.next());
                    if (iterator.hasNext()) {
                        sb.append(", ");
                    }

                }
            }

        }

        return fullName;

    }

    /**
     *
     * @return
     * @since 1.0.0
     */
    @Override
    public String getTimezone() {

        String str = "";
        Timezone timezone = getVCard().getTimezone();

        if (timezone != null) {
            str = timezone.getText();
        }

        return str;
    }

}