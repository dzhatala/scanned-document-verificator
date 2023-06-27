@rem CALL ONLY ONCE !!!
@rem goto NO_SET_CP

set PATH=""
set PATH=j:\master_yoga\java\eclipse-java-2022-03-R-win32-x86_64\eclipse\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.2.v20220201-1208\jre\bin;%PATH%

@rem 
set CP=
set CP=C:\Users\yoga520\.m2\repository\org\apache\commons\commons-imaging\1.0-alpha3\commons-imaging-1.0-alpha3.jar
rem 
set CP=F:\rsync\RESEARCHS\text_recognition_ocr_dns_scan\opencv\opencv\build\java\opencv-460.jar;%CP%
rem 
set CP=F:\rsync\RESEARCHS\text_recognition_ocr_dns_scan\tesseract_win\tess4j_01\jar_files\*.jar;%CP%
rem 
set CP=C:\Users\yoga520\.m2\repository\org\apache\sanselan\sanselan\0.97-incubator\sanselan-0.97-incubator.jar;%CP%


set JF=F:\rsync\RESEARCHS\text_recognition_ocr_dns_scan\tesseract_win\tess4j_01\jar_files
set CP=%JF%\commons-io-2.11.0.jar;%CP%
set CP=%JF%\commons-logging-1.2.jar;%CP%
set CP=%JF%\fontbox-2.0.26.jar;%CP%
set CP=%JF%\jai-imageio-core-1.4.0.jar;%CP%
set CP=%JF%\jbig2-imageio-3.0.4.jar;%CP%
set CP=%JF%\jboss-logging-3.1.4.GA.jar;%CP%
set CP=%JF%\jboss-vfs-3.2.16.Final.jar;%CP%
set CP=%JF%\jna-5.11.0.jar;%CP%
set CP=%JF%\lept4j-1.16.1.jar;%CP%
set CP=%JF%\pdfbox-2.0.26.jar;%CP%
set CP=%JF%\pdfbox-debugger-2.0.26.jar;%CP%
set CP=%JF%\pdfbox-tools-2.0.26.jar;%CP%
set CP=%JF%\slf4j-api-1.7.36.jar;%CP%     
set CP=%JF%\tess4j-5.2.1.jar;%CP%
set CP=F:\rsync\RESEARCHS\moodle\eclipse\javaws\moodle_login_01\libs\org.apache.commons.httpclient_3.1.0.v201012070820.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\poi-5.2.2.jar;%CP%	
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\poi-ooxml-5.2.2.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\poi-ooxml-full-5.2.2.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\ooxml-lib\xmlbeans-5.0.3.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\ooxml-lib\commons-compress-1.21.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\ooxml-lib\commons-logging-1.2.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\ooxml-lib\curvesapi-1.07.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\ooxml-lib\slf4j-api-1.7.36.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\ooxml-lib\xmlbeans-5.0.3.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\lib\commons-codec-1.15.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\lib\commons-collections4-4.4.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\lib\SparseBitSet-1.2.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\lib\log4j-api-2.17.2.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\lib\commons-math3-3.6.1.jar;%CP%
set CP=F:\rsync\RESEARCHS\plagiarisms\apache_poi_word_doc2txt\poi-bin-5.2.2-20220312\poi-bin-5.2.2\lib\commons-io-2.11.0.jar;%CP%
:NO_SET_CP
@rem set CLASS_NAME=tests.rev_03_DNSChecker
@rem set CLASS_NAME=tests.rev_05
set CLASS_NAME=dnsmatch.MainApp
date /T
time /T
@rem 
java -version
java -Djava.libary.path="F:\rsync\RESEARCHS\text_recognition_ocr_dns_scan\opencv\opencv\build\java\x64" -cp bin;%CP% %CLASS_NAME% %1 %2 %3 %4
