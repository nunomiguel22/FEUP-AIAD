# FEUP-AIAD

Compile Maven and disregard SSL Errors

`mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true`

For Windows:
`mvn clean install -D maven.wagon.http.ssl.insecure=true -D maven.wagon.http.ssl.allowall=true -D maven.wagon.http.ssl.ignore.validity.dates=true`
