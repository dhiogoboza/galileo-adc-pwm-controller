import threading
import socket
import select
import sys


import mraa # RUN ON DESKTOP

# from mraa import mraa # UNCOMMENT BEFORE SEND TO GALILEO

from time import sleep

ROT_MAX = 1024.0 # Max value as measured by ADC when pot is connected

tufx = None
tsdtcs = None

MIN_VALUE_SYSTEM_ON = 85.0

ADC_PIN = 0
PWM_PIN = 9
BUTTON_GPIO = 6
ADC_LED_PIN = 7
STATUS_LED_GPIO = 8

PWM_PERIOD = 1000

# socket vars
HOST = '' 
CONNECTIONS_LIST = []
RECV_BUFFER = 4096
PORT = 9010

# flags
system_running = 0
running_time_ellapsed = 0
functions_list = []
times_to_change_fx = []

closing = False

# global ports
btn = None
led_status = None
led_adc = None
pwm = None
adc = None

pwm_value = 0
adc_value = 0

# store current  function index
function_index = 0

# current f(x)
current_function = None
# fx = ax + b
fx = 0.0 # funQ
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

# Process functions received from client
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
	
	print("changing functions")

	index = 2
	size = len(split_rst)
	while index < size:
		x = Function()
		x.start = int(split_rst[index])
		x.end = int(split_rst[index + 1])
		x.a = float(split_rst[index + 2]) / 100
		x.b = float(split_rst[index + 3]) / 100

		print("adding", str(x))

		functions_list.append(x)
		times_to_change_fx.append(x.end)

		index += 4

	# Start system
	system_running = 1
	running_time_ellapsed = 0

def update_fx():
	global fx
	global system_running
	global running_time_ellapsed
	global times_to_change_fx
	global functions_list
	global function_index
	global current_function
	global closing

	# system is on
	# print("system_running: " + str(system_running)
	if system_running:
		#print("running_time_ellapsed: " + str(running_time_ellapsed)
		if (times_to_change_fx[function_index] == running_time_ellapsed):				
			if (function_index < len(functions_list)):
				current_function = functions_list[function_index]
				function_index+=1
				print("current_function changed:", current_function)
			else:
				system_running = 0

		if (current_function != None):
			# f(x) = ax + b
			fx = current_function.a * running_time_ellapsed + current_function.b
			print("current f(", running_time_ellapsed, "):", str(fx))
		
		running_time_ellapsed+=1

	if not closing:
		# sleep 1 second
		threading.Timer(1, update_fx).start()

# verify password sent by client
def verify_password(str_password):
	return str_password == "123456"

# send monitoring data to connected client
def send_data_to_client_socket():
	global system_running
	global pwm_value
	global adc_value
	global fx

	if len(CONNECTIONS_LIST) > 1:
		send_message(CONNECTIONS_LIST[1], "4," + str(system_running) + "," + str(int(pwm_value * 10000)) + "," + str(adc_value) + "," + str(fx))

	if not closing:
		# sleep 2 second
		threading.Timer(2, send_data_to_client_socket).start()

def listen_client_socket():
	global system_running
	global running_time_ellapsed
	
	server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	server_socket.bind((HOST, PORT))
	server_socket.listen(15)

	# add server socket object to the list of readable connections
	main_conn = Conn(server_socket)
	CONNECTIONS_LIST.append(main_conn)

	print("socket created")

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
				print("new connection")
				sockfd, addr = server_socket.accept()
				CONNECTIONS_LIST.append(Conn(sockfd, addr))
			
			# a message from a client, not a new connection
			else:
				print("data received")
				# process data recieved from client, 
				data = sock.recv(RECV_BUFFER)
				if data:
					split_rst = data.decode("utf-8").split(',')
					print("data received: ", str(split_rst))

					if (split_rst[0] == "0"):
						if (verify_password(split_rst[1])):
							print("Sending welcome message")
							send_message(currentConn, "1,1")
						else:
							send_message(currentConn, "1,0")
							CONNECTIONS_LIST.remove(currentConn)
					elif (split_rst[0] == "2"):
						running_time_ellapsed = 0
						system_running = int(split_rst[1])
						print("client switch on/off command result:", str(system_running))
						output_result()
					elif (split_rst[0] == "3"):
						process_fx(split_rst)
				else:
					# remove the socket that's broken    
					if currentConn in CONNECTIONS_LIST:
						CONNECTIONS_LIST.remove(currentConn)

# send message to specific user
def send_message(conn, message):
    try :
        conn.socket.send((message + "\n").encode("utf-8"))
    except Exception as e:
        print("Exception sending message:", str(e))
        # broken socket connection
        conn.socket.close()
        # broken socket, remove it
        if conn in CONNECTIONS_LIST:
            CONNECTIONS_LIST.remove(conn)

# broadcast message to all connected clients
def broadcast (message):
	size = len(CONNECTIONS_LIST)
	i = 1
	while (i < size):
        # send the message to a peer
		send_message(CONNECTIONS_LIST[i], message)
		i+=1

# Get adc voltage value from a defined pin upon function call
def read_inputs():
	try:
		adc_value = adc.read()
		# print("adc value:", adc_value)

		if (btn.read()):
			system_running = bool(system_running) ^ bool(1)
			sleep(0.5)
	except Exception as e:
		print("Exception:", str(e))

# Write results in ports
def output_result():
	global system_running
	global pwm_value
	global adc_value
	global fx
	global pwm
	global led_status
	global led_adc

	# system is on
	if (system_running):
		led_status.write(1)
		led_adc.write(adc_value / ROT_MAX)
		
		# compute pwm_value
		funV = float(adc_value)
		
		if (funV <= MIN_VALUE_SYSTEM_ON):
			funV = MIN_VALUE_SYSTEM_ON # MIN_VALUE_SYSTEM_ON representa a saida minima para o cooler funcionar

		auxX = (fx/100) * funV
		pwm_value = auxX / 1024.0
	
		print("computing pwm_value, funV:", str(funV), ", fx:", str(fx))
		print("pwm_value:", str(pwm_value), ", auxX:", str(auxX))

		pwm.write(pwm_value) ## AQUI DEVE SE ESCREVER O PERIODO ATIVO
	# system is off
	else:
		if (pwm_value != 0):
			led_status.write(0)
			led_adc.write(0)
			pwm.write(0)
		pwm_value = 0

def main():
	global pwm
	global adc
	global btn
	global led_status
	global led_adc
	global system_running

	global tufx
	global tlcs
	global tsdtcs

	process_fx("3,20,0,12,0,5000,12,30,-11,6333,30,36,83,-22000,36,48,0,8000,48,60,-70,42352".split(","))
	# system_running = 1

	############## Set up threads ##############
	# Update F(x)
	# tufx = threading.Thread(target=update_fx)
	# tufx.daemon = True
	# tufx.start()
	threading.Timer(0, update_fx).start()

	# Listen new client connections and data
	tlcs = threading.Thread(target=listen_client_socket)
	tlcs.daemon = True
	tlcs.start()
	
	# Send data to connected clients
	threading.Timer(0, send_data_to_client_socket).start()

	############## End set up threads ##############
	
	############## Set up ports ##############
	# Set up the ADC
	adc = mraa.Aio(ADC_PIN)
	
	# Set up the PWM
	pwm = mraa.Pwm(PWM_PIN)
	pwm.period_ms(PWM_PERIOD)
	pwm.enable(True)

	# Set up on/off switch
	btn = mraa.Gpio(BUTTON_GPIO)
	btn.dir(mraa.DIR_IN)
	
	# Set up status led
	led_status = mraa.Gpio(STATUS_LED_GPIO)
	led_status.dir(mraa.DIR_OUT)

	# Set up adc led
	led_adc = mraa.Pwm(ADC_LED_PIN)
	led_adc.enable(True)
	############## End set up ports ##############

	# Main thread loop
	while 1:
		read_inputs()
		output_result()
		sleep(1)


if __name__ == "__main__":
	global closing

	try:
		main()
	except KeyboardInterrupt:
		closing = True

	print("Exiting")










