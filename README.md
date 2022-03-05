#CAP - Useful Commands

###### Create a CAP project using CAP Java Maven archetype 
> mvn archetype:generate -DarchetypeArtifactId=cds-services-archetype -DarchetypeGroupId=com.sap.cds -DarchetypeVersion=RELEASE </br>
or </br>
> cds init <PROJECT-ROOT> --add java

###### Add sample CDS model [optional] 
> mvn com.sap.cds:cds-maven-plugin:addSample

###### Add integration test module [optional] 
> mvn com.sap.cds:cds-maven-plugin:addIntegrationTest

###### Build and run your project 
> mvn spring-boot:run

###### Enable HANA support for project [Default support is for SQL] 
> cds add hana

###### Add the multi target deployer descriptor
> cds add mta

###### Check SQL equivalent 
> cds compile target_file.cds --to sql </br>
> cds compile target_file.cds --to hana

###### Deploy changes to DB
> cds deploy --to sqlite:my.db </br>
> cds deploy --to hana


###### Run this project
- Run with in memory sqlite
	==> mvn spring-boot:run -Dspring-boot.run.profiles=default
- Run with file based sqlite
	==> mvn spring-boot:run -Dspring-boot.run.profiles=sqlite
- Run with HANA 
	==> mvn spring-boot:run -Dspring-boot.run.profiles=cloud