FROM mcr.microsoft.com/mssql/server:2017-latest-ubuntu

ENV ACCEPT_EULA Y
ENV SA_PASSWORD "yourStrong(!)Password"
ENV MSSQL_PID "Express"

COPY startdb.sh /
COPY mssql-setup.sql /

CMD /bin/bash -c /startdb.sh