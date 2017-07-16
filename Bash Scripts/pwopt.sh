#!/bin/bash

###############################################################################
#file: pwopt.sh
#author: Quoc-Bao Huynh
#course: CSI 3336
#due date: 10/11/2016
#
#date modified 10/11/2016
#   -file created
#
#This file will print the file specified. It takes in three different arguments.
#Duplex-will print on both sides of the paper
#Nup-will print multiple pages on one page. Numbers 1,2,4,6,9,16 valid
#Copy-will print out how many copies of the file the user specifies
###############################################################################

###########################################################################
# errorMessage
# errorMessage will print out the errormessage describing how to use the 
# function
#
# Parameters: none
#
# Output: the error message
###########################################################################
errorMessage() {
   echo pwopt.sh "[-duplex][-nUp][-copy]" filename
}


#print both sides
if [[ $# -eq 2 ]]
then
   #duplex
   var=`echo $1 | tr [[:upper:]] [[:lower:]]` 
   if [[ $var  == '-duplex' ]]
   then
     lpr -o sides=two-sided-long-edge $2
   #error
   else
      errorMessage   
   fi

#print nUp or copy
elif [[ $# -eq 3 ]]
then
   var=`echo $1 | tr [[:upper:]] [[:lower:]]`
   #check for copy
   if [[ $var == '-copy' ]]
   then
      `lpr -#$2num-copies $3`
 
   #check for nup 
   elif [[ $var  == '-nup' ]]
   then  
      #check if the number is valid
      if [[ $2 -eq 1 || $2 -eq 2 || $2 -eq 4 || $2 -eq 6 || $2 -eq 9 || $2 -eq 16 ]]
      then
        lpr -o number-up=$2 -o number-up-layout=tblr -o page-border=single $3
      else
         echo Only numbers 1,2,4,6,9,16 allowed
      fi
   #error
   else
      errorMessage
   fi
else
 errorMessage
fi
