<?xml version="1.0" encoding="utf-8"?>
<wsdl:definitions xmlns:tm="http://microsoft.com/wsdl/mime/textMatching/" xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/" xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/" xmlns:tns="http://tempuri.org/" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:s="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://schemas.xmlsoap.org/wsdl/soap12/" xmlns:http="http://schemas.xmlsoap.org/wsdl/http/" targetNamespace="http://tempuri.org/" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/">
  <wsdl:types>
    <s:schema elementFormDefault="qualified" targetNamespace="http://tempuri.org/">
      <s:element name="GetCode">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="func_code" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetCodeResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GetCodeResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetMessage">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="a0190" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetMessageResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GetMessageResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetShortMessage">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="a0190" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetShortMessageResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GetShortMessageResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetToken">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="UserCode" type="s:string" />
            <s:element minOccurs="1" maxOccurs="1" name="ExpirDate" type="s:int" />
          </s:sequence>
        </s:complexType>
      </s:element>
      <s:element name="GetTokenResponse">
        <s:complexType>
          <s:sequence>
            <s:element minOccurs="0" maxOccurs="1" name="GetTokenResult" type="s:string" />
          </s:sequence>
        </s:complexType>
      </s:element>
    </s:schema>
  </wsdl:types>
  <wsdl:message name="GetCodeSoapIn">
    <wsdl:part name="parameters" element="tns:GetCode" />
  </wsdl:message>
  <wsdl:message name="GetCodeSoapOut">
    <wsdl:part name="parameters" element="tns:GetCodeResponse" />
  </wsdl:message>
  <wsdl:message name="GetMessageSoapIn">
    <wsdl:part name="parameters" element="tns:GetMessage" />
  </wsdl:message>
  <wsdl:message name="GetMessageSoapOut">
    <wsdl:part name="parameters" element="tns:GetMessageResponse" />
  </wsdl:message>
  <wsdl:message name="GetShortMessageSoapIn">
    <wsdl:part name="parameters" element="tns:GetShortMessage" />
  </wsdl:message>
  <wsdl:message name="GetShortMessageSoapOut">
    <wsdl:part name="parameters" element="tns:GetShortMessageResponse" />
  </wsdl:message>
  <wsdl:message name="GetTokenSoapIn">
    <wsdl:part name="parameters" element="tns:GetToken" />
  </wsdl:message>
  <wsdl:message name="GetTokenSoapOut">
    <wsdl:part name="parameters" element="tns:GetTokenResponse" />
  </wsdl:message>
  <wsdl:portType name="GetFunc_codeSoap">
    <wsdl:operation name="GetCode">
      <wsdl:input message="tns:GetCodeSoapIn" />
      <wsdl:output message="tns:GetCodeSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="GetMessage">
      <wsdl:input message="tns:GetMessageSoapIn" />
      <wsdl:output message="tns:GetMessageSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="GetShortMessage">
      <wsdl:input message="tns:GetShortMessageSoapIn" />
      <wsdl:output message="tns:GetShortMessageSoapOut" />
    </wsdl:operation>
    <wsdl:operation name="GetToken">
      <wsdl:input message="tns:GetTokenSoapIn" />
      <wsdl:output message="tns:GetTokenSoapOut" />
    </wsdl:operation>
  </wsdl:portType>
  <wsdl:binding name="GetFunc_codeSoap" type="tns:GetFunc_codeSoap">
    <soap:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="GetCode">
      <soap:operation soapAction="http://tempuri.org/GetCode" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GetMessage">
      <soap:operation soapAction="http://tempuri.org/GetMessage" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GetShortMessage">
      <soap:operation soapAction="http://tempuri.org/GetShortMessage" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GetToken">
      <soap:operation soapAction="http://tempuri.org/GetToken" style="document" />
      <wsdl:input>
        <soap:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:binding name="GetFunc_codeSoap12" type="tns:GetFunc_codeSoap">
    <soap12:binding transport="http://schemas.xmlsoap.org/soap/http" />
    <wsdl:operation name="GetCode">
      <soap12:operation soapAction="http://tempuri.org/GetCode" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GetMessage">
      <soap12:operation soapAction="http://tempuri.org/GetMessage" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GetShortMessage">
      <soap12:operation soapAction="http://tempuri.org/GetShortMessage" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
    <wsdl:operation name="GetToken">
      <soap12:operation soapAction="http://tempuri.org/GetToken" style="document" />
      <wsdl:input>
        <soap12:body use="literal" />
      </wsdl:input>
      <wsdl:output>
        <soap12:body use="literal" />
      </wsdl:output>
    </wsdl:operation>
  </wsdl:binding>
  <wsdl:service name="GetFunc_code">
    <wsdl:port name="GetFunc_codeSoap" binding="tns:GetFunc_codeSoap">
      <soap:address location="http://hrs.eyasgloble.com:8081/ws/GetFunc_Code.asmx" />
    </wsdl:port>
    <wsdl:port name="GetFunc_codeSoap12" binding="tns:GetFunc_codeSoap12">
      <soap12:address location="http://hrs.eyasgloble.com:8081/ws/GetFunc_Code.asmx" />
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>