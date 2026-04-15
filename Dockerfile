FROM tomcat:9.0-jre11-slim

# Remove default tomcat applications to keep it clean
RUN rm -rf /usr/local/tomcat/webapps/*

# Create our application directory
RUN mkdir -p /usr/local/tomcat/webapps/iomt

# Copy the compiled classes and libraries
COPY web/WEB-INF/classes /usr/local/tomcat/webapps/iomt/WEB-INF/classes
COPY web/WEB-INF/lib /usr/local/tomcat/webapps/iomt/WEB-INF/lib

# Copy JSP, CSS, JS configuration files
COPY web/css /usr/local/tomcat/webapps/iomt/css
COPY web/js /usr/local/tomcat/webapps/iomt/js
COPY web/*.jsp /usr/local/tomcat/webapps/iomt/
COPY web/WEB-INF/web.xml /usr/local/tomcat/webapps/iomt/WEB-INF/web.xml

# Expose port 8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
