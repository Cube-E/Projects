
/*******************************************************************************
* files: fileInfo.c
* author: Quoc-Bao Huynh
* course: csi 3336
* due date:10/27/16
*
* date modified   10/25/16
*   -file created
*
* This program will take a list of files and order them by either
* 1. size
* 2. access time
* 3. modified time
* 4. status change time
*******************************************************************************/


#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>

/******************************************************************************/
struct FileInfo{
   char *path;
   struct stat info;
};
/******************************************************************************/

/******************************************************************************/
struct Node{
   struct FileInfo file;
   struct Node *next;
};
/******************************************************************************/

/******************************************************************************
 * initialize
 *
 * This function will start the linked list
 *
 * Parameters:
 *    arg: a pointer to the argument
 * Output:
 *    returns a pointer to the head of the list made
******************************************************************************/
struct Node* initialize( char* arg  ){
   struct Node *head = NULL;
   head = malloc(sizeof(struct Node));
   stat(arg, &(head->file.info));
   head->file.path = arg;
   head->next = NULL; 
   return head;
}
/******************************************************************************/


/******************************************************************************
 * addFront
 *
 * This function adds a node to the front of the linked list
 *
 * Parameters:
 *   head: a pointer to the beginning of the linked list
 *   arg: a pointer to the argument
 * Output:
 *   returns a pointer to the new head of the list
******************************************************************************/
struct Node* addFront(struct Node* head, char* arg){
   struct Node *new = NULL;
   new = malloc(sizeof(struct Node));
   stat(arg, &(new->file.info));
   new->file.path = arg;
   new->next = head;
   return new;
}  
/******************************************************************************/

/******************************************************************************
 * sortSize
 *
 * This function will sort an array of integers(sizes)
 *
 * Parameters:
 *    sArr: a pointer to the array of sizes
 *    key: a pointer to the array of key values
 *    size: the size of the arrays
 * Output:
 *    none
******************************************************************************/
void sortSize(int* sArr, int* key, int size){
   int temp;
   int i = 0;
   int j = 0;
   bool flag = false; 
   for( i; i < size-1;i++){
      flag = false;
      for(j=0; j < size-1-i; j++){
         if(sArr[j] > sArr[j+1]){
            temp = sArr[j];
            sArr[j] = sArr[j+1];
            sArr[j+1] = temp;
            temp = key[j];
            key[j] = key[j+1];
            key[j+1] = temp;
            flag = true;  
         }
      }   
      if(flag==false){break;}
   }
}
/******************************************************************************/

/******************************************************************************
 * sortTime
 *
 * This function will sort an array of time_t values
 *
 * Parameters:
 *    tArr: an array of time_t values
 *    key: an array of key values
 *    size: the size of the arrays
 * Output:
 *    none
******************************************************************************/
void sortTime(time_t* tArr, int* key, int size ){
   int temp;
   int i = 0;
   int j = 0;
   bool flag = false; 
   for( i; i < size-1;i++){
      flag = false;
      for(j=0; j < size-1-i; j++){
         double seconds = difftime(tArr[j], tArr[j+1]);
         if(seconds > 0){
            temp = tArr[j];
            tArr[j] = tArr[j+1];
            tArr[j+1] = temp;
            temp = key[j];
            key[j] = key[j+1];
            key[j+1] = temp;
            flag = true;  
         }
      }   
      if(flag==false){break;}
   }
}
/******************************************************************************/

/******************************************************************************
 * printIt
 *
 * This function will print all the arguments in the linked list to stdout
 *
 * Parameters:
 *    head: a pointer to the beginning of the list
 * Output:
 *    the arguments
******************************************************************************/
void printIt(struct Node* head){
   struct Node *temp = head;
   while(temp != NULL){
      
      int size = strSize(temp->file.path);
      write(1, temp->file.path, size);
      write(1, " ", 1);
      temp = temp->next;
   }
   write(1, "\n", 1);
} 
/******************************************************************************/


/******************************************************************************
 * strSize
 *
 * This function determines the size of the character array given
 *
 * Parameters:
 *    arg: a pointer to the character array 
 * Output:
 *    an integer telling how large the array is
******************************************************************************/
int strSize(char* arg){
   int i = 0;
   int size = 0;
   while(arg[i] != '\0'){
      size++;
      i++;
   }
   return size;
}
/******************************************************************************/

/******************************************************************************
 * destroy
 *
 * This function will free all memory allocated to the linked list
 *
 * Parameters:
 *    head: a pointer the beginning of the list 
 * Output:
 *    none
******************************************************************************/
void destroy(struct Node* head){
   while(head != NULL){
      struct Node* ptr = head;
      head = head->next;
      ptr->next = NULL;
      free(ptr);
   }
}
/******************************************************************************/

int main(int argc, char *argv[]){
   struct Node a;
   /*
    *i = for loop counter
    *ndx = index into arrays
    */
   int i = 0;
   int ndx = 0;

   /*Check if any arguments were passed*/
   /**********************************************************/   
   if(argc == 1){
      write(1, "Nothing", 8);
   }
   else{
      struct Node *head = NULL;
      struct Node test;
      char choice[1];
      /*************Print out the menu options*************/
      write(1, "Choose your order:\n", 19);
      write(1, "1. by size\n", 11);
      write(1, "2. by access time\n", 18);
      write(1, "3. by modified time\n", 20);
      write(1, "4. by status change time\n", 25);
      write(1, ": ", 2);
      read(0, choice, 1);
      /****************************************************/
   
      /*Sort by Size*/
      if(*choice == '1'){
      /*Make an array of integers storing indices of argv*/
         i = 1;
         ndx = 0;
         int key[argc-1];
         int sizes[argc-1];
         struct stat fileStat;
         for(i; i < argc; i++){
	    key[ndx] = i;
	    if( stat(argv[i], &fileStat)<0){
	       write(2, "ERROR:Not all files exist", 25);
	       return 1;
            }
         sizes[ndx] = fileStat.st_size;
         ndx++;   
         }         
         sortSize(sizes, key, argc-1);
         head = initialize(argv[key[0]]);
         for(i=1; i < argc-1; i++){
            head = addFront(head, argv[key[i]]);
         }
         printIt(head);
         destroy(head);
      }
      /*Sort by Access Time*/
      else if(*choice == '2'){
	 i = 1;
	 ndx = 0;
	 int key[argc-1];
	 time_t atimes[argc-1];
	 struct stat fileStat;
	 for(i; i < argc; i++){
	    key[ndx] = i;
	    if(stat(argv[i], &fileStat)<0){
	       write(2, "Error:Not all files exist", 25);
	       return 1;
	    }
	 atimes[ndx] = fileStat.st_atime;
	 ndx++;
	 }
	 sortTime(atimes, key, argc-1);
	 head = initialize(argv[key[0]]);
	 for(i=1; i < argc-1; i++){
	    head = addFront(head, argv[key[i]]);
	 } 
	 printIt(head);
	 destroy(head);      
      }
      /*Sort by Modification Time*/
      else if(*choice == '3'){        
	 i = 1;
	 ndx = 0;
	 int key[argc-1];
	 time_t mtimes[argc-1];
	 struct stat fileStat;
	 for(i; i < argc; i++){
	    key[ndx] = i;
	    if(stat(argv[i], &fileStat)<0){
	       write(2, "Error:Not all files exist", 25);
	       return 1;
	    }
	 mtimes[ndx] = fileStat.st_mtime;
	 ndx++;
	 }
	 sortTime(mtimes, key, argc-1);
	 head = initialize(argv[key[0]]);
	 for(i=1; i < argc-1; i++){
	    head = addFront(head, argv[key[i]]);
	 } 
	 printIt(head);
	 destroy(head);      	 
      }
     /*Sort by Status Change Time*/
      else if(*choice =='4'){
	 i = 1;
	 ndx = 0;
	 int key[argc-1];
	 time_t ctimes[argc-1];
	 struct stat fileStat;
	 for(i; i < argc; i++){
	    key[ndx] = i;
	    if(stat(argv[i], &fileStat)<0){
	       write(2, "Error:Not all files exist", 25);
	       return 1;
	    }
	 ctimes[ndx] = fileStat.st_ctime;
	 ndx++;
	 }
	 sortTime(ctimes, key, argc-1);
	 head = initialize(argv[key[0]]);
	 for(i=1; i < argc-1; i++){
	    head = addFront(head, argv[key[i]]);
	 } 
	 printIt(head);
	 destroy(head);              
      }
      else{
         write(2, "Error:Not a valid option\n" ,25);
         return 1;
      }
   }
return 0;
}

