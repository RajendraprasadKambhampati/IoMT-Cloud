FROM tomcat:9.0-jre11-slim

# Remove default Tomcat apps (like the welcome page)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy our entire web folder into Tomcat's ROOT directory
# This ensures your app is at the main URL (e.g., https://yourdomain.com/) instead of /iomt/
COPY web /usr/local/tomcat/webapps/ROOT/

# Expose port 8080
EXPOSE 8080

CMD ["catalina.sh", "run"]
