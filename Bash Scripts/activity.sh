#!/bin/bash

# files: activity.sh
# author: Quoc-Bao Huynh
# course: csi 3336
# due date:9/15/16
#
# date modified   9/14/15
#   -file created
#
# This script looks at modification date on files and 
# categorizes them into active, recent, and idle
# depending on when they were modified. It also outputs
# the size of the files together



#error check
if [[ $# -ne 1 ]]
then
   echo activity.sh "[directory]"
   exit
elif [[ ! -d $1 ]]
then
   echo activity.sh "[directory]"
   exit 
fi

#output directory
echo "$1"

#24 hours
total24bytes=0
bytes=0
numfiles1=0
filenames1=($(find $1 -type f -mtime -1))
#go through file names and calculate size
for i in ${filenames1[@]}
do
   bytes=($(du -cb $i))
   numfiles1=$(($numfiles1+1))
   total24bytes=$(($total24bytes+$bytes))	
done

#print out results for 24 hours
echo active: $numfiles1 "("$total24bytes")"

#3days
total3bytes=0
bytes=0
numfiles2=0
filenames2=($(find $1 -type f -mtime -3))

#go through file names and calculate size
for u in ${filenames2[@]}
do
   
   bytes=($(du -cb $u))
   numfiles2=$(($numfiles2+1)) 
   total3bytes=$(($total3bytes+$bytes))
done

#calculate size and number of files
   total3bytes=$((total3bytes-total24bytes))
   numfiles2=$(($numfiles2-$numfiles1))

#print out results for 3 days
echo recent: $numfiles2 "("$total3bytes")"

#more than 3 days
totalAllbytes=0
bytes=0
numfiles3=0
filenames3=($(find $1 -type f))

#go through file names and calculate size
for i in ${filenames3[@]}
do
   bytes=($(du -cb $i))
   numfiles3=$(($numfiles3+1))
   totalAllbytes=$(($totalAllbytes+$bytes))
done

#calculate size and number of files
   totalAllbytes=$(($totalAllbytes-total3bytes))
   totalAllbytes=$(($totalAllbytes-total24bytes))
   numfiles3=$(($numfiles3-$numfiles2))
   numfiles3=$(($numfiles3-$numfiles1))

#print out results for more than 3 days
echo idle: $numfiles3 "("$totalAllbytes")"



