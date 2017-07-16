/********************************************************************
 * files: genRandom.c
 * author: Quoc-Bao Huynh
 * course: csi 3336
 * due date: 10/25/16
 *
 * date modified   10/24/16
 *    -file created
 *
 * #
 * This script will generate a space delimited list of
 * random numbers given zero, one, or three command line
 * parameters.
 * option -b = will write to binary file. has to be first parameter
 * 0 parameter = 10 number in range 0-9999
 * 1 parameters = However many numbers specified in range 0-9999
 * 2 parameters = # specified with a lower range to 9999
 * 3 parametesr = # specified with a lower and upper range
********************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>

int main(int argc, char *argv[]){

time_t t;
srand((unsigned) time (&t));

/*0 parameters */
if(argc == 1){

   int i = 0;
   for(i; i < 10; i++){
      printf("%d ", rand() % 10000);
   }

}
/*2 parameters */
else if(argc == 2){

   /*write to binary file*/
   if(argv[1] == "-b"){
      FILE *fp = fopen("binary", "wb");
      if(fp == NULL){
         printf("error");
         return -1;
      }
      int i = 0;
      for(i; i < 10; i++){
         int rNum = rand() % 10000;
         printf("%d ",rNum);
         fwrite((const void*) &rNum, sizeof(int), 1,fp);
      }
     
   }
   /*do normal*/
   else{
      int i = 0;
      int count = atoi(argv[1]);
      for(i; i < count; i++){
         printf("%d ", rand() % 10000);
      }
   }
}
/*3 parameters */
else if (argc == 3){

   /*write to binary file*/
   if(argv[1] == "-b"){
      FILE *fp = fopen("binary", "wb");
      if(fp == NULL){
         printf("error");
         return -1;
      }
      int i = 0;
      int count = atoi(argv[2]);
      for(i; i < count; i++){
         int rNum = rand() % 10000;
         printf("%d ",rNum);
         fwrite((const void*) &rNum, sizeof(int), 1,fp);
      }
     
   }  
    /*do normal*/
    else{
      int i = 0;
      int count = atoi(argv[1]);
      int lower = atoi(argv[2]);
      int range = 10000 - lower;
      for(i; i < count; i++){
         printf("%d ", lower+rand()%range);

      }
   }
}
/*4 parameters*/
else if (argc == 4){

   /*write to binary file*/
   if(argv[1] == "-b"){
      FILE *fp = fopen("binary", "wb");
      if(fp == NULL){
         printf("error");
         return -1;
      }
      int i = 0;
      int count = atoi(argv[2]);
      int lower = atoi(argv[3]);
      int range = 10000 - lower;
      for(i; i < count; i++){
         int rNum = lower + rand()% range;
         printf("%d ",rNum);
         fwrite((const void*) &rNum, sizeof(int), 1,fp);
      }
     
   }
   /*do normal*/
   else{
      int i = 0; 
      int count = atoi(argv[1]);
      int lower = atoi(argv[2]);
      int range = (atoi(argv[3]) - lower);
      for(i; i < count; i++){
         printf("%d ", lower+rand()%range);
      }
   }
}
/*5 parameters*/
else if(argc >=5){
   /*write to binary file*/
   if(argv[1] == "-b"){
      FILE *fp = fopen("binary", "wb");
      if(fp == NULL){
         printf("error");
         return -1;
      }
      int i = 0;
      int count = atoi(argv[2]);
      int lower = atoi(argv[3]);
      int range = (atoi(argv[4]) - lower);
      for(i; i < count; i++){
         int rNum = lower + rand()% range;
         printf("%d ",rNum);
         fwrite((const void*) &rNum, sizeof(int), 1,fp);
      }
     
   }
   /*error*/
   else{
      printf("invalid set of arguments");
   } 

}

return 0;
}
