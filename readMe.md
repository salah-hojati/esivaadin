vaddin samples


http://localhost:8086/h2-console


3. Configure the ConnectionYou will see a login page. This is the most important step. You must change the JDBC URL to match the one in your application.properties file exactly.Fill in the fields like this:•Driver Class: org.h2.Driver•JDBC URL: jdbc:h2:file:./data/testdb  (This must match your properties file)•User Name: sa•Password: (leave this blank)
