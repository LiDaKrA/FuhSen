package de.ddb.common.constants

/**
 * Enum containing the values of the state facet
 *
 * @author boz
 */
enum StateFacetEnum {

    BADEN_WUERTTEMBERG("Baden-Württemberg"),
    BAYERN("Bayern"),
    BERLIN("Berlin"),
    BRANDENBURG("Brandenburg"),
    BREMEN("Bremen"),
    HAMBURG("Hamburg"),
    HESSEN("Hessen"),
    MECKLENBURG_VORPOMMERN("Mecklenburg-Vorpommern"),
    NIEDERSACHSEN("Niedersachsen"),
    NORDRHEIN_WESTFALEN("Nordrhein-Westfalen"),
    RHEINLAND_PFALZ("Rheinland-Pfalz"),
    SAARLAND("Saarland"),
    SACHSEN("Sachsen"),
    SACHSEN_ANHALT("Sachsen-Anhalt"),
    SCHLESWIG_HOLSTEIN("Schleswig-Holstein"),
    THUERINGEN("Thüringen");

    String name;

    /**
     * Constructor
     * @param name the name of the subsector
     */
    public StateFacetEnum(String name) {
        this.name = name;
    }

    /**
     * Return the name of the sector
     */
    public String getName (){
        return this.name;
    }
}