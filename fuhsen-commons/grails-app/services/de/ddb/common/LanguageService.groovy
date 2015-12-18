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

/**
 * Provides functionality for language settings
 *
 * @author boz
 */
class LanguageService {
    def transactional=false
    def configurationService

    public List<Locale> getSupportedLocales() {
        def locales = configurationService.getSupportedLocales()

        return locales
    }

    public Locale getDefaultLocale() {
        return Locale.default
    }

    public List<String> getSupportedLanguagesISO2() {
        List<Locale> supported = getSupportedLocales()

        ArrayList<String> out = new ArrayList<String>()
        for (Locale support : supported) {
            out.add(support.getLanguage())
        }
        return out
    }


    public List<String> getSupportedLanguagesISO3() {
        List<Locale> supported = getSupportedLocales()

        ArrayList<String> out = new ArrayList<String>()
        for (Locale support : supported) {
            out.add(support.getISO3Language())
        }
        return out
    }


    public boolean supports(Locale locale) {
        if (locale == null) {
            return false
        }

        String language = locale.getLanguage()

        List<Locale> supported = getSupportedLocales()
        for (Locale support : supported) {
            if (support.getLanguage().equals(language)) {
                return true
            }
        }
        return false
    }

    public Locale getBestMatchingLocale(Locale input){
        Locale locale = input

        if(!locale){
            locale = Locale.default
        }
        if(!supports(locale)){
            locale = Locale.default
        }

        List<Locale> supported = getSupportedLocales()
        for (Locale support : supported) {
            if(support.getLanguage().equals(locale.getLanguage())){
                locale = support
                break
            }
        }
        return locale
    }

    public Locale getBestMatchingLocale(String input){
        Locale locale = new Locale(input)
        return getBestMatchingLocale(locale)
    }

    public Locale getDefinedLocale(Locale locale) {
        if (locale == null) {
            return null
        }

        String language = locale.getLanguage()

        List<Locale> supported = getSupportedLocales()
        for (Locale support : supported) {
            if (support.getLanguage().equals(language)) {
                return support
            }
        }
        return null
    }
}
