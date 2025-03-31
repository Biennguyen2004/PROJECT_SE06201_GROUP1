from maix import gpio, pinmap, time

pinmap.set_pin_function("A18", "GPIOA18")
lock = gpio.GPIO("GPIOA18", gpio.Mode.OUT)
lock.value(0)

while 1:
    lock.toggle()
    time.sleep_ms(500)
