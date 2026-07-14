<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.13.dtd">
<Workset version="11">
  <WorksetName>log4j-1.2-api-2.21.1</WorksetName>
  <Options analyze-spring-beans="no" auto-reload="no" calculate-hashes="no" include-resources="no" />
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">/home/leo/repos/logging-log4j2-rel-2.21.1/log4j-1.2-api/target/log4j-1.2-api-2.21.1.jar</ClasspathPart>
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
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.util.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.spi.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.Logger</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.message.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.Level</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.core.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.Marker</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.status.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.LogManager</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.ThreadContext</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.logging.log4j.ThreadContext$ContextStack</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>