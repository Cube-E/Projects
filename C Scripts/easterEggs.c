
/*
# files: easterEggs.c
# author: Quoc-Bao Huynh
# course: csi 3336
# due date:11/29/16
#
# date modified   11/29/16
#   -file created
#
# This program will take in a filename and search the current directory for 
# all instances of the filename. It will look in each file and sum up the
# number of red, green, blue, yellow, and orange numbers 0,1,2,3,4,5 
# respectively
**/


#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <string.h>
#include <sys/stat.h>
#include <stdbool.h>
#include <sys/wait.h>

enum color{red, green, blue, yellow, orange};


/******************************************************************************
 * recurseDir 
 *
 * This function go through all subdirectories of the of the directory given
 * to find the filename given. If filename is found the function will call
 * other functions to compute the color desired
 *
 * Parameters:
 *    *dir: the directory
 *    *filename: the filename
 *    *count: the integer to store the number of times the color desired is 
 *            read from file
 *    color: the color to search for
******************************************************************************/
void recurseDir(char *dir, char *filename, int *count, int color)
{
   DIR *dp;
   struct dirent *entry;
   struct stat statbuf;

   if((dp = opendir(dir)) == NULL){
      printf( "cannot open director: %s\n", dir);
      return ;
   }
   chdir(dir);
   while((entry = readdir(dp)) != NULL){
      lstat(entry->d_name, &statbuf);
      if(S_ISDIR(statbuf.st_mode)){
         if(strcmp(".", entry->d_name) == 0 || strcmp("..", entry->d_name) == 0){continue;}
         recurseDir(entry->d_name, filename, count, color);
      }
      if(strcmp(entry->d_name, filename) == 0){
         char dir[256];
         char a = '/';
         getcwd(dir, sizeof(dir));
         strncat(dir, &a, 1);
         strcat(dir,filename);
         if(color == red){
           getRed(dir, count);
         }
         if(color == green){
            getGreen(dir, count);
         }
         if(color == blue){
            getBlue(dir,count);
         }
         if(color == yellow){
            getYellow(dir,count);
         }
         if(color == orange){
            getOrange(dir,count);
         }
      }
   }
   chdir("..");
   closedir(dp);
}

/******************************************************************************
 * getRed  
 *
 * This function sums all the instances of red(0) in the given file 
 *
 * Parameters:
 *    *path: the path to the file
 *    *count: the integer to store the number of times the color desired is 
 *            read from file
******************************************************************************/
void getRed(char *path, int *count){
  
   FILE *fp = fopen(path, "rb");
   int nr;
   int num;
   while((nr = fread(&num, sizeof(int), 1, fp))== 1){
      if(num == red){
         *count = *count +1;
      }
   }
   fclose(fp);
}

/******************************************************************************
 * getGreen  
 *
 * This function sums all the instances of green(1) in the given file 
 *
 * Parameters:
 *    *path: the path to the file
 *    *count: the integer to store the number of times the color desired is 
 *            read from file
******************************************************************************/


void getGreen(char *path, int *count){
  
   FILE *fp = fopen(path, "rb");
   int nr;
   int num;
   while((nr = fread(&num, sizeof(int), 1, fp))== 1){
      if(num == green){
         *count = *count +1;
      }
   }
   fclose(fp);
}

/******************************************************************************
 * getBlue
 *
 * This function sums all the instances of blue(2) in the given file 
 *
 * Parameters:
 *    *path: the path to the file
 *    *count: the integer to store the number of times the color desired is 
 *            read from file
******************************************************************************/
void getBlue(char *path, int *count){
  
   FILE *fp = fopen(path, "rb");
   int nr;
   int num;
   while((nr = fread(&num, sizeof(int), 1, fp))== 1){
      if(num == blue){
         *count = *count +1;
      }
   }
   fclose(fp);
}

/******************************************************************************
 * getYellow  
 *
 * This function sums all the instances of yellow(3) in the given file 
 *
 * Parameters:
 *    *path: the path to the file
 *    *count: the integer to store the number of times the color desired is 
 *            read from file
******************************************************************************/
void getYellow(char *path, int *count){
  
   FILE *fp = fopen(path, "rb");
   int nr;
   int num;
   while((nr = fread(&num, sizeof(int), 1, fp))== 1){
      if(num == yellow){
         *count = *count +1;
      }
   }
   fclose(fp);
}

/******************************************************************************
 * getOrange  
 *
 * This function sums all the instances of orange(4) in the given file 
 *
 * Parameters:
 *    *path: the path to the file
 *    *count: the integer to store the number of times the color desired is 
 *            read from file
******************************************************************************/
void getOrange(char *path, int *count){
  
   FILE *fp = fopen(path, "rb");
   int nr;
   int num;
   while((nr = fread(&num, sizeof(int), 1, fp))== 1){
      if(num ==orange){
         *count = *count +1;
      }
   }
   fclose(fp);
}

/*void searchDir()*/
int main(int argc, char *argv[]){


   if(argc == 1){
      printf("Please Enter a Filename");
   }
   else if(argc > 2){
      printf("Only one may be entered at a time");
   }
   else{
      
      /*dir - will hold directory */
      char dir[256];
      int pid;
      bool parent = true;
      int i;

      /* Get the current directory*/
      getcwd(dir,sizeof(dir));
      
      int statval;
      /*spawn 5 children*/
      for(i = 0; i < 5 && parent; i++){
 
         /*spawn processes*/
         /*****************************************/          
         pid = fork();
         if(pid == 0){
            parent = false;
           if(i == red ){
                int count = 0;
                recurseDir(dir, argv[1], &count, red);
                exit(count);
            }
            if(i == green){
                int count = 0;
                recurseDir(dir, argv[1], &count, green);                
                exit(count);
            }
             if(i == blue){
               int count = 0;
               recurseDir(dir, argv[1], &count, blue);
               exit(count);
            }
            if(i == yellow){
               int count = 0;
               recurseDir(dir, argv[1], &count, yellow);
               exit(count);
            }
            if(i == orange){
               int count = 0;
               recurseDir(dir, argv[1], &count, orange);  
               exit(count);
            }
         }
         /*****************************************/
         /*wait*/
         if(parent){
            wait(&statval);
            if(WIFEXITED(statval)){
               if(i == red){
                  printf("red: %10d\n", WEXITSTATUS(statval));
               }
               if(i == green){
                  printf("green: %8d\n", WEXITSTATUS(statval));
               }
               if(i == blue){
                  printf("blue: %9d\n", WEXITSTATUS(statval));
               }
               if(i == yellow){
                  printf("yellow: %7d\n", WEXITSTATUS(statval));
               }
               if(i == orange){
                  printf("orange: %7d\n", WEXITSTATUS(statval));
               }
            }
            else{
               printf("Child did not terminate with exit\n");   
            }
         }
      }
   } 
   return 0;
}
