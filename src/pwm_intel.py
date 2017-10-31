import mraa
import time
from threading import Thread

class PwmIntel(Thread):
	pwm_value = 0

	def __init__(self, pino, periodo, periodo_ativo:
		self.pino = pino
		self.periodo = periodo
		self.periodo_ativo = periodo_ativo

	def run(self):
		x = mraa.Pwm(self.pino) # seleciona o Pino de GPIO 
		x.period_ms(self.periodo)
		x.enable(True)
		x.write(self.periodo_ativo)
