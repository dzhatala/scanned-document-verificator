@rem CALL ONLY ONCE !!!
@rem goto NO_SET_CP

set PATH=""
set PATH=j:\master_yoga\java\eclipse-java-2022-03-R-win32-x86_64\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.2.v20220201-1208\jre\bin;%PATH%

@rem 
set CP=
set CP=portable_jars\commons-imaging-1.0-alpha3.jar
rem 


set CP=portable_jars\commons-codec-1.15.jar;%CP%
set CP=portable_jars\commons-collections4-4.4.jar;%CP%
set CP=portable_jars\commons-compress-1.21.jar;%CP%
set CP=portable_jars\commons-io-2.11.0.jar;%CP%
set CP=portable_jars\commons-logging-1.2.jar;%CP%
set CP=portable_jars\commons-math3-3.6.1.jar;%CP%
set CP=portable_jars\curvesapi-1.07.jar;%CP%
set CP=portable_jars\fontbox-2.0.26.jar;%CP%
set CP=portable_jars\jai-imageio-core-1.4.0.jar;%CP%
set CP=portable_jars\jbig2-imageio-3.0.4.jar;%CP%
set CP=portable_jars\jboss-logging-3.1.4.GA.jar;%CP%
set CP=portable_jars\jboss-vfs-3.2.16.Final.jar;%CP%
set CP=portable_jars\jna-5.11.0.jar;%CP%
set CP=portable_jars\lept4j-1.16.1.jar;%CP%
set CP=portable_jars\log4j-api-2.17.2.jar;%CP%
set CP=portable_jars\opencv-460.jar;%CP%
set CP=portable_jars\org.apache.commons.httpclient_3.1.0.v201012070820.jar;%CP%
set CP=portable_jars\pdfbox-2.0.26.jar;%CP%
set CP=portable_jars\pdfbox-debugger-2.0.26.jar;%CP%
set CP=portable_jars\pdfbox-tools-2.0.26.jar;%CP%
set CP=portable_jars\poi-5.2.2.jar;%CP%
set CP=portable_jars\poi-ooxml-5.2.2.jar;%CP%
set CP=portable_jars\poi-ooxml-full-5.2.2.jar;%CP%
set CP=portable_jars\slf4j-api-1.7.36.jar;%CP%    
set CP=portable_jars\SparseBitSet-1.2.jar;%CP%
set CP=portable_jars\tess4j-5.2.1.jar;%CP%
set CP=portable_jars\xmlbeans-5.0.3.jar;%CP%


:NO_SET_CP
@rem set CLASS_NAME=tests.rev_03_DNSChecker
@rem set CLASS_NAME=tests.rev_05
set CLASS_NAME=dnsmatch.MainApp
date /T
time /T
@rem 
java -version

java -Djava.libary.path="F:\rsync\RESEARCHS\text_recognition_ocr_dns_scan\opencv\opencv\build\java\x64" -cp bin;%CP% %CLASS_NAME% %1 %2 %3 %4
