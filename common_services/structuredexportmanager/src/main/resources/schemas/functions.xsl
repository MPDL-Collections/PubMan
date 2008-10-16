<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet xml:base="stylesheet" version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:func="urn:my-functions">
	
	<xsl:variable name="entities">

		<replacement>
			<with>\\acute{e}</with>
			<replace>&#xe9;</replace>
		</replacement>
		<replacement>
			<with>\\ast</with>
			<replace>\&#x002a;</replace>
		</replacement>
		<replacement>
			<with>\\star</with>
			<replace>\&#x002a;</replace>
		</replacement>
		<replacement>
			<with>{\\{}</with>
			<replace>\&#x007b;</replace>
		</replacement>
		<replacement>
			<with>\\{</with>
			<replace>\&#x007b;</replace>
		</replacement>
		<replacement>
			<with>{\\}}</with>
			<replace>\&#x007d;</replace>
		</replacement>
		<replacement>
			<with>\\}</with>
			<replace>\&#x007d;</replace>
		</replacement>
		<replacement>
			<with>^{\\underline{\\rm a}}</with>
			<replace>&#x00aa;</replace>
		</replacement>
		<replacement>
			<with>^{\\circ}</with>
			<replace>&#x00b0;</replace>
		</replacement>
		<replacement>
			<with>{\\pm}</with>
			<replace>&#x00b1;</replace>
		</replacement>
		<replacement>
			<with>^{2}</with>
			<replace>&#x00b2;</replace>
		</replacement>
		<replacement>
			<with>^{3}</with>
			<replace>&#x00b3;</replace>
		</replacement>
		<replacement>
			<with>^{1}</with>
			<replace>&#x00b9;</replace>
		</replacement>
		<replacement>
			<with>\\frac{1}{2}</with>
			<replace>&#x00bd;</replace>
		</replacement>
		<replacement>
			<with>{\\times}</with>
			<replace>&#x00d7;</replace>
		</replacement>
		<replacement>
			<with>\\times</with>
			<replace>&#x00d7;</replace>
		</replacement>
		<replacement>
			<with>{\\div}</with>
			<replace>&#x00f7;</replace>
		</replacement>
		<replacement>
			<with>\\div</with>
			<replace>&#x00f7;</replace>
		</replacement>
		<replacement>
			<with>\\dot G</with>
			<replace>&#x0120;</replace>
		</replacement>
		<replacement>
			<with>\\Gamma</with>
			<replace>&#x0393;</replace>
		</replacement>
		<replacement>
			<with>\\Delta</with>
			<replace>&#x0394;</replace>
		</replacement>
		<replacement>
			<with>\\Lambda</with>
			<replace>&#x039b;</replace>
		</replacement>
		<replacement>
			<with>\\Sigma</with>
			<replace>&#x03a3;</replace>
		</replacement>
		<replacement>
			<with>\\Omega</with>
			<replace>&#x03a9;</replace>
		</replacement>
		<replacement>
			<with>\\delta</with>
			<replace>&#x03b4;</replace>
		</replacement>
		<replacement>
			<with>\\alpha</with>
			<replace>&#x03b1;</replace>
		</replacement>
		<replacement>
			<with>\\beta</with>
			<replace>&#x03b2;</replace>
		</replacement>
		<replacement>
			<with>\\gamma</with>
			<replace>&#x03b3;</replace>
		</replacement>
		<replacement>
			<with>\\delta</with>
			<replace>&#x03b4;</replace>
		</replacement>
		<replacement>
			<with>\\epsilon</with>
			<replace>&#x03b5;</replace>
		</replacement>
		<replacement>
			<with>\\zeta</with>
			<replace>&#x03b6;</replace>
		</replacement>
		<replacement>
			<with>\\eta</with>
			<replace>&#x03b7;</replace>
		</replacement>
		<replacement>
			<with>\\theta</with>
			<replace>&#x03b8;</replace>
		</replacement>
		<replacement>
			<with>\\kappa</with>
			<replace>&#x03ba;</replace>
		</replacement>
		<replacement>
			<with>\\lambda</with>
			<replace>&#x03bb;</replace>
		</replacement>
		<replacement>
			<with>\\mu</with>
			<replace>&#x03bc;</replace>
		</replacement>
		<replacement>
			<with>\\nu</with>
			<replace>&#x03bd;</replace>
		</replacement>
		<replacement>
			<with>\\xi</with>
			<replace>&#x03be;</replace>
		</replacement>
		<replacement>
			<with>\\pi</with>
			<replace>&#x03c0;</replace>
		</replacement>
		<replacement>
			<with>\\rho</with>
			<replace>&#x03c1;</replace>
		</replacement>
		<replacement>
			<with>\\sigma</with>
			<replace>&#x03c3;</replace>
		</replacement>
		<replacement>
			<with>\\tau</with>
			<replace>&#x03c4;</replace>
		</replacement>
		<replacement>
			<with>\\phi</with>
			<replace>&#x03c6;</replace>
		</replacement>
		<replacement>
			<with>\\chi</with>
			<replace>&#x03c7;</replace>
		</replacement>
		<replacement>
			<with>\\omega</with>
			<replace>&#x03c9;</replace>
		</replacement>
		<replacement>
			<with>\\ell</with>
			<replace>&#x2113;</replace>
		</replacement>
		<replacement>
			<with>\\rightarrow</with>
			<replace>&#x2192;</replace>
		</replacement>
		<replacement>
			<with>\\to</with>
			<replace>&#x2192;</replace>
		</replacement>
		<replacement>
			<with>\\leftrightarrow</with>
			<replace>&#x2194;</replace>
		</replacement>
		<replacement>
			<with>\\nabla</with>
			<replace>&#x2207;</replace>
		</replacement>
		<replacement>
			<with>\\sim</with>
			<replace>&#x223c;</replace>
		</replacement>
		<replacement>
			<with>\\le</with>
			<replace>&#x2264;</replace>
		</replacement>
		<replacement>
			<with>\\ge</with>
			<replace>&#x2265;</replace>
		</replacement>
		<replacement>
			<with>\\lesssim</with>
			<replace>&#x2272;</replace>
		</replacement>
		<replacement>
			<with>\\gtrsim</with>
			<replace>&#x2273;</replace>
		</replacement>
		<replacement>
			<with>\\odot</with>
			<replace>&#x2299;</replace>
		</replacement>
		<replacement>
			<with>\\infty</with>
			<replace>&#x221e;</replace>
		</replacement>
		<replacement>
			<with>\\circ</with>
			<replace>&#x2218;</replace>
		</replacement>
		<replacement>
			<with>\\cdot</with>
			<replace>&#x22c5;</replace>
		</replacement>
		<replacement>
			<with>\\dot{P}</with>
			<replace>&#x1e56;</replace>
		</replacement>
		<replacement>
			<with>{\\vec B}</with>
			<replace>&#x20d7;</replace>
		</replacement>
		<replacement>
			<with>\\symbol{94}</with>
			<replace>\&#x005e;</replace>
		</replacement>
		<replacement>
			<with>\\symbol{126}</with>
			<replace>&#x007e;</replace>
		</replacement>
		<replacement>
			<with>\\~{}</with>
			<replace>&#x007e;</replace>
		</replacement>
		<replacement>
			<with>\$\\sim\$</with>
			<replace>&#x007e;</replace>
		</replacement>

		<replacement>
			<with>{!`}</with>
			<replace>&#x00a1;</replace>
		</replacement>
		<replacement>
			<with>{\\copyright}</with>
			<replace>&#x00a9;</replace>
		</replacement>
		<replacement>
			<with>{?`}</with>
			<replace>&#x00bf;</replace>
		</replacement>
		<replacement>
			<with>{\\`{A}}</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>{\\`A}</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>\\`{A}</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>\\`A</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>{\\'{A}}</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>{\\'A}</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>\\'{A}</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>\\'A</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>{\\^{A}}</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>{\\^A}</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>\\^{A}</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>\\^A</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>{\\~{A}}</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>{\\~A}</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>\\~{A}</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>\\~A</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>{\\"{A}}</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>{\\"A}</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>\\"{A}</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>\\"A</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>{\\AA}</with>
			<replace>&#x00c5;</replace>
		</replacement>
		<replacement>
			<with>{\\AE}</with>
			<replace>&#x00c6;</replace>
		</replacement>
		<replacement>
			<with>{\\c{C}}</with>
			<replace>&#x00c7;</replace>
		</replacement>
		<replacement>
			<with>\\c{C}</with>
			<replace>&#x00c7;</replace>
		</replacement>
		<replacement>
			<with>{\\`{E}}</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>{\\`E}</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>\\`{E}</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>\\`E</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>{\\'{E}}</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>{\\'E}</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>\\'{E}</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>\\'E</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>{\\^{E}}</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>{\\^E}</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>\\^{E}</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>\\^E</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>{\\"{E}}</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>{\\"E}</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>\\"{E}</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>\\"E</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>{\\`{I}}</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>{\\`I}</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>\\`{I}</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>\\`I</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>{\\'{I}}</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>{\\'I}</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>\\'{I}</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>\\'I</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>{\\^{I}}</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>{\\^I}</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>\\^{I}</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>\\^I</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>{\\"{I}}</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>{\\"I}</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>\\"{I}</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>\\"I</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>{\\~{N}}</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>\\~{N}</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>{\\~N}</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>\\~N</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>{\\`{O}}</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>{\\`O}</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>\\`{O}</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>\\`O</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>{\\'{O}}</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>{\\'O}</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>\\'{O}</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>\\'O</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>{\\^{O}}</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>{\\^O}</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>\\^{O}</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>\\^O</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>{\\~{O}}</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>{\\~O}</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>\\~{O}</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>\\~O</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>{\\"{O}}</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>{\\"O}</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>\\"{O}</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>\\"O</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>{\\O}</with>
			<replace>&#x00d8;</replace>
		</replacement>
		<replacement>
			<with>{\\`{U}}</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>{\\`U}</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>\\`{U}</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>\\`U</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>{\\'{U}}</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>{\\'U}</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>\\'{U}</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>\\'U</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>{\\^{U}}</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>{\\^U}</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>\\^{U}</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>\\^U</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>{\\"{U}}</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>{\\"U}</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>\\"{U}</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>\\"U</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>{\\'{Y}}</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>{\\'Y}</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>\\'{Y}</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>\\'Y</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>{\\ss}</with>
			<replace>&#x00df;</replace>
		</replacement>
		<replacement>
			<with>{\\`{a}}</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>{\\`a}</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>\\`{a}</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>\\`a</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>{\\'{a}}</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>{\\'a}</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>\\'{a}</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>\\'a</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>{\\^{a}}</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>{\\^a}</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>\\^{a}</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>\\^a</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>{\\~{a}}</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>{\\~a}</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>\\~{a}</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>\\~a</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>{\\"{a}}</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>{\\"a}</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>\\"{a}</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>\\"a</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>{\\aa}</with>
			<replace>&#x00e5;</replace>
		</replacement>
		<replacement>
			<with>{\\ae}</with>
			<replace>&#x00e6;</replace>
		</replacement>
		<replacement>
			<with>{\\c{c}}</with>
			<replace>&#x00e7;</replace>
		</replacement>
		<replacement>
			<with>\\c{c}</with>
			<replace>&#x00e7;</replace>
		</replacement>
		<replacement>
			<with>\\c c</with>
			<replace>&#x00e7;</replace>
		</replacement>
		<replacement>
			<with>{\\`{e}}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>{\\`e}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>{\\` e}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>\\`{e}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>\\`e</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>{\\'{e}}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>{\\'e}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>{\\' e}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>\\'{e}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>\\'e</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>{\\^{e}}</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>{\\^e}</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>\\^{e}</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>\\^e</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>{\\"{e}}</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>{\\"e}</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>\\"{e}</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>\\"e</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>{\\`{\\i}}</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>{\\`\\i}</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>\\`{\\i}</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>\\`\\i</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>{\\'{\\i}}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\'\\i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'{\\i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'\\i</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\'{i}}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\'i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'{i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'i</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\^{\\i}}</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>{\\^\\i}</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>\\^{\\i}</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>\\^\\i</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>{\\"{\\i}}</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>{\\"\\i}</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>\\"{\\i}</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>\\"\\i</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>{\\~{n}}</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>\\~{n}</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>{\\~n}</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>\\~n</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>{\\`{o}}</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>{\\`o}</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>\\`{o}</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>\\`o</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>{\\'{o}}</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>{\\'o}</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>\\'{o}</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>\\'o</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>{\\^{o}}</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>{\\^o}</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>\\^{o}</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>\\^o</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>{\\~{o}}</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>{\\~o}</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>\\~{o}</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>\\~o</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>{\\"{o}}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>{\\"o}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>{\\" o}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>\\"{o}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>\\"o</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>{\\o}</with>
			<replace>&#x00f8;</replace>
		</replacement>
		<replacement>
			<with>{\\o }</with>
			<replace>&#x00f8;</replace>
		</replacement>
		<replacement>
			<with>{\\`{u}}</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>{\\`u}</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>\\`{u}</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>\\`u</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>{\\'{u}}</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>{\\'u}</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>\\'{u}</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>\\'u</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>{\\^{u}}</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>{\\^u}</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>\\^{u}</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>\\^u</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>{\\"{u}}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>{\\"u}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>{\\" u}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>\\"{u}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>\\"u</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>{\\'{y}}</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>{\\'y}</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>\\'{y}</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>\\'y</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>{\\th}</with>
			<replace>&#x00fe;</replace>
		</replacement>
		<replacement>
			<with>{\\"{y}}</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>{\\"y}</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>\\"{y}</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>\\"y</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>{\\'{C}}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>\\'{C}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>{\\' C}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>{\\'C}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>\\'C</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>{\\'{c}}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>\\'{c}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>{\\' c}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>{\\'c}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>\\'c</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>\\`c</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>{\\v{C}}</with>
			<replace>&#x010c;</replace>
		</replacement>
		<replacement>
			<with>\\v{C}</with>
			<replace>&#x010c;</replace>
		</replacement>
		<replacement>
			<with>{\\v C}</with>
			<replace>&#x010c;</replace>
		</replacement>
		<replacement>
			<with>{\\v{c}}</with>
			<replace>&#x010d;</replace>
		</replacement>
		<replacement>
			<with>\\v{c}</with>
			<replace>&#x010d;</replace>
		</replacement>
		<replacement>
			<with>{\\v c}</with>
			<replace>&#x010d;</replace>
		</replacement>
		<replacement>
			<with>{\\u{g}}</with>
			<replace>&#x011f;</replace>
		</replacement>
		<replacement>
			<with>\\u{g}</with>
			<replace>&#x011f;</replace>
		</replacement>
		<replacement>
			<with>{\\u g}</with>
			<replace>&#x011f;</replace>
		</replacement>
		<replacement>
			<with>{\\u{\\i}}</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>{\\u\\i}</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>\\u{\\i}</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>\\u\\i</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>{\\i}</with>
			<replace>&#x0131;</replace>
		</replacement>
		<replacement>
			<with>{\\L}</with>
			<replace>&#x0141;</replace>
		</replacement>
		<replacement>
			<with>{\\l}</with>
			<replace>&#x0142;</replace>
		</replacement>
		<replacement>
			<with>\\l{}</with>
			<replace>&#x0142;</replace>
		</replacement>

		<replacement>
			<with>{\\'{N}}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>\\'{N}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>{\\' N}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>{\\'N}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>\\'N</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>{\\'{n}}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>\\'{n}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>{\\' n}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>{\\'n}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>\\'n</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>{\\OE}</with>
			<replace>&#x0152;</replace>
		</replacement>
		<replacement>
			<with>{\\oe}</with>
			<replace>&#x0153;</replace>
		</replacement>
		<replacement>
			<with>{\\v{r}}</with>
			<replace>&#x0159;</replace>
		</replacement>
		<replacement>
			<with>\\v{r}</with>
			<replace>&#x0159;</replace>
		</replacement>
		<replacement>
			<with>{\\v r}</with>
			<replace>&#x0159;</replace>
		</replacement>
		<replacement>
			<with>{\\'{S}}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>\\'{S}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>{\\' S}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>{\\'S}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>\\'S</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>{\\'{s}}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>\\'{s}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>{\\' s}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>{\\'s}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>\\'s</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>\\'s</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>{\\c{S}}</with>
			<replace>&#x015e;</replace>
		</replacement>
		<replacement>
			<with>\\c{S}</with>
			<replace>&#x015e;</replace>
		</replacement>
		<replacement>
			<with>{\\c{s}}</with>
			<replace>&#x015f;</replace>
		</replacement>
		<replacement>
			<with>\\c{s}</with>
			<replace>&#x015f;</replace>
		</replacement>
		<replacement>
			<with>{\\v{S}}</with>
			<replace>&#x0160;</replace>
		</replacement>
		<replacement>
			<with>\\v{S}</with>
			<replace>&#x0160;</replace>
		</replacement>
		<replacement>
			<with>{\\v S}</with>
			<replace>&#x0160;</replace>
		</replacement>
		<replacement>
			<with>{\\u{s}}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>\\u{s}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>{\\v{s}}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>\\v{s}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>{\\'{t}}</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>\\'{t}</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>{\\'t}</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>\\'t</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>{\\={u}}</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>{\\=u}</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>\\={u}</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>\\=u</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>{\\r{u}}</with>
			<replace>&#x016f;</replace>
		</replacement>
		<replacement>
			<with>\\r{u}</with>
			<replace>&#x016f;</replace>
		</replacement>
		<replacement>
			<with>{\\'{z}}</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>\\'{z}</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>{\\'z}</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>\\'z</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>\\'z</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>{\\.{Z}}</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>\\.{Z}</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>{\\.Z}</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>\\.Z</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>{\\.{z}}</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>\\.{z}</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>{\\.z}</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>\\.z</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>{\\v{Z}}</with>
			<replace>&#x017d;</replace>
		</replacement>
		<replacement>
			<with>\\v{Z}</with>
			<replace>&#x017d;</replace>
		</replacement>
		<replacement>
			<with>{\\v Z}</with>
			<replace>&#x017d;</replace>
		</replacement>
		<replacement>
			<with>{\\v{z}}</with>
			<replace>&#x017e;</replace>
		</replacement>
		<replacement>
			<with>\\v{z}</with>
			<replace>&#x017e;</replace>
		</replacement>
		<replacement>
			<with>{\\v z}</with>
			<replace>&#x017e;</replace>
		</replacement>
		<replacement>
			<with>{\\c{e}}</with>
			<replace>&#x0229;</replace>
		</replacement>
		<replacement>
			<with>\\c{e}</with>
			<replace>&#x0229;</replace>
		</replacement>
		<replacement>
			<with>{\\v{A}}</with>
			<replace>&#x01cd;</replace>
		</replacement>
		<replacement>
			<with>\\v{A}</with>
			<replace>&#x01cd;</replace>
		</replacement>
		<replacement>
			<with>{\\v A}</with>
			<replace>&#x01cd;</replace>
		</replacement>
		<replacement>
			<with>{\\v{a}}</with>
			<replace>&#x01ce;</replace>
		</replacement>
		<replacement>
			<with>\\v{a}</with>
			<replace>&#x01ce;</replace>
		</replacement>
		<replacement>
			<with>{\\v a}</with>
			<replace>&#x01ce;</replace>
		</replacement>

		<!-- Escaping of " -->
		<replacement>
			<with>{"}</with>
			<replace>&#x0022;</replace>
		</replacement>

		<!--<replacement><with>{\\_}</with>
			<replace>&#x005f;</replace>
		</replacement>
		<replacement><with>\\_</with>
			<replace>&#x005f;</replace>
		</replacement>
		<replacement>
			<with>\\hspace{0 cm}</with>
			<replace></replace>
		</replacement>
		<replacement>
			<with>\\hspace{0 pt}</with>
			<replace></replace>
		</replacement>
		<replacement><with>{\\#}</with>
					<replace>&#x0023;</replace>
				</replacement>
		<replacement><with>\\#</with>
					<replace>&#x0023;</replace>
				</replacement>
		<replacement><with>{\\$}</with>
					<replace>\&#x0024;</replace>
				</replacement>
		<replacement><with>\\$</with>
					<replace>\&#x0024;</replace>
				</replacement>
		<replacement><with>{\\%}</with>
					<replace>&#x0025;</replace>
				</replacement>
		<replacement><with>\\%</with>
					<replace>&#x0025;</replace>
				</replacement>
		<replacement><with>{\\&amp}</with>
					<replace>&#x0026;</replace>
				</replacement>
		<replacement><with>\\&amp;</with>
			<replace>&#x0026;</replace>
		</replacement>-->
	</xsl:variable>
	
	<xsl:function name="func:texString">
		<xsl:param name="str"/>
		<xsl:param name="counter"/>
		
		<xsl:choose>
			<xsl:when test="$counter &gt; count($entities/*)">
				<xsl:value-of select="$str"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="func:texString(replace($str, $entities/*[$counter]/replace, $entities/*[$counter]/with), $counter + 1)"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>

</xsl:stylesheet>