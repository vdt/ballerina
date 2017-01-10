/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.ballerina.core.nativeimpl.functions;

import org.apache.axiom.om.OMElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.ballerina.core.interpreter.SymScope;
import org.wso2.ballerina.core.model.BallerinaFile;
import org.wso2.ballerina.core.model.values.BMap;
import org.wso2.ballerina.core.model.values.BString;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.model.values.BXML;
import org.wso2.ballerina.core.nativeimpl.lang.xml.AddAttributeWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.AddElementWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.GetStringWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.GetXMLWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.RemoveWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.SetStringWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.SetXMLWithNamespaces;
import org.wso2.ballerina.core.nativeimpl.lang.xml.ToString;
import org.wso2.ballerina.core.utils.FunctionUtils;
import org.wso2.ballerina.core.utils.ParserUtils;
import org.wso2.ballerina.core.utils.XMLUtils;
import org.wso2.ballerina.lang.util.Functions;

public class XMLWithNamespacesTest {
    private BallerinaFile bFile;
    private static String xmlMessage;
    BMap<BString, BString> namespaces = new BMap<>();



    @BeforeClass
    public void setup() {
        // Load xml
        xmlMessage = XMLUtils.readFileToString("datafiles/messageWithNamespacesSimple.xml");
        // Define namespaces
        namespaces.put(new BString("soapenv"), new BString("http://schemas.xmlsoap.org/soap/envelope/"));
        namespaces.put(new BString("m0"), new BString("http://services.samples"));
        // Add Native functions.
        SymScope symScope = new SymScope(null);
        FunctionUtils.addNativeFunction(symScope, new GetStringWithNamespaces());
        FunctionUtils.addNativeFunction(symScope, new GetXMLWithNamespaces());
        FunctionUtils.addNativeFunction(symScope, new RemoveWithNamespaces());
        FunctionUtils.addNativeFunction(symScope, new SetStringWithNamespaces());
        FunctionUtils.addNativeFunction(symScope, new SetXMLWithNamespaces());
        FunctionUtils.addNativeFunction(symScope, new ToString());
        FunctionUtils.addNativeFunction(symScope, new AddAttributeWithNamespaces());
        FunctionUtils.addNativeFunction(symScope, new AddElementWithNamespaces());

        bFile = ParserUtils.parseBalFile("samples/nativeimpl/xmlTestNamespaces.bal", symScope);
    }

    @Test
    public void testGetString() {
        BValue[] args = {new BXML(xmlMessage), new BString
                ("/m0:SampleMessage/m0:getQuote/m0:request/m0:symbol/text()"), namespaces};
        BValue[] returns = Functions.invoke(bFile, "getString", args);

        Assert.assertTrue(returns[0] instanceof BString);

        final String expected = "IBM";
        Assert.assertEquals(returns[0].stringValue(), expected);
    }

    @Test
    public void testGetXML() {
        BValue[] args = {new BXML(xmlMessage), new BString("/m0:SampleMessage/m0:getQuote"), namespaces};
        BValue[] returns = Functions.invoke(bFile, "getXML", args);

        Assert.assertTrue(returns[0] instanceof BXML);

        OMElement returnElement = ((BXML) returns[0]).value();
        Assert.assertEquals(returnElement.toString(),
                "<m0:getQuote xmlns:m0=\"http://services.samples\">\n" +
                        "            <m0:request>\n" +
                        "                <m0:symbol>IBM</m0:symbol>\n" +
                        "            </m0:request>\n" +
                        "        </m0:getQuote>");
    }

    @Test
    public void testSetString() {
        BValue[] args = {new BXML(xmlMessage), new BString("/m0:SampleMessage/m0:getQuote/m0:request/m0:symbol/text()")
                , new BString("WSO2"), namespaces};
        BValue[] returns = Functions.invoke(bFile, "setString", args);

        Assert.assertEquals(returns[0].stringValue(), "<m0:SampleMessage xmlns:m0=\"http://services.samples\">\n" +
                "        <m0:getQuote>\n" +
                "            <m0:request>\n" +
                "                <m0:symbol>WSO2</m0:symbol>\n" +
                "            </m0:request>\n" +
                "        </m0:getQuote>\n" +
                "</m0:SampleMessage>");
    }

//    @Test
//    public void testSetXML() {
//        BValue[] args = {new BXML(xmlMessage), new BString("/m0:SampleMessage/m0:getQuote/m0:request"),
//                new BXML("<m0:request xmlns:m0=\"http://services.samples\">\n" +
//                        "                <m0:symbol>WSO2</m0:symbol>\n" +
//                        "            </m0:request>"), namespaces};
//        BValue[] returns = Functions.invoke(bFile, "setXML", args);
//
//        Assert.assertTrue(returns[0] instanceof BXML);
//
//        OMElement returnElement = ((BXML) returns[0]).value();
//        Assert.assertEquals(returnElement.toString(), "<m0:SampleMessage xmlns:m0=\"http://services.samples\">\n" +
//                "        <m0:getQuote>\n" +
//                "            <m0:request>\n" +
//                "                <m0:symbol>IBM</m0:symbol>\n" +
//                "            </m0:request>\n" +
//                "        </m0:getQuote>\n" +
//                "</m0:SampleMessage>");
//    }

//    @Test
//    public void testAddElement() {
//        BValue[] args = {new BXML(s2), new BString("/person"), new BXML("<address>wso2</address>")};
//        BValue[] returns = Functions.invoke(bFile, "addElement", args);
//
//        Assert.assertEquals(returns[0].stringValue(), "<person><name>Jack</name>" +
//                "<address>wso2</address></person>");
//    }
//
//    @Test
//    public void testAddAttribute() {
//        BValue[] args = {new BXML(s2), new BString("/person/name"), new BString("id"), new BString("person123")};
//        BValue[] returns = Functions.invoke(bFile, "addAttribute", args);
//
//        Assert.assertTrue(returns[0] instanceof BXML);
//
//        OMElement returnElement = ((BXML) returns[0]).value();
//        Assert.assertEquals(returnElement.toString(), "<person><name id=\"person123\">Jack</name></person>");
//    }
//
//    @Test
//    public void testGetXMLLarge() {
//        BMap<BString, BString> namespaces = new BMap<>();
//        namespaces.put(new BString("soapenv"), new BString("http://schemas.xmlsoap.org/soap/envelope/"));
//        namespaces.put(new BString("m0"), new BString("http://services.samples"));
//        BValue[] args = {new BXML(l1),
//                new BString("/soapenv:Envelope/soapenv:Body/m0:getQuote/m0:request/m0:symbol/text()"), namespaces};
//        BValue[] returns = Functions.invoke(bFile, "getXML", args);
//
//        Assert.assertTrue(returns[0] instanceof BString);
//
//        //OMElement returnElement = ((BXML) returns[0]).value();
////        Assert.assertEquals(returnElement.toString().replaceAll("\\r|\\n|\\t| ", ""), "<m0:getQuote xmlns:m0=" +
////                "\"http://services.samples\"><m0:request>\n" +
////                "                <m0:symbol>WSO2</m0:symbol>\n" +
////                "            </m0:request>\n" +
////                "        </m0:getQuote>");
//        Assert.assertEquals(((BString) returns[0]).stringValue(), "IBM");
//    }
//
//    @Test
//    public void testRemove() {
//        BValue[] args = {new BXML(s1), new BString("/persons/person/address")};
//        BValue[] returns = Functions.invoke(bFile, "remove", args);
//
//        Assert.assertTrue(returns[0] instanceof BXML);
//
//        OMElement returnElement = ((BXML) returns[0]).value();
//        Assert.assertEquals(returnElement.toString().replaceAll("\\r|\\n|\\t| ", ""), "<persons><person><name>Jack" +
//                "</name></person></persons>");
//    }
//
//
//    @Test(expectedExceptions = BallerinaException.class)
//    public void testGetNonExistingString() {
//        BValue[] args = {new BXML(s1), new BString("/xxx/text()")};
//        BValue[] returns = Functions.invoke(bFile, "getString", args);
//
//        Assert.assertEquals(returns[0], null);
//    }
//
//    @Test(expectedExceptions = BallerinaException.class)
//    public void testGetNonExistingXML() {
//        BValue[] args = {new BXML(s1), new BString("/xxx")};
//        BValue[] returns = Functions.invoke(bFile, "getXML", args);
//
//        Assert.assertEquals(returns[0], null);
//    }
//
//    @Test
//    public void testSetStringToNonExistingElement() {
//        BValue[] args = {new BXML(s1), new BString("/xxx/text()"), new BString("Peter")};
//        BValue[] returns = Functions.invoke(bFile, "setString", args);
//
//        Assert.assertEquals(returns[0].stringValue(), s1);
//    }
//
//    @Test
//    public void testSetXMLToNonExistingElement() {
//        BValue[] args = {new BXML(s2), new BString("/xxx"),
//                new BXML("<name><fname>Jack</fname><lname>Peter</lname></name>")};
//        BValue[] returns = Functions.invoke(bFile, "setXML", args);
//
//        Assert.assertTrue(returns[0] instanceof BXML);
//
//        OMElement returnElement = ((BXML) returns[0]).value();
//        Assert.assertEquals(returnElement.toString().replaceAll("\\r|\\n|\\t| ", ""), s2);
//    }
//
//    @Test(expectedExceptions = BallerinaException.class)
//    public void testAddElementToNonExistingElement() {
//        BValue[] args = {new BXML(s2), new BString("/xxx"), new BXML("<address>wso2</address>")};
//        BValue[] returns = Functions.invoke(bFile, "addElement", args);
//
//        Assert.assertEquals(returns[0].stringValue(), s2);
//    }
//
//    @Test(expectedExceptions = BallerinaException.class)
//    public void testAddAttributeToNonExistingElement() {
//        BValue[] args = {new BXML(s2), new BString("/xxx"), new BString("id"), new BString("person123")};
//        BValue[] returns = Functions.invoke(bFile, "addAttribute", args);
//
//        Assert.assertTrue(returns[0] instanceof BXML);
//
//        OMElement returnElement = ((BXML) returns[0]).value();
//        Assert.assertEquals(returnElement.toString(), s2);
//    }
//
//    @Test
//    public void testRemoveNonExistingElement() {
//        BValue[] args = {new BXML(s1), new BString("/xxx")};
//        Functions.invoke(bFile, "remove", args);
//    }
//
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testGetStringFromMalformedXpath() {
//        BValue[] args = {new BXML(s1), new BString("$worng#path")};
//        Functions.invoke(bFile, "getString", args);
//    }
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testGetXMLFromMalformedXpath() {
//        BValue[] args = {new BXML(s1), new BString("$worng#path")};
//        Functions.invoke(bFile, "getXML", args);
//    }
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testSetStringToMalformedXpath() {
//        BValue[] args = {new BXML(s1), new BString("$worng#path"), new BString("Peter")};
//        Functions.invoke(bFile, "setString", args);
//    }
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testSetXMLToMalformedXpath() {
//        BValue[] args = {new BXML(s2), new BString("$worng#path"),
//                new BXML("<name><fname>Jack</fname><lname>Peter</lname></name>")};
//        Functions.invoke(bFile, "setXML", args);
//    }
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testAddElementToMalformedXpath() {
//        BValue[] args = {new BXML(s2), new BString("$worng#path"), new BXML("<address>wso2</address>")};
//        Functions.invoke(bFile, "addElement", args);
//    }
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testAddAttributeToMalformedXpath() {
//        BValue[] args = {new BXML(s2), new BString("$worng#path"), new BString("id"), new BString("person123")};
//        Functions.invoke(bFile, "addAttribute", args);
//    }
//
//    @Test(expectedExceptions = {BallerinaException.class})
//    public void testRemoveFromMalformedXpath() {
//        BValue[] args = {new BXML(s1), new BString("$worng#path")};
//        Functions.invoke(bFile, "remove", args);
//    }
//
}