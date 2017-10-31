import threading
import socket
import select
import sys

from time import sleep

HOST = '' 
CONNECTIONS_LIST = []
RECV_BUFFER = 4096
PORT = 9010

system_running = True
running_time_ellapsed = 0
functions_list = []
times_to_change_fx = []

# store current  function index
function_index = 0

# current f(x)
current_function = None

# fx = ax + b
fx = 0.0
a = 0.0
b = 0.0

class Conn:
   main = False

   def __init__(self, socket, addr = "", main = True):
      self.socket = socket
      self.addr = addr
      self.main = main

class Function:
	start = 0
	end = 0
	a = 0.0
	b = 0.0

	def __str__(self):
		return "[start: " + str(self.start) + ", end: " + str(self.end) + ", a: " + str(self.a) + ", b:" + str(self.b) + "]"

def process_fx(split_rst):
	global functions_list
	global functon_index
	global times_to_change_fx
	global running_time_ellapsed
	global current_function

	# clear variables
	current_function = None
	function_index = 0
	running_time_ellapsed = 0
	functions_list = []
	times_to_change_fx = [0]
	
	print "changing functions"

	index = 2
	size = len(split_rst)
	while index < size:
		
		x = Function()
		x.start = int(split_rst[index])
		x.end = int(split_rst[index + 1])
		x.a = float(split_rst[index + 2]) / 100
		x.b = float(split_rst[index + 3]) / 100

		print "adicionando " + str(x)

		functions_list.append(x)
		times_to_change_fx.append(x.end)

		index += 4

def update_fx():
	global system_running
	global running_time_ellapsed
	global times_to_change_fx
	global functions_list
	global function_index
	global current_function

	# system is on
	for i in xrange(100): #while True:#
		if system_running:
			#print "running_time_ellapsed: " + str(running_time_ellapsed)
			if (times_to_change_fx[function_index] == running_time_ellapsed):
				
				if (function_index < len(functions_list)):
					current_function = functions_list[function_index]
					function_index+=1
					print "current_function changed: " + str(current_function )
				else:
					system_running = False
					

			if (current_function != None):
				# f(x) = ax + b
				fx = current_function.a * running_time_ellapsed + current_function.b
				#print "current f(" + str(running_time_ellapsed) + "): " + str(fx)
				running_time_ellapsed+=1
		# sleep 1 second
		sleep(1)

def verify_password(str_password):
	return str_password == "123456"

def listen_client_socket():
	server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	server_socket.bind((HOST, PORT))
	server_socket.listen(15)

	# add server socket object to the list of readable connections
	main_conn = Conn(server_socket)
	CONNECTIONS_LIST.append(main_conn)

	print "socket created"

	while 1:
		sockets = (o.socket for o in CONNECTIONS_LIST)
        
		# get the list sockets which are ready to be read through select
		# 4th arg, time_out  = 0 : poll and never block
		ready_to_read,ready_to_write,in_error = select.select(sockets,[],[],0)

		for sock in ready_to_read:
			for conn in CONNECTIONS_LIST:
				if conn.socket == sock:
					currentConn = conn

            # a new connection request recieved
			if currentConn == main_conn:
				print "new connection"
				sockfd, addr = server_socket.accept()
				CONNECTIONS_LIST.append(Conn(sockfd, addr))
			
			# a message from a client, not a new connection
			else:
				print "data received"
				# process data recieved from client, 
				data = sock.recv(RECV_BUFFER)
				if data:
					split_rst = data[:-2].split(',')
					print "data received: " + str(split_rst)

					if (split_rst[0] == "0"):
						if (verify_password(split_rst[1])):
							send_message(currentConn, "1,1")
						else:
							send_message(currentConn, "1,0")
							CONNECTIONS_LIST.remove(currentConn)
					elif (split_rst[0] == "1"):
						process_fx(split_rst)
				else:
					# remove the socket that's broken    
					if currentConn in CONNECTIONS_LIST:
						CONNECTIONS_LIST.remove(currentConn)

# send message to specific user
def send_message(conn, message):
    try :
        conn.socket.send(message + "\n")
    except :
        # broken socket connection
        conn.socket.close()
        # broken socket, remove it
        if conn in CONNECTIONS_LIST:
            CONNECTIONS_LIST.remove(conn)

def main():
	
	#process_fx("3,20,0,12000,0,5000,12000,30000,-11,6333,30000,36000,83,-22000,36000,48666,0,8000,48666,60000,-70,42352")
	
	process_fx("3,20,0,12,0,5000,12,30,-11,6333,30,36,83,-22000,36,48,0,8000,48,60,-70,42352".split(","))

	threading.Thread(target=update_fx).start()
	threading.Thread(target=listen_client_socket).start()

if __name__ == "__main__":
    sys.exit(main())  
