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

import org.apache.commons.lang.StringUtils
import org.apache.commons.validator.EmailValidator

/**
 * check password and new password.
 * @param userPassword
 * @param actualPassword
 * @param newPassword
 * @param confirmNewPassword
 * @return list with errors
 */
public static List<String> validatorPasswordChange(String userPassword, String actualPassword, String newPassword, String confirmNewPassword) {
    List<String> errors = []
    if (!userPassword.equals(actualPassword)) {
        errors.add("ddbcommon.Error_Password_Provided")
    }
    errors.addAll(validatorPassword(newPassword, confirmNewPassword))
    return errors
}

/**
 * check password and confirm-password.
 * @param password
 * @param cpassword
 * @return list with errors
 */
public static List<String> validatorPassword(String password, String cpassword) {
    List<String> errors = []
    if (StringUtils.isBlank(password) || StringUtils.isBlank(cpassword)) {
        if (StringUtils.isBlank(password)) {
            errors.add("ddbcommon.Error_Password_Empty")
        }
        if (StringUtils.isBlank(cpassword)) {
            errors.add("ddbcommon.Error_Confirm_Password_Empty")
        }
    }
    else {
        if (!password.equals(cpassword)) {
            errors.add("ddbcommon.Error_Password_Match")
        }
        //        if (newPassword.trim().length() < 8 || !newPassword.find("[0-9]") ||!newPassword.find("[a-z]") || !newPassword.find("[A-Z]")) {
        if (password.trim().length() < 8) {
            errors.add("ddbcommon.Error_Password_Rules")
        }
    }
    return errors
}

/**
 * check parameters for registration.
 * @param username
 * @param firstname
 * @param lastname
 * @param email
 * @param password
 * @param cpassword
 * @return list with errors
 */
public static List<String> validatorRegistration(String username, String firstname, String lastname, String email, String password, String cpassword) {
    List<String> errors = []
    if (StringUtils.isBlank(username) || username.length() < 2) {
        errors.add("ddbcommon.Error_Username_Empty")
    }
    if (!validatorEmail(email)) {
        errors.add("ddbcommon.Error_Valid_Email_Address")
    }
    errors.addAll(validatorPassword(password, cpassword))
    return errors
}

/**
 * 
 * @param email
 * @return true or false (valid or not)
 */
public static validatorEmail(String email){
    EmailValidator emailValidator = EmailValidator.getInstance()
    return emailValidator.isValid(email)
}

/**
 *
 * @param val1
 * @param val2
 * @return true or false (different or not)
 */
public static isDifferent(String val1, String val2) {
    if (StringUtils.isBlank(val1) && StringUtils.isBlank(val2)) {
        return false
    }
    if ((StringUtils.isBlank(val1) && !StringUtils.isBlank(val2))
    || (StringUtils.isBlank(val2) && !StringUtils.isBlank(val1))) {
        return true
    }
    if (!val1.equals(val2)) {
        return true
    }
    return false
}

