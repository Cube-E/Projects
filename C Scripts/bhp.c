
/*
# files: bhp.c
# author: Quoc-Bao Huynh
# course: csi 3336
# due date:10/20/16
#
# date modified   10/20/16
#   -file created
# date modified   10/24/16
#   -worked on program
#
# This program will take your bash history process and output a summary of
# how many times a specific command has been executed.
**/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/******************************************************************************
 * CmdRec
 * 
 * Struct used to keep count of commands encountered in .bash_history
 *
******************************************************************************/
struct CmdRec{
/*Name of the command and Frequency of the commaands encountered */
char cmdName[13];
int cmdCount;
};
/*****************************************************************************/

/******************************************************************************
 * resize
 *
 * This function will increase the max capacity of the dynamically allocated 
 * array by 5 each time it is called.
 *
 * Parameters:
 *    *old: the address of the CmdRec array
 *    capacity: the max size of the array
 * Output:
 *    *new: the address of the resized array
******************************************************************************/
struct CmdRec* resize(struct CmdRec *old, int capacity){

int i = 0;
struct CmdRec *new = NULL;
new = malloc(sizeof(struct CmdRec)*(capacity + 5));
i = 0; 
for(i; i < capacity; i++){
   strcpy(new[i].cmdName, old[i].cmdName);
   new[i].cmdCount = old[i].cmdCount;
}

free(old);
return new;

}

/******************************************************************************
 * checkList
 *
 * This function will check the cmdArray and return 1 if the command is found
 * and 0 if it is not.
 *
 * Parameters:
 *    *cList: the address of the Cmd array
 *    size: the amount of elements in the array
 *    word: the word to compare into the array
******************************************************************************/
int checkList(struct CmdRec *cList, int size, char* word){

int ndx = -1;
int i = 0;
for(i; i < size; i++){
   if((strcmp(cList[i].cmdName, word) ==0)){
      ndx = i;
      return ndx;
   }
}
return ndx;
}
/*****************************************************************************/



int main(int argc, char *argv[]){

/*file declaration
 *char array to hold the parsed command
 *com = holds command
 *fre = holds freq
 */
FILE *fp;
char word[13];
char com[8] = "command";
char fre[5] = "freq";

/*len to hold the size of the line read
 *readsize to hold the final size 
 *line to hold the text of the line
 */
size_t len = 0;
size_t readSize;
char *line = NULL;

/*i = for loop counter
 *ndx = location in word array 
 *ldx = where we are on the line 
 */
int i = 0;
int ndx = 0;
int ldx = 0;
int aVal = -1;
/*Allocate memory. Only one 
 *Size = elements in the array
 *Capacity = size of the array
 * */
int size = 0;
int capacity = 1;
struct CmdRec *cList = NULL;
cList = malloc(sizeof(struct CmdRec));

/*OPEN INPUT*/
/**************************************************************/
/*check if input is in stdin */
if(argc == 1){
   fp = stdin;
}
/*else it's in argv[1] as a filename*/
else{
   fp = fopen (argv[1], "r");
   if(fp == NULL){
      printf("Cannot open file");
      return 1;
   }
}
/***************************************************************/


/*PROCESS INFORMATION*/
/***************************************************************/
while((readSize = getline(&line, &len, fp)) != -1){

/*GET THE FIRST WORD*/
 /**********************************************/ 
   memset(word, '\0', sizeof(word)); 
   /*Get the first word*/
   while(line[ldx] != ' ' && line[ldx] != '\n' && ndx < 14){
      word[ndx] = line[ldx];
      ndx++;
      ldx++;
   }

  if(ndx <= 13){ 
     /*extra ++ to move past the space or newline*/
     ldx++;
     /*null terminate the word and set ndx back to the beginning of word*/
     ndx = 0;

     /*Add to the array */
       if(size < capacity){
        /*if -1 then not in the list*/
        aVal = checkList(cList,size, word);
        if(aVal == -1){
           strcpy(cList[size].cmdName,word);
           cList[size].cmdCount = 1;
           size += 1;      
        }
        else{
           cList[aVal].cmdCount += 1;
        }

     }
     else{
        cList = resize(cList, capacity);
        capacity += 5;
        aVal = checkList(cList,size,word);
        if(aVal == -1){
           strcpy(cList[size].cmdName,word);
           cList[size].cmdCount = 1;
           size += 1;
        }
        else{
           cList[aVal].cmdCount += 1;
        }
      
     }
  /**********************************************/
   
  /*CHECK FOR OTHER COMMANDS*/
  /**********************************************/ 
     while(ldx < readSize){
        while(line[ldx] != '`' && line[ldx] != '|' && ldx < readSize ){ ldx++; }
     /*Check for pipes(|)*/
      /**********************************************/
        if(ldx < readSize && line[ldx] == '|'){
           /*+2 to account for the space after the | */
           ldx += 2;
           memset(word, '\0', sizeof(word)); 
           while(line[ldx] != ' ' && line[ldx] != '\n'){
              word[ndx] = line[ldx];
              ndx++;
              ldx++;
           }
           /*extra ++ to move past the space or newline*/
           ldx++;
           /*null terminate the word and set ndx back to the beginning of word*/
           ndx = 0;
        
            /*Add to the array */
           if(size < capacity){
              /*if -1 then not in the list*/
              aVal = checkList(cList,size, word);
              if(aVal == -1){
                 strcpy(cList[size].cmdName,word);
                 cList[size].cmdCount = 1;
                 size += 1;      
              }
              else{
                 cList[aVal].cmdCount += 1;
              }

           }
           else{
              cList = resize(cList, capacity);
              capacity += 5;
              aVal = checkList(cList,size,word);
              if(aVal == -1){
                 strcpy(cList[size].cmdName,word);
                 cList[size].cmdCount = 1;
                 size += 1;
              }
              else{
                 cList[aVal].cmdCount += 1;
              }
           }
        } 
   
   /*Check for backticks(`)*/
   /**********************************************/
        else if( ldx < readSize && line[ldx] == '`'){
           ldx += 1;
           memset(word, '\0', sizeof(word)); 
           while(line[ldx] != ' ' && line[ldx] != '`' && line[ldx] != '\n'){
              word[ndx] = line[ldx];
              ndx++;
              ldx++;
           }
           /*extra ++ to move past the space or newline*/
           if(line[ldx] == ' ' && ldx < readSize){
              /*end of the command so ++ till you reach the other '`' */
              while(line[ldx] != '`' && ldx < readSize){ ldx++; }
              /*move past the '`' and ' ' into the next word */
              ldx += 2;
           }
           /*Have already reached the other '`', so pass ' ' into the next word*/
           else if (line[ldx] == '`'){ ldx += 2; }
           /*null terminate the word and set ndx back to the beginning of word*/
           ndx = 0;

            /*Add to the array */ 
           if(size < capacity){
              /*if -1 then not in the list*/
              aVal = checkList(cList,size, word);
              if(aVal == -1){
                 strcpy(cList[size].cmdName,word);
                 cList[size].cmdCount = 1;
                 size += 1;      
              }
              else{
                 cList[aVal].cmdCount += 1;
              }

           } 
           else{
              cList = resize(cList, capacity);
              capacity += 5;
              aVal = checkList(cList,size,word);
              if(aVal == -1){
                 strcpy(cList[size].cmdName,word);
                 cList[size].cmdCount = 1;
                 size += 1;
              }
              else{
                 cList[aVal].cmdCount += 1;
              }
           }
        }     
     }
   ldx = 0;
   }    
}

/*Print out results :) */

printf("%*s", -12, com);
printf("%*s\n", 4, fre);
i = 0;
for(i; i < size; i++){
   printf("%*s ",-12,cList[i].cmdName);
   printf("%*d \n",4,  cList[i].cmdCount);
 }   
/***************************************************************/

/*CLOSE AND FREE*/
/***************************************************************/
if(argc != 1){
   fclose(fp);
}

if(line){
   free(line);
}

free(cList);
/***************************************************************/
return 0;
}


