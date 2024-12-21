FROM nginx:alpine
RUN chown -R spring:spring /usr/share/nginx/html
USER spring:spring
COPY default.conf /etc/nginx/conf.d/default.conf