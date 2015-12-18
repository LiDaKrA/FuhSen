package de.ddb.next

class ScaleImageTagLib {
    static namespace = "ddb"

    def scaleImage = { attrs, body ->
        def inputString = body()
        def outputString = ""
        def scaleWidth = 140
        def scaleHeight = 90
        if(inputString){
            if (inputString.indexOf(".") > 0) {
                inputString = inputString.substring(0, inputString.lastIndexOf("."))
            }

            String[] parts = inputString.split("_")
            if(parts) {
                String part1 = parts[parts.size()-1]
                if(part1 && part1.indexOf("x") > 0) {
                    String[] heightWidth = part1.split("x")
                    if(heightWidth) {
                        if(heightWidth && heightWidth[0] && attrs.side.equalsIgnoreCase("width")) {
                            if(Integer.parseInt(heightWidth[0]) > scaleWidth ) {
                                outputString = scaleWidth
                            }
                        }
                        else if (heightWidth && heightWidth.size()>1 && attrs.side.equalsIgnoreCase("height")) {
                            if(Integer.parseInt(heightWidth[1]) > scaleHeight ) {
                                outputString = scaleHeight
                            }
                        }
                    }
                }
            }
        }
        out << outputString
    }
}
