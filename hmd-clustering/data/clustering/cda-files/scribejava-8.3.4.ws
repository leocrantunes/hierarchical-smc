<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.13.dtd">
<Workset version="11">
  <WorksetName>scribejava-8.3.4</WorksetName>
  <Options analyze-spring-beans="no" auto-reload="no" calculate-hashes="no" include-resources="no" />
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">/home/leo/github/scribejava/scribejava-apis/target/scribejava-apis-8.3.4-SNAPSHOT.jar</ClasspathPart>
  </Classpath>
  <ViewFilters>
    <PatternFilter active="yes" exclude="no">java.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">javax.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">com.sun.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.xml.sax*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.omg.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.w3c.dom.*</PatternFilter>
  </ViewFilters>
  <IgnoreFilters>
    <PatternFilter active="yes" exclude="no">java.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">javax.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">com.sun.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.xml.sax*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.omg.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.w3c.dom.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">com.github.scribejava.apis.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">*</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>