<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.13.dtd">
<Workset version="11">
  <WorksetName>lucene-luke-11.0.0</WorksetName>
  <Options analyze-spring-beans="no" auto-reload="no" calculate-hashes="no" include-resources="yes" />
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">/home/leo/github/lucene/lucene/**/*.jar</ClasspathPart>
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
    <PatternFilter active="no" exclude="no">java.*</PatternFilter>
    <PatternFilter active="no" exclude="no">javax.*</PatternFilter>
    <PatternFilter active="no" exclude="no">com.sun.*</PatternFilter>
    <PatternFilter active="no" exclude="no">org.xml.sax*</PatternFilter>
    <PatternFilter active="no" exclude="no">org.omg.*</PatternFilter>
    <PatternFilter active="no" exclude="no">org.w3c.dom.*</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>