This file holds projects and scripts I have worked. 

InstaYak is a simple application that allows a user to communicate their current state with either "SLMD" or "UON". "UON" is asking whether the recipient of the message is able to hang out. An accompanying picture can be sent with the "UON". "SLMD" tells the recipient that the sender is busy. The server allows multiple users and communicates with Twitter so messages sent can be posted and seen. The client creates a TCP connection to the server that validates the user credentials. Users on the client will then be able to send messages. 

HoOn is an application that gets InstaYak messages from Twitter. The server creates a connection to Twitter to service client needs. The client creates a UDP connection to the server. The user specifies the amount of messages that they desire.

