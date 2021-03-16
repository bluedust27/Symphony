$influxDBPath = ".\InfluxDB\influxdb-1.7.8-1"
$grafanaPath= "..\..\Grafana\grafana-7.0.1\bin"

cd $influxDBPath
Start-Process -FilePath ".\influxd.exe" -ArgumentList '-config .\influxdb.conf'
cd $grafanaPath
Start-Process -FilePath ".\grafana-server.exe"

Start-Process "http://localhost:4001/"
