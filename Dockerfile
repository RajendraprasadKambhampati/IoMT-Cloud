# ============================================
# Stage 1: Compile Java source code
# ============================================
FROM tomcat:9.0-jdk11 AS builder

# Create working directories
RUN mkdir -p /build/classes

# Copy the MySQL connector JAR (needed for compilation)
COPY web/WEB-INF/lib /build/lib

# Copy Java source code
COPY src/java /build/src

# Compile ALL Java files in one shot
RUN javac \
    -cp "/usr/local/tomcat/lib/servlet-api.jar:/build/lib/*" \
    -d /build/classes \
    -sourcepath /build/src \
    $(find /build/src -name "*.java")

# ============================================
# Stage 2: Deploy to Tomcat
# ============================================
FROM tomcat:9.0-jre11-slim

# Remove default Tomcat welcome apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Create the ROOT application directory structure
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/lib

# Copy compiled Java classes from builder stage
COPY --from=builder /build/classes /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/

# Copy MySQL connector JAR
COPY web/WEB-INF/lib /usr/local/tomcat/webapps/ROOT/WEB-INF/lib/

# Copy web.xml
COPY web/WEB-INF/web.xml /usr/local/tomcat/webapps/ROOT/WEB-INF/web.xml

# Copy db.properties into classes folder (classpath)
COPY db.properties /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/db.properties

# Copy all JSP pages
COPY web/*.jsp /usr/local/tomcat/webapps/ROOT/

# Copy CSS and JS
COPY web/css /usr/local/tomcat/webapps/ROOT/css/
COPY web/js /usr/local/tomcat/webapps/ROOT/js/

# Expose port (Railway will auto-detect this)
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
