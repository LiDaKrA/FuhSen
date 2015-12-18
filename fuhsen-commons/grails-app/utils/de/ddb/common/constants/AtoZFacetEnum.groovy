package de.ddb.common.constants

/**
 * Enum containing the values of the AtoZ facet
 *
 * @author boz
 */
enum AtoZFacetEnum {

    A("A"), B("B"),C("C"), D("D"),E("E"), F("F"),G("G"), H("H"),I("I"), J("J"),
    K("K"), L("L"),M("M"), N("N"),O("O"), P("P"),Q("Q"), R("R"),S("S"), T("T"),
    U("U"), V("V"),W("W"), X("X"),Y("Y"), Z("Z"),

    String name;

    /**
     * Constructor
     * @param name the name of the subsector
     */
    public AtoZFacetEnum(String name) {
        this.name = name;
    }

    /**
     * Return the name of the sector
     */
    public String getName (){
        return this.name;
    }
}