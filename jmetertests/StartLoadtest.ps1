$Reportpath = ".\Report"
$CurrentReportPath = Get-Date -Format "MMddyyyy_HHmmss"
If(!(test-path $Reportpath))
{
      New-Item -ItemType Directory -Force -Path $Reportpath
}
cd $Reportpath
If(!(test-path $CurrentReportPath))
{
      New-Item -ItemType Directory -Force -Path $CurrentReportPath
}
cd $CurrentReportPath
jmeter -p ..\..\test.properties -n -t ..\..\Loadtest.jmx -l log.jtl

jmeter -g log.jtl -o graphs