<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.13.dtd">
<Workset version="11">
  <WorksetName>aep-core-0.10.0</WorksetName>
  <Options analyze-spring-beans="no" auto-reload="no" calculate-hashes="no" include-resources="no" />
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">/home/leo/repos/appengine-plugins-core-0.10.0/target/appengine-plugins-core-0.10.0.jar</ClasspathPart>
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
    <PatternFilter active="yes" exclude="no">com.google.cloud.tools.project.package-info</PatternFilter>
    <PatternFilter active="yes" exclude="no">com.google.common.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">com.google.gson.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.yaml.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.*</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>