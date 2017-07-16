#!/bin/bash


# file: genRandom.sh
# author: Quoc-Bao Huynh
# course: csi 3336
# due date:9/15/16
#
# date modified   9/14/15
#   -file created
#
# This script will generate a space delimited list of
# random numbers given zero, one, or three command line
# parameters.
# 0 parameter = 10 number in range 0-9999
# 1 parameters = However many numbers specified in range 0-9999
# 2 parameters = # specified with a lower range to 9999
# 3 parametesr = # specified with a lower and upper range

#0 parameters
if [[ $# -eq 0 ]]
then
   for i in {1..10}
   do
      printf "$(( $RANDOM % 10000 )) "
   done

#1 parameter
elif [[ $# -eq 1 ]]
then
   i=$1
   while [[ $i -ge 1 ]]
   do
      printf "$(( $RANDOM % 10000 )) "
      i=$(($i - 1))
   done

#2 parameters
elif [[ $# -eq 2 ]]
then
   i=$1
   lower=$2
   range=$(( 10000 - $2 ))
   while [[ $i -ge 1 ]]
   do
      printf "$(( $lower+$RANDOM % $range)) "
      i=$(($i - 1))
   done

#3 parameters
elif [[ $# -eq 3 ]]
then 
   i=$1
   lower=$2
   range=$(( $3 - $2 ))
   while [[ $i -ge 1 ]]
   do
      printf "$(( $lower+$RANDOM % $range)) "
      i=$(($i - 1))
   done
elif [[ $# -ge 4 ]]
then
   echo genRandom.sh "[# of numbers] [lower bound] [upper bound]"
fi






