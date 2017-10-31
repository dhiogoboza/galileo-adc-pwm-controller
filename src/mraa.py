class Pwm:
    def __init__(self, pin_number):
        print(pin_number)
    def enable(self, value):
        print("enabled:", str(value))
    def write(self, value):
        print("Pwm write:", str(value))
    def period_ms(self, value):
        print("Pwm period_ms:", str(value))


class Gpio:
    def __init__(self, pin_number):
        print(pin_number)
    def dir(self, value):
        print("dir:", str(value))
    def write(self, value):
        print("Gpio write:", str(value))
    def read(self):
        return 0

class Aio(object):
    def __init__(self, pin_number):
        print(pin_number)
    def dir(self, value):
        print("dir:", str(value))
    def read(self):
        return 0

class Mraa(object):
    DIR_IN = "DIR_IN"
    DIR_OUT = "DIR_OUT"

    def Aio(self, p):
        return Aio()
    def Pwm(self, p):
        return Pwm()
    def Gpio(self, p):
        return Gpio()

mraa = Mraa()

DIR_IN = "DIR_IN"
DIR_OUT = "DIR_OUT"
