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
package de.ddb.common.beans

import groovy.transform.ToString
import de.ddb.common.JsonUtil

/**
 * Class for the elastic search mapping of a "folderList" 
 * 
 * @author boz
 */
@ToString(includeNames=true)
class FolderList {

    String folderListId
    String title
    Date creationDate
    Collection users
    Collection folders

    /**
     * Constructor
     * @param folderListId
     * @param userId
     * @param title
     * @param creationDateAsLong
     */
    public FolderList(String folderListId,  String title, def creationDateAsLong, def users,def folders) {
        this.folderListId = folderListId
        this.title = title

        if(JsonUtil.isAnyNull(creationDateAsLong)){
            this.creationDate = new Date()
        }else{
            this.creationDate = new Date(creationDateAsLong)
        }

        if(JsonUtil.isAnyNull(folders)){
            this.folders = null
        }else if(folders instanceof String){
            String folderString = folders.toString()
            if(folderString.startsWith("[")){
                folderString = folderString.substring(1,folderString.length()-1)
                this.folders = Arrays.asList(folderString.split(","))
            }else{
                this.folders = [folders]
            }
        } else{
            this.folders = folders
        }

        if(JsonUtil.isAnyNull(users)){
            this.users = null
        }else if(users instanceof String){
            String userString = users.toString()
            if(userString.startsWith("[")){
                userString = userString.substring(1,userString.length()-1)
                this.folders = Arrays.asList(userString.split(","))
            }else{
                this.users = [users]
            }
        } else{
            this.users = users
        }
    }

    /**
     * Return the objects stae as an map
     * @return
     */
    public def getAsMap() {
        def out = [:]
        out["folderListId"] = folderListId
        out["title"] = title
        out["creationDate"] = creationDate
        out["users"] = users
        out["folders"] = folders
        return out
    }

    /**
     * Checks whether the object has an valid state. At least an id and a user must be available.
     * @return <code>true</true> if the object has an valid state
     */
    boolean isValid(){
        if(folderListId != null
        && creationDate != null && title != null) {
            return true
        }
        return false
    }
}
