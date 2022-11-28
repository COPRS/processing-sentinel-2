echo '['
n=0
for f in $( ls -rt $(find . -name *.log))
do 
  start=$(grep -E '(IDP-SC.*start)|(Started)' $f | gawk '{print $1}')
  stop=$(grep -E '(IDP-SC.*finished)|(Finished)' $f | gawk '{print $1}')
  if [ $n == 0 ]; then
    sep=''
  else
    sep=','
  fi
  echo "$sep" '{"name":"'$(basename $f .log)'","start":"'$start'","stop":"'$stop'"}'
  n=$((n+1))
done
echo ']'
