package de.ddb.common.constants

/**
 * Enum containing the values of the terms for the organization categories
 *
 * @author ttr
 */
enum TermEnum {

    pid ("ISIL"),
    ISIS ("ISIS"),
    dateofregistration ("Registrierungsdatum"),
    email ("Email"),
    city ("Stadt"),
    user ("Benutzer"),
    sector ("Kategorie"),
    status ("Status");

    String name;

    /**
     * Constructor
     * @param name the name of the term
     */
    public TermEnum(String name) {
        this.name = name;
    }

    /**
     * Return the name of the term
     */
    public String getName (){
        return this.name;
    }
}