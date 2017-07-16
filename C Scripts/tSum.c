
/*
# files: tSum.c 
# author: Quoc-Bao Huynh
# course: csi 3336
# due date:12/01/16
#
# date modified   12/01/16
#   -file created
#
# This program will take in a filename and a number. The program will then
# spawn the same amount of threads as the number given. Each thread will
# open up the file and read in the numbers to sum them. Each thread will read
# in an equal amount of numbers, and give them back to main to output the 
# results.
*/

#include <pthread.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <semaphore.h>


static sem_t lock;

/******************************************************************************
 * threadData  
 *
 * This struct holds the data for the threads 
******************************************************************************/
struct threadData{
   char *fileName;  
   int tid;
   int num;
   int offset;
   int *total;
   
};

/******************************************************************************
 * sumIt  
 *
 * This thread function opens a file and sums the numbers in the file 
 *
 * Parameters:
 *    *arg: a pointer that holds data for the thread
******************************************************************************/
void *sumIt( void *arg){
   struct threadData *data = arg;
   int j = 0;
   int *tot = data->total;
   int fd = open(data->fileName, O_RDONLY );
   lseek(fd, data->offset, SEEK_SET);
   sem_wait(&lock);
   int *arr =(int*)malloc((data->num*sizeof(int)));
   sem_post(&lock);
   read(fd,arr, (data->num*sizeof(int)));
   close(fd);
   sem_wait(&lock);
   for(j = 0; j < data->num; j++){
      *tot = *tot + arr[j];
   }
   free(arr);
   sem_post(&lock);
   return NULL;
}


int main(int argc, char *argv[]){

if(argc <= 2){
   write(1,"Not enough parameters", 22);
   
}
else if(argc > 3){
   write(1, "Too many parameters",20); 
}
else{

   struct stat fileStat;
   if(stat(argv[1], &fileStat) < 0){
      perror("Error: ");
   } 
   else{
      int numT = atoi(argv[2]);
      int tot= 0;
      /*get check if the number of threads is valid*/
      if(numT > 0){
         sem_init(&lock, 0, 1);
         /*Get the amount of numbers in the file, calc num per thread, calc 
          *leftofver numbers
          */
         int totNum = fileStat.st_size / 4;
         int numPerThread = totNum / numT;
         int extra = totNum % numT;
         /*input all data into threadData array to be able to pass in*/
         pthread_t *thr;
         thr = (pthread_t*)malloc(sizeof(pthread_t)*numT);
         struct threadData tData[numT];
         int i = 0;
         int offs = 0;
         for(i; i < numT; i++){
            if(extra > 0){
               tData[i].num = numPerThread + 1;
               tData[i].offset = offs*sizeof(int);
               offs = offs + numPerThread + 1;
               extra -= 1;
            }
            else{
               tData[i].num = numPerThread;
               tData[i].offset = offs*sizeof(int);
               offs = offs + numPerThread;
            }
            tData[i].fileName = argv[1];
            tData[i].total = &tot;
            tData[i].tid = i;
 
         }
        for(i = 0; i < numT; ++i){
           pthread_create(&thr[i], NULL, sumIt, &tData[i]);
        }   
        
        for(i = 0; i < numT; i++){
           pthread_join(thr[i], NULL);
        }
        write(1, "Sum: ", 6);
        char buff[11];
        
        if(tot > 0){
           for(i = 10; i >= 0 && tot; i--){
              buff[i] = (tot % 10);
              buff[i] = buff[i] + '0';
              tot = tot / 10;
           }
           for(; i >= 0; i--){
              buff[i] = '\0';
           }
           for(i = 0; i < 11; i++){
              if(buff[i])
                 write(1, &buff[i], 1);
           }
        }
        else{
           buff[0] = '0';
           write(1, &buff[0], 1);
        }
        buff[0] = '\n';
        write(1, &buff[0], 1);
        free(thr);
        sem_destroy(&lock);
      }
      else{
         write(1, "Zero threads given to sum\n",27); 
      }   
   }
}
return 0;
}
