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

/* Search namespace  */
de.ddb.next.search = de.ddb.next.search || {};

//This patter searches for _1_ which is the divider between literal and role
de.ddb.next.search.literalDividerPattern = /_[0-9]+_/;

/**
 * Returns a role without its literal part.
 *
 * If a concrete role looks like this
 * "Cotta_1_affiliate_fct_involved"
 *
 * The method return
 * "_1_affiliate_fct_involved"
 *
 * Roles have the following structure:
 * <Wert des Literals>_<Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene>
 *
 * If the divider pattern does not match, return <code>null</code>
 */
de.ddb.next.search.getRoleWithoutLiteral = function(role) {
  var roleWithoutLiteral = null;

  //Check if the role value matches the pattern
  var divider = de.ddb.next.search.literalDividerPattern.exec(role);
  if (divider) {
    var indexOfDivider = role.indexOf(divider);
    roleWithoutLiteral = role.substring(indexOfDivider);
  }

  return roleWithoutLiteral;
};

/**
 * Returns the literal part of a role
 *
 * If a concrete role looks like this
 * "Cotta_1_affiliate_fct_involved"
 *
 * The method return
 * "Cotta"
 *
 * Roles have the following structure:
 * <Wert des Literals>_<Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene>
 *
 * If the divider pattern does not match, return <code>null</code>
 */
de.ddb.next.search.getLiteralFromRole = function(role) {
  var literal = null;

  //Check if the role value matches the pattern
  var divider = de.ddb.next.search.literalDividerPattern.exec(role);
  if (divider) {
    var split = role.split(divider);
    literal = split[0];
  }
  return literal;
};

/**
 * Returns the role without literal and hierarchie number
 *
 * If a concrete role looks like this
 * "Cotta_1_affiliate_fct_involved"
 *
 * The method return
 * "affiliate_fct_involved"
 *
 * Roles have the following structure:
 * <Wert des Literals>_<Stufe der Hierarchie der Rolle>_<Name der Facette>_<Name der ersten Rollen-Ebene>_..._<Name der n-ten Rollen-Ebene>
 *
 * If the divider pattern does not match, return <code>null</code>
 */
de.ddb.next.search.getRoleWithoutLiteralAndHierarchieNumber = function(role) {
  var splittedRole = null;

  //Check if the role value matches the pattern
  var divider = de.ddb.next.search.literalDividerPattern.exec(role);
  if (divider) {
    var split = role.split(divider);
    splittedRole = split[1];
  }
  return splittedRole;
};

/**
 *
 */
de.ddb.next.search.isRole  = function(value) {
  //Check if the role value matches the pattern
  var isRole = de.ddb.next.search.literalDividerPattern.test(value);
  return isRole;
};