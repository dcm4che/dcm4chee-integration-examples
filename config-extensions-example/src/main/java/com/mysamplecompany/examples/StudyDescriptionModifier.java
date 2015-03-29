package com.mysamplecompany.examples;

import org.dcm4che3.conf.api.extensions.CommonAEExtension;
import org.dcm4che3.conf.core.api.ConfigurableClass;
import org.dcm4che3.conf.core.api.ConfigurableProperty;

/**
 * This is a CDI bean that extends CommonAEExtension, that's how it will get picked up by the config engine
 * It must be annotated with ConfigurableClass and ConfigurableProperty annotations.
 *
 * @author Roman K
 */
@ConfigurableClass
public class StudyDescriptionModifier extends CommonAEExtension {

    /**
     * Simplest way - by default name equals field name
     */
    @ConfigurableProperty(description = "Defines a text chunk that will be added to descriptions of all stored studies")
    String extraStudyDescription;

    /**
     * You can override name, put default, and more...
     */
    @ConfigurableProperty(name = "useBrackets",
            description = "Defines whether the text extra description text should be put in brakcets",
            defaultValue = "true")
    boolean doPutTheExtraDescriptionInBrackets = true;

    /**
     * You can override getter/setter and have a fully custom persist/load/reconfigure behavior
     */
    @ConfigurableProperty(description = "How many spaces to put before the extra description", defaultValue = "0")
    String spacesBeforeExtraDescription;

    transient int actualNumberOfSpaces = 5;

    public String getSpacesBeforeExtraDescription() {
        return String.valueOf(actualNumberOfSpaces - 5);
    }

    public void setSpacesBeforeExtraDescription(String spacesBeforeExtraDescription) {
        // somehow we decided we want always 5 spaces more
        actualNumberOfSpaces = Integer.valueOf(spacesBeforeExtraDescription) + 5;
    }

    public String getExtraStudyDescription() {
        return extraStudyDescription;
    }

    public void setExtraStudyDescription(String extraStudyDescription) {
        this.extraStudyDescription = extraStudyDescription;
    }

    public boolean isDoPutTheExtraDescriptionInBrackets() {
        return doPutTheExtraDescriptionInBrackets;
    }

    public void setDoPutTheExtraDescriptionInBrackets(boolean doPutTheExtraDescriptionInBrackets) {
        this.doPutTheExtraDescriptionInBrackets = doPutTheExtraDescriptionInBrackets;
    }

    public int getActualNumberOfSpaces() {
        return actualNumberOfSpaces;
    }

    public void setActualNumberOfSpaces(int actualNumberOfSpaces) {
        this.actualNumberOfSpaces = actualNumberOfSpaces;
    }
}
