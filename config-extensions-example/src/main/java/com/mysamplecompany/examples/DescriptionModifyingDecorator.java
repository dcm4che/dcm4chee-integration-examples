package com.mysamplecompany.examples;

import org.dcm4che3.conf.api.DicomConfiguration;
import org.dcm4che3.conf.core.api.ConfigurationException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.ElementDictionary;
import org.dcm4che3.data.Tag;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.service.DicomServiceException;
import org.dcm4chee.archive.store.DelegatingStoreService;
import org.dcm4chee.archive.store.StoreContext;
import org.dcm4chee.archive.store.StoreService;
import org.dcm4chee.conf.cdi.dynamicdecorators.DynamicDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

/**
 * A sample decorator that will modify the description of each study being stored using the
 * custom configuration extension (StudyDescriptionModifier) of the application entity that is used for storing.
 *
 * @author Roman K
 */
@DynamicDecorator
public class DescriptionModifyingDecorator extends DelegatingStoreService {

    private static Logger log = LoggerFactory
            .getLogger(DescriptionModifyingDecorator.class);

    @Inject
    DicomConfiguration config;

    @Inject
    Device archiveDevice;


    /**
     * "Business logic" ;)
     * <br/>
     * Modifies a given description using the provided configuration of the modifier
     */
    String getModifiedDescription(String original, StudyDescriptionModifier modifierConfig) {

        // get configured extra description
        String mod = modifierConfig.getExtraStudyDescription();

        // add brackets if configured
        if (modifierConfig.isDoPutTheExtraDescriptionInBrackets())
            mod = "(" + mod + ")";

        // add configured amount of spaces
        String spaces = new String(new char[modifierConfig.getActualNumberOfSpaces()]).replace('\0', ' ');
        mod = spaces + mod;

        // concat with original
        return original + mod;
    }

    @Override
    public void coerceAttributes(StoreContext context) throws DicomServiceException {

        String localAET = context.getStoreSession().getLocalAET();
        log.warn("Found local AET: {}", localAET);

        // get ae extension of the AE that is called in this context
        StudyDescriptionModifier modifierConfig = archiveDevice.getApplicationEntity(localAET).getAEExtension(StudyDescriptionModifier.class);

        // initialize if extension not exists (like a singleton)
        // this is just a demo of how to update the configuration programmatically - most decorators should not do that
        // (i.e. they should access the config read-only)
        if (modifierConfig == null) {

            modifierConfig = new StudyDescriptionModifier();
            modifierConfig.setExtraStudyDescription("some default additional info");
            modifierConfig.setActualNumberOfSpaces(5);

            archiveDevice.getApplicationEntity(localAET).addAEExtension(modifierConfig);

            try {
                // this is how the configuration can be modified in the configuration storage programmatically
                config.merge(archiveDevice);
            } catch (ConfigurationException e) {
                log.error("not able to update config", e);
            }
        }

        // now we can use the information from the extension
        log.warn("Study description modifier for AE title {} is configured to :" +
                " \n use extra description {} " +
                " \n put in brackets: {}" +
                " \n use {} offset spaces", localAET, modifierConfig.getExtraStudyDescription(), modifierConfig.isDoPutTheExtraDescriptionInBrackets(), modifierConfig.getActualNumberOfSpaces());

        // apply business logic based on extension config
        Attributes attributes = context.getAttributes();
        log.warn("Changing description of study {}", attributes.getString(Tag.StudyInstanceUID));

        String studyDescription = attributes.getString(Tag.StudyDescription);
        studyDescription = getModifiedDescription(studyDescription, modifierConfig);
        attributes.setString(Tag.StudyDescription, ElementDictionary.vrOf(Tag.StudyDescription, null), studyDescription);

        // proceed with coercion
        getDelegate().coerceAttributes(context);

    }
}
