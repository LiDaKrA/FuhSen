package de.ddb.common.constants

/**
 * Enum containing the values of the subsector facet
 *
 * @author boz
 */
enum SubSectorEnum {

    Staatliche_Archive ("Staatliche Archive"),
    Kommunale_Archive ("Kommunale Archive"),
    Kirchliche_Archive ("Kirchliche Archive"),
    Herrschafts_und_Familienarchive ("Herrschafts- und Familienarchive"),
    Wirtschaftsarchive ("Wirtschaftsarchive"),
    Archive_der_Parlamente__politischen_Parteien__Stiftungen_und_Verbaende ("Archive der Parlamente, politischen Parteien, Stiftungen und Verbände"),
    Medienarchive ("Medienarchive"),
    Archive_der_Hochschulen_sowie_wissenschaftlicher_Institutionen ("Archive der Hochschulen sowie wissenschaftlicher Institutionen"),
    Sonstige_Archive ("Sonstige Archive");

    String name;

    /**
     * Constructor
     * @param name the name of the subsector
     */
    public SubSectorEnum(String name) {
        this.name = name;
    }

    /**
     * Return the name of the sector
     */
    public String getName (){
        return this.name;
    }
}