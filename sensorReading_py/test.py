import RPi.GPIO as GPIO
import time
from smbus2 import SMBus

GPIO.setmode(GPIO.BCM)
GPIO.setup(21,GPIO.OUT)
adcvallue=0

bus = SMBus(1)
address = 0x48
def conver(data):
    val= data[0]*2^8+data[1]
    return val if val >10 else -1
def read_from_sensor():
        data = [193,227]
        bus.write_i2c_block_data(address, 1, data)    
        val = bus.read_i2c_block_data(address, 0,2)
        return val
    
while True:
    GPIO.output(21,GPIO.HIGH)
    time.sleep(280/1000000.0)
    adcvallue = read_from_sensor()
##    print(adcvallue)
    val=conver(adcvallue)
    if val !=-1: print(val)
    adcvallue=0
    GPIO.output(21,GPIO.LOW)
    time.sleep(1)
##    densinity = conver(adcvallue)
    