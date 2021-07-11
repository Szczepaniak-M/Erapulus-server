FROM mcr.microsoft.com/mssql/server:latest

USER root

COPY setup.sql setup.sql
COPY entrypoint.sh entrypoint.sh
COPY run-setup.sh run-setup.sh

RUN chmod +x entrypoint.sh
RUN chmod +x run-setup.sh

CMD /bin/bash ./entrypoint.sh

