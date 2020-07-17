import time
import paho.mqtt.client as mqtt
import ssl
import logging, traceback
import sys
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
    
ca = "/home/pi/Sienial/cacrt.crt" 
cert = "/home/pi/Sienial/clientcert.crt"
private = "/home/pi/Sienial/clientkey.key" 

logger = logging.getLogger()
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler(sys.stdout)
log_format = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(log_format)
logger.addHandler(handler)

def ssl_alpn():
    try:
        #debug print opnessl version
        logger.info("open ssl version:{}".format(ssl.OPENSSL_VERSION))
        ssl_context = ssl.create_default_context()
        ssl_context.set_alpn_protocols(["TLS1.2"])
        ssl_context.load_verify_locations(cafile=ca)
        ssl_context.load_cert_chain(certfile=cert, keyfile=private)

        return  ssl_context
    except Exception as e:
        print("exception ssl_alpn()")
        raise e


#define callbacks


if __name__ == '__main__':
    topic = "sensor"
    mqttc = mqtt.Client()
    ssl_context= ssl_alpn()
    mqttc.tls_set_context(context=ssl_context)
    logger.info("start connect")
    mqttc.username_pw_set("username", "password")
    mqttc.connect("IP ADDRESS", PORT, TIMEOUT)
    logger.info("connect success")
    mqttc.loop_start()
    while True:
        GPIO.output(21,GPIO.HIGH)
        time.sleep(280/1000000.0)
        adcvallue = read_from_sensor()
##    print(adcvallue)
        val=conver(adcvallue )
        if val !=-1: mqttc.publish(topic, str(val)+" μg/m3")
        adcvallue=0
        GPIO.output(21,GPIO.LOW)
        time.sleep(1)



##start loop to process received messages
##client.loop_start()
#wait to allow publish and logging and exit
time.sleep(1)
