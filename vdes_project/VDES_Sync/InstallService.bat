set SYNCDIR=E:\Codes\NMC_Sync
set JSEXE=%SYNCDIR%\service\JavaService.exe
set CP=%SYNCDIR%\bin;%SYNCDIR%\lib\guice-1.0.jar;%SYNCDIR%\lib\endorsed\xercesImpl-2.6.2.jar;%SYNCDIR%\lib\endorsed\xml-apis-2.6.2.jar;%SYNCDIR%\lib\activation.jar;%SYNCDIR%\lib\axis-ant.jar;%SYNCDIR%\lib\axis.jar;%SYNCDIR%\lib\bsf.jar;%SYNCDIR%\lib\castor-0.9.5.2.jar;%SYNCDIR%\lib\commons-codec-1.2.jar;%SYNCDIR%\lib\commons-discovery-0.2.jar;%SYNCDIR%\lib\commons-httpclient-3.0-rc2.jar;%SYNCDIR%\lib\commons-logging-1.0.4.jar;%SYNCDIR%\lib\commons-net-1.0.0-dev.jar;%SYNCDIR%\lib\httpunit.jar;%SYNCDIR%\lib\ibmjsse.jar;%SYNCDIR%\lib\javax.jms.jar;%SYNCDIR%\lib\jaxrpc.jar;%SYNCDIR%\lib\JimiProClasses.jar;%SYNCDIR%\lib\junit-3.8.1.jar;%SYNCDIR%\lib\log4j-1.2.14.jar;%SYNCDIR%\lib\mailapi_1_3_1.jar;%SYNCDIR%\lib\saaj.jar;%SYNCDIR%\lib\servlet.jar;%SYNCDIR%\lib\wsdl4j-1.5.1.jar;%SYNCDIR%\lib\sqljdbc.jar;%SYNCDIR%\lib\msbase.jar;%SYNCDIR%\lib\mssqlserver.jar;%SYNCDIR%\lib\msutil.jar;%SYNCDIR%\lib\commons-lang-2.0.jar;%SYNCDIR%\lib\hibernate3.jar;%SYNCDIR%\lib\dom4j-1.6.1.jar;%SYNCDIR%\lib\commons-collections-3.1.jar;%SYNCDIR%\lib\cglib-2.1.3.jar;%SYNCDIR%\lib\asm.jar;%SYNCDIR%\lib\jta.jar
%JSEXE% -install NMCSyncService "D:\Program Files\Java\jdk1.5\jre\bin\server\jvm.dll" -Djava.class.path=%CP% -start proc.gmcc.sso.MainSync -out %SYNCDIR%\std\stdout.log -err %SYNCDIR%\std\stderr.log -current %SYNCDIR% -auto -description "NMC Sync Service"