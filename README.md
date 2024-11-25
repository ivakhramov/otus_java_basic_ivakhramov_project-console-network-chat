#Ivan Vakhramov
ivan.vakhramov@yandex.ru

# otus_java_basic_ivakhramov_project-console-network-chat
The course project "Console network chat" of the Otus. Java Developer. Basic

Project "Console Network Chat"
Difficulty: 4
Note: The project is a continuation of the chat implemented in the course
Objective: Apply the knowledge gained in the course
Task: Implement a client-server application "Network Chat" in java.io, which has the following capabilities:

# Server part:
• Launching a ServerSocket, listening on a specific port, and waiting for connections
• Handling each connection in a separate thread
• Receiving messages from clients: control commands and chat messages
• Ability for client registration and authentication, with client data stored in a database
• Broadcasting messages to all authorized clients
• The server must add the timestamp to the messages before broadcasting
• Support for private messages
• Ability for the client to change their nickname upon request
• Broadcasting the list of active clients
• Automatic disconnection of clients who have been inactive for more than 20 minutes
• Support for user roles (admin, user)

#Client part:
• Ability to connect to the server
• Ability to send and receive messages
• Ability to disconnect from the server using the command /exit

#General questions:
• All administrative actions can be performed through regular messages that start with /
• /reg – registration
• /auth – authentication
• /w – private message
• /exit – exit (for the client)
• /getActiveClients – list of active clients
• /changeName – change nickname
