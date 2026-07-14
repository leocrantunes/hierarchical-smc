<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.13.dtd">
<Workset version="11">
  <WorksetName>lucene-codecs-11.0.0</WorksetName>
  <Options analyze-spring-beans="no" auto-reload="no" calculate-hashes="no" include-resources="no" />
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">/home/leo/github/lucene/lucene/luke/build/lucene-luke-11.0.0-SNAPSHOT/lucene-codecs-11.0.0-SNAPSHOT.jar</ClasspathPart>
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
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.bitvectors.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.blockterms.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.blocktreeords.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.bloom.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.memory.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.simpletext.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.lucene.codecs.uniformsplit.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.lucene.*</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>