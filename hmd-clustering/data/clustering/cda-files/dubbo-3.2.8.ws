<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.13.dtd">
<Workset version="11">
  <WorksetName>dubbo-cluster-3.2.8</WorksetName>
  <Options analyze-spring-beans="no" auto-reload="no" calculate-hashes="no" include-resources="no" />
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">/home/leo/repos/dubbo-dubbo-3.2.8/dubbo-cluster/target/dubbo-cluster-3.2.8.jar</ClasspathPart>
  </Classpath>
  <ViewFilters>
    <PatternFilter active="yes" exclude="no">java.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">javax.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">com.sun.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.xml.sax*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.omg.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.w3c.dom.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.yaml.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.common.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.metrics.*</PatternFilter>
  </ViewFilters>
  <IgnoreFilters>
    <PatternFilter active="yes" exclude="no">java.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">javax.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">com.sun.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.xml.sax*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.omg.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.w3c.dom.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.yaml.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.common.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.metrics.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.rpc.model.*</PatternFilter>
    <PatternFilter active="yes" exclude="yes">org.apache.dubbo.rpc.cluster.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.rpc.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.config.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">io.micrometer.*</PatternFilter>
    <PatternFilter active="yes" exclude="no">org.apache.dubbo.remoting.*</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>