## Custom Dockerfile
FROM consol/ubuntu-xfce-vnc
ENV REFRESHED_AT 2018-03-18

## Install JRE
USER 0
RUN apt-get update \
	&& apt-get -y install openjdk-9-jre \
    && apt-get clean
## switch back to default user
USER 1000
