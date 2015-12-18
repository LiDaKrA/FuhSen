/*
 * Copyright (C) 2014 FIZ Karlsruhe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.common

class RenderInfoTooltipTagLib {

    static namespace = "ddbcommon"

    def grailsLinkGenerator

    /**
     * Renders the infoTooltip template.
     * The tooltip can contain a link. The path of the link is specified via an (infoDir & infoId) or via an controllerAction
     *
     * @attr messageCode, infoId, infoDir, controllerAction
     */
    def renderInfoTooltip = { attrs, body ->
        def link = null
        def infoId = attrs.infoId
        def infoDir = attrs.infoDir
        def controllerAction = attrs.controllerAction
        def controllerController = attrs.controllerController ? attrs.controllerController : "content"
        def params = [:]

        if (infoId) {
            params.put("id", infoId)
        }
        if (infoDir) {
            params.put("dir", infoDir)
        }
        if (infoId || infoDir) {
            link = grailsLinkGenerator.link(controller: controllerController, params: params)
        }
        else if (controllerAction) {
            link = grailsLinkGenerator.link(controller: controllerController, action: controllerAction)
        }

        out << render(template:"/common/infoTooltip", model:[messageCode: attrs.messageCode, link: link, hasArrow: attrs.hasArrow])
    }
}
