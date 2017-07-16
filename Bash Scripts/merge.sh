#!/bin/bash

###########################################################################
# file: merge.sh
# author: Quoc-Bao Huynh
# course: CSI 3336
# due date: 9/21/2016
# date modified: 9/20/2016 
#      -file Created
# this script will take in 2 source directories and one directory
# that will made and put the files from the 2 source directories
# into the new one  
###########################################################################


#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~#
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
   echo merge.sh sourceDirectory sourceDirectory newDirectory "[-keep] [-larger]"
}
###########################################################################


###########################################################################
# merge
# merge will copy the files from the 2 source directories into the 3rd 
# directory. It will keep the newest file if two files with the same
# name are encountered
#
# Parameters: 3 Parameters. 2 directories and 1 new directory to make
#
# Output: no output
###########################################################################
merge() {
     mkdir -p $3
      #look through the files in dir1 
      for i in `ls $1`
      do
        #look through files in dir2
        for u in `ls $2`
        do
           #compare if the filenames are the same
   	   if [[ $i == $u ]]
	   then
	      #test to see which one is older and copy the newer one
	      if [[ $1/$i -ot $2/$u ]]
	      then
	         cp -r $2/$u $3
	      else
	         cp -r $1/$i $3
	      fi
	   fi
        done
      done
      #copy the rest
      cp -n -r $1/* $3
      cp -n -r $2/* $3 
}
###########################################################################


###########################################################################
# keep
# keep will copy the files from the 2 source directories into the 3rd 
# directory and change the older file's names to have a .old 
# extension. It will exit if the file with.old already exists
#
# Parameters: 3 Parameters. 2 directories and 1 new directory to make
#
# Output: no output
###########################################################################
keep() {
   mkdir -p $3
   

   #copy files in 1st directory  
   cp -n -r $1/* $3
  
   #look for files in dir1
   for i in `ls $1`
   do
      #look for files in dir 2
      for u in `ls $2`
      do 
	 #compare the files
         if [[ $i == $u  ]]
	 then 
	    #see which file is older
	    if [[ $2/$u -ot $1/$i ]]
	    then
	       #copy the files
	       if [[ ! -f $3/${u}.old ]]
	       then
	          cp -r $2/$u $3/${u}.old
               else
                  exit
               fi
	    else
	       if [[ ! -f $3/${i}.old ]]
	       then
   	          mv "$3/$i" "$3/${i}.old"
	          cp -r $2/$u $3
	       else
		  exit
               fi
	    fi
	 else
            #copy the rest
	    cp -n -r $2/$u $3
	 fi
      done
   done      
 
}
###########################################################################


###########################################################################
# larger
# larger will copy the files from the 2 source directories into the 3rd 
# directory. It will keep the larger file if two files with the same 
# name are encountered
#
# Parameters: 3 Parameters. 2 directories and 1 new directory to make
#
# Output: no output
###########################################################################
larger() {

     mkdir -p $3
      #look through the files in dir1 
      for i in `ls $1`
      do
        #look through files in dir2
        for u in `ls $2`
        do
           #compare if the filenames are the same
   	   if [[ $i == $u ]]
	   then
	      #test to see which one is larger and copy the larger one
	      if [[ `du -b $1/$i | cut -f1` -gt `du -b $2/$u | cut -f1` ]]
	      then
	        cp -r $1/$i $3
	      else
	        cp -r $2/$u $3
	      fi
	   fi
        done
      done
      #copy the rest
      cp -n -r $1/* $3
      cp -n -r $2/* $3 
}
###########################################################################


###########################################################################
# keepLarger
# keepLarger will copy the files from the 2 source directories into the 3rd 
# directory. If two files of the same name are encounted then the file will
# add .small to the end of the smaller file. It will exit if the name with
# .small already exists
#
# Parameters: 3 Parameters. 2 directories and 1 new directory to make
#
# Output: no output
###########################################################################
keepLarger() {
   mkdir -p $3
   
   #copy files in 1st directory  
   cp -n -r $1/* $3
   #look through files in dir 1 
   for i in `ls $1`
   do
      #look though files in dir2
      for u in `ls $2`
      do
         #compare file names
         if [[ $i == $u  ]]
	 then 
	    #compare file sizes
	    if [[ `du -b $2/$u | cut -f1` -lt `du -b $1/$i | cut -f1` ]]
	    then
	       #change file name
	       if [[ ! -f $3/${u}.small ]]
	       then
	          cp -r $2/$u $3/${u}.small
               else
                  exit
               fi
	    else
	       #change file name
	       if [[ ! -f $3/${i}.small ]]
	       then
   	          mv "$3/$i" "$3/${i}.small"
	          cp -r $2/$u $3
	       else
		  exit
               fi
	    fi
	 else
            #copy the rest
	    cp -n -r $2/$u $3
	 fi
      done
   done      
}
###########################################################################


#error check for < 3
if [[ $# -lt 3 ]]
then
   errorMessage
   exit

#check for 3 parameters
elif [[ $# -eq 3 ]]
then 

   #check for 2 source directories
   if [[ ! -d $1 ]] || [[ ! -d $2 ]] 
   then
       errorMessage
       exit
   #check if 3 argument is a directory
  elif [[ -d $3 ]]
   then 
      errorMessage
      exit
   else
   merge $1 $2 $3
  fi
#check for 4 parameters
elif [[ $# -eq 4 ]]
then
   
   #check for keep and larger
   kFlag=false
   lFlag=false
   parameter=""
   for i in $@
   do
      if [[ $i == '-keep' || $i == '-Keep' ]]
      then
         kFlag=true
      elif [[ $i == '-larger' || $i == '-Larger' ]]
      then 
    	 lFlag=true
      else
	 parameter="$parameter $i"
      fi
   done
   set $parameter
   
   #call functions based on flags
   if [[ "$kFlag" == true ]]
   then
      keep $1 $2 $3
   elif [[ "$lFlag" == true ]]
   then 
      larger $1 $2 $3
   elif [[ "$kFlag" == false && "lFlag" == false]]
   then
      errorMessage
      exit
   fi
#for 5 parameters
elif [[ $# -eq 5 ]]
then
   #check flags
   kFlag=false
   lFlag=false
   parameter=""
   for i in $@
   do
      if [[ $i == '-keep' || $i == '-Keep' ]]
      then
         kFlag=true
      elif [[ $i == '-larger' || $i == '-Larger' ]]
      then 
    	 lFlag=true
      else
	 parameter="$parameter $i"
      fi
   done
   set $parameter
   #call functions
   if [[ "$kFlag" == true && "$lFlag" == true ]]
   then 
      keepLarger $1 $2 $3
   elif [["kFlag" == flase && "lFlag" == false ]]
   then
      errorMessage
      exit
   fi
fi
